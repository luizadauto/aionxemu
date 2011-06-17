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
import java.util.Collection;

import gameserver.model.siege.SiegeLocation;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;


/**
 * 
 * @author Sylar, Vial, Ritsu
 *
 */
public class SM_SIEGE_AETHERIC_FIELDS extends AionServerPacket
{
    private Collection<SiegeLocation> locations;

    public SM_SIEGE_AETHERIC_FIELDS(Collection<SiegeLocation> locations)
    {
        this.locations = locations;
    }

    @Override
    public void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeH(buf, locations.size()); // Artifact Count
        
        for(SiegeLocation loc : locations)
        {
            writeD(buf, loc.getLocationId());
            if(loc.isVulnerable() && loc.isShieldActive()) // display shield if vulnerable and generator alive	
                writeC(buf, 1);
            else
                writeC(buf,0); // peace mode
        }
    }
}
