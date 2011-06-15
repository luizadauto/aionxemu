package gameserver.services;

import java.util.Date;

import com.aionemu.commons.database.dao.DAOManager;
import gameserver.configs.main.GSConfig;
import gameserver.dao.PurchaseLimitDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.templates.TradeListTemplate;
import gameserver.model.templates.TradeListTemplate.TradeTab;
import gameserver.model.templates.goods.GoodsList;
import gameserver.model.trade.TradeItem;
import gameserver.model.trade.TradeList;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.World;


/**
 * @author ginho1
 */
public class PurchaseLimitService
{

    private PurchaseLimitService()
    {
    }

    public static final PurchaseLimitService getInstance()
    {
        return SingletonHolder.instance;
    }

    public void load()
    {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                deleteAllPurchaseLimit();

                World.getInstance().doOnAllPlayers(new Executor<Player>(){
                    @Override
                    public boolean run(Player player)
                    {
                        player.getPurchaseLimit().reset();
                        return true;
                    }
                });
            }
        }, getNextRoundHour() * 1000, GSConfig.PURCHASE_LIMIT_RESTOCK_TIME * 60 * 60 * 1000);

    }

    @SuppressWarnings("deprecation")
    private int getNextRoundHour()
    {
        Date now = new Date();
        int minutes = now.getMinutes();
        int seconds = now.getSeconds();
        int totalElapsedSecs = (minutes * 60) + seconds;
        switch(now.getHours())
        {
            case 0:
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
            case 14:
            case 16:
            case 18:
            case 20:
            case 22:
                return (3600 - totalElapsedSecs) + 3600;
        }
        now = null;
        return (3600 - totalElapsedSecs);
    }

    public void savePurchaseLimit(Player player)
    {
        DAOManager.getDAO(PurchaseLimitDAO.class).savePurchaseLimit(player);
    }

    private void deleteAllPurchaseLimit()
    {
        DAOManager.getDAO(PurchaseLimitDAO.class).deleteAllPurchaseLimit();
    }

    public int getCountItem(int itemId)
    {
        return DAOManager.getDAO(PurchaseLimitDAO.class).loadCountItem(itemId);
    }

    public boolean addCache(AionObject obj, Player player, TradeList tradeList)
    {
        if(!GSConfig.ENABLE_PURCHASE_LIMIT)
            return true;
        
        Npc npc = (Npc)obj;
        
        boolean save = false;

        for(TradeItem tradeItem : tradeList.getTradeItems())
        {
            GoodsList.Item item = getItem(npc.getNpcId(), tradeItem.getItemId());

            int itemId = tradeItem.getItemId();
            int countItem = (int)tradeItem.getCount();

            if(item.getBuylimit() > 0)
            {
                save = true;

                if(item.getBuylimit() < (countItem + player.getPurchaseLimit().getItemLimitCount(itemId)))
                    return false;
            }

            if(item.getSelllimit() > 0)
            {
                save = true;

                if(item.getSelllimit() < (countItem + getCountItem(itemId)))
                    return false;
            }

            player.getPurchaseLimit().addItem(itemId, countItem);
        }

        if(save)
            PurchaseLimitService.getInstance().savePurchaseLimit(player);

        return true;
    }

    protected GoodsList.Item getItem(int sellerObjId, int itemId)
    {
        TradeListTemplate tlist = TradeService.getTradeListData().getTradeListTemplate(sellerObjId);

        for(TradeTab tradeTabl : tlist.getTradeTablist())
        {
            GoodsList goodsList = DataManager.GOODSLIST_DATA.getGoodsListById(tradeTabl.getId());

            if(goodsList != null && goodsList.getItemsList() != null)
            {
                for(GoodsList.Item item : goodsList.getItemsList())
                {
                    if(item.getId() == itemId)
                        return item;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder
    {
        protected static final PurchaseLimitService instance = new PurchaseLimitService();
    }
}
