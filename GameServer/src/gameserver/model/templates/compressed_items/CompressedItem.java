/* 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 
 * of the License, or (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,  
 * MA  02110-1301, USA. 
 * 
 * http://www.gnu.org/copyleft/gpl.html 
 */ 
 
package gameserver.model.templates.compressed_items; 
 
import com.aionemu.commons.utils.Rnd; 
import gameserver.itemengine.actions.AbstractItemAction; 
import gameserver.model.TaskId; 
import gameserver.model.gameobjects.Item; 
import gameserver.model.gameobjects.player.Player; 
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION; 
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE; 
import gameserver.services.ItemService;
import gameserver.utils.PacketSendUtility; 
import gameserver.utils.ThreadPoolManager; 

import java.util.List; 
import javax.xml.bind.annotation.XmlAccessType; 
import javax.xml.bind.annotation.XmlAccessorType; 
import javax.xml.bind.annotation.XmlAttribute; 
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlType; 

/** 
 * @author Mr. Poke, ZeroSignal
 * 
 */ 
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(name = "CompressedItem", propOrder = 
{ "production" }) 
public class CompressedItem extends AbstractItemAction {

    @XmlElement(required = true) 
    protected List<Production>      production; 
    @XmlAttribute(required = true) 
    protected int                           id; 

    public List<Production> getProduction() { 
        return this.production; 
    } 

    /** 
     * Gets the value of the id property. 
     *  
     */ 
    public int getId() { 
        return id; 
    } 

    @Override 
    public boolean canAct(Player player, Item parentItem, Item targetItem) { 
        if (production == null) 
            return false; 
        if (parentItem.getItemTemplate().getTemplateId() != id) { 
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_ERROR); 
            return false; 
        } 
        if (player.getInventory().isFull()) { 
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL); 
            return false; 
        } 
        return true; 
    } 

    @Override 
    public void act(final Player player, final Item parentItem, Item targetItem) { 
        PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate() 
                        .getTemplateId(), 3000, 0, 0)); 
        player.getController().cancelTask(TaskId.ITEM_USE); 
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() 
        { 
            @Override 
            public void run() {
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate() 
                                .getTemplateId(), 0, 1, 0));
                if (!player.getInventory().removeFromBagByObjectId(parentItem.getObjectId(), 1))
                    return; 
                int rand = Rnd.get(0, 100); 
                int chance = -1; 
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_UNCOMPRESS_COMPRESSED_ITEM_SUCCEEDED(parentItem.getNameID())); 
                for (Production product : production) { 
                    if (product.getChance() >= rand || chance == product.getChance()) { 
                        if (rand != -1) { 
                            rand = -1; 
                            chance = product.getChance(); 
                        }
                        ItemService.addItem(player, product.getItemId(), product.getCount());
                    } 
                }
            } 
        }, 3000)); 
    } 
}
