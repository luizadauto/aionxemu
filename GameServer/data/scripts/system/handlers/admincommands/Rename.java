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

package admincommands;

import com.aionemu.commons.database.dao.DAOManager;
import gameserver.configs.administration.AdminConfig;
import gameserver.dao.PlayerDAO;
import gameserver.model.gameobjects.player.Friend;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_LEGION_UPDATE_MEMBER;
import gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.PlayerService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.Util;
import gameserver.utils.chathandlers.AdminCommand;
import gameserver.world.Executor;
import gameserver.world.World;

import java.util.Iterator;

/**
 * @author xitanium
 * @Updated By Kamui
 */

public class Rename extends AdminCommand {
    public Rename() {
        super("rename");
    }

    @Override
    public void executeCommand(Player admin, String[] params) {

        if (admin.getAccessLevel() < AdminConfig.COMMAND_RENAME) {
            PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command.");
            return;
        }

        if (params.length < 2) {
            PacketSendUtility.sendMessage(admin, "syntax //rename <player name> <new player name>");
            return;
        }

        final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));

        if (player == null) {
            PacketSendUtility.sendPacket(admin, SM_SYSTEM_MESSAGE.PLAYER_IS_OFFLINE(params[0]));
            return;
        }

        if (!PlayerService.isValidName(params[1])) {
            PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400151));
            return;
        }

        if (!PlayerService.isFreeName(params[1])) {
            PacketSendUtility.sendPacket(admin, new SM_SYSTEM_MESSAGE(1400155));
            return;
        }

        player.getCommonData().setName(params[1]);
        PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
        Iterator<Friend> knownFriends = player.getFriendList().iterator();

        DAOManager.getDAO(PlayerDAO.class).storePlayer(player);

        player.getKnownList().doOnAllPlayers(new Executor<Player>() {
            @Override
            public boolean run(Player p) {
                PacketSendUtility.sendPacket(p, new SM_PLAYER_INFO(player, player.isEnemyPlayer(p)));
                return true;
            }
        }, true);

        while (knownFriends.hasNext()) {
            Friend nextObject = knownFriends.next();
            if (nextObject.getPlayer() != null) {
                if (nextObject.getPlayer().isOnline())
                    PacketSendUtility.sendPacket(nextObject.getPlayer(), new SM_PLAYER_INFO(player, false));
            }
        }

        if (player.isLegionMember()) {
            PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_UPDATE_MEMBER(player, 0, ""));
        }
        PacketSendUtility.sendMessage(player, "You have been renamed to [" + params[1] + "] by " + admin.getName());
        PacketSendUtility.sendMessage(admin, "Player " + params[0] + " has been renamed to " + params[1]);
    }
}
