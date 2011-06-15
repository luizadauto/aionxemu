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

package gameserver.network.aion.serverpackets;

import java.nio.ByteBuffer;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * @author blakawk, Arkshadow
 */
public class SM_INSTANCE_COOLDOWN extends AionServerPacket
{
    private Player player;
    private boolean init = false;
    private int instanceId = 0;
    private int remainingTime = 0;
    private int type = 2;
    private boolean self = false;

    public SM_INSTANCE_COOLDOWN(Player player)
    {
        this.player = player;
    }

    public SM_INSTANCE_COOLDOWN(boolean init)
    {
        this.init = init;
    }

    public SM_INSTANCE_COOLDOWN(Player player, int id, int time, int type, boolean self)
    {
        this.player = player;
        this.instanceId = id;
        this.remainingTime = time;
        this.type = type;
        this.self = self;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        if(!init)
        {
            writeH(buf, type);
            writeD(buf, 0x0);
            writeH(buf, 0x1);
            writeD(buf, player.getObjectId());
            if(instanceId != 0 && remainingTime != 0)
            {
                writeH(buf, 0x1); //instance info
                writeD(buf, instanceId);
                writeD(buf, 0x0);
                writeD(buf, remainingTime); //remaingTime in seconds
                if(self)
                    writeH(buf, 0x0);
                writeS(buf, player.getName());
            }
            else
            {
                writeH(buf, 0x0); //not instance info
                writeS(buf, player.getName());
            }
        }
        else
        {
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
            writeH(buf, 0x0);
        }
    }
}
