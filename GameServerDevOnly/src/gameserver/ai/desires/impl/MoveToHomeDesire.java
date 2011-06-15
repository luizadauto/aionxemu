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
import gameserver.ai.desires.MoveDesire;
import gameserver.ai.events.Event;
import gameserver.controllers.movement.MovementType;
import gameserver.model.EmotionType;
import gameserver.model.ShoutEventType;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.model.templates.spawn.SpawnTemplate;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.services.NpcShoutsService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * @author ATracer
 */
public class MoveToHomeDesire extends AbstractDesire implements MoveDesire {
    private Npc owner;
    private float x;
    private float y;
    private float z;
    boolean started = false;
    private int restoreHpValue;
    private int restoreMpValue;

    public MoveToHomeDesire(Npc owner, int desirePower) {
        super(desirePower);
        this.owner = owner;
        SpawnTemplate template = owner.getSpawn();
        x = template.getX();
        y = template.getY();
        z = template.getZ();
        restoreHpValue = owner.getLifeStats().getMaxHp() / 5;
        restoreMpValue = owner.getLifeStats().getMaxMp() / 5;
    }

    @Override
    public boolean handleDesire(AI<?> ai) {
        if (owner == null || owner.getLifeStats().isAlreadyDead())
            return false;

        if(!started)
        {
            if(MathUtil.getDistance(owner, owner.getSpawn().getX(), owner.getSpawn().getY(), owner.getSpawn().getZ()) > 50){
                // target too far, teleport to spawn point.
                World.getInstance().updatePosition(owner, owner.getSpawn().getX(), owner.getSpawn().getY(), owner.getSpawn().getZ(), owner.getSpawn().getHeading(), true);
                started = true;
            }
            else {
                owner.getMoveController().stopFollowing();
                owner.getMoveController().setNewDirection(x, y, z);
                owner.getGameStats().setStat(StatEnum.SPEED, (int) owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1000);
                owner.getMoveController().setSpeed(owner.getObjectTemplate().getStatsTemplate().getRunSpeed());
                owner.getMoveController().setMovementType(MovementType.NPC_MOVEMENT_TYPE_III);
                PacketSendUtility.broadcastPacket(owner, new SM_EMOTION(owner, EmotionType.START_EMOTE2));
                started = true;
            }
            NpcShoutsService.getInstance().handleEvent(owner, owner, ShoutEventType.LEAVE);
        }

        if (!owner.getMoveController().isScheduled())
            owner.getMoveController().schedule();

        owner.getLifeStats().increaseHp(TYPE.NATURAL_HP, restoreHpValue);
        owner.getLifeStats().increaseMp(TYPE.NATURAL_MP, restoreMpValue);

        double dist = MathUtil.getDistance(owner.getX(), owner.getY(), owner.getZ(), x, y, z);
        if (dist < 2) {
            ai.handleEvent(Event.BACK_HOME);
            return false;
        }
        return true;
    }

    @Override
    public int getExecutionInterval() {
        return 5;
    }

    @Override
    public void onClear() {
        owner.getMoveController().stop();
        owner.getController().stopMoving();
    }
}
