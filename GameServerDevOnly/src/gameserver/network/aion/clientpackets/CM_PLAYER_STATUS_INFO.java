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

import gameserver.model.alliance.PlayerAlliance;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.AllianceService;
import gameserver.services.GroupService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * Called when entering the world and during group management
 * 
 * @author Lyahim, ATracer, Simple, ginhol
 */

public class CM_PLAYER_STATUS_INFO extends AionClientPacket
{
    /**
     * Definitions
     */
    private int                status;
    private int                playerObjId;
    private int                allianceGroupId;
    private int                secondObjectId;

    public CM_PLAYER_STATUS_INFO(int opcode)
    {
        super(opcode);
    }

    @Override
    protected void readImpl()
    {
        status = readC();
        playerObjId = readD();
        allianceGroupId = readD();
        secondObjectId = readD();
    }

    @Override
    protected void runImpl()
    {
        Player myActivePlayer = getConnection().getActivePlayer();

        switch(status)
        {
            // Note: This is currently used for PlayerGroup...
            // but it also is sent when leaving the alliance.
            case 9:
                getConnection().getActivePlayer().setLookingForGroup(playerObjId == 2);
            break;
            
            //Alliance Statuses
            case 12:
            case 14:
            case 15:
            case 19:
            case 23:
            case 24:
                AllianceService.getInstance().playerStatusInfo(myActivePlayer, status, playerObjId);
            break;
            case 25:
                PlayerAlliance alliance = myActivePlayer.getPlayerAlliance();

                if (alliance == null)
                {
                    // Huh... packet spoofing?
                    PacketSendUtility.sendPacket(myActivePlayer, new SM_SYSTEM_MESSAGE(1301015));
                    return;
                }

                if (!alliance.hasAuthority(myActivePlayer.getObjectId()))
                {
                    // You are not the leader!
                    PacketSendUtility.sendPacket(myActivePlayer, new SM_SYSTEM_MESSAGE(1400749));
                    return;
                }

                AllianceService.getInstance().handleGroupChange(alliance, playerObjId, allianceGroupId, secondObjectId);
            break;
            //PlayerGroup Statuses
            case 2:
            case 3:
            case 6:
                Player player = null;

                if(playerObjId == 0)
                    player = getConnection().getActivePlayer();
                else
                    player = World.getInstance().findPlayer(playerObjId);

                if(player == null || player.getPlayerGroup() == null)
                    return;

                GroupService.getInstance().playerStatusInfo(status, player);
                break;
        }
    }
}
