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
package gameserver.quest.model;

import gameserver.model.gameobjects.PersistentState;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author MrPoke, Rolandas
 */
public class QuestState
{
    private final int questId;
    private QuestVars questVars;
    private QuestStatus status;
    private int completeCount;
    private PersistentState persistentState;
    private Timestamp completeTime;

    public QuestState(int questId)
    {
       this.questId = questId;
       status = QuestStatus.START;
       questVars = new QuestVars();
       completeCount = 0;
       persistentState = PersistentState.NEW;
    }

    public QuestState(int questId, QuestStatus status, int questVars, int completeCount)
    {
       this.questId = questId;
       this.status = status;
       this.questVars = new QuestVars(questVars);
       this.completeCount = completeCount;
       this.persistentState = PersistentState.NEW;
    }

    public QuestVars getQuestVars()
    {
       return questVars;
    }

    /**
    * @param id
    * @param var
    */
    public void setQuestVarById(int id, int var)
    {
       questVars.setVarById(id, var);
       setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    /**
    * @param id
    * @return Quest var by id.
    */
    public int getQuestVarById(int id)
    {
       return questVars.getVarById(id);
    }

    public void setQuestVar(int var)
    {
       questVars.setVar(var);
       setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public QuestStatus getStatus()
    {
       return status;
    }

    public void setStatus(QuestStatus status)
    {
       if (status == QuestStatus.COMPLETE && this.status != QuestStatus.COMPLETE)
           updateCompleteTime();
       else if (status != QuestStatus.COMPLETE)
           completeTime = null;
       this.status = status;
       setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public Timestamp getCompleteTime()
    {
       return completeTime;
    }

    public void setCompleteTime(Timestamp time)
    {
       completeTime = time;
    }

    public void updateCompleteTime()
    {
       completeTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public int getQuestId()
    {
       return questId;
    }

    public void setCompliteCount(int completeCount)
    {
       this.completeCount = completeCount;
       setPersistentState(PersistentState.UPDATE_REQUIRED);
    }

    public boolean canRepeat(int maxCompleteCount)
    {
       if (this.status == QuestStatus.COMPLETE)
       {
           if (questId >= 80000 || maxCompleteCount == 255)
               return true;
           return completeCount < maxCompleteCount;  
       }
       return false;
    }

    public int getCompleteCount()
    {
       return completeCount;
    }

    /**
    * @return the pState
    */
    public PersistentState getPersistentState()
    {
       return persistentState;
    }

    /**
    * @param persistentState the pState to set
    */
    public void setPersistentState(PersistentState persistentState)
    {
        switch(persistentState)
        {
            case DELETED:
                if(this.persistentState == PersistentState.NEW)
                    throw new IllegalArgumentException("Cannot change state to DELETED from NEW");
            case UPDATE_REQUIRED:
                if(this.persistentState == PersistentState.NEW)
                    break;
            default:
                this.persistentState = persistentState;
        }
    }
}
