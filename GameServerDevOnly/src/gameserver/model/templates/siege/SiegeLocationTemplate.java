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
package gameserver.model.templates.siege;

import gameserver.model.siege.SiegeType;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author Sarynth
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SiegeLocation")
public class SiegeLocationTemplate {
    @XmlAttribute(name = "id")
    protected int id;
    @XmlAttribute(name = "type")
    protected SiegeType type;
    @XmlAttribute(name = "world")
    protected int world;
    
    @XmlAttribute(name = "vulnerable_time")
    protected String vulnerableTime;

    
    @XmlAttribute(name = "vulnerability_duration")
    protected int vulnerabilityDuration;
    
    @XmlElement(name = "siege_reward")
    protected List<SiegeRewardTemplate> siegeRewards;

    @XmlElement(name = "defense_reward")
    protected DefenseReward defenseReward;
    
    @XmlAttribute(name = "artifact_cooldown")
    protected int artifactCooldown;
    
    /**
     * @return the artifactCooldown
     */
    public int getArtifactCooldown()
    {
        return artifactCooldown;
    }

    /**
     * @return the location id
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return the location type
     */
    public SiegeType getType() {
        return this.type;
    }

    /**
     * @return the world id
     */
    public int getWorldId() {
        return this.world;
    }

    public List<SiegeRewardTemplate> getSiegeRewards() {
        return this.siegeRewards;
    }

    public DefenseReward getDefenseReward()
    {
        return this.defenseReward;
    }

    public int getVulnerabilityDuration() {
        return vulnerabilityDuration;
    }
    
    public String getVulnerabilityTime() {
        return vulnerableTime;
    }

}
