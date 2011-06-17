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

package gameserver.controllers;

import gameserver.ai.events.Event;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.teleport.TelelocationTemplate;
import gameserver.model.templates.teleport.TeleportLocation;
import gameserver.model.templates.teleport.TeleporterTemplate;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.exceptionhandlers.exception_enums;

/**
 * @author Dns
 * 
 */

public class RestrictedPortalController extends NpcController
{
    @Override
    public void onDialogRequest(final Player player)
    {
        getOwner().getAi().handleEvent(Event.TALK);

        // Inggison & Gelkmaros teleporters
        Npc npc = getOwner();
        if(npc.getNpcId() == exception_enums.NPC_TELEPORT_BALAUREA_ASMO
            || npc.getNpcId() == exception_enums.NPC_TELEPORT_BALAUREA_ELYOS)
        {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
            return;
        }
    }

    @Override
    public void onDialogSelect(int dialogId, final Player player, int questId)
    {
        Npc npc = getOwner();
        int targetObjectId = npc.getObjectId();

        // 1st case : Gelkmaros & Inggison
        if(dialogId == 10000
            && (npc.getNpcId() == exception_enums.NPC_TELEPORT_BALAUREA_ASMO || npc.getNpcId() == exception_enums.NPC_TELEPORT_BALAUREA_ELYOS))
        {
            int completedquestid = 0;
            switch(player.getCommonData().getRace())
            {
                case ASMODIANS:
                    completedquestid = 20001;
                    break;
                case ELYOS:
                    completedquestid = 10001;
                    break;
            }
            QuestState qstel = player.getQuestStateList().getQuestState(completedquestid);
            if(qstel == null || qstel.getStatus() != QuestStatus.COMPLETE)
            {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
                return;
            }

            TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplate(npc.getNpcId());
            if(template != null)
            {
                TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
                if(loc != null)
                {
                    if(!player.getInventory().decreaseKinah(loc.getPrice()))
                        return;
                    
                    TelelocationTemplate tlt = DataManager.TELELOCATION_DATA.getTelelocationTemplate(loc.getLocId());
                    TeleportService.teleportTo(player, tlt.getMapId(), tlt.getX(), tlt.getY(), tlt.getZ(), 1000);
                }
            }
        }
    }
}