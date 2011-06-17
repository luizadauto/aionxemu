/**
 * This file is part of alpha team <alpha-team.com>.
 *
 * alpha team is pryvate software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * alpha team is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with alpha team.  If not, see <http://www.gnu.org/licenses/>.
 */

package gameserver.controllers;


import gameserver.controllers.movement.ActionObserver;
import gameserver.controllers.movement.ActionObserver.ObserverType;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.GroupGate;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.services.TeleportService;
import gameserver.skill.model.Skill;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;

/**
 * @author LokiReborn
 */
public class GroupGateController extends NpcWithCreatorController
{
    
    @Override
    public void onDialogRequest(final Player player)
    {
        final GroupGate groupgate = (GroupGate)this.getOwner();
        
        if (MathUtil.getDistance(player, groupgate) > 10)
            return;
        
        //check race
        if (((Player)groupgate.getCreator()).getCommonData().getRace() != player.getCommonData().getRace())
            return;
        //check level
        if (player.getLevel() < 9)
            return;
        
        boolean isMember = false;
        
        if(player.getObjectId() == ((Player)groupgate.getCreator()).getObjectId()) isMember = true;
        
        if (player.isInGroup())
        {
            for(Player member : player.getPlayerGroup().getMembers())
            {
                if (member.getObjectId() == ((Player)groupgate.getCreator()).getObjectId()) {
                    isMember = true;
                    break;
                }
            }
        }
        
        if (isMember)
        {
            final RequestResponseHandler responseHandler = new RequestResponseHandler(groupgate) {
                
                @Override
                public void acceptRequest(Creature requester, Player responder)
                {
                    switch(groupgate.getNpcId())
                    {
                        case 749017:
                            TeleportService.teleportTo(responder, 110010000, 1, 1444.9f, 1577.2f, 572.9f, 0);
                            break;
                        case 749083:
                            TeleportService.teleportTo(responder, 120010000, 1, 1657.5f, 1398.7f, 194.7f, 0);
                            break;
                    }
                }
    
                @Override
                public void denyRequest(Creature requester, Player responder)
                {
                    // Nothing Happens
                }
            };
            
            boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, responseHandler);
            
            //if players moves or uses skill, request is denied
            final ActionObserver obSkill = new ActionObserver(ObserverType.SKILLUSE)
                {
                    @Override
                    public void skilluse(Skill skill)
                    {        
                        player.getResponseRequester().respond(SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, 0);
                    }
                };
            
            player.getObserveController().attach(obSkill);    
            player.getObserveController().attach(new ActionObserver(ObserverType.MOVE)
            {
                @Override
                public void moved()
                {        
                    if (obSkill != null)
                        player.getObserveController().removeObserver(obSkill);
                    
                    player.getResponseRequester().respond(SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, 0);
                }
            }
        );
            if (requested)
            {
                PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, player.getObjectId()));
            }
            
        }
        else 
        {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_MAGIC_PASSAGE);
        }
    }
}
