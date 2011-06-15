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

package gameserver.controllers;

import gameserver.ai.events.Event;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.NpcWithCreator;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import gameserver.utils.PacketSendUtility;

/**
 * @author kecimis
 *
 */
public class NpcWithCreatorController extends NpcController
{

    @Override
    public void onDie(Creature lastAttacker)
    {
        super.onCreatureDie(lastAttacker);
        
        PacketSendUtility.broadcastPacket(this.getOwner(),
            new SM_EMOTION(this.getOwner(), EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()));
        
        this.getOwner().getAi().handleEvent(Event.DIED);

        // deselect target at the end
        this.getOwner().setTarget(null);
        PacketSendUtility.broadcastPacket(this.getOwner(), new SM_LOOKATOBJECT(this.getOwner()));
        
        onDelete();
    }

    @Override
    public void onDialogRequest(Player player)
    {
        return;
    }

    @Override
    public NpcWithCreator getOwner()
    {
        return  (NpcWithCreator)super.getOwner();
    }
    @Override
    public void onDelete()
    {
        getOwner().setCreator(null);
        super.onDelete();
    }
}
