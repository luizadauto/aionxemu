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
package gameserver.skill.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.skill.model.Effect;
import gameserver.utils.PacketSendUtility;


/**
 * @author Sylar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XPBoostEffect")
public class XPBoostEffect extends EffectTemplate
{
    @XmlAttribute
    protected int percent;
    
    @Override
    public void applyEffect(Effect effect)
    {
        effect.addToEffectedController();
    }
    @Override
    public void calculate(Effect effect)
    {
        effect.addSucessEffect(this);
    }
    @Override
    public void startEffect(Effect effect)
    {
        Logger.getLogger(XPBoostEffect.class).info("Starting " + percent + "% XP Boost");
        if(!(effect.getEffected() instanceof Player))
        {
            Logger.getLogger(XPBoostEffect.class).error("Effected creature of XPBoostEffect is not a player ! Aborting.");
            return;
        }
        Player player = (Player)effect.getEffected();
        player.setXpBoost(percent);
    }

    @Override
    public void endEffect(Effect effect)
    {
        Logger.getLogger(XPBoostEffect.class).info("Ending XP Boost");
        if(!(effect.getEffected() instanceof Player))
        {
            Logger.getLogger(XPBoostEffect.class).error("Effected creature of XPBoostEffect is not a player ! Aborting.");
            return;
        }
        Player player = (Player)effect.getEffected();
        player.setXpBoost(0);
        // STR_MSG_DELETE_CASH_XPBOOST_BY_TIMEOUT
        PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1390246));
    }
}
