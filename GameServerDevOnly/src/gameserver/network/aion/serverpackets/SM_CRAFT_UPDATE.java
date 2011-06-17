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

import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

import java.nio.ByteBuffer;

/**
 * @author Mr. Poke, HellBoy
 *
 */
public class SM_CRAFT_UPDATE extends AionServerPacket
{
    private int skillId;
    private int itemId;
    private int action;
    private int success;
    private int failure;
    private int nameId;
    private int delay;

    /**
     * @param skillId
     * @param item
     * @param success
     * @param failure
     * @param action
     */
    public SM_CRAFT_UPDATE(int skillId, ItemTemplate item, int success, int failure, int action)
    {
        this.action = action;
        this.skillId = skillId;
        this.itemId = item.getTemplateId();
        this.success = success;
        this.failure = failure;
        this.nameId = item.getNameId();
        if(skillId == 40009)
            this.delay = 1500;
        else
            this.delay = 700;
    }

    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeH(buf, skillId);
        writeC(buf, action);
        writeD(buf, itemId);

        switch(action)
        {
            case 0: //init
                writeD(buf, success);
                writeD(buf, failure);
                writeD(buf, 0);
                writeD(buf, 1200);    //delay after which bar will start moving (ms)
                writeD(buf, 1330048);    //start crafting system message
                writeH(buf, 0x24);
                writeD(buf, nameId);    //item nameId to display it's name in system message above
                writeH(buf, 0);
                break;
            case 1: //regular update
            case 2: //speed up update
                writeD(buf, success);
                writeD(buf, failure);
                writeD(buf, delay);    //time of moving execution (ms)
                writeD(buf, 1200);    //delay after which bar will start moving (ms)
                writeD(buf, 0);
                writeH(buf, 0);
                break;
            case 3: //crit
                writeD(buf, success);
                writeD(buf, failure);
                writeD(buf, 0);
                writeD(buf, 0);
                writeD(buf, 0);
                writeH(buf, 0);
                break;
            case 4:    //cancel crafting
                writeD(buf, success);
                writeD(buf, failure);
                writeD(buf, 0);
                writeD(buf, 0);
                writeD(buf, 1330051);    //canceled crafting system message
                writeH(buf, 0);
                break;
            case 5: //success finish
                writeD(buf, success);
                writeD(buf, failure);
                writeD(buf, 0);
                writeD(buf, 0);    
                writeD(buf, 1300788);    //success crafting system message
                writeH(buf, 0x24);
                writeD(buf, nameId);    //item nameId to display it's name in system message above
                writeH(buf, 0);
                break;
            case 6: //fail finish
                writeD(buf, success);
                writeD(buf, failure);
                writeD(buf, 0);
                writeD(buf, 0);    
                writeD(buf, 1330050);    //fail crafting system message
                writeH(buf, 0x24);
                writeD(buf, nameId);    //item nameId to display it's name in system message above
                writeH(buf, 0);
                break;
        }
    }
}
