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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import com.aionemu.commons.database.dao.DAOManager;
import gameserver.ai.AI;
import gameserver.ai.events.Event;
import gameserver.ai.npcai.DummyAi;
import gameserver.configs.main.LegionConfig;
import gameserver.controllers.attack.AttackStatus;
import gameserver.dao.SpawnDAO;
import gameserver.dataholders.DataManager;
import gameserver.model.EmotionType;
import gameserver.model.ShoutEventType;
import gameserver.model.TaskId;
import gameserver.model.alliance.PlayerAlliance;
import gameserver.model.alliance.PlayerAllianceMember;
import gameserver.model.gameobjects.AionObject;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.QuestStateList;
import gameserver.model.gameobjects.player.RequestResponseHandler;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.group.PlayerGroup;
import gameserver.model.templates.TradeListTemplate;
import gameserver.model.templates.quest.NpcQuestData;
import gameserver.model.templates.teleport.TelelocationTemplate;
import gameserver.model.templates.teleport.TeleportLocation;
import gameserver.model.templates.teleport.TeleporterTemplate;
import gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_LOOKATOBJECT;
import gameserver.network.aion.serverpackets.SM_PET;
import gameserver.network.aion.serverpackets.SM_PLASTIC_SURGERY;
import gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import gameserver.network.aion.serverpackets.SM_REPURCHASE;
import gameserver.network.aion.serverpackets.SM_SELL_ITEM;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_TRADELIST;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.restrictions.RestrictionsManager;
import gameserver.services.CraftSkillUpdateService;
import gameserver.services.CubeExpandService;
import gameserver.services.GuildService;
import gameserver.services.LegionService;
import gameserver.services.NpcShoutsService;
import gameserver.services.RespawnService;
import gameserver.services.SiegeService;
import gameserver.services.TeleportService;
import gameserver.services.TradeService;
import gameserver.services.WarehouseService;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.exceptionhandlers.exception_enums;
import gameserver.world.World;
import gameserver.world.WorldType;


/**
 * This class is for controlling Npc's
 * 
 * @author -Nemesiss-, ATracer (2009-09-29), Sarynth
 */
public class NpcController extends CreatureController<Npc>
{
    private static final Logger    log    = Logger.getLogger(NpcController.class);

    @Override
    public void notSee(VisibleObject object, boolean isOutOfRange)
    {
        super.notSee(object, isOutOfRange);
        if(object instanceof Creature)
            getOwner().getAggroList().remove((Creature) object);
        if(object instanceof Player || object instanceof Summon)
            getOwner().getAi().handleEvent(Event.NOT_SEE_PLAYER);            
    }

    @Override
    public void see(VisibleObject object)
    {
        super.see(object);
        Npc owner = getOwner();
        owner.getAi().handleEvent(Event.SEE_CREATURE);
        if(object instanceof Player)
        {
            owner.getAi().handleEvent(Event.SEE_PLAYER);        
            if(owner.hasWalkRoutes())
                PacketSendUtility.sendPacket((Player) object, new SM_EMOTION(owner, EmotionType.START_EMOTE2));
            NpcShoutsService.getInstance().handleEvent(owner, (Player)object, ShoutEventType.SEEUSER);
        }    
        else if(object instanceof Summon)
        {
            owner.getAi().handleEvent(Event.SEE_PLAYER);
        }
    }

    @Override
    public void onRespawn()
    {
        super.onRespawn();

        cancelTask(TaskId.DECAY);

        Npc owner = getOwner();

        if (owner != null && owner.isCustom())
        {
            DAOManager.getDAO(SpawnDAO.class).setSpawned(owner.getSpawn().getSpawnId(),owner.getObjectId(), true);
        }

        //set state from npc templates
        if(owner.getObjectTemplate().getState() != 0)
            owner.setState(owner.getObjectTemplate().getState());
        else
            owner.setState(CreatureState.NPC_IDLE);

        owner.getLifeStats().setCurrentHpPercent(100);
        owner.getAi().handleEvent(Event.RESPAWNED);

        if(owner.getSpawn().getNpcFlyState() != 0)
        {
            owner.setState(CreatureState.FLYING);
        }
        
    }

