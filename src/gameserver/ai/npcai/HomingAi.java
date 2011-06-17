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

package gameserver.ai.npcai;

import gameserver.ai.AI;
import gameserver.ai.desires.AbstractDesire;
import gameserver.ai.desires.impl.AttackDesire;
import gameserver.ai.desires.impl.MoveToTargetDesire;
import gameserver.ai.events.Event;
import gameserver.ai.events.handler.EventHandler;
import gameserver.ai.state.AIState;
import gameserver.ai.state.handler.NoneNpcStateHandler;
import gameserver.ai.state.handler.StateHandler;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Homing;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.skill.SkillEngine;
import gameserver.skill.model.Skill;



/**
 * @author ATracer
 * 
 */
public class HomingAi extends NpcAi
{
    public HomingAi()
    {
        /**
         * Event handlers
         */
        this.addEventHandler(new RespawnEventHandler());

        /**
         * State handlers
         */
        this.addStateHandler(new ActiveHomingStateHandler());
        this.addStateHandler(new NoneNpcStateHandler());
    }

    public class RespawnEventHandler implements EventHandler
    {
        @Override
        public Event getEvent()
        {
            return Event.RESPAWNED;
        }

        @Override
        public void handleEvent(Event event, AI<?> ai)
        {
            ai.setAiState(AIState.ACTIVE);

            if(!ai.isScheduled())
                ai.analyzeState();
        }

    }

    class ActiveHomingStateHandler extends StateHandler
    {
        @Override
        public AIState getState()
        {
            return AIState.ACTIVE;
        }

        @Override
        public void handleState(AIState state, AI<?> ai)
        {
            // ai logic is rather strange but ok till global refactoring
            ai.clearDesires();
            Homing homing = (Homing) ai.getOwner();
            Creature target = (Creature) homing.getOwner().getTarget();
            if(target == null || target.getLifeStats().isAlreadyDead())
            {
                owner.getLifeStats().reduceHp(10000, owner);
                return;
            }
            if (homing.getCreator() == null)
            {
                owner.getLifeStats().reduceHp(10000, owner);
                return;
            }
            ai.getOwner().getAggroList().addHate(target, 1);

            ai.addDesire(new MoveToTargetDesire(homing, target, ai.getOwner().getGameStats().getCurrentStat(StatEnum.ATTACK_RANGE)/1000f, AIState.ATTACKING.getPriority()));
            if (homing.getSkillId() == 0)
                ai.addDesire(new HomingAttackDesire(homing, target, homing.getAttackCount(), AIState.ATTACKING.getPriority()));
            else//skillId != 0, Call Condor, etc
                ai.addDesire(new HomingSkillUseDesire(homing, target, homing.getAttackCount(), AIState.ATTACKING.getPriority()));

            if(!ai.isScheduled())
                ai.schedule();
        }
    }

    private final class HomingAttackDesire extends AttackDesire
    {
        public HomingAttackDesire(Npc npc, Creature target,int attackCount, int desirePower)
        {
            super(npc, target, desirePower);
        }
        
        @Override
        public boolean handleDesire(AI<?> ai)
        {
            if(target == null || target.getLifeStats().isAlreadyDead())
            {
                owner.getLifeStats().reduceHp(10000, owner);
                return false;
            }
            //despawn if creator == null
            if (((Homing)owner).getActingCreature() == null)
            {
                owner.getLifeStats().reduceHp(10000, owner);
                return false;
            }
            return super.handleDesire(ai);
        }
        @Override
        public int getExecutionInterval()
        {
            return 4;
        }
    }
    
    private class HomingSkillUseDesire extends AbstractDesire
    {
        /**
         * Homing object
         */
        private Homing        owner;
        /**
         * Target of homing
         */
        private Creature    target;

        /**
         * 
         * @param desirePower
         * @param owner
         */
        private HomingSkillUseDesire(Homing owner, Creature target, int attackCount, int desirePower)
        {
            super(desirePower);
            this.owner = owner;
            this.target = target;
        }

        @Override
        public boolean handleDesire(AI<?> ai)
        {        
            if(target == null || target.getLifeStats().isAlreadyDead())
            {
                owner.getLifeStats().reduceHp(10000, owner);
                return false;
            }
            //despawn if creator == null
            if (((Homing)owner).getActingCreature() == null)
            {
                owner.getLifeStats().reduceHp(10000, owner);
                return false;
            }
            if(!owner.isEnemy(target))
                return false;
            
            Skill skill = SkillEngine.getInstance().getSkill(owner, owner.getSkillId(), 1, target);
            if(skill != null)
            {
                skill.useSkill();
            }
            
            return true;
        }

        @Override
        public int getExecutionInterval()
        {
            //TODO found out interval for call condor etc
            return 4;
        }

        @Override
        public void onClear()
        {
            // TODO Auto-generated method stub
        }
    }
}
