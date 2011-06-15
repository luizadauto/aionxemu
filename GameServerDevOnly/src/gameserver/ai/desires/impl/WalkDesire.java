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

package gameserver.ai.desires.impl;

import com.aionemu.commons.utils.Rnd;
import gameserver.ai.AI;
import gameserver.ai.desires.AbstractDesire;
import gameserver.ai.desires.MoveDesire;
import gameserver.configs.main.NpcMovementConfig;
import gameserver.controllers.movement.MovementType;
import gameserver.dataholders.DataManager;
import gameserver.model.EmotionType;
import gameserver.model.ShoutEventType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.model.templates.walker.RouteData;
import gameserver.model.templates.walker.RouteStep;
import gameserver.model.templates.walker.WalkerTemplate;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.services.NpcShoutsService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;


/**
 * @author KKnD
 * 
 */
public class WalkDesire extends AbstractDesire implements MoveDesire
{
    private Npc            owner;
    private RouteData    route;
    private boolean        isWalkingToNextPoint    = false;
    private int            targetPosition;
    private long        nextMoveTime;
    private boolean        isRandomWalk            = false;

    private RouteStep    randomPoint                = null;
    private float        walkArea                = 10;
    private float        halfWalkArea            = 5;
    private float        minRandomDistance        = 2;
    private MovementType NpcMoveTypes[] = {    MovementType.NPC_MOVEMENT_TYPE_I,
                                            MovementType.NPC_MOVEMENT_TYPE_II,
                                            MovementType.NPC_MOVEMENT_TYPE_III,
                                            MovementType.NPC_WALKROUTE_MOVEMENT_TYPE_I};
    // MovementType.NPC_WALKROUTE_MOVEMENT_TYPE_II Unused ATM

    public WalkDesire(Npc npc, int power)
    {
        super(power);
        owner = npc;

        WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(owner.getSpawn().getWalkerId());
        isRandomWalk = owner.getSpawn().hasRandomWalk();
        if(template != null)
        {
            route = template.getRouteData();
            owner.getGameStats().setStat(StatEnum.SPEED, (int) (owner.getObjectTemplate().getStatsTemplate().getWalkSpeed() * 1000));
            owner.getMoveController().setSpeed(owner.getObjectTemplate().getStatsTemplate().getWalkSpeed());
            owner.getMoveController().setWalking(true);
            owner.getMoveController().setMovementType(NpcMoveTypes[3]);
            PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2));    
        }
        else if(isRandomWalk && NpcMovementConfig.ACTIVE_NPC_MOVEMENT)
        {
            walkArea = Math.max(5, owner.getSpawn().getRandomWalkNr()); // The walk area is at least 5 meter.
            halfWalkArea = walkArea / 2f;
            minRandomDistance = Math.min(walkArea / 5f, 2); // The stop distance is between 1 and 2 meter.

            route = null;
            randomPoint = new RouteStep(owner.getX(), owner.getY(), owner.getZ());

            owner.getGameStats().setStat(StatEnum.SPEED, (int) (owner.getObjectTemplate().getStatsTemplate().getWalkSpeed() * 1000));
            owner.getMoveController().setSpeed(owner.getObjectTemplate().getStatsTemplate().getWalkSpeed());
            owner.getMoveController().setWalking(true);
            owner.getMoveController().setDistance(minRandomDistance);
            owner.getMoveController().setMovementType(getRandomMovementType(3));
            PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.WALK));
        }
    }

    @Override
    public boolean handleDesire(AI<?> ai)
    {
        if(owner == null)
            return false;

        if(route == null && !isRandomWalk)
            return false;

        if(isWalkingToNextPoint())
            checkArrivedToPoint();

        walkToLocation();
        return true;
    }

    /**
     * Check owner is in a route point
     */
    private void checkArrivedToPoint()
    {
        RouteStep step = randomPoint;
        if (route != null)
        {
            if (route.getRouteSteps().size() > targetPosition)
                step = route.getRouteSteps().get(targetPosition);
            else
                targetPosition = 0;
        }

        float x = step.getX();
        float y = step.getY();
        float z = step.getZ();

        double dist = MathUtil.getDistance(owner, x, y, z);
        float minDist = (route == null ? minRandomDistance : 2);
        if(dist <= minDist)
        {
            if (owner.getKnownList() != null)
            {
                Object[] players = owner.getKnownList().getPlayers().toArray();
                int size = players.length;
                
                if (size > 0)
                {
                    Player randomPlayer = (Player)players[Rnd.get(size - 1)];
                    NpcShoutsService.getInstance().handleEvent(owner, (Player)randomPlayer, ShoutEventType.WAYPOINT);
                    players = null;
                }
            }
            setWalkingToNextPoint(false);
            getNextTime();
        }
    }

    /**
     * set next route point if not set and time is ready
     */
    private void walkToLocation()
    {
        if(!isWalkingToNextPoint() && nextMoveTime <= System.currentTimeMillis())
        {
            setNextPosition();
            setWalkingToNextPoint(true);

            RouteStep step = randomPoint;
            if (route != null)
            {
                if (route.getRouteSteps().size() > targetPosition)
                    step = route.getRouteSteps().get(targetPosition);
                else
                    targetPosition = 0;
            }

            float x = step.getX();
            float y = step.getY();
            float z = step.getZ();
            
            if(owner.getTarget() instanceof Creature)
                NpcShoutsService.getInstance().handleEvent(owner, (Creature)owner.getTarget(), ShoutEventType.DIRECTION);
            
            owner.getMoveController().setNewDirection(x, y, z);
            owner.getMoveController().schedule();
        }
    }

    private boolean isWalkingToNextPoint()
    {
        return isWalkingToNextPoint;
    }

    private void setWalkingToNextPoint(boolean value)
    {
        isWalkingToNextPoint = value;
    }

    private void setNextPosition()
    {
        if(route == null)
        {
            getNextRandomPoint();
            return;
        }

        if(isRandomWalk)
        {
            targetPosition = Rnd.get(0, route.getRouteSteps().size() - 1);
        }
        else
        {
            if(targetPosition < (route.getRouteSteps().size() - 1))
                targetPosition++;
            else
                targetPosition = 0;
        }
    }

    private void getNextTime()
    {
        int nextDelay;
        if(route == null)
            nextDelay = Rnd.get(NpcMovementConfig.MINIMIMUM_DELAY, NpcMovementConfig.MAXIMUM_DELAY);
        else
            nextDelay = isRandomWalk ? Rnd.get(5, 60) : route.getRouteSteps().get(targetPosition).getRestTime();
        nextMoveTime = System.currentTimeMillis() + nextDelay * 1000;
    }

    private void getNextRandomPoint()
    {
        float x = owner.getSpawn().getX() - halfWalkArea + Rnd.get() * walkArea;
        float y = owner.getSpawn().getY() - halfWalkArea + Rnd.get() * walkArea;
        randomPoint = new RouteStep(x, y, owner.getSpawn().getZ());
    }

    private MovementType getRandomMovementType(int maxExcl)
    {
        return NpcMoveTypes[Rnd.get(maxExcl)];
    }
    @Override
    public int getExecutionInterval()
    {
        return 1;
    }

    @Override
    public void onClear()
    {
        owner.getMoveController().stop();
    }
}

