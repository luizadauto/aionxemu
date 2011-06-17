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

package gameserver.model.gameobjects.player;

import java.util.LinkedHashMap;

/**
 * @author ginho1
 */
public class PurchaseLimit
{
    private LinkedHashMap<Integer, Integer> items;

    public PurchaseLimit()
    {
        items = new LinkedHashMap<Integer, Integer>();
    }

    public void addItem(int itemId, int itemCount)
    {
        if(items.containsKey(itemId))
        {
            LinkedHashMap<Integer, Integer> newItems = new LinkedHashMap<Integer, Integer>();
            for(int itemIds : items.keySet())
            {
                if(itemIds != itemId)
                    newItems.put(itemIds, items.get(itemIds));
                else
                    newItems.put(itemIds, items.get(itemIds) + itemCount);
            }
            this.items = newItems;

        }else{
            items.put(itemId, itemCount);
        }
    }

    public void removeItem(int itemId)
    {
        if(items.containsKey(itemId))
        {
            this.items.remove(itemId);
        }
    }

    public void reset()
    {
        items = new LinkedHashMap<Integer, Integer>();
    }

    public void setItems(LinkedHashMap<Integer, Integer> items)
    {
        this.items = items;
    }

    public int getItemLimitCount(int itemId)
    {
        if(items.containsKey(itemId))
            return items.get(itemId);

        return 0;
    }

    public LinkedHashMap<Integer, Integer> getItems()
    {
        return items;
    }
}
