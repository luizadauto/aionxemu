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
package gameserver.services;

import com.aionemu.commons.utils.Rnd;
import gameserver.configs.main.CustomConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.DescriptionId;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.StaticObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.RewardType;
import gameserver.model.templates.item.ItemTemplate;
import gameserver.model.templates.recipe.ComboProduct;
import gameserver.model.templates.recipe.Component;
import gameserver.model.templates.recipe.RecipeTemplate;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.skill.model.SkillTemplate;
import gameserver.skill.task.CraftingTask;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.world.World;

/**
 * @author MrPoke, sphinx, PZIKO333
 */
public class CraftService {
    private static final Logger log = Logger.getLogger(CraftService.class);

    /**
     * 
     * @param player
     * @param recipetemplate
     */
    public static ItemTemplate finishCrafting(Player player, RecipeTemplate recipetemplate, int itemId)
    {
        boolean critical = false;
        boolean getReward = false;
        int critItemId = 0;
        
        if(recipetemplate.getComboProduct().size() == 0)
            getReward = true;
        else
            critical = Rnd.get(100) <= CustomConfig.CRITICAL_CRAFTING_SUCCESS;
        
        if(critical)
        {
            boolean getNext = false;
            
            for(ComboProduct comboProduct: recipetemplate.getComboProduct())
            {
                if(itemId == recipetemplate.getProductid() && critItemId == 0)
                {
                    critItemId = comboProduct.getItemid();
                    break;
                }
                else if(itemId == comboProduct.getItemid())
                    getNext = true;
                else if(getNext)
                {
                    critItemId = comboProduct.getItemid();
                    break;
                }
            }
            
            if(critItemId == 0)
                getReward = true;
        }
        else
            getReward = true;
        
        if(critical && !getReward)
            return ItemService.getItemTemplate(critItemId);
        else
        {
            int productItemId = itemId;
            int skillId = recipetemplate.getSkillid();
            
            if(recipetemplate.getMaxProductionCount() == 1)
                player.getRecipeList().deleteRecipe(player, recipetemplate.getId());
            
            if(skillId == 40009)
            {
                ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), player.getName(), 0, 0);
                PacketSendUtility.sendPacket(player,SM_SYSTEM_MESSAGE.STR_COMBINE_SUCCESS(new DescriptionId(ItemService.getItemTemplate(productItemId).getNameId())));
            }
            else if(productItemId != 0 && skillId != 40009)
            {
                int skillPoint = recipetemplate.getSkillpoint();
                SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
                
                ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), player.getName(), 0, 0);
                
                if(skillPoint + 40 > player.getSkillList().getSkillLevel(skillId))
                {
                    int xpReward = (int)((0.0144*skillPoint*skillPoint+3.5*skillPoint+270)*player.getRates().getCraftingLvlRate());
                    if(player.getXpBoost() > 0)
                        xpReward = xpReward * ((player.getXpBoost() / 100) + 1);
    
                    if(player.getSkillList().addSkillXp(player, skillId, xpReward))
                    {
                        player.getCommonData().addExp((int)(xpReward*player.getRates().getCraftingXPRate()/player.getRates().getCraftingLvlRate()));
                        PacketSendUtility.sendPacket(player ,SM_SYSTEM_MESSAGE.STR_CRAFT_SUCCESS_GETEXP);
                    }
                    else
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(skillTemplate.getNameId())));
                }
                else
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(skillTemplate.getNameId())));
            }
            player.setCraftingTask(null);
            return null;
        }
    }

    /**
     * @param player
     * @param targetTemplateId
     * @param recipeId
     * @param targetObjId
     */
    public static void startComboCrafting(Player player, int recipeId, int targetObjId, int comboStep) {
        RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);

        if (recipeTemplate != null) {

            int skillId = recipeTemplate.getSkillid();
            AionObject target = World.getInstance().findAionObject(targetObjId);

            ItemTemplate itemTemplate = ItemService.getItemTemplate(recipeTemplate.getComboProduct(comboStep));
            if (itemTemplate == null)
                return;

            int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();

            player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, itemTemplate, recipeId, skillLvlDiff, comboStep));
            player.getCraftingTask().combo();
        }
    }

    /**
     * 
     * @param player
     * @param recipeId
     * @param targetObjId
     */
    public static void startCrafting(Player player, int recipeId, int targetObjId)
        if (player.getCraftingTask() != null && player.getCraftingTask().isInProgress())
            return;

        RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);

        if(recipeTemplate == null)
        {
            log.warn(String.format("recipeTemplate with id %d not found", recipeId));
            return;
        }

        // check for pre-usage crafting -----------------------------------------------------
        int skillId = recipeTemplate.getSkillid();
        AionObject target = World.getInstance().findAionObject(targetObjId);

        //morphing dont need static object/npc to use
        if ((skillId != 40009) && (target == null || !(target instanceof StaticObject))) {
            log.info("[AUDIT] Player " + player.getName() + " tried to craft incorrect target.");
            return;
        }
        //check distance to avoid sending of fake packets
        if((skillId != 40009) && !MathUtil.isIn3dRange(player, (StaticObject)target, 10))
        {
            log.info("[AUDIT] Player " + player.getName() + " sending fake packet CM_CRAFT.");
            return;
        }
        if (recipeTemplate.getDp() != null && (player.getCommonData().getDp() < recipeTemplate.getDp())) {
            log.info("[AUDIT] Player " + player.getName() + " modded her/his client.");
            return;
        }

        if (player.getInventory().isFull()) {
            if (player.getInventory().getItemCountByItemId(recipeTemplate.getProductid()) == 0) {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.COMBINE_INVENTORY_IS_FULL);
                return;
            }
        }

        for (Component component : recipeTemplate.getComponent()) {
            if (player.getInventory().getItemCountByItemId(component.getItemid()) < component.getQuantity()) {
                log.info("[AUDIT] Player " + player.getName() + " modded her/his client.");
                return;
            }
        }
        // ---------------------------------------------------------------------------------

        //craft item template --------------------------------------------------------------
        ItemTemplate itemTemplate = ItemService.getItemTemplate(recipeTemplate.getProductid());
        if(itemTemplate == null)
        {
            log.warn(String.format("itemTemplate with id %d not found", recipeTemplate.getProductid()));
            return;
        }

        if (recipeTemplate.getDp() != null)
            player.getCommonData().addDp(-recipeTemplate.getDp());

        for (Component component : recipeTemplate.getComponent()) {
            if(!player.getInventory().removeFromBagByItemId(component.getItemid(), component.getQuantity()))
                return;
        }
        // ----------------------------------------------------------------------------------

        // start crafting
        int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();
        int morph = 99999;

        if (skillId == 40009) {
            player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, itemTemplate, recipeId, morph, -1));
        } else {
            player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, itemTemplate, recipeId, skillLvlDiff, -1));
        }
        player.getCraftingTask().start();
    }
}
