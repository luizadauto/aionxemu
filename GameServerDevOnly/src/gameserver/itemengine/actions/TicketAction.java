package gameserver.itemengine.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import gameserver.services.CubeExpandService;
import gameserver.services.WarehouseService;
import gameserver.utils.PacketSendUtility;


/**
 * @author ginho1
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TicketAction")
public class TicketAction extends AbstractItemAction
{
    @XmlAttribute
    protected String function;
    @XmlAttribute
    protected int param;

    /**
     * Gets the value of the function property.
     */
    public String getFunction() {
        return function;
    }

    /**
     * Gets the value of the param property.
     */
    public int getParam() {
        return param;
    }

    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem)
    {
        if(function.equals("addCube"))
        {
            return (player.getCubeSize() < 9);
        }

        if(function.equals("addWharehouse"))
        {
            return (player.getWarehouseSize() < 9);
        }

        return false;
    }

    @Override
    public void act(Player player, Item parentItem, Item targetItem)
    {
        Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());

        if(item != null)
        {
            if(player.getInventory().removeFromBag(item, true))
            {
                if(function.equals("addCube"))
                {
                    CubeExpandService.expand(player);
                }

                if(function.equals("addWharehouse"))
                {
                    WarehouseService.expand(player);
                }

                PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(parentItem.getObjectId()));
            }
        }
    }

}
