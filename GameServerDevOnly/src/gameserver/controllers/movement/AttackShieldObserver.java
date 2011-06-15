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
package gameserver.controllers.movement;

import java.util.List;

import com.aionemu.commons.utils.Rnd;
import gameserver.controllers.attack.AttackResult;
import gameserver.controllers.attack.AttackStatus;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.skill.action.DamageType;
import gameserver.skill.model.AttackType;
import gameserver.skill.model.Effect;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;



/**
 * @author ATracer
 * @edit kecimis
 */
public class AttackShieldObserver extends AttackCalcObserver
{
    private Effect effect;
    private int value;
    private int hit;
    private boolean percent;
    private int probability;
    private AttackType attackType;
    private int shieldType;
    
    /**
     * @param hit - either hit or value
     * @param value - either totalhit or radius or value
     * @param percent
     * @param effect 
     * @param attackType
     * @param shieldType, 2 = shield, 1 = reflector, 4 = protect
     */
    public AttackShieldObserver(int hit, int value, boolean percent, Effect effect, AttackType attackType, int probability, int shieldType)
    {
        this.effect = effect;
        this.value = value;    //totalHit for shield, radius for reflector    , value for protect
        this.hit = hit;//hit for shield,reflector, range for protect
        this.percent = percent;
        this.attackType = attackType;
        this.probability = probability;
        this.shieldType = shieldType;
    }


    @Override
    public void checkShield(List<AttackResult> attackList, Creature attacker)
    {
        for(AttackResult attackResult : attackList)
        {
            if (attackResult.getAttackStatus().toString().contains("DODGE") || attackResult.getAttackStatus().toString().contains("RESIST"))
                continue;
            
            switch(attackType)
            {
                case MAGICAL_SKILL:
                    if (attackResult.getDamageType() == DamageType.PHYSICAL)
                        continue;
                case PHYSICAL_SKILL:
                    if (attackResult.getDamageType() == DamageType.MAGICAL)
                        continue;
            }
            if(Rnd.get(0, 100) > probability)
                continue;
            
            if (shieldType == 2)//shield type 2, normal shield
            {
                int damage = attackResult.getDamage();
                
                int absorbedDamage = 0;
                if(percent)
                    absorbedDamage = damage * hit / 100;
                else
                    absorbedDamage = damage >= hit ? hit : damage;
                
                absorbedDamage = absorbedDamage >= value ? value : absorbedDamage;
                value -= absorbedDamage;
            
                if(absorbedDamage > 0)
                    attackResult.setShieldType(shieldType);
                attackResult.setDamage(damage - absorbedDamage);
            
                if(value <= 0)
                {
                    effect.endEffect();
                    return;
                }
            }
            else if (shieldType == 1)//shield type 1, reflected damage
            {
                int radius = value;
                
                if(MathUtil.isIn3dRange(attacker, effect.getEffected(), radius))
                {
                    attackResult.setShieldType(shieldType);
                    attackResult.setReflectedDamage(hit);
                    attackResult.setSkillId(effect.getSkillId());
                    attacker.getController().onAttack(effect.getEffected(), hit, AttackStatus.NORMALHIT, false);
                    
                    if (effect.getEffected() instanceof Player)
                        PacketSendUtility.sendPacket((Player)effect.getEffected(), SM_SYSTEM_MESSAGE.STR_SKILL_PROC_EFFECT_OCCURRED(effect.getSkillTemplate().getNameId()));
                }
            }
            else if (shieldType == 4)//shield type 4, protect
            {
                int range = hit;
                
                if (effect.getEffector().getLifeStats().isAlreadyDead())
                {
                    effect.endEffect();
                    break;
                }
                
                if(MathUtil.isIn3dRange(effect.getEffector(), effect.getEffected(), range))
                {
                    int damageProtected = 0;

                    if (percent)
                        damageProtected = ((int)(attackResult.getDamage() * value * 0.01));
                    else
                        damageProtected = value;
                    
                    int finalDamage = attackResult.getDamage() - damageProtected;
                    
                    attackResult.setDamage((finalDamage <= 0 ? 0 : finalDamage));
                    attackResult.setShieldType(shieldType);
                    attackResult.setSkillId(effect.getSkillId());
                    effect.getEffector().getController().onAttack(attacker, damageProtected, AttackStatus.NORMALHIT, false);
                }
            }
        }    
    }
}
