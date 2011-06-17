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

import gameserver.configs.main.CustomConfig;
import gameserver.model.gameobjects.player.Emotion;
import gameserver.model.gameobjects.player.EmotionList;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.services.EmotionService;

import java.nio.ByteBuffer;

/**
 *
 * @author ginho1
 *
 */
public class SM_EMOTION_LIST extends AionServerPacket
{
    private EmotionList    emotionList;

    public SM_EMOTION_LIST(Player player)
    {
        this.emotionList = player.getEmotionList();
    }

    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeC(buf, 0x00);

        if(CustomConfig.RETAIL_EMOTIONS)
        {
            Player player = emotionList.getOwner();
            EmotionService.removeExpiredEmotions(player);
            
            writeH(buf, emotionList.size());

            for(Emotion emotion : emotionList.getEmotions())
            {
                writeH(buf, emotion.getEmotionId());
                writeD(buf, (int) emotion.getEmotionTimeLeft());
            }
        }else{
            writeH(buf, 57);
            for (int i = 0; i < 57; i++)
            {
                writeH(buf, 64 + i);
                writeD(buf, 0x00);
            }
        }
    }
} 
