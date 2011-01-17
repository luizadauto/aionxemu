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

package mysql5;

import com.aionemu.commons.database.DatabaseFactory;
import gameserver.dao.SurveyDAO;
import gameserver.model.gameobjects.Survey;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ginho1
 */
public class MySQL5SurveyDAO extends SurveyDAO {
    private static final Logger log = Logger.getLogger(MySQL5InventoryDAO.class);

    public static final String DELETE_QUERY = "DELETE FROM `surveys` WHERE `survey_id`=?";
    public static final String SELECT_QUERY = "SELECT * FROM `surveys` WHERE `player_id`=?";
    public static final String SELECT_SURVEY_QUERY = "SELECT * FROM `surveys` WHERE `survey_id`=? AND `player_id`=?";

    @Override
    public boolean supports(String arg0, int arg1, int arg2) {
        return MySQL5DAOUtils.supports(arg0, arg1, arg2);
    }

    @Override
    public boolean deleteSurvey(int survey_id) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
            stmt.setInt(1, survey_id);
            stmt.execute();
            stmt.close();
        }
        catch (Exception e) {
            log.error("Error delete survey_id: " + survey_id, e);
            return false;
        }
        finally {
            DatabaseFactory.close(con);
        }
        return true;
    }

    @Override
    public List<Survey> loadSurveys(int playerId) {
        final List<Survey> surveys = new ArrayList<Survey>();

        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
            stmt.setInt(1, playerId);
            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                int survey_id = rset.getInt("survey_id");
                int player_id = rset.getInt("player_id");
                String title = rset.getString("title");
                String message = rset.getString("message");
                String select_text = rset.getString("select_text");
                int itemId = rset.getInt("itemId");
                int itemCount = rset.getInt("itemCount");

                Survey survey = new Survey(survey_id, player_id, title, message, select_text, itemId, itemCount);
                surveys.add(survey);
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
            log.fatal("Could not restore Survey data for player: " + playerId + " from DB: " + e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(con);
        }
        return surveys;
    }

    @Override
    public Survey loadSurvey(int player_id, int survey_id) {
        Survey survey = null;

        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_SURVEY_QUERY);
            stmt.setInt(1, survey_id);
            stmt.setInt(2, player_id);

            ResultSet rset = stmt.executeQuery();
            while (rset.next()) {
                String title = rset.getString("title");
                String message = rset.getString("message");
                String select_text = rset.getString("select_text");
                int itemId = rset.getInt("itemId");
                int itemCount = rset.getInt("itemCount");

                survey = new Survey(survey_id, player_id, title, message, select_text, itemId, itemCount);
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
            log.fatal("Could not restore Survey data for player: " + player_id + " from DB: " + e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(con);
        }
        return survey;
    }
}
