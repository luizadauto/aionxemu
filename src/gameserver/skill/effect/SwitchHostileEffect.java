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
import javax.xml.bind.annotation.XmlType; 

import gameserver.controllers.attack.AggroInfo;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.player.Player;
import gameserver.skill.model.Effect;
 
 
/** 
 *  
 * @author ViAl 
 * 
 */ 
 @XmlAccessorType(XmlAccessType.FIELD) 
 @XmlType(name = "SwitchHostileEffect") 
 public class SwitchHostileEffect extends EffectTemplate { 

 @Override 
 public void applyEffect(Effect effect)  
 { 
     Creature effected = effect.getEffected(); 
     Creature effector = effect.getEffector(); 
     if (((Player) effector).getSummon() != null) 
     { 
         int summonHate = 0;
         int playerHate = 0;
         for(AggroInfo al : effected.getAggroList().getList()) 
         { 
             if (al.getAttacker() == ((Player)effector).getSummon()) 
             { 
                 summonHate = al.getHate(); 
             }
             else if (al.getAttacker() == effector)
             { 
                 playerHate = al.getHate();
             }
         }
         //switch hate
         effected.getAggroList().stopHating(((Player)effector).getSummon());
         effected.getAggroList().stopHating(effector);
         effected.getAggroList().addHate(((Player)effector).getSummon(), playerHate);
         effected.getAggroList().addHate(effector, summonHate);
         
     } 
 } 

} 
