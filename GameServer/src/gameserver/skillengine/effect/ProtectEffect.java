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

import gameserver.skillengine.model.AttackType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Sippolo
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProtectEffect")
public class ProtectEffect extends BufEffect {

	@XmlAttribute
	protected boolean	percent;
    @XmlAttribute
    protected int value;
    @XmlAttribute
    protected int range;
    @XmlAttribute
    protected AttackType attacktype;

    @Override
    public void startEffect(final Effect effect) {
        super.startEffect(effect);

          Creature effector = effect.getEffector();
          Creature effected = effect.getEffected();
          // value is always in percentage
          if (effected instanceof Player){
        	  ((Player) effected).setProtect(true);
          }
          effected.getController().setProtectState(effector,value,range,attacktype);
    }

    @Override
    public void endEffect(Effect effect) {
        super.endEffect(effect);
          Creature effected = effect.getEffected();
          effected.getController().removeProtectState();
          if (effected instanceof Player){
        	  ((Player) effected).setProtect(false);
          }
    }

    @Override
    public void calculate(Effect effect) 
	{
        super.calculate(effect);
    }
}