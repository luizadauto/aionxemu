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

package gameserver.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import gameserver.dao.PlayerEmotionListDAO;
import gameserver.model.gameobjects.player.Emotion;
import gameserver.model.gameobjects.player.EmotionList;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import gameserver.utils.PacketSendUtility;


/**
 * @author ginho1
 *
 */
public class EmotionService
{
    public static boolean isExpired(long expires_time, long date)
    {
        if(expires_time > 0)
        {
            long timeLeft = (date + (expires_time * 1000L)) - System.currentTimeMillis();
            if(timeLeft < 0)
            {
                return true;
            }
        }
        return false;
    }

    public static void removeEmotion(int playerId, int emotionId)
    {
        DAOManager.getDAO(PlayerEmotionListDAO.class).removeEmotion(playerId, emotionId);
    }

    public static void removeExpiredEmotions(Player player)
    {
        EmotionList emotionList = player.getEmotionList();
        List<Integer> delEmotions = new ArrayList<Integer>();
        boolean removed = false;

        for(Emotion emotion : emotionList.getEmotions())
        {
            if(EmotionService.isExpired(emotion.getEmotionExpiresTime(), emotion.getEmotionDate()))
            {
                delEmotions.add(emotion.getEmotionId());
            }
        }

        Iterator<Integer> iterator = delEmotions.iterator();
        while(iterator.hasNext())
        {
            int emotionId = iterator.next();
            removeEmotion(player.getObjectId(), emotionId);
            emotionList.remove(emotionId);
            removed = true;
        }

        if(removed)
        {
            PacketSendUtility.sendPacket(player, new SM_EMOTION_LIST(player));
            PacketSendUtility.sendMessage(player, "The usage time of emotion has expired.");
        }
    }
}