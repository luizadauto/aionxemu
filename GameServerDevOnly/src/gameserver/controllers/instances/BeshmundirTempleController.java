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

package gameserver.controllers.instances;

import gameserver.ai.events.Event;
import gameserver.controllers.NpcController;
import gameserver.dataholders.DataManager;
import gameserver.model.EmotionType;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.NpcTemplate;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import gameserver.services.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 *
 */
public class BeshmundirTempleController extends NpcController
{
    //private VisibleObject target = null;
    Npc npc = getOwner();

    @Override
    public void onDialogRequest(final Player player)
    {
        getOwner().getAi().handleEvent(Event.TALK);
        
        NpcTemplate npctemplate = DataManager.NPC_DATA.getNpcTemplate(getOwner().getNpcId());
        if (npctemplate.getNameId() == 354971)
        {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
            return;
        }		
        

        switch (getOwner().getNpcId())
        {
            case 730275:
            {
            ThreadPoolManager.getInstance().schedule(new Runnable()
            {
                @Override
                    public void run()
                    {
                        final int defaultUseTime = 3000;
                        PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),getOwner().getObjectId(), defaultUseTime, 1));
                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getOwner().getObjectId()), true);
                ThreadPoolManager.getInstance().schedule(new Runnable(){
                    @Override
                    public void run()
                    {
                        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getOwner().getObjectId()), true);
                        PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), 
                            getOwner().getObjectId(), defaultUseTime, 0));
                        PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 443));
                        ThreadPoolManager.getInstance().schedule(new Runnable(){
                            @Override
                                    public void run()
                                    {
                                    TeleportService.teleportTo(player, 300170000, 528.27496f, 1345.001f, 223.52919f, 14);
                                    }
                        }, 35000);
                    }
                }, defaultUseTime);
                    }
        }, 0);

                return;
            }
        }
    }

    @Override
    public void onDialogSelect(int dialogId, final Player player, int questId)
    {
        Npc npc = getOwner();
        //int targetObjectId = npc.getObjectId();

        if (dialogId == 10000 && (npc.getNpcId() == 799517))//Boatman
        {
            PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 448));
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 0));
            ThreadPoolManager.getInstance().schedule(new Runnable(){
                @Override
                        public void run()
                        {
                            TeleportService.teleportTo(player, 300170000, 958.45233f, 430.4892f, 219.80301f, 0);
                        }
            }, 23000);
                return;
        }
        else if (dialogId == 10000 && (npc.getNpcId() == 799518))//Boatman
        {
            PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 449));
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 0));
            ThreadPoolManager.getInstance().schedule(new Runnable(){
            @Override
                    public void run()
                    {
                        TeleportService.teleportTo(player, 300170000, 822.0199f, 465.1819f, 220.29918f, 0);
                    }
        }, 23000);

                return;
        }
        else if (dialogId == 10000 && (npc.getNpcId() == 799519))//Boatman
        {
            PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 450));
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 0));
            ThreadPoolManager.getInstance().schedule(new Runnable(){
            @Override
                    public void run()
                    {
                        TeleportService.teleportTo(player, 300170000, 777.1054f, 300.39005f, 219.89926f, 94);
                    }
        }, 23000);

                return;
        }
        else if (dialogId == 10000 && (npc.getNpcId() == 799520))//Boatman
        {
            PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 451));
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 0));
            ThreadPoolManager.getInstance().schedule(new Runnable(){
            @Override
                    public void run()
                    {
                        TeleportService.teleportTo(player, 300170000, 942.3092f, 270.91855f, 219.86185f, 86);
                    }
        }, 23000);

                return;
        }
    }
}