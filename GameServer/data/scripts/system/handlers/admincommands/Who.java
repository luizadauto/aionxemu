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

package admincommands;

import java.util.Iterator;

import gameserver.configs.administration.AdminConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.World;


/**
* Admin who command.
*
* @author Mrakobes
*/

@SuppressWarnings("unused")
public class Who extends AdminCommand
{

   private World   world;

   /**
    * Constructor.
    */

   public Who()
   {
      super("who");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void executeCommand(Player admin, String[] params)
   {
      if(admin.getAccessLevel() < AdminConfig.COMMAND_WHO)
      {
         PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
         return;
      }
      
      String sPlayerNames = "";
      int szPlayersCount = 0;
     for(Player player : World.getInstance().getPlayers())
      {
         sPlayerNames += player.getName()+" ; "; // or other name delimiter:)
         szPlayersCount++;
      }
      PacketSendUtility.sendMessage(admin, "Now  "+String.valueOf( szPlayersCount )+" are online !");
      PacketSendUtility.sendMessage(admin, "Names of players : "+sPlayerNames);   
   }
}
