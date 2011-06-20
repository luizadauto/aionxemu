/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.controllers.movement.ActionObserver;
import gameserver.controllers.movement.ActionObserver.ObserverType;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SUMMON_USESKILL;
import gameserver.skill.model.Effect;
import gameserver.skill.model.Skill;
import gameserver.utils.PacketSendUtility;


/**
 * @author ATracer
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PetOrderUseUltraSkillEffect")
public class PetOrderUseUltraSkillEffect extends EffectTemplate
{
    @XmlAttribute(name = "disappear")
    protected boolean disappear;
    
    @XmlAttribute(name = "ultra_skill")
    protected int ultraSkill;
    
    @Override
    public void applyEffect(final Effect effect)
    {
        Player effector = (Player) effect.getEffector();
        final Summon summon = effector.getSummon();
        
        if(summon == null)
            return;
        
        int effectorId = summon.getObjectId();
        
        int npcId = summon.getNpcId();
        int orderSkillId = effect.getSkillId();
        
        int petUseSkillId = DataManager.PET_SKILL_DATA.getPetOrderSkill(orderSkillId, npcId);
        int targetId = effect.getEffected().getObjectId();

        PacketSendUtility.sendPacket(effector, new SM_SUMMON_USESKILL(effectorId, petUseSkillId,
            1, targetId));
        
        if(disappear)
        {
            summon.getObserveController().attach(new ActionObserver(ObserverType.SKILLUSE) {
                @Override
                public void skilluse(Skill skill)
                {
                    if (skill.getEffector() == summon)
                        summon.getController().release(UnsummonType.UNSPECIFIED);
                }
            });
        }
    }

    @Override
    public void calculate(Effect effect)
    {
        if(effect.getEffector() instanceof Player && effect.getEffected() != null)
            super.calculate(effect);
    }
}
