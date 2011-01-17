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
package gameserver.services;

import gameserver.controllers.SummonController.UnsummonType;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.zone.ZoneName;

/**
 * @author HellBoy
 */
public class ArenaService {
    public static final ArenaService getInstance() {
        return SingletonHolder.instance;
    }

    public void onDie(final Player player, Creature lastAttacker) {
        DuelService.getInstance().loseArenaDuel(player);

        Summon summon = player.getSummon();
        if (summon != null)
            summon.getController().release(UnsummonType.UNSPECIFIED);

        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()), true);

        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
        player.getObserveController().notifyDeath(player);

        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (isInTrinielArena(player))
                    TeleportService.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f, 0);
                if (isInSanctumArena(player))
                    TeleportService.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f, 21);
            }
        }, 5000);
    }

    public boolean isInArena(Player player) {
        if (isInTrinielArena(player) || isInSanctumArena(player))
            return true;
        return false;
    }

    private boolean isInTrinielArena(Player player) {
        int world = player.getWorldId();
        if (world == 120010000 && ZoneService.getInstance().isInsideZone(player, ZoneName.TRINIEL_PVP_ZONE))
            return true;
        return false;
    }

    private boolean isInSanctumArena(Player player) {
        int world = player.getWorldId();
        if (world == 110010000 && ZoneService.getInstance().isInsideZone(player, ZoneName.COLISEUM))
            return true;
        return false;
    }

    public boolean isInSameGroup(Player player1, Player player2) {
        if (player1.isInGroup()) {
            if (player2.isInGroup()) {
                if (player1.getPlayerGroup().getGroupId() == player2.getPlayerGroup().getGroupId())
                    return true;
                return false;
            }
            return false;
        }
        return false;
    }

    @SuppressWarnings("synthetic-access")
    public static class SingletonHolder {
        protected static final ArenaService instance = new ArenaService();
    }
}
