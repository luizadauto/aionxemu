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
package gameserver.model.items;

import com.aionemu.commons.utils.Rnd;
import gameserver.controllers.movement.ActionObserver;
import gameserver.controllers.movement.ActionObserver.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.PersistentState;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.item.GodstoneInfo;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.ItemService;
import gameserver.skillengine.SkillEngine;
import gameserver.skillengine.model.Skill;
import gameserver.utils.PacketSendUtility;
import org.apache.log4j.Logger;

/**
 * @author ATracer
 */
public class GodStone extends ItemStone {
    private static final Logger log = Logger.getLogger(GodStone.class);

    private final GodstoneInfo godstoneInfo;
    private ActionObserver actionListener;
    private final int probability;
    private final int probabilityLeft;

    public GodStone(int itemObjId, int itemId, PersistentState persistentState) {
        super(itemObjId, itemId, 0, ItemStoneType.GODSTONE, persistentState);
        ItemTemplate itemTemplate = ItemService.getItemTemplate(itemId);
        godstoneInfo = itemTemplate.getGodstoneInfo();

        if (godstoneInfo != null) {
            probability = godstoneInfo.getProbability();
            probabilityLeft = godstoneInfo.getProbabilityleft();
        } else {
            probability = 0;
            probabilityLeft = 0;
            log.warn("CHECKPOINT: Godstone info missing for item : " + itemId);
        }

    }

    /**
     * @param player
     */
    public void onEquip(final Player player) {
        if (godstoneInfo == null)
            return;

        actionListener = new ActionObserver(ObserverType.ATTACK) {
            @Override
            public void attack(Creature creature) {
                int rand = Rnd.get(probability - probabilityLeft, probability);
                if (rand > Rnd.get(0, 1000)) {
                    Skill skill = SkillEngine.getInstance().getSkill(player, godstoneInfo.getSkillid(),
                            godstoneInfo.getSkilllvl(), player.getTarget());
                    PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301062, "Godstone"));
                    skill.useSkill();
                }
            }
        };

        player.getObserveController().addObserver(actionListener);
    }

    /**
     * @param player
     */
    public void onUnEquip(Player player) {
        if (actionListener != null)
            player.getObserveController().removeObserver(actionListener);

	}
}
