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

import gameserver.configs.main.CustomConfig;
import gameserver.configs.administration.AdminConfig;
import gameserver.configs.main.GSConfig;
import gameserver.configs.main.GeoDataConfig;
import gameserver.controllers.SummonController.UnsummonType;
import gameserver.controllers.attack.AttackResult;
import gameserver.controllers.attack.AttackStatus;
import gameserver.controllers.attack.AttackUtil;
import gameserver.controllers.instances.FortressInstanceTimer;
import gameserver.controllers.movement.MovementType;
import gameserver.dataholders.DataManager;
import gameserver.model.EmotionType;
import gameserver.model.ShoutEventType;
import gameserver.model.TaskId;
import gameserver.model.alliance.PlayerAllianceEvent;
import gameserver.model.gameobjects.Creature;
import gameserver.model.gameobjects.Gatherable;
import gameserver.model.gameobjects.GroupGate;
import gameserver.model.gameobjects.Kisk;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.StaticObject;
import gameserver.model.gameobjects.Summon;
import gameserver.model.gameobjects.VisibleObject;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.gameobjects.player.SkillListEntry;
import gameserver.model.gameobjects.state.CreatureState;
import gameserver.model.gameobjects.state.CreatureVisualState;
import gameserver.model.gameobjects.stats.PlayerGameStats;
import gameserver.model.gameobjects.stats.StatEnum;
import gameserver.model.gameobjects.stats.modifiers.Executor;
import gameserver.model.group.GroupEvent;
import gameserver.model.templates.quest.QuestItems;
import gameserver.model.templates.stats.PlayerStatsTemplate;
import gameserver.network.aion.serverpackets.SM_DELETE;
import gameserver.network.aion.serverpackets.SM_DIE;
import gameserver.network.aion.serverpackets.SM_EMOTION;
import gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import gameserver.network.aion.serverpackets.SM_NPC_INFO;
import gameserver.network.aion.serverpackets.SM_PET;
import gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import gameserver.network.aion.serverpackets.SM_STATS_INFO;
import gameserver.network.aion.serverpackets.SM_SUMMON_PANEL;
import gameserver.network.aion.serverpackets.SM_SUMMON_UPDATE;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import gameserver.quest.QuestEngine;
import gameserver.quest.model.QuestCookie;
import gameserver.restrictions.RestrictionsManager;
import gameserver.services.AllianceService;
import gameserver.services.ArenaService;
import gameserver.services.ClassChangeService;
import gameserver.services.DredgionInstanceService;
import gameserver.services.DuelService;
import gameserver.services.InstanceService;
import gameserver.services.ItemService;
import gameserver.services.LegionService;
import gameserver.services.NpcShoutsService;
import gameserver.services.PvpService;
import gameserver.services.QuestService;
import gameserver.services.SkillLearnService;
import gameserver.services.ToyPetService;
import gameserver.services.ZoneService;
import gameserver.services.ZoneService.ZoneUpdateMode;
import gameserver.skill.SkillEngine;
import gameserver.skill.model.Effect;
import gameserver.skill.model.HealType;
import gameserver.skill.model.Skill;
import gameserver.skill.model.Skill.SkillType;
import gameserver.skill.properties.FirstTargetAttribute;
import gameserver.spawn.SpawnEngine;
import gameserver.task.tasks.PacketBroadcaster.BroadcastMode;
import gameserver.utils.MathUtil;
import gameserver.utils.PacketSendUtility;
import gameserver.utils.ThreadPoolManager;
import gameserver.world.Executor;
import gameserver.world.World;
import gameserver.world.WorldMapInstance;
import gameserver.world.WorldType;
import gameserver.world.zone.ZoneInstance;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * This class is for controlling players.
 *
 * @author -Nemesiss-, ATracer (2009-09-29), xavier, Sarynth
 * @author RotO (Attack-speed hack protection)
 */
public class PlayerController extends CreatureController<Player> {
    private boolean isInShutdownProgress = false;

    private boolean canAutoRevive = true;

    /**
     * Zone update mask
     */
    private volatile byte zoneUpdateMask;

    private long lastAttackMilis = 0;
    private long lastSkillMilis = 0;
    private int lastSkillAnimationTime = 0;
    
