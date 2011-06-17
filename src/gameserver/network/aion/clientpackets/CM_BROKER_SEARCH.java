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

import java.util.ArrayList;
import java.util.List;

import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.AionClientPacket;
import gameserver.services.BrokerService;


/**
 * @author ginho1
 *
 */
public class CM_BROKER_SEARCH extends AionClientPacket
{
    @SuppressWarnings("unused")
    private int brokerId;
    private int sortType;
    private int page;
    private int mask;
    private int items_length;
    private List<Integer> items_id;

    public CM_BROKER_SEARCH(int opcode)
    {
        super(opcode);
    }

    @Override
    protected void readImpl()
    {
        this.brokerId = readD();
        this.sortType = readC(); // 1 - name; 2 - level; 4 - totalPrice; 6 - price for piece
        this.page = readH();
        this.mask = readH();
        this.items_length = readH();
        this.items_id = new ArrayList<Integer>();
        for (int i = 0; i<this.items_length; i++)
            this.items_id.add(readD());
    }

    @Override
    protected void runImpl()
    {
        Player player = getConnection().getActivePlayer();

        BrokerService.getInstance().showRequestedItems(player, mask, sortType, page, items_id, true);
    }
}