    public void onDespawn(boolean forced)
    {
        if(forced)
            cancelTask(TaskId.DECAY);

        Npc owner = getOwner();

        if(owner == null || !owner.isSpawned())
            return;

        if (owner != null && owner.isCustom())
        {
            DAOManager.getDAO(SpawnDAO.class).setSpawned(owner.getSpawn().getSpawnId(), owner.getSpawn().isNoRespawn(1)?-1:owner.getObjectId(), false);
        }

        owner.getAi().handleEvent(Event.DESPAWN);
        
        if(owner.getTarget() != null && owner.getTarget() instanceof Creature)
        {
            NpcShoutsService.getInstance().handleEvent(owner, (Creature)owner.getTarget(), ShoutEventType.DESPAWN);
        }
        
        World.getInstance().despawn(owner);
    }

    @Override
    public void onDie(Creature lastAttacker)
    {
        super.onDie(lastAttacker);

        Npc owner = getOwner();

        addTask(TaskId.DECAY, RespawnService.scheduleDecayTask(this.getOwner()));
        scheduleRespawn();

        PacketSendUtility.broadcastPacket(owner,
            new SM_EMOTION(owner, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()));

        // Monster Controller overrides this method.
        this.doReward();
        
        if((owner.getWorldType() == WorldType.ABYSS || owner.getWorldType() == WorldType.BALAUREA) && lastAttacker instanceof Player)
        {
            int relatedLocId = SiegeService.getInstance().getSiegeNpcLocation(owner.getObjectId());
            if(relatedLocId > 0)
            {
                AionObject winner = getOwner().getAggroList().getMostDamage();
                if(winner != null)
                {
                    ArrayList<Player> toReward = new ArrayList<Player>();
                    if (winner instanceof PlayerAlliance)
                    {
                        PlayerAlliance alliance = (PlayerAlliance)winner;
                        for(PlayerAllianceMember m : alliance.getMembers())
                        {
                            if(m.getPlayer().getWorldType() == WorldType.ABYSS || m.getPlayer().getWorldType() == WorldType.BALAUREA)
                                toReward.add(m.getPlayer());
                        }
                    }
                    else if (winner instanceof PlayerGroup)
                    {
                        PlayerGroup group = (PlayerGroup)winner;
                        for(Player p : group.getMembers())
                        {
                            if(p.getWorldType() == WorldType.ABYSS || p.getWorldType() == WorldType.BALAUREA)
                                toReward.add(p);
                        }
                    }
                    else if(winner instanceof Player)
                    {
                        Player p = (Player)winner;
                        if(p.isInGroup() && p.getPlayerGroup() != null)
                        {
                            for(Player pl : p.getPlayerGroup().getMembers())
                            {
                                if(pl.getWorldType() == WorldType.ABYSS || pl.getWorldType() == WorldType.BALAUREA)
                                    toReward.add(pl);
                            }
                        }
                        else
                        {
                            toReward.add(p);
                        }
                    }
                    for(Player trp : toReward)
                    {
                        if(!trp.isOnline() || trp.getLifeStats().isAlreadyDead())
                            continue;
                        // Do some AP reward and record it
                        trp.getCommonData().addAp(10);
                        SiegeService.getInstance().recordAPGain(relatedLocId, trp.getObjectId(), 10);
                    }
                }
            }
        }
        
        owner.getAi().handleEvent(Event.DIED);
        
        if(lastAttacker != null && lastAttacker instanceof Creature)
        {
            NpcShoutsService.getInstance().handleEvent(owner, (Creature)lastAttacker, ShoutEventType.DIE);
        }

        // deselect target at the end
        owner.setTarget(null);
        PacketSendUtility.broadcastPacket(owner, new SM_LOOKATOBJECT(owner));
    }

    @Override
    public Npc getOwner()
    {
        return (Npc) super.getOwner();
    }

