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

package gameserver.model.gameobjects;

import java.util.concurrent.Future;

import gameserver.ai.npcai.HomingAi;
import gameserver.controllers.HomingController;
import gameserver.controllers.NpcController;
import gameserver.controllers.movement.ActionObserver;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.spawn.SpawnTemplate;

/**
 * @author ATracer, kecimis
 */
public class Homing extends NpcWithCreator
{
    /**
     * Number of performed attacks
     */
    private int attackCount;

    /**
     * counts number of usages of attack/skill
     */
    private int counter = 0;
    private ActionObserver observer = null;
    private Future<?> task = null;
    /**
     * 
     * @param objId
     * @param controller
     * @param spawnTemplate
     * @param objectTemplate
     */
    public Homing(int objId, NpcController controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate)
    {
        super(objId, controller, spawnTemplate, objectTemplate);
    }

    @Override
    public Homing getOwner()
    {
        return (Homing)this;
    }
    @Override
    public HomingController getController()
    {
        return (HomingController) super.getController();
    }
    /**
     * @param attackCount
     *            the attackCount to set
     */
    public void setAttackCount(int attackCount)
    {
        this.attackCount = attackCount;
    }

    /**
     * @return the attackCount
     */
    public int getAttackCount()
    {
        return attackCount;
    }

    @Override
    public void initializeAi()
    {
        this.ai = new HomingAi();// TODO
        ai.setOwner(this);
    }

    /**
     * @return NpcObjectType.HOMING
     */
    @Override
    public NpcObjectType getNpcObjectType()
    {
        return NpcObjectType.HOMING;
    }

    public void setCounter(int number)
    {
        this.counter = number;
    }
    public int getCounter()
    {
        return this.counter;
    }
    public void setObserver(ActionObserver acO)
    {
        this.observer = acO;
    }
    public ActionObserver getObserver()
    {
        return this.observer;
    }
    public void setDespawnTask(Future<?> task)
    {
        this.task = task;
    }
    public Future<?> getDespawnTask()
    {
        return this.task;
    }
    public void cancelDespawnTask()
    {
        if (task != null)
        {
            task.cancel(true);
            task = null;
        }
    }
}

