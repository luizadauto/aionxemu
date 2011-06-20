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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.GroupGate;
import gameserver.model.templates.spawn.SpawnTemplate;
import gameserver.skill.model.Effect;
import gameserver.spawn.SpawnEngine;
import gameserver.utils.ThreadPoolManager;


/**
 * @author LokiReborn
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummonGroupGateEffect")
public class SummonGroupGateEffect extends SummonEffect
{
    @Override
    public void applyEffect(Effect effect)
    {
        final Creature effector = effect.getEffector();
        SpawnEngine spawnEngine = SpawnEngine.getInstance();
        float x = effector.getX();
        float y = effector.getY();
        float z = effector.getZ();
        byte heading = effector.getHeading();
        int worldId = effector.getWorldId();
        int instanceId = effector.getInstanceId();

        SpawnTemplate spawn = spawnEngine.addNewSpawn(worldId, instanceId, npcId, x, y, z, heading, 0, 0, true, true);
        final GroupGate groupgate = spawnEngine.spawnGroupGate(spawn, instanceId, effector);

        ThreadPoolManager.getInstance().schedule(new Runnable(){

            @Override
            public void run()
            {
                groupgate.getController().onDelete();
            }
        }, time * 1000);
    }

    @Override
    public void calculate(Effect effect)
    {
        super.calculate(effect);
    }
}
