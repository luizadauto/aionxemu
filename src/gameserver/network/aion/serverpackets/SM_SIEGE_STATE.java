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

import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author dns
 * 
 */
public class SM_SIEGE_STATE extends AionServerPacket
{
    private int locationId;
    private int value;

    public SM_SIEGE_STATE(int locationId, int value)
    {
        this.locationId = locationId;
        this.value = value;
    }

    @Override
    public void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeD(buf, locationId);
        writeD(buf, value); //This value goes to 1 when fortress is vulnerable
        writeC(buf, 0);
    }
}