    private static Logger    log    = Logger.getLogger(PlayerController.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void see(VisibleObject object) {
        super.see(object);
        if (object instanceof Player) {
            Player player = (Player) object;
            PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO(player, getOwner().isEnemyPlayer((Player) object)));
            if (player.getToyPet() != null) {
                Logger.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has toypet");
                PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, player.getToyPet()));
            }
            getOwner().getEffectController().sendEffectIconsTo((Player) object);
        } else if (object instanceof Kisk) {
            Kisk kisk = ((Kisk) object);
            PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(getOwner(), kisk));
            if (getOwner().getCommonData().getRace() == kisk.getOwnerRace())
                PacketSendUtility.sendPacket(getOwner(), new SM_KISK_UPDATE(kisk));
        } else if (object instanceof GroupGate) {
            GroupGate groupgate = ((GroupGate) object);
            PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(getOwner(), groupgate));
        } else if (object instanceof Npc) {
            boolean update = false;
            Npc npc = ((Npc) object);

            PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));

            for(int questId : QuestEngine.getInstance().getNpcQuestData(npc.getNpcId()).getOnQuestStart())
            {
                if(QuestService.checkNearBy(new QuestCookie(object, getOwner(), questId, 0), 2))
                {
                    if(!getOwner().getNearbyQuests().contains(questId))
                    {
                        update = true;
                        getOwner().getNearbyQuests().add(questId);
                    }
                }
            }

            if (update)
                updateNearbyQuestList();

            if(npc.getNpcId() == 206089) // Siel's / Sulfur fortress instance event trigger 
            {
                WorldMapInstance instance = InstanceService.getRegisteredInstance(getOwner().getWorldId(), getOwner().getPlayerGroup().getGroupId());
                if(instance != null && instance.getTimerEnd() == null)
                    FortressInstanceTimer.schedule(getOwner(), 900);
            }
            if(npc.getNpcId() == 206095 || npc.getNpcId() == 206096 || npc.getNpcId() == 206097) // Miren-Krotan-Kysis fortress instance event trigger
            {
                WorldMapInstance instance = InstanceService.getRegisteredInstance(getOwner().getWorldId(), getOwner().getPlayerGroup().getGroupId());
                if(instance != null && instance.getTimerEnd() == null)
                    FortressInstanceTimer.schedule(getOwner(), 600);
            }

            if (npc.hasWalkRoutes()) {
                double distanceToTarget = MathUtil.getDistance(npc.getX(), npc.getY(), npc.getZ(), npc.getMoveController().getTargetX(), npc.getMoveController().getTargetY(), npc.getMoveController().getTargetZ());
                float x2 = (float) (((npc.getMoveController().getTargetX() - npc.getX()) / distanceToTarget) * npc.getMoveController().getSpeed() * 0.5);
                float y2 = (float) (((npc.getMoveController().getTargetY() - npc.getY()) / distanceToTarget) * npc.getMoveController().getSpeed() * 0.5);
                float z2 = (float) (((npc.getMoveController().getTargetZ() - npc.getZ()) / distanceToTarget) * npc.getMoveController().getSpeed() * 0.5);
                byte h2 = (byte) (Math.toDegrees(Math.atan2(y2, x2)) / 3);

                PacketSendUtility.sendPacket(getOwner(), new SM_MOVE(npc.getObjectId(),
                        npc.getX(), npc.getY(), npc.getZ(),
                        x2, y2, z2, h2, MovementType.MOVEMENT_START_KEYBOARD));
            }

        } else if (object instanceof Summon) {
            Summon npc = ((Summon) object);
            PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc));
        } else if (object instanceof Gatherable || object instanceof StaticObject) {
            PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notSee(VisibleObject object, boolean isOutOfRange) {
        super.notSee(object, isOutOfRange);
        if (object instanceof Npc) {
            boolean update = false;
            for (int questId : QuestEngine.getInstance().getNpcQuestData(((Npc) object).getNpcId()).getOnQuestStart()) {
                if(QuestService.checkNearBy(new QuestCookie(object, getOwner(), questId, 0), 2))
                {
                    if(getOwner().getNearbyQuests().contains(questId))
                    {
                        update = true;
                        getOwner().getNearbyQuests().remove(getOwner().getNearbyQuests().indexOf(questId));
                    }
                }
            }
            if (update)
                updateNearbyQuestList();
        }

        PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, isOutOfRange ? 0 : 15));
    }

    public void updateNearbyQuests() {
        getOwner().getNearbyQuests().clear();

        getOwner().getKnownList().doOnAllNpcs(new Executor<Npc>() {
            @Override
            public boolean run(Npc obj) {
                for(int questId : QuestEngine.getInstance().getNpcQuestData(((Npc) obj).getNpcId()).getOnQuestStart())
                {
                    if(QuestService.checkNearBy(new QuestCookie(obj, getOwner(), questId, 0), 2))
                    {
                        if(!getOwner().getNearbyQuests().contains(questId))
                        {
                            getOwner().getNearbyQuests().add(questId);
                        }
                    }
                }
                return true;
            }
        }, true);

        updateNearbyQuestList();
    }

    /**
     * Will be called by ZoneManager when player enters specific zone
     * 
     * @param zoneInstance
     */
    public void onEnterZone(ZoneInstance zoneInstance)
    {
        QuestEngine.getInstance().onEnterZone(new QuestCookie(null, this.getOwner(), 0, 0), zoneInstance.getTemplate().getName());
    }

    /**
     * Will be called by ZoneManager when player leaves specific zone
     * 
     * @param zoneInstance
     */
    public void onLeaveZone(ZoneInstance zoneInstance)
    {

    }

    /**
     * Set zone instance as null (Where no zones defined)
     */
    public void resetZone() {
        getOwner().setZoneInstance(null);
    }

    public void onEnterWorld()
    {
        // Display Dark Poeta counter when entering 300040000
        if(getOwner().getWorldId() == 300040000 && !getOwner().getInDarkPoeta() && getOwner().getPlayerGroup() != null){
            PacketSendUtility.sendPacket(getOwner(), new SM_INSTANCE_SCORE(getOwner().getWorldId(), (int)((getOwner().getPlayerGroup().getInstanceStartTime() + 14400000) - System.currentTimeMillis()), 2097152, getOwner().getPlayerGroup().getGroupInstancePoints(), 0, 0, 7));
            getOwner().setInDarkPoeta(true);
        }
        // Remove Dark Poeta Counter on map change
        if(getOwner().getInDarkPoeta() && getOwner().getWorldId() != 300040000){
            PacketSendUtility.sendPacket(getOwner(), new SM_INSTANCE_SCORE(0, 14400000, 2097152, 0, 0, 0, 7));
            getOwner().setInDarkPoeta(true);
        }

        for (Effect ef : getOwner().getEffectController().getAbnormalEffects())
        {
            //remove abyss transformation if worldtype != abyss && worldtype != balaurea
            if (ef.isAvatar())
            {
                if (getOwner().getWorldType() != WorldType.ABYSS && 
                    getOwner().getWorldType() != WorldType.BALAUREA ||
                    getOwner().isInInstance())
                {
                    getOwner().getEffectController().removeEffect(ef.getSkillId());
                    getOwner().getEffectController().removeEffect(ef.getLaunchSkillId());
                    break;
                }
            }
            //remove Instance Transformation if player is not in instance (Kromede, Taloc....)
            if (ef.getStack().contains("POLYMORPH_CROMEDE"))
            {
                if (getOwner().getWorldId() != 300230000)
                {
                    getOwner().getEffectController().removeEffect(ef.getSkillId());
                    break;                    
                }
            }
            else if (ef.getStack().contains("SHAPE_IDELIM"))
            {
                if (getOwner().getWorldId() != 300190000)
                {
                    getOwner().getEffectController().removeEffect(ef.getSkillId());
                    break;                    
                }
            }
        }

        //remove arena status if not in arena zone
        if (ArenaService.getInstance().isInArena(getOwner()) && !ArenaService.getInstance().isInArenaZone(getOwner()))
            ArenaService.getInstance().unregister(getOwner());
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Should only be triggered from one place (life stats)
     */
    @Override
    public void onDie(Creature lastAttacker) {
        Player player = this.getOwner();

        Creature master = null;
        if (lastAttacker != null)
            master = lastAttacker.getMaster();

        if (master instanceof Player) {
            if (player.getInArena()) {
                ArenaService.getInstance().onDie(player, lastAttacker);
                return;
            }
            else if (isDueling((Player) master)) {
                DuelService.getInstance().onDie(player);
                return;
            }
        }

        if(lastAttacker instanceof Npc)
        {
            NpcShoutsService.getInstance().handleEvent((Npc)lastAttacker, player, ShoutEventType.WIN);
        }

        this.doReward();

        // Effects removed with super.onDie()
        boolean hasSelfRezEffect = player.getReviveController().checkForSelfRezEffect(player) && canAutoRevive;

        if (player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING))
        {
            player.unsetState(CreatureState.FLYING);
            player.unsetState(CreatureState.GLIDING);
            player.setFlyState(0);
        }

        super.onDie(lastAttacker);

        if (master instanceof Npc || master == player) {
            if(player.getLevel() > 4 && !DredgionInstanceService.isDredgion(player.getWorldId()))
                player.getCommonData().calculateExpLoss();
        }

        /**
         * Release summon
         */
        Summon summon = player.getSummon();
        if (summon != null)
            summon.getController().release(UnsummonType.UNSPECIFIED);

        if (player.getToyPet() != null)
            ToyPetService.getInstance().dismissPet(player, player.getToyPet().getPetId());

        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 :
                lastAttacker.getObjectId()), true);

        // SM_DIE Packet
        int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
        boolean hasSelfRezItem = player.getReviveController().checkForSelfRezItem(player) && canAutoRevive;
        PacketSendUtility.sendPacket(player, new SM_DIE(hasSelfRezEffect, hasSelfRezItem, kiskTimeRemaining));

        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.DIE);
        QuestEngine.getInstance().onDie(new QuestCookie(null, player, 0, 0));
        player.getObserveController().notifyDeath(player);
    }


    @Override
    public void doReward() {
        Player victim = getOwner();
        PvpService.getInstance().doReward(victim);

        // DP reward
        // TODO: Figure out what DP reward should be for PvP
        //int currentDp = winner.getCommonData().getDp();
        //int dpReward = StatFunctions.calculateSoloDPReward(winner, getOwner());
        //winner.getCommonData().setDp(dpReward + currentDp);

    }

    @Override
    public void onRespawn() {
        if (hasTask(TaskId.SKILL_RESURRECT))
            cancelTask(TaskId.SKILL_RESURRECT, true);

        super.onRespawn();
        startProtectionActiveTask();
    }

    @Override
    public void attackTarget(Creature target) {
        Player player = getOwner();

        /**
         * Check all prerequisites
         */
        if (target == null || !player.canAttack())
            return;

        PlayerGameStats gameStats = player.getGameStats();

        // check player attack Z distance
        if (Math.abs(player.getZ() - target.getZ()) > 6)
            return;

        if (!RestrictionsManager.canAttack(player, target))
            return;

        int attackSpeed = gameStats.getCurrentStat(StatEnum.ATTACK_SPEED);
        long milis = System.currentTimeMillis();
        if (milis - lastAttackMilis < attackSpeed) {
            /**
             * Hack!
             */
            return;
        }
        lastAttackMilis = milis;

        /**
         * notify attack observers
         */
        super.attackTarget(target);

        /**
         * Calculate and apply damage
         */
        List<AttackResult> attackResult = AttackUtil.calculateAttackResult(player, target);

        int damage = 0;
        for (AttackResult result : attackResult) {
            damage += result.getDamage();
        }

        long time = System.currentTimeMillis();
        int attackType = 0; // TODO investigate attack types
        PacketSendUtility.broadcastPacket(player, new SM_ATTACK(player, target, gameStats.getAttackCounter(),
                (int) time, attackType, attackResult), true);

        target.getController().onAttack(player, damage, true);

        gameStats.increaseAttackCounter();
    }

    public void onAttack(Creature creature, int skillId, TYPE type, int damage, int unknown, boolean notifyAttackedObservers) {
        Player player = getOwner();

        if (player.getLifeStats().isAlreadyDead())
            return;

        // Reduce the damage to exactly what is required to ensure death.
        // - Important that we don't include 7k worth of damage when the
        //   creature only has 100 hp remaining. (For AggroList dmg count.)
        if (damage > player.getLifeStats().getCurrentHp())
            damage = player.getLifeStats().getCurrentHp() + 1;

        super.onAttack(creature, skillId, type, damage, notifyAttackedObservers);

        if (player.isInvul() || player.isProtect() || player.isProtectionActive())
            damage = 0;

        player.getLifeStats().reduceHp(damage, creature, false);

        PacketSendUtility.broadcastPacket(player, new SM_ATTACK_STATUS(player, type, skillId, damage, unknown), true);
        if (player.getLifeStats().isAlreadyDead()) {
            player.getController().onDie(creature);
        }
    }

    @Override
    public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttackedObservers) {
        this.onAttack(creature, skillId, type, damage, 0xA6, notifyAttackedObservers);
    }

    /**
     * @param skillId
     * @param targetType
     * @param x
     * @param y
     * @param z
     */
    public void useSkill(int skillId, int targetType, float x, float y, float z, int time)
    {
        Player player = getOwner();
        
        Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());
        
        if(skill != null)
        {
            if (skill.getSkillTemplate() == null || skill.getSkillTemplate().isPassive())
                return;    
            
            skill.setTargetType(targetType, x, y, z);
            skill.setTime(time);
            if(!RestrictionsManager.canUseSkill(player, skill))
                return;
            
            if (player.getSummon() != null)
            {
                int cooldown = skill.getSkillTemplate().getCooldown();
                player.getSummon().getController().setOrderSkillId(skillId);
                player.getSummon().getController().setOrderSkillCooldown(cooldown);
            }
            
            skill.useSkill();
        }
        
        skill = null;
    }

    @Override
    public void onMove() {
        super.onMove();
        addZoneUpdateMask(ZoneUpdateMode.ZONE_UPDATE);
    }

    @Override
    public void onStopMove() {
        cancelCurrentSkill();
        super.onStopMove();
    }

    @Override
    public void onStartMove() {
        cancelCurrentSkill();
        super.onStartMove();
    }

    /**
     * Perform tasks on Player jumping
     */
    public void onJump() {
        getOwner().getObserveController().notifyJumpObservers();
    }


    /**
     * Cancel current skill and remove cooldown
     */
    @Override
    public void cancelCurrentSkill()
    {
        Player player = getOwner();
        Skill castingSkill = player.getCastingSkill();
        if(castingSkill != null)
        {
            int skillId = castingSkill.getSkillTemplate().getSkillId();
            castingSkill.cancelCast();
            player.removeSkillCoolDown(castingSkill.getSkillTemplate().getDelayId());
            player.setCasting(null);
            if (castingSkill.getSkillType() == SkillType.ITEM)
            {
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), castingSkill.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(), castingSkill.getItemTemplate().getTemplateId(), 0, 3, 0));
                getOwner().removeItemCoolDown(castingSkill.getItemTemplate().getDelayId());
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED());
            }
            else
            {    
                PacketSendUtility.sendPacket(player, new SM_SKILL_CANCEL(player, skillId));
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED());
            }
        }    
    }

    /**
     *
     */
    public void updatePassiveStats() {
        Player player = getOwner();
        for (SkillListEntry skillEntry : player.getSkillList().getAllSkills()) {
            Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
            if (skill != null && skill.isPassive()) {
                skill.useSkill();
            }
        }
    }

    @Override
    public Player getOwner() {
        return (Player) super.getOwner();
    }

    @Override
    public void onRestore(HealType healType, int value) {
        super.onRestore(healType, value);
        switch (healType) {
            case DP:
                getOwner().getCommonData().addDp(value);
                break;
        }
    }


    /**
     * @param player
     * @return
     */
    public boolean isDueling(Player player) {
        return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
    }

    public void updateNearbyQuestList() {
        getOwner().addPacketBroadcastMask(BroadcastMode.UPDATE_NEARBY_QUEST_LIST);
    }

    public void updateNearbyQuestListImpl() {
        PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(getOwner().getNearbyQuests()));
    }

    public boolean isInShutdownProgress() {
        return isInShutdownProgress;
    }

    public void setInShutdownProgress(boolean isInShutdownProgress) {
        this.isInShutdownProgress = isInShutdownProgress;
    }

    /**
     * Handle dialog
     */
    @Override
    public void onDialogSelect(int dialogId, Player player, int questId) {
        switch (dialogId) {
            case 2:
                PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore()));
                break;
        }
    }

    /**
     * @param level
     */
    public void upgradePlayer(int level) {
        Player player = getOwner();

        PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
        player.setPlayerStatsTemplate(statsTemplate);

        // update stats after setting new template
        player.getGameStats().doLevelUpgrade();
        player.getLifeStats().synchronizeWithMaxStats();
        player.getLifeStats().updateCurrentStats();

        PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(
            player.getObjectId(), 0, level), true);
        PacketSendUtility.sendPacket(player, new SM_CUBE_UPDATE(
            player, 6, player.getCommonData().getAdvancedStigmaSlotSize()));
        PacketSendUtility.sendPacket(player, new SM_CUBE_UPDATE(
            player, 5, player.getCommonData().getStigmaSlotSize()));

        // Temporal
        ClassChangeService.showClassChangeDialog(player);

        QuestEngine.getInstance().onLvlUp(new QuestCookie(null, player, 0, 0));
        updateNearbyQuests();
        PacketSendUtility.sendPacket(player, new SM_QUEST_LIST(player));

        PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));

        if (level == 10 && player.getSkillList().getSkillEntry(30001) != null) {
            int skillLevel = player.getSkillList().getSkillLevel(30001);
            player.getSkillList().removeSkill(30001);
            PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player));
            player.getSkillList().addSkill(player, 30002, skillLevel, true);
        }
        // add new skills
        SkillLearnService.addNewSkills(player, false);
        player.getController().updatePassiveStats();

        /**
         * Broadcast Update to all that may care.
         */
        if (player.isInGroup())
            player.getPlayerGroup().updateGroupUIToEvent(player, GroupEvent.UPDATE);
        if (player.isInAlliance())
            AllianceService.getInstance().updateAllianceUIToEvent(player, PlayerAllianceEvent.UPDATE);
        if (player.isLegionMember())
            LegionService.getInstance().updateMemberInfo(player);

        if (CustomConfig.ENABLE_SURVEYS)
            HTMLService.checkSurveys(player);
    }

    /**
     * After entering game player char is "blinking" which means that it's in under some protection, after making an
     * action char stops blinking. - Starts protection active - Schedules task to end protection
     */
    public void startProtectionActiveTask() {
        getOwner().setVisualState(CreatureVisualState.BLINKING);
        PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
        Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                stopProtectionActiveTask();
            }
        }, 60000);
        addTask(TaskId.PROTECTION_ACTIVE, task);
    }

    /**
     * Stops protection active task after first move or use skill
     */
    public void stopProtectionActiveTask() {
        cancelTask(TaskId.PROTECTION_ACTIVE);
        Player player = getOwner();
        if (player != null && player.isSpawned()) {
            player.unsetVisualState(CreatureVisualState.BLINKING);
            PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
        }
    }

    /**
     * When player arrives at destination point of flying teleport
     */
    public void onFlyTeleportEnd() {
        Player player = getOwner();
        player.unsetState(CreatureState.FLIGHT_TELEPORT);
        player.setFlightTeleportId(0);
        player.setFlightDistance(0);
        player.setState(CreatureState.ACTIVE);
        addZoneUpdateMask(ZoneUpdateMode.ZONE_REFRESH);
    }

    public void onEnterZone(ZoneInstance zoneInstance)
    {
        addZoneUpdateMask(ZoneUpdateMode.ZONE_REFRESH);
        QuestEngine.getInstance().onEnterZone(new QuestCookie(null, this.getOwner(), 0, 0), zoneInstance.getTemplate().getName());
        
        Player player = getOwner();
        ZoneInstance currentZone = player.getZoneInstance();
        if(currentZone != null && GSConfig.FREEFLY == true) {
            currentZone.isFlightAllowed();
        }
        if(currentZone != null && !currentZone.isFlightAllowed() && player.getAccessLevel() < AdminConfig.GM_FLIGHT_FREE) {
            checkNoFly(player);
        }
    }

    public void onLeaveZone(ZoneInstance zoneInstance)
    {
        
    }

    public void checkNoFly(final Player player)	
    {
        if(player.isInState(CreatureState.FLYING))
            player.getFlyController().endFly();	
    }


    /**
     * Zone update mask management
     *
     * @param mode
     */
    public final void addZoneUpdateMask(ZoneUpdateMode mode) {
        zoneUpdateMask |= mode.mask();
        ZoneService.getInstance().add(getOwner());
    }

    public final void removeZoneUpdateMask(ZoneUpdateMode mode) {
        zoneUpdateMask &= ~mode.mask();
    }

    public final byte getZoneUpdateMask() {
        return zoneUpdateMask;
    }

    /**
     * Update zone taking into account the current zone
     */
    public void updateZoneImpl() {
        ZoneService.getInstance().checkZone(getOwner());
    }

    /**
     * Refresh completely zone irrespective of the current zone
     */
    public void refreshZoneImpl() {
        ZoneService.getInstance().findZoneInCurrentMap(getOwner());
    }

    /**
     *
     */
    public void ban() {
        // sp.getTeleportService().teleportTo(this.getOwner(), 510010000, 256f, 256f, 49f, 0);
    }

    /**
     * Check water level (start drowning) and map death level (die)
     */
    public void checkWaterLevel() {
        Player player = getOwner();
        World world = World.getInstance();
        float z = player.getZ();

        if (player.getLifeStats().isAlreadyDead())
            return;

        if (z < world.getWorldMap(player.getWorldId()).getDeathLevel()) {
            die();
            return;
        }

        ZoneInstance currentZone = player.getZoneInstance();
        if (currentZone != null && currentZone.isBreath())
            return;

        //TODO need fix character height
        float playerheight = player.getPlayerAppearance().getHeight() * 1.6f;
        if (z < world.getWorldMap(player.getWorldId()).getWaterLevel() - playerheight)
            ZoneService.getInstance().startDrowning(player);
        else
            ZoneService.getInstance().stopDrowning(player);
    }

    @Override
    public void createSummon(int npcId, int skillLvl) {
        Player master = getOwner();

        if (master.getSummon() != null) //check to avoid spawns of multiple summons
            return;

        Summon summon = SpawnEngine.getInstance().spawnSummon(master, npcId, skillLvl);
        master.setSummon(summon);
        summon.getObjectTemplate().getStatsTemplate().setFlySpeed(master.getGameStats().getCurrentStat(StatEnum.FLY_SPEED));
        summon.getObjectTemplate().getStatsTemplate().setRunSpeed(master.getGameStats().getCurrentStat(StatEnum.SPEED));
        PacketSendUtility.sendPacket(master, new SM_SUMMON_PANEL(summon));
        PacketSendUtility.broadcastPacket(summon, new SM_EMOTION(summon, EmotionType.START_EMOTE2));
        PacketSendUtility.broadcastPacket(summon, new SM_SUMMON_UPDATE(summon));
    }

    public boolean addItems(int itemId, int count) {
        return ItemService.addItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
    }

    public void setCanAutoRevive(boolean canAutoRevive)
    {
        this.canAutoRevive = canAutoRevive;
    }

    
    public boolean getCanAutoRevive()
    {
        return this.canAutoRevive;
    }

    public boolean checkAttackPacketSpeed()
    {
        int attackSpeed = getOwner().getGameStats().getCurrentStat(StatEnum.ATTACK_SPEED);
        long milis = System.currentTimeMillis();
        if (milis - lastAttackMilis < attackSpeed)
            return false;
        else
        {
            lastAttackMilis = milis;
            return true;
        }                    
    }
    
    public boolean checkSkillPacket(int spellid, int time, int targetId)
    {        
        Skill skill = SkillEngine.getInstance().getSkillFor(getOwner(), spellid, getOwner().getTarget());
        if(CustomConfig.LOG_CASTSPELL_TARGETHACK && skill.getFirstTargetProperty() == FirstTargetAttribute.TARGET && (skill.getFirstTarget() == null || skill.getFirstTarget().getObjectId() != targetId))
        {
            log.info("[CHEAT] " + getOwner().getName() + " CM_CASTSPELL packet hack. TARGETID WRONG.");
            return false;
        }
        
        long milis = System.currentTimeMillis();
        long clientDelay = milis - lastSkillMilis;
        long diff = lastSkillAnimationTime - clientDelay;
        if (CustomConfig.LOG_CASTSPELL_SPEEDHACK && diff > lastSkillAnimationTime * 0.25f)
        {
            log.info("[CHEAT] " + getOwner().getName() + " CM_CASTSPELL packet hack. Packet force send. SPEED HACK. Server delay: " + String.valueOf(lastSkillAnimationTime) + "ms. Client delay: " + String.valueOf(clientDelay) + "ms");
            return false;
        }
        else
        {            
            long skillCooldownTime = getOwner().getSkillCoolDown(skill.getSkillTemplate().getDelayId());

            if(CustomConfig.LOG_CASTSPELL_COOLDOWNHACK && milis < skillCooldownTime)
            {
                log.info("[CHEAT] " + getOwner().getName() + " CM_CASTSPELL packet hack. Packet force send. COOLDOWN AVOID. Difference: " + String.valueOf(skillCooldownTime - milis) + "ms");
                return false;
            }
            
            lastSkillMilis = milis;
            lastSkillAnimationTime = time;
            return true;
        }
    }

}