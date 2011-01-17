/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package quest.heiron;

import gameserver.dataholders.DataManager;
import gameserver.dataholders.QuestsData;
import gameserver.model.PlayerClass;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.model.templates.QuestTemplate;
import gameserver.model.templates.bonus.AbstractInventoryBonus;
import gameserver.model.templates.bonus.CoinBonus;
import gameserver.model.templates.bonus.InventoryBonusType;
import gameserver.questEngine.HandlerResult;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestCookie;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;

/**
 * @author Vincas
 */
public class _1508GoldOrSilverWarriorScout extends QuestHandler {

    private final static int questId = 1508;
    static QuestsData questsData = DataManager.QUEST_DATA;

    public _1508GoldOrSilverWarriorScout() {
        super(questId);
    }

    @Override
    public void register() {
        qe.setNpcQuestData(204515).addOnQuestStart(questId);
        qe.setNpcQuestData(204515).addOnTalkEvent(questId);
        qe.addOnQuestFinish(questId);
        qe.setQuestBonusType(InventoryBonusType.COIN).add(questId);
    }

    @Override
    public boolean onDialogEvent(QuestCookie env) {
        final Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestTemplate template = questsData.getQuestById(questId);
        if (targetId == 204515) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialogId() == 2) {
                    PlayerClass playerClass = player.getCommonData().getPlayerClass();
                    PlayerClass startPC = null;
                    try {
                        startPC = PlayerClass.getStartingClassFor(playerClass);
                    }
                    catch (IllegalArgumentException e) {
                        startPC = playerClass; // already a start class
                    }
                    if (startPC == PlayerClass.SCOUT || startPC == PlayerClass.WARRIOR) {
                        QuestService.startQuest(env, QuestStatus.START);
                        return sendQuestDialog(env, 1011);
                    } else {
                        return sendQuestDialog(env, 3739);
                    }
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialogId() == 2) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1011) {
                    if (player.getInventory().getItemCountByItemId(186000004) >= 20) {
                        qs.setQuestVarById(0, 0);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestDialog(env, 1009);
                    }
                } else if (env.getDialogId() == 1352) {
                    if (player.getInventory().getItemCountByItemId(186000004) >= 40) {
                        qs.setQuestVarById(0, 1);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 6);
                    } else {
                        return sendQuestDialog(env, 1009);
                    }
                } else if (env.getDialogId() == 1693) {
                    if (player.getInventory().getItemCountByItemId(186000003) >= 8) {
                        qs.setQuestVarById(0, 2);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 7);
                    } else {
                        return sendQuestDialog(env, 1009);
                    }
                } else if (env.getDialogId() == 2034) {
                    if (player.getInventory().getItemCountByItemId(186000003) >= 16) {
                        qs.setQuestVarById(0, 3);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 8);
                    } else {
                        return sendQuestDialog(env, 1009);
                    }
                }
            } else if (qs.getStatus() == QuestStatus.COMPLETE) {
                if (env.getDialogId() == 2) {
                    if ((qs.getCompliteCount() <= template.getMaxRepeatCount())) {
                        QuestService.startQuest(env, QuestStatus.START);
                        return sendQuestDialog(env, 1011);
                    } else
                        return sendQuestDialog(env, 1008);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                return defaultQuestEndDialog(env, qs.getQuestVarById(0));
            }
        }
        return false;
    }

    @Override
    public HandlerResult onBonusApplyEvent(QuestCookie env, int index, AbstractInventoryBonus bonus) {
        if (!(bonus instanceof CoinBonus))
            return HandlerResult.UNKNOWN;
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (index == 0 && qs.getQuestVarById(0) == 0 ||
                    index == 1 && qs.getQuestVarById(0) == 1 ||
                    index == 2 && qs.getQuestVarById(0) == 2 ||
                    index == 3 && qs.getQuestVarById(0) == 3)
                return HandlerResult.SUCCESS;
        }
        return HandlerResult.FAILED;
    }
}
