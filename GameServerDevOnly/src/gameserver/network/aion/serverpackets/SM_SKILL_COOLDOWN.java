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

import gameserver.dataholders.DataManager;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;
import gameserver.skill.model.SkillTemplate;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ATracer
 */
public class SM_SKILL_COOLDOWN extends AionServerPacket
{

    private Map<Integer, Long> cooldowns;
    
    public SM_SKILL_COOLDOWN(Map<Integer, Long> cooldowns)
    {
        this.cooldowns = cooldowns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        long currentTime = System.currentTimeMillis();
        Map<Integer, Integer> cooldowns = new HashMap<Integer, Integer>();
        for(Map.Entry<Integer, Long> entry : this.cooldowns.entrySet())
        {
            List<SkillTemplate> sts = DataManager.SKILL_DATA.getSkillTemplatesForDelayId(entry.getKey());
            int left = Math.round((entry.getValue() - currentTime) / 1000);
            for (SkillTemplate st : sts)
            {
                cooldowns.put(st.getSkillId(), left > 0 ? left : 0);
            }
        }
        
        writeH(buf, cooldowns.size());
        for(Map.Entry<Integer, Integer> entry : cooldowns.entrySet())
        {
            writeH(buf, entry.getKey());
            writeD(buf, entry.getValue());
        }
    }
}

