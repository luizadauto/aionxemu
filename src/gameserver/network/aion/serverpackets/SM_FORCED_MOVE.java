/**
 * This file is part of alpha team <alpha-team.com>.
 *
 * alpha team is private software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alpha team is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alpha team.  If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.network.aion.serverpackets;

import gameserver.model.gameobjects.Creature;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

import java.nio.ByteBuffer;


/**
 * @author Sweetkr
 */
public class SM_FORCED_MOVE extends AionServerPacket
{
    private Creature creature;
    private Creature target;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    
    public SM_FORCED_MOVE(Creature creature, float x, float y, float z)
    {
        this.creature = creature;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SM_FORCED_MOVE(Creature creature, Creature target)
    {
        this.creature = creature;
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeD(buf, creature.getObjectId());
        if(target != null)
            writeD(buf, target.getObjectId());
        else
            writeD(buf, creature.getObjectId());
        writeC(buf, 16); // unk
        if(x == 0 && y == 0 && z == 0)
        {
            writeF(buf, target.getX());
            writeF(buf, target.getY());
            writeF(buf, target.getZ() + 0.25f);
        }
        else
        {
            writeF(buf, x);
            writeF(buf, y);
            writeF(buf, z);
        }
    }
}
