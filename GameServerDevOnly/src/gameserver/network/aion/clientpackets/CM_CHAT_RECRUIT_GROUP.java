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

import gameserver.model.gameobjects.player.DeniedStatus;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.GroupService;
import gameserver.utils.Util;
import gameserver.world.World;

/**
 * 
 * @author ginho1
 * 
 */
public class CM_CHAT_RECRUIT_GROUP extends AionClientPacket
{
    private String name;

    public CM_CHAT_RECRUIT_GROUP(int opcode)
    {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl()
    {
        name = readS();
    }

    /**
     * {@inheritDoc}n
     */
    @Override
    protected void runImpl()
    {
        final String captainName = Util.convertName(name);

        final Player player = getConnection().getActivePlayer();
        final Player captain = World.getInstance().findPlayer(captainName);

        if(captain != null)
        {
            if(captain.getPlayerSettings().isInDeniedStatus(DeniedStatus.GROUP))
            {
                sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_REJECTED_INVITE_PARTY(captain.getName()));
                return;
            }

            GroupService.getInstance().requestToGroup(player, captain);
        }
        else
            player.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.PLAYER_IS_OFFLINE(name));
    }
}
