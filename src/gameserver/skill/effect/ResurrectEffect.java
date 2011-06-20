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

import java.util.concurrent.Future;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.TaskId;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_RESURRECT;
import gameserver.skill.model.Effect;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;


/**
 * @author ATracer
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectEffect")
public class ResurrectEffect extends EffectTemplate
{
    @Override
    public void applyEffect(Effect effect)
    {
        PacketSendUtility.sendPacket((Player) effect.getEffected(), new SM_RESURRECT(effect.getEffector(), effect.getSkillId()));
        
        //add task to player
        Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable(){
            @Override
            public void run()
            {
                //blank
            }
        }, 5 * 60 * 1000);//5minutes

        ((Player)effect.getEffected()).getController().addTask(TaskId.SKILL_RESURRECT, task);

    }

    @Override
    public void calculate(Effect effect)
    {
        if(effect.getEffected() instanceof Player && effect.getEffected().getLifeStats().isAlreadyDead())
            super.calculate(effect);
    }
}
