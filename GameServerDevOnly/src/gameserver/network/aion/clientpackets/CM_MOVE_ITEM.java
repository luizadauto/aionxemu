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

import java.util.Collections;

import org.apache.log4j.Logger;
import gameserver.model.EmotionType;
import gameserver.model.TaskId;
import gameserver.model.gameobjects.Item;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.StorageType;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE;
import gameserver.network.aion.serverpackets.SM_WAREHOUSE_UPDATE;
import gameserver.services.ItemService;
import gameserver.utils.PacketSendUtility;


/**
 * @author alexa026, kosyachok
 * 
 */
public class CM_MOVE_ITEM extends AionClientPacket
{

    /**
     * Target object id that client wants to TALK WITH or 0 if wants to unselect
     */
    private int                    targetObjectId;
    private int                    source;
    private int                    destination;
    private int                    slot;
    private static final Logger    log    = Logger.getLogger(CM_MOVE_ITEM.class);
    /**
     * Constructs new instance of <tt>CM_CM_REQUEST_DIALOG </tt> packet
     * @param opcode
     */
    public CM_MOVE_ITEM(int opcode)
    {
        super(opcode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl()
    {
        targetObjectId = readD();// empty
        source = readC();        //FROM (0 - player inventory, 1 - regular warehouse, 2 - account warehouse, 3 - legion warehouse)
        destination = readC();   //TO
        slot = readH();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl()
    {
        Player player = getConnection().getActivePlayer();
        
        if (player != null)
        {
            if(player.isTrading() && source != destination)
            {
                log.warn("[AUDIT] Trying to use trade exploit: " + player.getName());

                Item item = player.getStorage(source).getItemByObjId(targetObjectId);

                if(source == StorageType.CUBE.getId())
                    PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE(Collections.singletonList(item)));
                else
                    PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_UPDATE(item, source));

                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player,EmotionType.END_LOOT,0,0));

                return;
            }
            
            //prevent from using items endless  amount of times with packets.
            if (player.getController().hasTask(TaskId.ITEM_USE))
            {
                log.info("[AUDIT] "+player.getName()+" sending fake CM_MOVE_ITEM packet. Trying to dupe item.");
                return;
            }

            ItemService.moveItem(player, targetObjectId, source, destination, slot);
            
            PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player,EmotionType.END_LOOT,0,0));
        }
    }
}
