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
package gameserver.model.templates;

import gameserver.model.trade.TradeListType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;


/**
 * @author orz
 */
@XmlRootElement(name = "tradelist_template")
@XmlAccessorType(XmlAccessType.NONE)
public class TradeListTemplate {
    /**
     * Npc Id.
     */
    @XmlAttribute(name = "npc_id", required = true)
    private int npcId;

    /**
     * Npc name.
     */
    @XmlAttribute(name = "name", required = true)
    private String name = "";

    /**
     * Number of twin instances [players will be balanced so every one could exp easy]
     */
    @XmlAttribute(name = "count", required = true)
    private int Count = 0;

    @XmlAttribute(name = "buy_rate")
    private float    buyRate = 1;
    
    @XmlAttribute(name = "sell_rate")
    private float    sellRate = 1;
    
    @XmlAttribute(name = "type")
    private TradeListType type = TradeListType.KINAH;

    @XmlAttribute(name = "category")
    private int category = 0;

    @XmlElement(name = "tradelist")
    protected List<TradeTab> tradeTablist;

    /**
     * @return List<TradeTab>
     */
    public List<TradeTab> getTradeTablist() {
        if (tradeTablist == null) {
            tradeTablist = new ArrayList<TradeTab>();
        }
        return this.tradeTablist;
    }

    public String getName() {
        return name;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getCount() {
        return Count;
    }

    public float getBuyRate()
    {
        return buyRate;
    }
    
    public float getSellRate()
    {
        return sellRate;
    }

    /**
     * @return the type of trade list
     */
    public TradeListType getType()
    {
        return type;
    }

    /**
     * @return the category
     */
    public int getCategory() {
        return category;
    }

    public boolean isAbyss() {
    	return false;
    }


    /**
     * <p>Java class for anonymous complex type.
     * <p/>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p/>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Tradelist")
    public static class TradeTab {

        @XmlAttribute
        protected int id;

        /**
         * Gets the value of the id property.
         */
        public int getId() {
            return id;
        }
    }
}
