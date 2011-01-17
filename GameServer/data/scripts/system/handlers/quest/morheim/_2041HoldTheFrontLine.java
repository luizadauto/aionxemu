/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.morheim;

import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.questEngine.handlers.QuestHandler;
import gameserver.questEngine.model.QuestCookie;
import gameserver.questEngine.model.QuestState;
import gameserver.questEngine.model.QuestStatus;
import gameserver.services.QuestService;

/**
 * @author Atomics @edit by Mcrizza
 */
public class _2041HoldTheFrontLine extends QuestHandler {

    private final static int questId = 2041;
    private final static int[] npcIds = {204301, 204403, 204423, 204432, 700183};


    public _2041HoldTheFrontLine() {
        super(questId);
    }

    @Override
    public void register() {
        qe.addOnEnterWorld(questId);
        qe.addQuestLvlUp(questId);
        qe.setNpcQuestData(214103).addOnKillEvent(questId);
        qe.addOnDie(questId);
        for (int npcId : npcIds)
            qe.setNpcQuestData(npcId).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestCookie env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        boolean lvlCheck = QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel());
        if (qs == null || qs.getStatus() != QuestStatus.LOCKED || !lvlCheck)
            return false;
        int[] quests = {2300};
        for (int id : quests) {
            QuestState qs2 = player.getQuestStateList().getQuestState(id);
            if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE)
                return false;
        }

        qs.setStatus(QuestStatus.START);
        updateQuestStatus(env);
        return true;
    }

    @Override
    public boolean onDialogEvent(QuestCookie env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null)
            return false;
        final int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204301) {
                if (env.getDialogId() == -1)
                    return sendQuestDialog(env, 2375);
                return defaultQuestEndDialog(env);
            }
        } else if (qs.getStatus() != QuestStatus.START)
            return false;
        switch (targetId) {
            case 204301:
                switch (env.getDialogId()) {
                    case 25:
                        if (var == 0)
                            return sendQuestDialog(env, 1011);
                    case 10000:
                        if (var == 0) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 0);
                        }
                    default:
                        return false;
                }
            case 204403:
                switch (env.getDialogId()) {
                    case 25:
                        if (var == 1)
                            return sendQuestDialog(env, 1352);
                    case 10001:
                        if (var == 1) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 0);
                        }
                    default:
                        return false;
                }
            case 204432:
                switch (env.getDialogId()) {
                    case 25:
                        if (var == 2)
                            return sendQuestDialog(env, 1693);
                        else if (var == 9)
                            return sendQuestDialog(env, 2034);
                    case 10002:
                        if (var == 2) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 0);
                        }
                    case 10003:
                        if (var == 9) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 0);
                        }
                    default:
                        return false;
                }
            default:
                return false;
        }

    }

    @Override
    public boolean onKillEvent(QuestCookie env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START)
            return false;

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();

        if (targetId == 214103) {
            if (var > 2 && var < 8) {
                qs.setQuestVarById(0, var + 1);
                return true;
            } else if (var == 8) {
                qs.setStatus(QuestStatus.REWARD);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}
