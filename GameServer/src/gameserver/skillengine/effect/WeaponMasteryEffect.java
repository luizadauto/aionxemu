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
package gameserver.skillengine.effect;

import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.WeaponType;
import gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeaponMasteryEffect")
public class WeaponMasteryEffect extends BufEffect {
    @XmlAttribute(name = "weapon")
    private WeaponType weaponType;

    /**
     * @return the weaponType
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public void calculate(Effect effect) {
        //right now only players are affected
        Player player = (Player) effect.getEffector();
        //check best mastery skill
        Integer skillId = player.getSkillList().getWeaponMasterySkill(weaponType);
        if (skillId != null && skillId != effect.getSkillId())
            return;
        //check weather already skill applied and weapon isEquipeed
        boolean weaponMasterySet = player.getEffectController().isWeaponMasterySet(skillId);
        boolean isWeaponEquiped = player.getEquipment().isWeaponEquipped(weaponType);
        if (!weaponMasterySet && isWeaponEquiped)
            effect.addSucessEffect(this);
    }

    @Override
    public void endEffect(Effect effect) {
        super.endEffect(effect);
        Player player = (Player) effect.getEffector();
        player.getEffectController().unsetWeaponMastery();
    }

    @Override
    public void startEffect(Effect effect) {
        Player player = (Player) effect.getEffector();
        player.getEffectController().removeEffect(player.getEffectController().getWeaponMastery());
        super.startEffect(effect);
        player.getEffectController().setWeaponMastery(effect.getSkillId());
    }


}
