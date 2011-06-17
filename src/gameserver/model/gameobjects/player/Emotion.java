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

/**
 * @author ginho1
 *
 */
public class Emotion
{
    private int emotionId;
    private long emotion_date = 0;
    private long emotion_expires_time = 0;

    public Emotion(int emotionId, long emotion_date, long emotion_expires_time)
    {
        this.emotionId = emotionId;
        this.emotion_date = emotion_date;
        this.emotion_expires_time = emotion_expires_time;
    }

    public int getEmotionId()
    {
        return emotionId;
    }

    public long getEmotionDate()
    {
        return emotion_date;
    }

    public long getEmotionExpiresTime()
    {
        return emotion_expires_time;
    }

    public void setEmotionId(int emotionId)
    {
        this.emotionId = emotionId;
    }

    public long getEmotionTimeLeft()
    {
        if(emotion_expires_time == 0)
            return 0;

        long timeLeft = (emotion_date + ((emotion_expires_time )  * 1000L)) - System.currentTimeMillis();
        if(timeLeft < 0)
            timeLeft = 0;

        return timeLeft /1000L ;
    }

    public void setEmotionDate(long emotion_date)
    {
        this.emotion_date = emotion_date;
    }

    public void setEmotionExpiresTime(long emotion_expires_time)
    {
        this.emotion_expires_time = emotion_expires_time;
    }
}