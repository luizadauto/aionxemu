/**
 * This file is part of alpha team <alpha-team.com>.
 *
 * alpha team is pryvate software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alpha team is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alpha team.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.controllers;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.siege.AethericField;
import gameserver.model.siege.SiegeLocation;
import gameserver.network.aion.serverpackets.SM_SIEGE_LOCATION_INFO;
import gameserver.services.SiegeService;
import gameserver.utils.PacketSendUtility;


/**
 * @author Sylar, Vial, Ritsu
 *
 */
public class AethericFieldController extends NpcController
{    

    public void onDie(Creature lastAttacker)
    {
        super.onDie(lastAttacker);
        int id = getOwner().getFortressId();
        SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(id);
        //disable field
        loc.setShieldActive(false);
        //TODO : Find sys message sended on generator death
        getOwner().getKnownList().doOnAllPlayers(new Executor<Player>(){        
            @Override
            public boolean run(Player object)
            {
                //Needed to update the display of shield effect
                PacketSendUtility.sendPacket(object, new SM_SIEGE_LOCATION_INFO());
                return true;
            }
        }, true);
    }

    @Override
    public AethericField getOwner()
    {
        return (AethericField) super.getOwner();
    }    
}
