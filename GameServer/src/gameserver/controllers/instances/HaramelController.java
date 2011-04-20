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
package gameserver.controllers.instances;


import gameserver.ai.events.Event;
import gameserver.controllers.NpcController;
import gameserver.model.gameobjects.player.Player;
import gameserver.services.TeleportService;


public class HaramelController extends NpcController {

    @Override
    public void onDialogRequest(final Player player) {
        getOwner().getAi().handleEvent(Event.TALK);

        switch (getOwner().getNpcId()) {
            //Lift
            case 730321:
                TeleportService.teleportTo(player, 300200000, player.getInstanceId(), 220, 213, 126, 0);
                break;
        }
    }
}