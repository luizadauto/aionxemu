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
package gameserver.quest.handlers.template;

import gameserver.dataholders.DataManager;
import gameserver.model.NpcType;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.NpcTemplate;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.quest.HandlerResult;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

import org.apache.log4j.Logger;

/**
 * @author MrPoke Like: Sleeping on the Job quest.
 */
public class ReportTo extends QuestHandler
{
    private static final Logger    log        = Logger.getLogger(ReportTo.class);
    
    private final int    questId;
    private final int    startNpc;
    private final int    endNpc;
    private final int    addEndNpc;
    private final int    itemId;
    private final int    readableItemId;

    /**
     * @param questId
     * @param startNpc
     * @param endNpc
     */
    public ReportTo(int questId, int startNpc, int endNpc, int addEndNpc, int itemId, int readableItemId)
    {
        super(questId);
        this.startNpc = startNpc;
        this.endNpc = endNpc;
        if(addEndNpc != 0)
            this.addEndNpc = addEndNpc;
        else
            this.addEndNpc = endNpc;
        this.questId = questId;
        this.itemId = itemId;
        this.readableItemId = readableItemId;
    }

    @Override
    public void register()
    {
        qe.setNpcQuestData(startNpc).addOnQuestStart(questId);
        qe.setNpcQuestData(startNpc).addOnTalkEvent(questId);
        NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(startNpc);
        if (template == null)
            log.warn("Q" + questId + " has invalid start NPC");
        else if (template.getNpcType() == NpcType.USEITEM)
            qe.setNpcQuestData(startNpc).addOnActionItemEvent(questId);
        qe.setNpcQuestData(endNpc).addOnTalkEvent(questId);
        if(addEndNpc != endNpc)
            qe.setNpcQuestData(addEndNpc).addOnTalkEvent(questId);
        if(readableItemId != 0)
            qe.setQuestItemIds(readableItemId).add(questId);
    }
    
    @Override
    public boolean onActionItemEvent(QuestCookie env)
    {
        return (startNpc == env.getTargetId());
    }

    @Override
    public boolean onDialogEvent(QuestCookie env)
    {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        boolean daily = false;
        
        if(player.getGuild().getCurrentQuest() == env.getQuestId())
            daily = true;
        
        if(defaultQuestStartDaily(env))
            return true;
        if(defaultQuestNoneDialog(env, startNpc, itemId, 1))
            return true;
        
        if(qs == null)
            return false;
        int var = qs.getQuestVarById(0);
        if(qs.getStatus() == QuestStatus.START)
        {
            if(daily)
            {
                if(env.getTargetId() == startNpc)
                {
                    switch(env.getDialogId())
                    {
                        case 25:
                            if(var == 0)
                                return sendQuestDialog(env, 1352);
                        case 10000:
                            return defaultCloseDialog(env, 0, 1);
                    }
                }
                if(env.getTargetId() == endNpc || env.getTargetId() == addEndNpc)
                {
                    switch(env.getDialogId())
                    {
                        case 25:
                            if(var == 1)
                                return sendQuestDialog(env, 2375);
                        case 1009:
                            if(qs.getStatus() != QuestStatus.COMPLETE)
                            {
                                defaultQuestRemoveItem(env, itemId, 1);
                                return defaultCloseDialog(env, 1, 0, true, true);
                            }
                    }
                }
            }
            else
            {
                if(env.getTargetId() == endNpc)
                {
                    switch(env.getDialogId())
                    {
                        case 25:
                            if(var == 0)
                                return sendQuestDialog(env, 2375);
                        case 1009:
                            if(qs.getStatus() != QuestStatus.COMPLETE)
                            {
                                defaultQuestRemoveItem(env, itemId, 1);
                                return defaultCloseDialog(env, 0, 1, true, true);
                            }
                    }
                }
            }
        }
        if(defaultQuestRewardDialog(env, endNpc, 0) || (defaultQuestRewardDialog(env, addEndNpc, 0) && daily))
            return true;
        else
            return false;
    }
    
    @Override
    public HandlerResult onItemUseEvent(QuestCookie env, Item item)
    {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE)
            return HandlerResult.UNKNOWN;
        
        if (id != readableItemId)
            return HandlerResult.UNKNOWN;
        
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 1000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable(){
            @Override
            public void run()
            {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
            }
        }, 1000);
        return HandlerResult.FAILED; // we did that
    }
}
