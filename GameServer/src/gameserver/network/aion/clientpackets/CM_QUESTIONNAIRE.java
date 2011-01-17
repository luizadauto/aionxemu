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
import gameserver.services.HTMLService;

/**
 * @author lhw, Kaipo and ginho1
 */
public class CM_QUESTIONNAIRE extends AionClientPacket {
    private int objectId;

    public CM_QUESTIONNAIRE(int opcode) {
        super(opcode);
    }

    /* (non-Javadoc)
      * @see com.aionemu.commons.network.packet.BaseClientPacket#readImpl()
      */

    @Override
    protected void readImpl() {
        objectId = readD();
        readH();
        readH();
        readH();
        readH();
    }

    /* (non-Javadoc)
      * @see com.aionemu.commons.network.packet.BaseClientPacket#runImpl()
      */

    @Override
    protected void runImpl() {
        if (objectId > 0) {
            Player player = getConnection().getActivePlayer();
            HTMLService.getMessage(player, objectId);
        }
    }
}
