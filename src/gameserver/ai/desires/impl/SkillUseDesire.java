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

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import gameserver.ai.AI;
import gameserver.ai.desires.AbstractDesire;
import gameserver.dataholders.DataManager;
import gameserver.model.ShoutEventType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Monster;
import gameserver.model.gameobjects.Npc;
import gameserver.model.templates.npcskill.NpcSkillList;
import gameserver.model.templates.npcskill.NpcSkillTemplate;
import gameserver.services.NpcShoutsService;
import gameserver.skill.SkillEngine;
import gameserver.skill.effect.BackDashEffect;
import gameserver.skill.effect.EffectTemplate;
import gameserver.skill.effect.FpAttackEffect;
import gameserver.skill.effect.FpAttackInstantEffect;
import gameserver.skill.effect.PulledEffect;
import gameserver.skill.effect.RandomMoveLocEffect;
import gameserver.skill.effect.StaggerEffect;
import gameserver.skill.model.Skill;
import gameserver.skill.model.SkillTemplate;
import gameserver.utils.ThreadPoolManager;
import gameserver.utils.exceptionhandlers.exception_enums;


/**
 * @author ATracer
 * 
 */
public class SkillUseDesire extends AbstractDesire
{

    protected Creature        owner;
    private NpcSkillList    skillList;
    private boolean            InitialSkillCasted    = false;

    /**
     * @param owner
     * @param desirePower
     */
    public SkillUseDesire(Creature owner, int desirePower)
    {
        super(desirePower);
        this.owner = owner;
        this.skillList = ((Npc) owner).getNpcSkillList();
    }

    @Override
    public boolean handleDesire(AI<?> ai)
    {
        if(owner.isCasting())
            return true;

        /**
         * Demo mode - take random skill
         */
        List<NpcSkillTemplate> skills = skillList.getNpcSkills();
        NpcSkillTemplate npcSkill = skills.get(Rnd.get(0, skills.size() - 1));

        // Temporary hack to disable GeoData and KnockBack/Stagger skills from NPCs
        // While we're there, check for pull or crash effects
        boolean acceptedSkill = false;
        boolean initialForcedSkill = false;
        int initialForcedSkillID = 0;
        int initialForcedSkillLvl = 0;
        for(int i = 0; i < 10; i++)
        {
            SkillTemplate sT = DataManager.SKILL_DATA.getSkillTemplate(npcSkill.getSkillid());
            if(sT != null && sT.getEffects() != null)
            {
                boolean hasDangerous = false;
                for(EffectTemplate e : sT.getEffects().getEffects())
                {
                    if((e instanceof BackDashEffect || e instanceof RandomMoveLocEffect || e instanceof StaggerEffect))
                        hasDangerous = true;
                    if(e instanceof PulledEffect || e instanceof FpAttackEffect || e instanceof FpAttackEffect
                        || e instanceof FpAttackInstantEffect)
                    {
                        initialForcedSkill = true;
                        initialForcedSkillID = sT.getSkillId();
                        initialForcedSkillLvl = sT.getLvl();
                    }
                }
                if(!hasDangerous)
                {
                    acceptedSkill = true;
                    break;
                }
            }
            npcSkill = skills.get(Rnd.get(0, skills.size() - 1));
        }
        if(!acceptedSkill)
            return false;

        /**
         * Cast initial skill once
         */
        if(initialForcedSkill == true && InitialSkillCasted == false)
        {
            Skill initialskill = SkillEngine.getInstance().getSkill(owner, initialForcedSkillID, initialForcedSkillLvl,
                owner.getTarget());
            initialskill.useSkill();
            InitialSkillCasted = true;
        }

        /**
         * Demo mode - use probability from template
         */

        int skillProbability = npcSkill.getProbability();
        if(Rnd.get(0, 100) < skillProbability)
        {
            Skill skill = SkillEngine.getInstance().getSkill(owner, npcSkill.getSkillid(), npcSkill.getSkillLevel(),
                owner.getTarget());

            if(skill != null)
            {
                if(owner instanceof Npc && owner.getTarget() instanceof Creature)
                {
                    if(skill.getSkillTemplate() != null && skill.getSkillTemplate().getDuration() > 0)
                        NpcShoutsService.getInstance().handleEvent((Npc) owner, (Creature) owner.getTarget(),
                            ShoutEventType.CAST);
                    else if(skill.getSkillTemplate() != null)
                        NpcShoutsService.getInstance().handleEvent((Npc) owner, (Creature) owner.getTarget(),
                            ShoutEventType.SKILL);
                }
                skill.useSkill();
            }

            // Iron Explosive Mine
            // 1. Gets aggro on player
            // 2. Casts skill and explodes
            // 3. Wait 1 sec then kill the bomb (one-shot NPC)

            // TODO : specific controller needed, there is much more than one npc id for mines.
            if(owner != null && owner instanceof Monster
                && ((Monster) owner).getNpcId() == exception_enums.NPC_SIEGE_MINE_I)
            {
                ThreadPoolManager.getInstance().schedule(new Runnable(){

                    @Override
                    public void run()
                    {
                        owner.getController().die();
                    }
                }, 500);
            }

        }

        return true;
    }

    @Override
    public void onClear()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public int getExecutionInterval()
    {
        return 1;
    }
}

