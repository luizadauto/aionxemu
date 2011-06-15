/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.model.gameobjects.stats.modifiers;

import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.shield.Shield;
import gameserver.utils.MathUtil;
import gameserver.world.knownlist.KnownList;


/**
 * @author blakawk
 *
 */
public class ShieldKnownList extends KnownList
{
    public ShieldKnownList(Shield owner)
    {
        super(owner);
    }
    
    @Override
    protected boolean checkObjectInRange(VisibleObject owner, VisibleObject newObject)
    {
        
        return MathUtil.isIn3dRange(owner, newObject, ((Shield)owner).getTemplate().getRadius());
    }

}
