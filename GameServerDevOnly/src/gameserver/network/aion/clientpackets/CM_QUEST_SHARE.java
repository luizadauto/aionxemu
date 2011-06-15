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
import gameserver.dataholders.QuestsData;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.group.PlayerGroup;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.QuestService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;

/**
 * @author Kamui - Bio
 * 
 */
public class CM_QUEST_SHARE extends AionClientPacket
{
    static QuestsData questsData = DataManager.QUEST_DATA;
    public int questId;

    public CM_QUEST_SHARE(int opcode)
    {
        super(opcode);
    }

    @Override
    protected void readImpl()
    {
        questId = readD();
    }

    @Override
    protected void runImpl()
    {
        final Player player = getConnection().getActivePlayer();

        //NPE Check - Exploit Check
        if (player == null || questsData.getQuestById(questId).isCannotShare())
            return;

        //Player can only share quests within a group
        if (!player.isInGroup())
        {
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100000));//[there are no group members to share that quest with]
            return;
        }

        //Player cannot share quests he dont have or its already completed
        final QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.COMPLETE)
            return;

        //Player must try to share quests with all his group members
        PlayerGroup playerGroup = player.getPlayerGroup();

        for(Player target : playerGroup.getMembers())
        {
            if( target == player || !MathUtil.isIn3dRange(target, player, 95))
                continue;

            //Cannot share quests if the target player dont meet the level requirements
            if (!QuestService.checkLevelRequirement(questId, target.getCommonData().getLevel()))
            {
                PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100003, (target.getName())));//[you failed to share the quest with %playername]
                PacketSendUtility.sendPacket(target, new SM_SYSTEM_MESSAGE(1100003, (player.getName())));//[you failed to share the quest with %playername]
                return;
            }

            //Send share quest dialog question to target players and wait for the answer
            PacketSendUtility.sendPacket(target, new SM_QUEST_ACCEPTED(questId, ((VisibleObject) player).getObjectId(), true));
        }
    }
}
