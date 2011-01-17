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

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.services.MailService;

/**
 * @author kosyachok
 */
public class CM_SEND_MAIL extends AionClientPacket {

    private String recipientName;
    private String title;
    private String message;
    private int itemObjId;
    private int itemCount;
    private int kinahCount;
    private int express;

    public CM_SEND_MAIL(int opcode) {
        super(opcode);
    }

    @Override
    protected void readImpl() {
        recipientName = readS();
        title = readS();
        message = readS();
        itemObjId = readD();
        itemCount = readD();
        readD();
        kinahCount = readD();
        readD();
        express = readC();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        if(kinahCount < 0 || itemCount < 0)
        	return;
        if (player.isTrading())
            return;

        MailService.getInstance().sendMail(player, recipientName, title, message, itemObjId, itemCount, kinahCount, express == 1, false);
    }
}
