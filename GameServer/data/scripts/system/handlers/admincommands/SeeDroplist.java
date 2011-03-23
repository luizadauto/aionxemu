/*
*This file is part of Aion X Emu <aionxemu.com>
*
*  aion-emu is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  aion-emu is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
*/
package admincommands;

import gameserver.configs.administration.AdminConfig;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.model.drop.DropTemplate;
import gameserver.services.DropService;

/**
* @AionChs 
*/

public class SeeDroplist extends AdminCommand
{
    public SeeDroplist()
    {
        super("droplist");
    }

    @Override
    public void executeCommand(Player admin, String[] params)
    {
        if(admin.getAccessLevel() < AdminConfig.COMMAND_SEEDROPLIST )
        {
            PacketSendUtility.sendMessage(admin, "No right to use the command");
            return;
        }
        
        VisibleObject target = admin.getTarget();
        if(target == null)
        {
            PacketSendUtility.sendMessage(admin, "Please select a destination, and enter //droplist");
            return;
        }
        
        if(target instanceof Npc)
        {
            Npc npc = (Npc) admin.getTarget();
            int npc_id = npc.getNpcId();
            PacketSendUtility.sendMessage(admin, "The monster may drop the following items\n ---------------------------");
            String dropname = "";
            for (DropTemplate dropTemplate : DropService.getInstance().getDropList().getDropsFor(npc_id)) 
            {
                int itemId = dropTemplate.getItemId();
                dropname+="[item:"+itemId+"];|";
            }
            PacketSendUtility.sendMessage(admin, dropname);
        }
        else
            PacketSendUtility.sendMessage(admin, "Please select a NPC as the goal, then enter //droplist");

    }
} 
