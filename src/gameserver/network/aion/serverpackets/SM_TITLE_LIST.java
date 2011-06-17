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

package gameserver.network.aion.serverpackets;

import gameserver.configs.main.GSConfig;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.Title;
import gameserver.model.gameobjects.player.TitleList;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.services.TitleService;
import gameserver.utils.PacketSendUtility;

import java.nio.ByteBuffer;

/**
 * @author Nemiroff
 * @author Xavier Date: 01.12.2009
 */
public class SM_TITLE_LIST extends AionServerPacket {
    private TitleList titleList;
    private int objectId;
    private int titleId;

    // TODO Make List from DataBase

    public SM_TITLE_LIST(Player player) {
        this.titleList = player.getTitleList();
    }

    public SM_TITLE_LIST(int objectId, int titleId) {
        this.objectId = objectId;
        this.titleId = titleId;
    }

    public SM_TITLE_LIST(int titleId) {
        this.titleId = titleId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf) {
        if (titleList != null) {
            writeImplTitleList(buf);
            return;
        }

        if (objectId > 0 && titleId > 0) {
            writeImplTitleUpdate(buf);
            return;
        }

        writeImplTitleSet(buf);
        return;
    }

    private void writeImplTitleList(ByteBuffer buf)
    {
        Player player = titleList.getOwner();
        TitleService.removeExpiredTitles(player);

        if(GSConfig.SERVER_VERSION.startsWith("2."))
            writeH(buf, 0); // unk
        else
            writeC(buf, 0); // unk

        writeH(buf, titleList.size());

        for(final Title title : titleList.getTitles())
        {
            writeD(buf, title.getTitleId());
            writeD(buf, (int)title.getTitleTimeLeft());
        }

        if(player.getCommonData().getTitleId() > 0)
        {
            if(titleList.canAddTitle(player.getCommonData().getTitleId()))
            {
                player.getCommonData().setTitleId(0);
                PacketSendUtility.sendMessage(player, "The usage time of title has expired.");
            }
        }
    }

    private void writeImplTitleUpdate(ByteBuffer buf) {
        writeD(buf, objectId);
        writeD(buf, titleId);
    }

    protected void writeImplTitleSet(ByteBuffer buf) {
        writeC(buf, 1);
        writeD(buf, titleId);
    }
}
