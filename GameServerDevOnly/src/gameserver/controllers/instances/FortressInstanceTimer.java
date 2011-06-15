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

package gameserver.controllers.instances;


import gameserver.controllers.NpcController;
import gameserver.model.NpcType;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.network.aion.serverpackets.SM_QUEST_ACCEPTED;
import gameserver.services.instance.InstanceService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.WorldMapInstance;


/**
 * @author HellBoy, Dns, Ritsu
 * 
 */

public class FortressInstanceTimer extends NpcController
{
    public static void schedule(final Player player, int timeInSeconds)
    {
        if(!player.getQuestTimerOn() && player.isInInstance())
        {
            // usual delay is 15 minutes (inf' strat) 10 minutes (strat sup')
            final WorldMapInstance instance = InstanceService.getRegisteredInstance(player.getWorldId(), player.getPlayerGroup().getGroupId());
            instance.setTimerEnd(timeInSeconds);
            
            ThreadPoolManager.getInstance().schedule(new Runnable()
            {
                @Override
                public void run()
                {
                    instance.doOnAllObjects(new Executor<AionObject>(){
                        @Override
                        public boolean run(AionObject obj)
                        {
                            if(obj instanceof Player)
                                ((Player)obj).setQuestTimerOn(false);
                            else if(obj instanceof Npc && ((Npc)obj).getObjectTemplate().getNpcType() == NpcType.CHEST)
                                ((Npc)obj).getController().delete();
                            return true;
                        }
                    }, true);
                }
            }, timeInSeconds * 1000);
            
            for(Player member : player.getPlayerGroup().getMembers())
            {
                if(!member.getQuestTimerOn() && member.getWorldId() == instance.getMapId() && member.getInstanceId() == instance.getInstanceId())
                {
                    member.setQuestTimerOn(true);
                    //member.getController().addTask(TaskId.QUEST_TIMER, task);
                    PacketSendUtility.sendPacket(member, new SM_QUEST_ACCEPTED(4, 0, timeInSeconds));
                }
            }
        }
    }
}
