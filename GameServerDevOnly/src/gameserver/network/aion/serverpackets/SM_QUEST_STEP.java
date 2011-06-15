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

import java.nio.ByteBuffer;

import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.quest.model.QuestStatus;


/**
 * @author MrPoke
 * 
 */
public class SM_QUEST_STEP extends AionServerPacket
{
    private int questId;
    private int status;
    private int vars;

    public SM_QUEST_STEP(int questId, QuestStatus status, int vars) {
        this.questId = questId;
        this.status = status.value();
        this.vars = vars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf) {
        writeH(buf, questId);
        writeC(buf, status);
        writeD(buf, vars);
        writeC(buf, 0);
    }

}
