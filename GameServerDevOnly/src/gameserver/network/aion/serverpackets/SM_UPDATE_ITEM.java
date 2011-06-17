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

import gameserver.model.gameobjects.Item;
import gameserver.model.items.ItemId;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.InventoryPacket;

import java.nio.ByteBuffer;

/**
 * @author ATracer
 */
public class SM_UPDATE_ITEM extends InventoryPacket {
    private Item item;
    private boolean isWeaponSwitch = false;

    public SM_UPDATE_ITEM(Item item) {
        this.item = item;
    }

    public SM_UPDATE_ITEM(Item item, boolean isWeaponSwitch) {
        this.item = item;
        this.isWeaponSwitch = isWeaponSwitch;
    }

    @Override
    protected void writeGeneralInfo(ByteBuffer buf, Item item) {
        writeD(buf, item.getObjectId());
        ItemTemplate itemTemplate = item.getItemTemplate();
        writeH(buf, 0x24);
        writeD(buf, itemTemplate.getNameId());
        writeH(buf, 0);
    }
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {

        writeGeneralInfo(buf, item);

        ItemTemplate itemTemplate = item.getItemTemplate();

        if(itemTemplate.getTemplateId() == ItemId.KINAH.value())
        {
            writeKinah(buf, item);
            writeC(buf, 0x1A); // FF FF equipment
            writeC(buf, 0);
        }
        else if (itemTemplate.isWeapon())
        {
            if(isWeaponSwitch)
                writeWeaponSwitch(buf, item);
            else
            {
                writeWeaponInfo(buf, item);
                writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot());
                writeC(buf, 0);
            }
        }
        else if (itemTemplate.isArmor())
        {
            writeArmorInfo(buf, item);
            writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot());
            writeC(buf, 0);
        }
        else if (itemTemplate.isStigma())
        {
            writeStigmaInfo(buf,item);
        }
        else
        {
            writeGeneralItemInfo(buf, item);
            writeH(buf, item.isEquipped() ? 255 : item.getEquipmentSlot());
            writeC(buf, 0);
        }
    }
    
    @Override
    protected void writeStigmaInfo(ByteBuffer buf, Item item)
    {
        int itemSlotId = item.getEquipmentSlot();
        writeH(buf, 0x05); //length of details
        writeC(buf, 0x06); //unk
        writeD(buf, item.isEquipped() ? itemSlotId : 0);
    }
}