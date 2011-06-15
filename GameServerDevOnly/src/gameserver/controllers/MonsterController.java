/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.controllers;

import gameserver.model.Race;
import gameserver.model.alliance.PlayerAlliance;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Monster;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.group.PlayerGroup;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestCookie;
import gameserver.services.AllianceService;
import gameserver.services.DropService;
import gameserver.services.GroupService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.stats.StatFunctions;
import gameserver.world.World;
import gameserver.world.WorldType;


/**
 * @author ATracer, Sarynth
 */
public class MonsterController extends NpcController
{
    @Override
    public void doReward()
    {
        AionObject winner = getOwner().getAggroList().getMostDamage(); 
        
        if(winner == null)
            return;
        
        // TODO: Split the EXP based on overall damage.
        
        if (winner instanceof PlayerAlliance)
        {
            AllianceService.getInstance().doReward((PlayerAlliance)winner, getOwner());
        }
        else if (winner instanceof PlayerGroup)
        {
            GroupService.getInstance().doReward((PlayerGroup)winner, getOwner());
        }
        else if (((Player)winner).isInGroup())
        {
            GroupService.getInstance().doReward(((Player)winner).getPlayerGroup(), getOwner());
        }
        else
        {
            super.doReward();
            
            Player player = (Player)winner;
            
            // Exp reward
            long expReward = StatFunctions.calculateSoloExperienceReward(player, getOwner());
            // is there a boost ?
            if(player.getXpBoost() > 0)
            {
                long bonusValue = Math.round((expReward * player.getXpBoost()) / 100);
                long bonusXP = expReward + bonusValue;
                player.getCommonData().addExp(bonusXP, getOwner());
                PacketSendUtility.sendMessage(player, "Experience Boost item bonus : " + bonusValue + " XP"); // TODO : Find retail msg
            }
            else
            {
                player.getCommonData().addExp(expReward, getOwner());
            }

            // DP reward
            int currentDp = player.getCommonData().getDp();
            int dpReward = StatFunctions.calculateSoloDPReward(player, getOwner());
            player.getCommonData().setDp(dpReward + currentDp);
            
            // AP reward
            WorldType worldType = World.getInstance().getWorldMap(player.getWorldId()).getWorldType();
            if(worldType == WorldType.ABYSS || 
            (worldType == WorldType.BALAUREA && (getOwner().getObjectTemplate().getRace() == Race.DRAKAN || getOwner().getObjectTemplate().getRace() == Race.LIZARDMAN)))
            {
                int apReward = StatFunctions.calculateSoloAPReward(player, getOwner());
                player.getCommonData().addAp(apReward);
            }
            
            QuestEngine.getInstance().onKill(new QuestCookie(getOwner(), player, 0 , 0));
            
            // Give Drop
            DropService.getInstance().registerDrop(getOwner() , player, player.getLevel());            
        }
    }
    
    @Override
    public void onRespawn()
    {
        super.onRespawn();
        DropService.getInstance().unregisterDrop(getOwner());        
    }

    @Override
    public Monster getOwner()
    {
        return (Monster) super.getOwner();
    }
}
