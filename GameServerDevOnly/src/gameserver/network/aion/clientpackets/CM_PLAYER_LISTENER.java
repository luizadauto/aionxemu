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

package gameserver.network.aion.clientpackets;

import java.util.Calendar;

import gameserver.configs.main.CustomConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.services.instance.DredgionInstanceService;
import gameserver.services.EmotionService;
import gameserver.services.HTMLService;
import gameserver.services.TitleService;


/**
 *
 * @author ginho1
 *
 */
public class CM_PLAYER_LISTENER extends AionClientPacket
{
    /*
     * this CM is send every five minutes by client.
     */
    public CM_PLAYER_LISTENER(int opcode)
    {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl()
    {
    }

    /**c
     * {@inheritDoc}
     */
    @Override
    protected void runImpl()
    {
        Player player = getConnection().getActivePlayer();

        if(player == null)
            return;

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        TitleService.checkPlayerTitles(player);

        if(CustomConfig.RETAIL_EMOTIONS)
            EmotionService.removeExpiredEmotions(player);

        if(CustomConfig.ENABLE_SURVEYS)
            HTMLService.onPlayerLogin(player);

        //send dredgion instance entry
        if((hour >= 0 && hour <= 1) || (hour >= 12 && hour <= 13) || (hour >= 20 && hour <= 21))
        {
            if(!player.getReceiveEntry())
            {
                DredgionInstanceService.getInstance().sendDredgionEntry(player);
                player.setReceiveEntry(true);
            }
        }
    }
}