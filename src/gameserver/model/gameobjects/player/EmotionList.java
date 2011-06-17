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

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * @author ginho1
 */
public class EmotionList
{
    private LinkedHashMap<Integer, Emotion> emotions;
    private Player owner;

    public EmotionList()
    {
        this.emotions = new LinkedHashMap<Integer, Emotion>();
        this.owner = null;
    }

    public void setOwner(Player owner)
    {
        this.owner = owner;
    }

    public Player getOwner()
    {
        return owner;
    }

    public boolean add(int id, long date, long expires_time)
    {
        if(!emotions.containsKey(id))
        {
            emotions.put(id, new Emotion(id, date, expires_time));
            return true;
        }
        return false;
    }

    public void remove(int id)
    {
        if(emotions.containsKey(id))
        {
            emotions.remove(id);
        }
    }

    public Emotion get(int id)
    {
        if(emotions.containsKey(id))
            return emotions.get(id);

        return null;
    }

    public boolean canAdd(int id)
    {
        if(emotions.containsKey(id))
            return false;

        return true;
    }

    public int size()
    {
        return emotions.size();
    }

    public Collection<Emotion> getEmotions()
    {
        return emotions.values();
    }
}
