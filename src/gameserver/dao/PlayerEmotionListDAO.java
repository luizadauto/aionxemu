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

package gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import gameserver.model.gameobjects.player.EmotionList;
import gameserver.model.gameobjects.player.Player;

/**
 * @author ginho1
 * 
 */
public abstract class PlayerEmotionListDAO implements DAO
{
    @Override
    public final String getClassName()
    {
         return PlayerEmotionListDAO.class.getName();
    }

    public abstract EmotionList loadEmotionList(int playerId);

    public abstract boolean storeEmotions(Player player);

    public abstract void removeEmotion(int playerId, int emotionId);
}