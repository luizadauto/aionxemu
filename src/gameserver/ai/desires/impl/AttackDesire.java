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

import gameserver.ai.AI;
import gameserver.ai.desires.AbstractDesire;
import gameserver.ai.events.Event;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.model.templates.stats.NpcRank;
import gameserver.utils.MathUtil;

/**
 * This class indicates that character wants to attack somebody
 *
 * @author SoulKeeper, Pinguin, ATracer
 */
public class AttackDesire extends AbstractDesire {
    private int attackNotPossibleCounter;
	private int maxAtkTry		= 6;
    private int attackCounter = 1;

    /**
     * Target of this desire
     */
    protected Creature target;

    protected Npc owner;

    /**
     * Creates new attack desire, target can't be changed
     *
     * @param npc         The Npc that's attacking
     * @param target      whom to attack
     * @param desirePower initial attack power
     */
    public AttackDesire(Npc npc, Creature target, int desirePower) {
        super(desirePower);
        this.target = target;
        this.owner = npc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handleDesire(AI<?> ai) {
        if(target == null || target.getLifeStats().isAlreadyDead() || target.getLifeStats().getCurrentHp() <= 0 || 
            owner instanceof FortressGate || owner instanceof Kisk)
        {
            owner.getAggroList().stopHating(target);
            owner.getAggroList().remove(target);
            target = owner.getAggroList().getMostHated();
            if (target == null) {
                owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
                return false;
            }
        }

        double distance = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), target.getX(), target.getY(), target.getZ());

        if (distance > 50) {
            owner.getAggroList().stopHating(target);
            owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
            return false;
        }

        if (target.getVisualState() != 0 && owner instanceof Npc) {
            NpcRank npcrank = owner.getObjectTemplate().getRank();
            /* 3 currently GM invis
                * This will only exclude elites from hide1
                */
            if (target.getVisualState() == 3 || (npcrank != NpcRank.LEGENDARY && npcrank != NpcRank.HERO &&
                    (target.getVisualState() != 1 || npcrank != NpcRank.ELITE))) {
                owner.getAggroList().stopHating(target);
                owner.getAggroList().remove(target);
                owner.getController().cancelCurrentSkill();//prevent npc from ending cast of skill
                owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
                return false;
            }
        }

        attackCounter++;

        if (attackCounter % 2 == 0) {
            if (!owner.getAggroList().isMostHated(target)) {
                owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
                return false;
            }
        }
        int attackRange = owner.getGameStats().getCurrentStat(StatEnum.ATTACK_RANGE);
        long lastAttackDiff = System.currentTimeMillis() - owner.getLastAttack();
        if (distance * 1000 <= attackRange && lastAttackDiff > 1000) {
            owner.getController().attackTarget(target);
            owner.setLastAttack(System.currentTimeMillis());
            attackNotPossibleCounter = 0;
        } else if (lastAttackDiff > 1000) {
            attackNotPossibleCounter++;
        }

        // Instance mobs take MUCH more time to loose aggro
        if(owner.isInInstance())
            maxAtkTry = 20;
        
        if(attackNotPossibleCounter > maxAtkTry && attackNotPossibleCounter > 10)
        {
            owner.getAggroList().stopHating(target);
            owner.getAggroList().remove(target);
            owner.getAi().handleEvent(Event.TIRED_ATTACKING_TARGET);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AttackDesire))
            return false;

        AttackDesire that = (AttackDesire) o;

        return target.equals(that.target);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return target.hashCode();
    }

    /**
     * Returns target of this desire
     *
     * @return target of this desire
     */
    public Creature getTarget() {
        return target;
    }

    @Override
    public int getExecutionInterval() {
        return 2;
    }

    @Override
    public void onClear() {
        owner.unsetState(CreatureState.WEAPON_EQUIPPED);
	}

}