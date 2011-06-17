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


import java.util.HashMap;
import java.util.Map;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_INSTANCE_COOLDOWN;
import gameserver.services.instance.InstanceService;
import gameserver.utils.PacketSendUtility;


/**
 * @author Lyahim, Arkshadow
 */
public class CM_INSTANCE_CD_REQUEST extends AionClientPacket
{
    public CM_INSTANCE_CD_REQUEST(int opcode)
    {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl()
    {
        readD();
        readC(); // channel
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl()
    {
        Player activePlayer = getConnection().getActivePlayer();

        PacketSendUtility.sendPacket(activePlayer, new SM_INSTANCE_COOLDOWN(true)); //clear everything
        Map<Integer, Integer> infos = new HashMap<Integer, Integer>();
        boolean first = true;

        if(activePlayer.getPlayerGroup() != null)
        {
            for(Player member: activePlayer.getPlayerGroup().getMembers())
            {
                if(!activePlayer.equals(member))
                {
                    infos = InstanceService.getTimeInfo(member);
                    for(int i : infos.keySet())
                    {
                        int time = infos.get(i);
                        if(time!=0)
                        {
                            if(first)
                            {
                                first = false;
                                PacketSendUtility.sendPacket(activePlayer, new SM_INSTANCE_COOLDOWN(member, i, time, 1, false));
                            }
                            else
                                PacketSendUtility.sendPacket(activePlayer, new SM_INSTANCE_COOLDOWN(member, i, time, 2, false));
                        }
                    }
                }
            }
        }

        infos = InstanceService.getTimeInfo(activePlayer);

        for(int i : infos.keySet())
        {
            int time = infos.get(i);
            if(time!=0)
                PacketSendUtility.sendPacket(activePlayer, new SM_INSTANCE_COOLDOWN(activePlayer, i, time, 2, true));
        }
    }
}
