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
package gameserver.model.instances;

import gameserver.model.group.PlayerGroup;
import gameserver.world.WorldMap;
import gameserver.world.WorldMapInstance;

/**
 *
 * @author ArkShadow
 * @redone kosyachok
 */

public class Dredgion extends WorldMapInstance
{
    private PlayerGroup secondGroup = null;    
    
    /**
     * @param parent
     * @param instanceId
     */
    public Dredgion(WorldMap parent, int instanceId)
    {
        super(parent, instanceId);
    }
    
    public void registerSecondGroup(PlayerGroup group)
    {
        group.setGroupInstancePoints(0);
        this.secondGroup = group;
    }
    
    public void removeSecondGroup()
    {
        this.secondGroup = null;
    }
    
    public PlayerGroup getSecondGroup()
    {
        return secondGroup;
    }
}
