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

package gameserver.network.aion.clientpackets;

import gameserver.configs.main.GSConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.TradeListTemplate;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.model.trade.TradeItem;
import gameserver.model.trade.TradeList;
import gameserver.model.trade.TradeListType;
import gameserver.model.trade.TradeRepurchaseList;
import gameserver.network.aion.AionClientPacket;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestCookie;
import gameserver.services.PrivateStoreService;
import gameserver.services.PurchaseLimitService;
import gameserver.services.RepurchaseService;
import gameserver.services.TradeService;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.i18n.CustomMessageId;
import gameserver.utils.i18n.LanguageHandler;
import gameserver.world.World;

import org.apache.log4j.Logger;

/**
 * @author orz modified by ATracer
 *         modified by Simple
 */
public class CM_BUY_ITEM extends AionClientPacket {
    private static final Logger log = Logger.getLogger(CM_BUY_ITEM.class);

    private int sellerObjId;
    private int unk1;
    private int amount;
    private int itemId = 0;
    private int itemSlot = 0;
    private int count;

    @SuppressWarnings("unused")
    private int                    unk2;
    private Player                player;
    private TradeList tradeList;
    private TradeRepurchaseList    repurchaseList;

    public CM_BUY_ITEM(int opcode) {
        super(opcode);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        sellerObjId = readD();
        if(sellerObjId == 0)
            return;

        unk1 = readH();
        amount = readH(); // total no of items
        player = getConnection().getActivePlayer();
        if (player == null)
            return;

        tradeList = new TradeList();
        tradeList.setSellerObjId(sellerObjId);

        if(GSConfig.ENABLE_REPURCHASE) {
            repurchaseList = new TradeRepurchaseList();
        }

        tradeList.setSellerObjId(sellerObjId);

        for (int i = 0; i < amount; i++) {
            int tmpInt1 = readD();
            if (unk1 == 0)
                itemSlot = tmpInt1;
            else
                itemId = tmpInt1;

            count = readD();
            unk2 = readD();

            // prevent exploit packets
            if (count < 1)
                continue;

            if (unk1 == 13 || unk1 == 14 || unk1 == 15) {
                tradeList.addBuyItem(itemId, count);
            }
            else if (unk1 == 1) {
                tradeList.addSellItem(itemId, count);
            }
            else if (unk1 == 0) {
                tradeList.addSellItem(itemSlot, count);
            }
            else if(unk1 == 2)
            {
                if(GSConfig.ENABLE_REPURCHASE)
                    repurchaseList.addBuyItemRepurchase(itemId, player);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        if (player == null)
            return;

        AionObject obj = World.getInstance().findAionObject(sellerObjId);
        TradeListTemplate tlist = null;
        Npc npc = null;
        Player targetPlayer = null;

        try {
            player.setTrading(true);
            switch (unk1) {
                case 0:
                    Player targetPlayer = (Player) obj;
                    targetPlayer.setTrading(true);
                    tradeList = PrivateStoreService.sellStoreItem(targetPlayer, player, tradeList);
                    break;

                case 1:
                    TradeService.performSellToShop(player, tradeList);
                    break;
                case 2:
                    if(GSConfig.ENABLE_REPURCHASE)
                        RepurchaseService.performBuyFromRepurchase(player, repurchaseList);
                    break;

                case 13:
                    if(PurchaseLimitService.getInstance().addCache(obj, player, tradeList))
                    {
                        Npc npc = (Npc) World.getInstance().findAionObject(sellerObjId);
                        TradeListTemplate tlist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
                        if(tlist.getType() == TradeListType.KINAH)
                            TradeService.performBuyFromShop(player, tradeList);
                        else
                            log.info("[CHEAT]Player: "+player.getName()+" abusing CM_BUY_ITEM!");
                    }
                    else
                        PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400353));
                    break;

                case 14:
                    npc = (Npc) World.getInstance().findAionObject(sellerObjId);
                    TradeListTemplate tlist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
                    if(tlist.getType() == TradeListType.ABYSS)
                        TradeService.performBuyFromAbyssShop(player, tradeList);
                    break;

                case 15:
                    npc = (Npc) World.getInstance().findAionObject(sellerObjId);
                    TradeListTemplate elist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
                    if(elist.getType() == TradeListType.EXTRA)
                        TradeService.performBuyWithExtraCurrency(player, tradeList);
                    break;

                default:
                    log.info(String.format("Unhandle shop action unk1: %d", unk1));
                    break;
            }
        }
        finally
        {
            if(targetPlayer != null)
                targetPlayer.setTrading(false);
            player.setTrading(false);
        }

        if (tradeList == null)
            return;

        VisibleObject visibleObject = null;
        if (obj instanceof VisibleObject)
            visibleObject = (VisibleObject) obj;

        for (TradeItem item : tradeList.getTradeItems()) {
            ItemTemplate itemTemplate = item.getItemTemplate();
            if (itemTemplate == null) {
                log.warn(LanguageHandler.translate(CustomMessageId.ERROR_TRADEITEM_TEMPATE_MISSING,
                    player.getObjectId(), item.getItemId()));
                continue;
            }
            QuestEngine.getInstance().onItemSellBuyEvent(new QuestCookie(visibleObject, player, itemTemplate.getItemQuestId(), 0),
                    itemTemplate.getTemplateId());
        }

    }

}
