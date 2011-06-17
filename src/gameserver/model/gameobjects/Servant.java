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

import gameserver.ai.npcai.ServantAi;
import gameserver.controllers.NpcController;
import gameserver.model.gameobjects.player.Player;
import gameserver.controllers.NpcWithCreatorController;
import gameserver.model.templates.VisibleObjectTemplate;
import gameserver.model.templates.spawn.SpawnTemplate;

/**
 * @author ATracer
 */
public class Servant extends NpcWithCreator {
    /**
     * Target of this servant
     */
    private Creature target;
    /**
     * Hp used on skill usage
     */
    private int hpRatio;
    /**
     * This servant is healing servant
     */
    private boolean isHealingServant;

    /**
     * @param objId
     * @param controller
     * @param spawnTemplate
     * @param objectTemplate
     */
    public Servant(int objId, NpcController controller, SpawnTemplate spawnTemplate, VisibleObjectTemplate objectTemplate) {
        super(objId, controller, spawnTemplate, objectTemplate);
    }

    /**
     * @param skillId the skillId to set
     */
    public void setSkillId(int skillId) {
        this.skillId = skillId;

        if (skillId == 18886 || skillId == 18887)
            this.isHealingServant = true;
        else
            this.isHealingServant = false;
    }

    @Override
    public NpcWithCreatorController getController()
    {
        return (NpcWithCreatorController) super.getController();
    }
    public Servant getOwner()
    {
        return (Servant)this;
    }

    /**
     * @return the target
     */
    @Override
    public Creature getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(Creature target) {
        this.target = target;
    }

    /**
     * @return the hpRatio
     */
    public int getHpRatio() {
        return hpRatio;
    }

    /**
     * @param hpRatio the hpRatio to set
     */
    public void setHpRatio(int hpRatio) {
        this.hpRatio = hpRatio;
    }

    /**
     * @return isHealingServant
     */
    public boolean isHealingServant() {
        return isHealingServant;
    }

    @Override
    public void initializeAi() {
        this.ai = new ServantAi();
        ai.setOwner(this);
    }

    /**
     * @return NpcObjectType.TRAP
     */
    @Override
    public NpcObjectType getNpcObjectType() {
        return NpcObjectType.SERVANT;
    }

    @Override
    public Creature getActingCreature() {
        return this.creator;
    }
}
