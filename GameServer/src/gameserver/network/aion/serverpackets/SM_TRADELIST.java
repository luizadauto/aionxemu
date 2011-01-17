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

import gameserver.model.gameobjects.Npc;
import gameserver.model.templates.TradeListTemplate;
import gameserver.model.templates.TradeListTemplate.TradeTab;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * @author alexa026
 *         modified by ATracer, Sarynth
 */
public class SM_TRADELIST extends AionServerPacket {

    private int targetObjectId;
    private int npcTemplateId;
    private TradeListTemplate tlist;
    private int buyPriceModifier;

    public SM_TRADELIST(Npc npc, TradeListTemplate tlist, int buyPriceModifier) {
        this.targetObjectId = npc.getObjectId();
        this.npcTemplateId = npc.getNpcId();
        this.tlist = tlist;
        this.buyPriceModifier = buyPriceModifier;
    }

    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf) {
        if ((tlist != null) && (tlist.getNpcId() != 0) && (tlist.getCount() != 0)) {
            writeD(buf, targetObjectId);
            writeC(buf, tlist.isAbyss() ? 2 : 1); //abyss or normal
            writeD(buf, buyPriceModifier); // Vendor Buy Price Modifier
            writeH(buf, tlist.getCount());
            for (TradeTab tradeTabl : tlist.getTradeTablist()) {
                writeD(buf, tradeTabl.getId());
            }
        } else if (tlist == null) {
            Logger.getLogger(SM_TRADELIST.class).warn("Empty TradeListTemplate for NpcId: " + npcTemplateId);
            writeD(buf, targetObjectId);
            writeC(buf, 1);
            writeD(buf, buyPriceModifier);
            writeH(buf, 0);
        }
    }
}
