package gameserver.itemengine.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import gameserver.network.aion.serverpackets.SM_EMOTION_LIST;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;


/**
 * @author ginho1
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmotionAction")
public class EmotionAction extends AbstractItemAction
{
    @XmlAttribute
    protected int emotionid;
    @XmlAttribute
    protected int expire;

    /**
     * Gets the value of the id property.
     */
    public int getEmotionId() {
        return emotionid;
    }

    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem)
    {
        boolean valid = false;

        if(player.getEmotionList().canAdd(emotionid))
            valid = true;
        else
            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400088));

        return valid;
    }

    @Override
    public void act(Player player, Item parentItem, Item targetItem)
    {
        Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());

        if(item != null)
        {
            if(player.getInventory().removeFromBag(item, true))
            {
            
                if(player.getEmotionList().add(emotionid, System.currentTimeMillis(), (expire  * 60L)))
                {
                    if(expire > 0)
                        PacketSendUtility.sendMessage(player, "Temporarily Acquired a New Emote.");
                    else
                        PacketSendUtility.sendMessage(player, "Permanently Acquired a New Emote.");

                    PacketSendUtility.sendPacket(player, new SM_EMOTION_LIST(player));
                }

                PacketSendUtility.sendPacket(player, new SM_DELETE_ITEM(parentItem.getObjectId()));
            }
        }
    }

}
