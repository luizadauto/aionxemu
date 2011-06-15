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

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.services.TeleportService;

/**
 * @author dns
 * Used on dark poeta, when you get rank F, to be teleported outside instance.
 *
 */
public class CM_EXIT_LOCATION extends AionClientPacket
{
    public CM_EXIT_LOCATION(int opcode)
    {
        super(opcode);
    }

    @Override
    protected void readImpl()
    {

    }

    @Override
    protected void runImpl()
    {	
        final Player activePlayer = getConnection().getActivePlayer();
        if(activePlayer.getInDarkPoeta() || activePlayer.getInDredgion()){
            switch(activePlayer.getCommonData().getRace().getRaceId())
            {
            case 0:
                TeleportService.teleportTo(activePlayer, 110010000, 1, 1444.9f, 1577.2f, 572.9f, 0);
                break;
            case 1:
                TeleportService.teleportTo(activePlayer, 120010000, 1, 1657.5f, 1398.7f, 194.7f, 0);
                break;
            }
        }
    }
}
