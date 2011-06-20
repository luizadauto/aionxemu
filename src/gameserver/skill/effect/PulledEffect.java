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
package gameserver.skill.effect;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import gameserver.skill.model.Effect;
import gameserver.skill.model.SpellStatus;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sarynth
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PulledEffect")
public class PulledEffect extends EffectTemplate {
    @Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
    }

    @Override
    public void calculate(Effect effect)
    {
        //player with shield on and in abyss transformation cannot be pulled
        if (effect.getEffected() instanceof Player)
        {
            for (Effect ef : effect.getEffected().getEffectController().getAbnormalEffects())
            {
                for (EffectTemplate et : ef.getEffectTemplates())
                {
                    if (et instanceof ShieldEffect && !(et instanceof ReflectorEffect)
                        && !(et instanceof ProvokerEffect) && !(et instanceof ProtectEffect))
                        return;
                }
                if (ef.isAvatar())
                    return;
            }
        }
        
        super.calculate(effect, null, SpellStatus.NONE);
    }

    @Override
    public void startEffect(final Effect effect) {
        final Creature effector = effect.getEffector();
        final Creature effected = effect.getEffected();
        effect.setAbnormal(EffectId.CANNOT_MOVE.getEffectId());
        effected.getEffectController().setAbnormal(EffectId.CANNOT_MOVE.getEffectId());
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                World.getInstance().updatePosition(
                        effected,
                        effector.getX(),
                        effector.getY(),
                        effector.getZ() + 0.25F,
                        effected.getHeading());
                PacketSendUtility.broadcastPacketAndReceive(effected, new SM_FORCED_MOVE(effector, effected));
                if (effected.isCasting())
                    effected.getController().cancelCurrentSkill();
            }
        }, 1000);
    }

    @Override
    public void endEffect(final Effect effect) {
        effect.getEffected().getEffectController().unsetAbnormal(EffectId.CANNOT_MOVE.getEffectId());
    }
}
