/**
 * This file is part of alpha team <alpha-team.com>.
 *
 * alpha team is private software: you can redistribute it and/or modify
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
package gameserver.model.gameobjects;

/**
 * @author ginho1
 */
public class Survey {

    private int survey_id;
    private int player_id;
    private String title;
    private String message;
    private String select_text;
    private int itemId;
    private int itemCount;

    public Survey(int survey_id, int player_id, String title, String message, String select_text, int itemId, int itemCount) {
        this.survey_id = survey_id;
        this.player_id = player_id;
        this.title = title;
        this.message = message;
        this.select_text = select_text;
        this.itemId = itemId;
        this.itemCount = itemCount;
    }

    public int getSurveyId() {
        return survey_id;
    }

    public int getPlayerId() {
        return player_id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getSelectText() {
        return select_text;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemCount() {
        return itemCount;
    }
}