    @Override
    public void onDialogRequest(Player player)
    {
        if(player.isTrading())
        {
            log.warn("[AUDIT] Trying to use trade exploit: " + player.getName());
            return;
        }

        getOwner().getAi().handleEvent(Event.TALK);

        if(QuestEngine.getInstance().onDialog(new QuestCookie(getOwner(), player, 0, -1)))
            return;
        
        // Zephyr Deliveryman
        if(getOwner().getObjectId() == player.getZephyrObjectId())
        {
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 18));
            return;
        }

        int titleId = getOwner().getObjectTemplate().getTitleId();
        if
        (
            (
                // title ids of npcs
                titleId == 315018
                || titleId == 350474 
                || titleId == 350473 
                || titleId == 350212 
                || titleId == 350304 
                || titleId == 350305
                || titleId == 370000
                || titleId == 370003
                // aerolinks
                || (getOwner().getNpcId() >= exception_enums.NPC_TELEPORT_AERO_RANGE_I && getOwner().getNpcId() <= exception_enums.NPC_TELEPORT_AERO_RANGE_II && getOwner().getNpcId() != exception_enums.NPC_TELEPORT_REGULAR_ASMO_I)
                // balaurea gates
                || (getOwner().getNpcId() >= 730296 && getOwner().getNpcId() <= 730303)
            )
        )
        {            
            NpcQuestData npcQD = QuestEngine.getInstance().getNpcQuestData(getOwner().getNpcId());
            QuestStateList list = player.getQuestStateList();
            List<Integer> events = npcQD.getOnTalkEvent();
            boolean hasQuestFromNpc = false;
            for(int e : events)
            {
                QuestState qs = list.getQuestState(e);
                if(qs != null && qs.getStatus() != QuestStatus.COMPLETE)
                {
                    hasQuestFromNpc = true;
                    break;
                }
                else
                {
                    continue;
                }
            }
            if(hasQuestFromNpc)
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
            else
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
            
        }
        else
            PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getOwner().getObjectId(), 10));
    }

    /**
     * This method should be called to make forced despawn of NPC and delete it from the world
     */
    public void onDelete()
    {
        if(getOwner().isInWorld())
        {
            this.getOwner().getAi().clearDesires();
            this.onDespawn(true);
            this.delete();
        }
    }

    /**
     * Handle dialog
     */
    @Override
    public void onDialogSelect(int dialogId, final Player player, int questId)
    {

        Npc npc = getOwner();
        int targetObjectId = npc.getObjectId();

        if(QuestEngine.getInstance().onDialog(new QuestCookie(npc, player, questId, dialogId)))
            return;

        //Eracus Temple, Check if the player did the quest to enter. Teleport: Meneus
        if (dialogId == 30 && (npc.getNpcId() == 203981))
        {
            int completedquestid = 0;
            completedquestid = 1346;
            QuestState qstel = player.getQuestStateList().getQuestState(completedquestid);
            if (qstel == null || qstel.getStatus() != QuestStatus.COMPLETE)
            {
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 27));
                return;
            }
            TeleportService.teleportTo(player, 210020000, 1, 439.3f, 422.2f, 274.3f, 0);
                return;
        }

        switch(dialogId)
        {
            case 2:
                // buy
                TradeListTemplate tradeList = TradeService.getTradeListData().getTradeListTemplate(npc.getNpcId());
                if (tradeList == null)
                    log.warn("Missing tradelist for NPC : " + npc.getNpcId());
                else
                    PacketSendUtility.sendPacket(player, new SM_TRADELIST(npc, tradeList, player.getPrices().getVendorBuyModifier()));
                break;
            case 3:
                // sell
                PacketSendUtility.sendPacket(player, new SM_SELL_ITEM(targetObjectId, player.getPrices().getVendorSellModifier(player.getCommonData().getRace())));
                break;
            case 4:
                // stigma
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 1));
                break;
            case 5:
                // create legion
                if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
                {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 2));
                }
                else
                {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.LEGION_CREATE_TOO_FAR_FROM_NPC());
                }
                break;
            case 6:
                // disband legion
                if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
                {
                    LegionService.getInstance().requestDisbandLegion(npc, player);
                }
                else
                {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_TOO_FAR_FROM_NPC());
                }
                break;
            case 7:
                // recreate legion
                if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
                {
                    LegionService.getInstance().recreateLegion(npc, player);
                }
                else
                {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.LEGION_DISPERSE_TOO_FAR_FROM_NPC());
                }
                break;
            case 20:
                // warehouse
                if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
                {
                    if(!RestrictionsManager.canUseWarehouse(player))
                        return;
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 26));
                    WarehouseService.sendWarehouseInfo(player, true);
                }
                break;
            case 25:
                // TODO hotfix to prevent opening the legion wh when a quest returns false.
                break;
            case 27:
                // trade broker
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 13));
                break;
            case 29:
                // soul healing
                final long expLost = player.getCommonData().getExpRecoverable();
                if (expLost == 0)
                player.getEffectController().removeEffect(8291);
                final double factor = (expLost < 1000000 ?
                    0.25 - (0.00000015 * expLost) 
                    : 0.1);
                final int price = (int) (expLost * factor);

                RequestResponseHandler responseHandler = new RequestResponseHandler(npc){
                    @Override
                    public void acceptRequest(Creature requester, Player responder)
                    {
                        if(player.getInventory().getKinahItem().getItemCount() >= price)
                        {
                            if(!player.getInventory().decreaseKinah(price))
                                return;
                            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.EXP(String.valueOf(expLost)));
                            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.SOUL_HEALED());                            
                            player.getCommonData().resetRecoverableExp();                            
                            player.getEffectController().removeEffect(8291);
                        }
                        else
                        {
                            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.NOT_ENOUGH_KINAH(price));
                        }
                    }

                    @Override
                    public void denyRequest(Creature requester, Player responder)
                    {
                        // no message
                    }
                };
                if(player.getCommonData().getExpRecoverable() > 0)
                {
                    boolean result = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_SOUL_HEALING,
                        responseHandler);
                    if(result)
                    {
                        PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(
                            SM_QUESTION_WINDOW.STR_SOUL_HEALING, 0, String.valueOf(price)
                        ));
                    }
                }
                else
                {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DONT_HAVE_RECOVERED_EXP());
                }
                break;
            case 30:
                // arena teleporter
                switch(npc.getNpcId())
                {
                    case exception_enums.NPC_ARENA_TELEPORT_ASMO_IN:
                        player.setInArena(true);
                        TeleportService.teleportTo(player, 120010000, 1, 984f, 1543f, 222.1f, 0);
                        break;
                    case exception_enums.NPC_ARENA_TELEPORT_ELYOS_IN:
                        player.setInArena(true);
                        TeleportService.teleportTo(player, 110010000, 1, 1462.5f, 1326.1f, 564.1f, 0);
                        break;
                    case exception_enums.NPC_AREA_TELEPORT_ERACUS_IN:
                        // TODO : move me to portal controller so I can be quest restricted
                        TeleportService.teleportTo(player, 210020000, 1, 439.3f, 422.2f, 274.3f, 0);
                        break;
                }
                break;
            case 31:
                // arena teleporter
                switch(npc.getNpcId())
                {
                    case exception_enums.NPC_ARENA_TELEPORT_ASMO_OUT:
                        player.setInArena(true);
                        TeleportService.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f, 0);
                        break;
                    case exception_enums.NPC_ARENA_TELEPORT_ELYOS_OUT:
                        player.setInArena(true);
                        TeleportService.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f, 21);
                        break;
                    case exception_enums.NPC_AREA_TELEPORT_ERACUS_OUT:
                        TeleportService.teleportTo(player, 210020000, 1, 446.2f, 431.1f, 274.5f, 0);
                        break;
                }
                break;
            case 35:
                // Godstone socketing
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 21));
                break;
            case 36:
                // remove mana stone
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 20));
                break;
            case 37:
                // modify appearance
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 19));
                break;
            case 38:
                // flight and teleport
                TeleportService.showMap(player, targetObjectId, npc.getNpcId());
                break;
            case 39:
                // improve extraction
            case 40:
                // learn tailoring armor smithing etc...
                CraftSkillUpdateService.getInstance().learnSkill(player, npc);
                break;
            case 41:
                // expand cube
                CubeExpandService.expandCube(player, npc);
                break;
            case 42:
                // expand warehouse
                WarehouseService.expandWarehouse(player, npc);
                break;
            case 47:
                // legion warehouse
                if(LegionConfig.LEGION_WAREHOUSE)
                    if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
                        LegionService.getInstance().openLegionWarehouse(player);
                break;
            case 50:
                // WTF??? Quest dialog packet
                break;
            case 52:
                if(MathUtil.isInRange(npc, player, 10)) // avoiding exploit with sending fake dialog_select packet
                {
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 28));
                }
                break;
            case 53:
                // coin reward
                QuestEngine.getInstance().onDialog(new QuestCookie(npc, player, questId, 25));
                break;
            case 55:
            case 56:
                byte changesex = 0; //0 plastic surgery, 1 gender switch
                byte check_ticket = 2; // 2 no ticket, 1 have ticket
                if (dialogId == 56)
                {
                    //Gender Switch
                    changesex = 1;
                    if (player.getInventory().getItemCountByItemId(169660000) > 0 || player.getInventory().getItemCountByItemId(169660001) > 0)
                        check_ticket = 1;
                }
                else
                {
                    //Plastic Surgery
                    if (player.getInventory().getItemCountByItemId(169650000) > 0 || player.getInventory().getItemCountByItemId(169650001) > 0)
                        check_ticket = 1;
                }
                PacketSendUtility.sendPacket(player, new SM_PLASTIC_SURGERY(player,check_ticket, changesex));
                player.setEditMode(true);
                break;
            case 60:
                // armsfusion
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 29));
                break;
            case 61:
                // armsbreaking
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, 30));
                break;
            case 62:
                // join guild
                GuildService.getInstance().enterGuild(player, npc);
                break;
            case 63:
                // leave guild
                GuildService.getInstance().exitGuild(player, npc);
                break;
            case 64:
                // repurchase
                PacketSendUtility.sendPacket(player, new SM_REPURCHASE(npc, player));
                break;
            case 65:
                // adopt pet
                PacketSendUtility.sendPacket(player, new SM_PET(6));
                break;
            case 66:
                // surrender pet
                PacketSendUtility.sendPacket(player, new SM_PET(7));
                break;
            case 10000:
                // generic npc reply (most are teleporters)
                TeleporterTemplate template = DataManager.TELEPORTER_DATA.getTeleporterTemplate(npc.getNpcId());
                if(template != null)
                {
                    TeleportLocation loc = template.getTeleLocIdData().getTelelocations().get(0);
                    if(loc != null)
                    {

                        if( template.getRace() != null && !player.getCommonData().getRace().equals(template.getRace()) )
                        {
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MOVE_PORTAL_ERROR_INVALID_RACE);
                                return;
                        }
                        
                        if(!player.getInventory().decreaseKinah(loc.getPrice()))
                            return;
                        
                        TelelocationTemplate tlt = DataManager.TELELOCATION_DATA.getTelelocationTemplate(loc.getLocId());
                        TeleportService.teleportTo(player, tlt.getMapId(), tlt.getX(), tlt.getY(), tlt.getZ(), 1000);
                    }
                }
                else if(getOwner().getNpcId() >= 730296 && getOwner().getNpcId() <= 730303){
                    double radian = Math.toRadians(MathUtil.convertHeadingToDegree(player.getHeading()));
                    int worldId = getOwner().getWorldId();
                    float x = (float)(player.getX() + (10 * Math.cos(radian)));
                    float y = (float)(player.getY() + (10 * Math.sin(radian)));
                    float z = player.getZ() + 0.2f;
                    TeleportService.teleportTo(player, worldId, x, y, z, 1);
                }
                break;
            default:
                if(questId > 0)
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId, questId));
                else
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(targetObjectId, dialogId));
                break;
        }
    }

    @Override
    public void onAttack(final Creature creature, int skillId, TYPE type, int damage, int logId, AttackStatus status, boolean notifyAttackedObservers, boolean sendPacket)
    {
        if(getOwner().getLifeStats().isAlreadyDead())
            return;

        super.onAttack(creature, skillId, type, damage, logId, status, notifyAttackedObservers, sendPacket);

        Npc npc = getOwner();

        Creature actingCreature = creature.getActingCreature();
        if(actingCreature instanceof Player)
            if(QuestEngine.getInstance().onAttack(new QuestCookie(npc, (Player) actingCreature, 0, 0)))
                return;

        AI<?> ai = npc.getAi();
        if(ai instanceof DummyAi)
        {
            log.warn("CHECKPOINT: npc attacked without ai " + npc.getObjectTemplate().getTemplateId());
            return;
        }
        
        getOwner().getKnownList().doOnAllNpcs(new Executor<Npc>(){
            @Override
            public boolean run(Npc tmp)
            {
                if (getOwner().isSupportFrom(tmp) && MathUtil.isInRange(getOwner(), tmp, 10))
                {
                    if (getOwner().getWorldId() != 300100000 && (Math.abs(getOwner().getZ() - tmp.getZ()) < 3))
                        tmp.getAggroList().addHate(creature, 1);
                }
                return true;
            }
        }, true);
        
        NpcShoutsService.getInstance().handleEvent(npc, creature, ShoutEventType.WOUNDED);
        
        if(npc.getLifeStats().getHpPercentage() <= 35)
        {
            NpcShoutsService.getInstance().handleEvent(npc, creature, ShoutEventType.ATK);
        }
        
    }

    @Override
    public void onStartMove()
    {
        super.onStartMove();
    }

    @Override
    public void onMove()
    {
        super.onMove();
    }

    @Override
    public void onStopMove()
    {
        super.onStopMove();
    }

    /**
     * Schedule respawn of npc
     * In instances - no npc respawn
     */
    public void scheduleRespawn()
    {    
        if(getOwner().isInInstance())
            return;

        int instanceId = getOwner().getInstanceId();
        if(!getOwner().getSpawn().isNoRespawn(instanceId))
        {
            Future<?> respawnTask = RespawnService.scheduleRespawnTask(getOwner());
            addTask(TaskId.RESPAWN, respawnTask);
        }
    }
    public void onCreatureDie(Creature lastAttacker)
    {
        super.onDie(lastAttacker);
    }
}
