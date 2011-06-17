/**
 * This file is part of Aion X Emu <aionxemu.com>
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.network.aion.clientpackets;

import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.QuestTemplate;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.ClassChangeService;
import gameserver.services.QuestService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

import org.apache.log4j.Logger;

/**
 * @author KKnD, orz, avol, kamui
 */
public class CM_DIALOG_SELECT extends AionClientPacket
{
    /**
     * Target object id that client wants to TALK WITH or 0 if wants to unselect
     */
    private int        targetObjectId;
    private int        dialogId;
    private int        selectableReward;
    @SuppressWarnings("unused")
    private int        lastPage;
    private int        questId;

    private static final Logger log = Logger.getLogger(CM_DIALOG_SELECT.class);
    /**
     * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
     * 
     * @param opcode
     */
    public CM_DIALOG_SELECT(int opcode)
    {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl()
    {
        targetObjectId = readD();// empty
        dialogId = readH(); // total no of choice
        selectableReward = readH(); // selectable reward number in case of last reward for multiple time quests
        lastPage = readH();
        questId = readD();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl()
    {
        final Player player = getConnection().getActivePlayer();
        if (player == null)
            return;
        
        if(dialogId == 1002)
        {
            if(!exceptionQuest(questId))
            {
                int activeQuestCount = 0;
                for(QuestState qs : player.getQuestStateList().getAllQuestState())
                {
                    if(!exceptionQuest(qs.getQuestId()))
                    {
                        QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(qs.getQuestId());
                        if((qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD) && !questTemplate.isCannotGiveup())
                            activeQuestCount++;
                    }
                }
                if(activeQuestCount >= 30)
                {
                    PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300622));
                    return;
                }
            }
        }

        //check for fake quest packets (except taloc and kromede trial)
        if (questId != 0 && questId != 10021 && questId != 20021 && questId != 18602 && questId != 28602)
        {
            QuestState qs = player.getQuestStateList().getQuestState(questId);
            int questMaxRepeatCount = DataManager.QUEST_DATA.getQuestById(questId).getMaxRepeatCount();
            
            if ( qs != null )
            {
                 if ( qs.getStatus()==QuestStatus.COMPLETE && questMaxRepeatCount<=1 )
                 {
                     log.info("[AUDIT] "+ player.getName()+" sending fake quest reward packet, quest: " + questId);
                     return;
                 }
            }
        }
        //end fake check
        
        if (targetObjectId == 0)
        {
            if (QuestEngine.getInstance().onDialog(new QuestCookie(null, player, questId, dialogId)))
                return;
            // FIXME client sends unk1=1, targetObjectId=0, dialogId=2 (trader) => we miss some packet to close window
            ClassChangeService.changeClassToSelection(player, dialogId);
            return;
        }
        
        if(targetObjectId == player.getObjectId())
        {
            if(QuestEngine.getInstance().onDialog(new QuestCookie(null, player, questId, dialogId)))
                return;
        }

        AionObject object = World.getInstance().findAionObject(targetObjectId);

        if (object instanceof Player && dialogId == 1002)
        {
            if (QuestService.startQuest(new QuestCookie(null, player, questId, 0), QuestStatus.START))
            {
                PacketSendUtility.sendPacket(player, new SM_QUEST_ACCEPTED(2, questId, QuestStatus.START, 0));
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100002, ((Player)object).getName()));//[you shared the quest with %playername]
                PacketSendUtility.sendPacket((Player)object, new SM_SYSTEM_MESSAGE(1100002, player.getName()));//[you shared the quest with %playername]
                player.getController().updateNearbyQuests();
            }
            else
            {
                PacketSendUtility.sendPacket((Player)object, new SM_SYSTEM_MESSAGE(1100003, player.getName()));//[you failed to share the quest with %playername]
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300597));//[you cannot receive a quest that you are already working on]
                return;
            }
        }
        else if (object instanceof Creature)
        {
            Creature creature = (Creature) object;
            switch(selectableReward)
            {
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                    dialogId = selectableReward;
                    break;
            }
            creature.getController().onDialogSelect(dialogId, player, questId);
        }
    }
    
    private boolean exceptionQuest(int questId)
    {
        if(questId >= 5000 && questId <= 6540) //Work Orders
            return true;
        if(questId >= 48000 && questId <= 48005) //Guild Entering pre-quests
            return true;
        return false;
    }
}
