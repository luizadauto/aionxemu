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

import gameserver.model.alliance.PlayerAlliance;
import gameserver.model.group.LootDistribution;
import gameserver.model.group.LootGroupRules;
import gameserver.model.group.LootRuleType;
import gameserver.network.aion.AionConnection;
import gameserver.network.aion.AionServerPacket;

import java.nio.ByteBuffer;

/**
 * @author Sarynth (Thx Rhys2002 for Packets)
 *
 */
public class SM_ALLIANCE_INFO extends AionServerPacket
{
    private PlayerAlliance alliance;
    
    public SM_ALLIANCE_INFO(PlayerAlliance alliance)
    {
        this.alliance = alliance;
    }
    @Override
    protected void writeImpl(AionConnection con, ByteBuffer buf)
    {
        writeH(buf, 4);
        writeD(buf, alliance.getObjectId());
        writeD(buf, alliance.getCaptainObjectId());
        
        int i = 0;
        for (int group : alliance.getViceCaptainObjectIds())
        {
            writeD(buf,group);
            i++;
        }

        for (;i<4 ; i++)
        {
            writeD(buf,0);
        }

        LootGroupRules lootRules = this.alliance.getLootAllianceRules();
        LootRuleType lootruletype = lootRules.getLootRule();
        LootDistribution autodistribution = lootRules.getAutodistribution();

        writeD(buf, lootruletype.getId());
        writeD(buf, autodistribution.getId());
        writeD(buf, lootRules.getCommon_item_above());
        writeD(buf, lootRules.getSuperior_item_above());
        writeD(buf, lootRules.getHeroic_item_above());
        writeD(buf, lootRules.getFabled_item_above());
        writeD(buf, lootRules.getEthernal_item_above());
        writeD(buf, lootRules.getOver_ethernal());
        writeD(buf, lootRules.getOver_over_ethernal());
        
        writeC(buf, 0); //unk
        writeD(buf, 0); //unk

        for (i = 0; i < 4; i++)
        {
            writeD(buf, i);
            writeD(buf, 1000+i);
        }

        writeD(buf, 0); //System message ID
        writeS(buf, ""); //System message

        // TODO: League
/*
        if (alliance.getLeague() != null)
        {
            lgr = alliance.getLeague().getLootGroupRules();
            writeH(buf, alliance.getLeague().size());
            writeD(buf, lgr.getLootRule().getId()); //loot rule type - 0 freeforall, 1 roundrobin, 2 leader
            writeD(buf, lgr.getAutodistribution()); //autoDistribution - 0 or 1
            writeD(buf, lgr.getCommonItemAbove()); //this.common_item_above); - 0 normal 2 roll 3 bid
            writeD(buf, lgr.getSuperiorItemAbove()); //this.superior_item_above); - 0 normal 2 roll 3 bid
            writeD(buf, lgr.getHeroicItemAbove()); //this.heroic_item_above); - 0 normal 2 roll 3 bid
            writeD(buf, lgr.getFabledItemAbove()); //this.fabled_item_above); - 0 normal 2 roll 3 bid
            writeD(buf, lgr.getEthernalItemAbove()); //this.ethernal_item_above); - 0 normal 2 roll 3 bid
            writeD(buf, lgr.getOverEthernal()); //this.over_ethernal); - 0 normal 2 roll 3 bid
            writeD(buf, lgr.getOverOverEthernal()); //this.over_over_ethernal); - 0 normal 2 roll 3 bid
            i = 0;
            for (PlayerAlliance alli : alliance.getLeague().getMembers())
            {
                writeD(buf, i++);
                writeD(buf, alli.getCaptainObjectId());
                writeD(buf, alli.getMembers().size());
                writeS(buf, alli.getName());
            }
        }
*/
    }
}
