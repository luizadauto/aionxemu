/*
 *  This file is part of Aion X EMU <aionxemu.com>.
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package admincommands;

import gameserver.configs.administration.AdminConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Shepper
 *
 */
public class Admin extends AdminCommand 
{
	public Admin() 
    {
	    super("admin");
	}

	@Override
	public void executeCommand(Player admin, String[] params) 
	{
        if(admin.getAccessLevel() < AdminConfig.COMMAND_ADMIN)
        {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
            return;
        }
        PacketSendUtility.sendMessage(admin, "|----------------------|-----------------------");
		PacketSendUtility.sendMessage(admin, "|  //commands     |    params                  ");
        PacketSendUtility.sendMessage(admin, "|----------------------|-----------------------");
	    PacketSendUtility.sendMessage(admin, "| //promote <characterName> <rolemask>");
		PacketSendUtility.sendMessage(admin, "| //revoke <characterName>");
	    PacketSendUtility.sendMessage(admin, "| //add <itemID> <quantity>");
	    PacketSendUtility.sendMessage(admin, "| //addskill <skillId> <skillLevel>");
	    PacketSendUtility.sendMessage(admin, "| //givemissingskills");
	    PacketSendUtility.sendMessage(admin, "| //addtitle <title_id> <playerName>");
	    PacketSendUtility.sendMessage(admin, "| //announce <message>");
	    PacketSendUtility.sendMessage(admin, "| //notice <message>");
	    PacketSendUtility.sendMessage(admin, "| //info <target player>");
	    PacketSendUtility.sendMessage(admin, "| //set level <level>");
	    PacketSendUtility.sendMessage(admin, "| //set exp <value>");
	    PacketSendUtility.sendMessage(admin, "| //set title <title_id>");
	    PacketSendUtility.sendMessage(admin, "| //set class <class_id>");
	    PacketSendUtility.sendMessage(admin, "| //speed <value>");
		PacketSendUtility.sendMessage(admin, "| //heal <target>");
		PacketSendUtility.sendMessage(admin, "| //kill <playername>");
	    PacketSendUtility.sendMessage(admin, "| //kick <playername>");
	   	PacketSendUtility.sendMessage(admin, "| //unstuck");
	    PacketSendUtility.sendMessage(admin, "| //moveplayertoplayer <player1> <player2>");
	    PacketSendUtility.sendMessage(admin, "| //movetome <player>");
	    PacketSendUtility.sendMessage(admin, "| //movetoplayer <player>");
	    PacketSendUtility.sendMessage(admin, "| //moveto < X Y Z>");
	    PacketSendUtility.sendMessage(admin, "| //goto <place name>");
	    PacketSendUtility.sendMessage(admin, "| //spawn <npc_id>");
	    PacketSendUtility.sendMessage(admin, "| //delete <target npc>");
	    PacketSendUtility.sendMessage(admin, "| //save_spawn");
	    PacketSendUtility.sendMessage(admin, "| //reload_spawn");
	    PacketSendUtility.sendMessage(admin, "| //adddrop <mobid> <itemid> <min> <max> <chance> <quest>");
	    PacketSendUtility.sendMessage(admin, "| //zone");
	    PacketSendUtility.sendMessage(admin, "| //reload skill");
	    PacketSendUtility.sendMessage(admin, "| //reload quest");
	    PacketSendUtility.sendMessage(admin, "| //quest <start|delete|step|info|vars> <quest_id>");
	    PacketSendUtility.sendMessage(admin, "| //sys <info|memory|gc|restart|shutdown>");
	    PacketSendUtility.sendMessage(admin, "| //ai <info|event|state>");
	    PacketSendUtility.sendMessage(admin, "| //weather <location> <0 - 8>");
	    PacketSendUtility.sendMessage(admin, "| //legion <disband|setlevel|setname|setpoints>");
        PacketSendUtility.sendMessage(admin, "| //sprison <player> <delay>");
		PacketSendUtility.sendMessage(admin, "| //rprison <player>");
        PacketSendUtility.sendMessage(admin, "|____________________________________________");
        }
}