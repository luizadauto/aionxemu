/*
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
package gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CustomConfig {


    /**
     * Enable or Disable Character Passkey
     */
    @Property(key = "passkey.enable", defaultValue = "true")
    public static boolean PASSKEY_ENABLE;

    /**
     * Enter the maximum number of incorrect password set
     */
    @Property(key = "passkey.wrong.maxcount", defaultValue = "5")
    public static int PASSKEY_WRONG_MAXCOUNT;
    /**
     * Factions speaking mode
     */
    @Property(key = "gameserver.factions.speaking.mode", defaultValue = "0")
    public static int FACTIONS_SPEAKING_MODE;

    /*
     * Factions search mode
     */
    @Property(key = "gameserver.factions.search.mode", defaultValue = "false")
    public static boolean FACTIONS_SEARCH_MODE;

    /**
     * Skill autolearn
     */
    @Property(key = "gameserver.skill.autolearn", defaultValue = "false")
    public static boolean SKILL_AUTOLEARN;

    /**
     * Stigma autolearn
     */
    @Property(key = "gameserver.stigma.autolearn", defaultValue = "false")
    public static boolean STIGMA_AUTOLEARN;

     /**
     * Stigma level antihack
     */
    @Property(key = "gameserver.stigma.antihack", defaultValue = "level")
    public static String STIGMA_ANTIHACK;

    /**
     * Advanced Stigma level antihack
     */
    @Property(key = "gameserver.advstigma.antihack", defaultValue = "level")
    public static String ADVANCED_STIGMA_ANTIHACK;

    /**
     * Retail like char deletion
     */
    @Property(key = "gameserver.character.delete.retail", defaultValue = "true")
    public static boolean RETAIL_CHAR_DELETION;

    /**
     * Disable monsters aggressive behave
     */
    @Property(key = "gameserver.disable.mob.aggro", defaultValue = "false")
    public static boolean DISABLE_MOB_AGGRO;

    /**
     * Enable 2nd class change simple mode
     */
    @Property(key = "gameserver.enable.simple.2ndclass", defaultValue = "false")
    public static boolean ENABLE_SIMPLE_2NDCLASS;

    /**
     * Unstuck delay
     */
    @Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
    public static int UNSTUCK_DELAY;

    /**
     * Enable instances
     */
    @Property(key = "gameserver.instances.enable", defaultValue = "true")
    public static boolean ENABLE_INSTANCES;

    /**
     * Base Fly Time
     */
    @Property(key = "gameserver.base.flytime", defaultValue = "60")
    public static int BASE_FLYTIME;

    /**
     * Allows players of opposite factions to bind in enemy territory
     */
    @Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
    public static boolean ENABLE_CROSS_FACTION_BINDING;

    /**
     * Disable drop rate reduction based on level diference between players and mobs
     */
    @Property(key = "gameserver.disable.drop.reduction", defaultValue = "false")
    public static boolean DISABLE_DROP_REDUCTION;

    /**
     * Allowed Kills in time period for full AP. Move to separate config when more pvp options.
     */
    @Property(key = "gameserver.pvp.maxkills", defaultValue = "5")
    public static int MAX_DAILY_PVP_KILLS;

    /**
     * Time period for max daily kills in pvp
     */
    @Property(key = "gameserver.pvp.period", defaultValue = "24")
    public static int DAILY_PVP_PERIOD;

    /**
     * Enable customs channels
     */
    @Property(key = "gameserver.channels.all.enabled", defaultValue = "false")
    public static boolean CHANNEL_ALL_ENABLED;

    /**
     * Enable custom channel .world
     */
    @Property(key = "gameserver.channels.world.enabled", defaultValue = "false")
    public static boolean CHANNEL_WORLD_ENABLED;

    /**
     * Enable V-research showing all connected players from both faction for GMs
     */
    @Property(key = "gameserver.search.listall", defaultValue = "false")
    public static boolean SEARCH_LIST_ALL;

    /**
     * Enable or disable gm tags
     */
    @Property(key = "gameserver.gmtag.display", defaultValue = "false")
    public static boolean GMTAG_DISPLAY;

    @Property(key = "gameserver.gmtag.level1", defaultValue = "<HELPER>")
    public static String GM_LEVEL1;

    @Property(key = "gameserver.gmtag.level2", defaultValue = "<GM>")
    public static String GM_LEVEL2;

    @Property(key = "gameserver.gmtag.level3", defaultValue = "<HEADGM>")
    public static String GM_LEVEL3;
	
	@Property(key = "gameserver.gmtag.level4", defaultValue = "<ADMIN>")
    public static String GM_LEVEL4;
	
	@Property(key = "gameserver.gmtag.level5", defaultValue = "<HEADADMIN>")
    public static String GM_LEVEL5;

    /**
     * Announce on GM connection
     */
    @Property(key = "gameserver.announce.gm.connection", defaultValue = "false")
    public static boolean ANNOUNCE_GM_CONNECTION;

    /**
     * Invis on GM connection
     */
    @Property(key = "gameserver.invis.gm.connection", defaultValue = "false")
    public static boolean INVIS_GM_CONNECTION;

    /**
     * Invul on GM connection
     */
    @Property(key = "gameserver.invul.gm.connection", defaultValue = "false")
    public static boolean INVUL_GM_CONNECTION;

    /**
     * Silence on GM connection
     */
    @Property(key = "gameserver.silence.gm.connection", defaultValue = "false")
    public static boolean SILENCE_GM_CONNECTION;

    /**
     * Speed on GM connection
     */
    @Property(key = "gameserver.speed.gm.connection", defaultValue = "0")
    public static int SPEED_GM_CONNECTION;

    /**
     * Enable or disable instance cooldown
     */
    @Property(key = "gameserver.instance.cooldown", defaultValue = "true")
    public static boolean INSTANCE_COOLDOWN;


    /**
     * Enable or disable Global announce for rare drops
     */
    @Property(key = "gameserver.announce.raredrops", defaultValue = "false")
    public static boolean ANNOUNCE_RAREDROPS;

    /**
     * Enable or disable Kick players using speed hack
     */
    @Property(key = "gameserver.kick.speedhack.enable", defaultValue = "true")
    public static boolean KICK_SPEEDHACK;

    /**
     * Ping minimun Interval to consider hack
     */
    @Property(key = "gameserver.kick.speedhack.pinginterval", defaultValue = "100000")
    public static long KICK_PINGINTERVAL;

    /**
     * Chain trigger rate. If false all Chain are 100% success.
     */
    @Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
    public static boolean SKILL_CHAIN_TRIGGERRATE;

    /**
     * Add a reward to player for pvp kills
     */
    @Property(key = "gameserver.pvpreward.enable", defaultValue = "false")
    public static boolean PVPREWARD_ENABLE;

    /**
     * Kills needed for item reward
     */
    @Property(key = "gameserver.pvpreward.kills.needed1", defaultValue = "5")
    public static int PVPREWARD_KILLS_NEEDED1;

    @Property(key = "gameserver.pvpreward.kills.needed2", defaultValue = "10")
    public static int PVPREWARD_KILLS_NEEDED2;

    @Property(key = "gameserver.pvpreward.kills.needed3", defaultValue = "15")
    public static int PVPREWARD_KILLS_NEEDED3;

    /**
     * Item Rewards
     */
    @Property(key = "gameserver.pvpreward.item.reward1", defaultValue = "186000031")
    public static int PVPREWARD_ITEM_REWARD1;

    @Property(key = "gameserver.pvpreward.item.reward2", defaultValue = "186000030")
    public static int PVPREWARD_ITEM_REWARD2;

    @Property(key = "gameserver.pvpreward.item.reward3", defaultValue = "186000096")
    public static int PVPREWARD_ITEM_REWARD3;

    /**
     * Send to the prison (artmoney hack)
     */
    @Property(key = "gameserver.artmoney.hack", defaultValue = "true")
    public static boolean		ARTMONEY_HACK;

    @Property(key = "gameserver.artmoney.hackbuy.time", defaultValue = "120")
    public static int		ARTMONEY_HACKBUY_TIME;

    /**
     * Player Search Level Restriction (Level 10)
     */
    @Property(key = "search.level.restriction", defaultValue = "10")
    public static int LEVEL_TO_SEARCH;

    /**
     * Whisper Level Restriction (Level 10)
     */
    @Property(key = "whisper.level.restriction", defaultValue = "10")
    public static int LEVEL_TO_WHISPER;

    @Property(key = "gameserver.player.experience.control", defaultValue = "false")
    public static boolean PLAYER_EXPERIENCE_CONTROL;

    /**
     * Time in seconds which character stays online after closing client window
     */
    @Property(key = "gameserver.disconnect.time", defaultValue = "10")
    public static int DISCONNECT_DELAY;

    /**
     * Enable Surveys
     */
    @Property(key = "gameserver.enable.surveys", defaultValue = "false")
    public static boolean ENABLE_SURVEYS;

    /**
     * Enable the HTML Welcome Message Window on Player Login
     */
    @Property(key = "enable.html.welcome", defaultValue = "false")
    public static boolean ENABLE_HTML_WELCOME;

    /**
     * Time when top ranking is updated
     */
    @Property(key = "gameserver.topranking.time", defaultValue = "0:00:00")
    public static String TOP_RANKING_TIME;

    /**
     * Time between updates of top ranking
     */
    @Property(key = "gameserver.topranking.delay", defaultValue = "24")
    public static int TOP_RANKING_DELAY;

    /**
     * Time between using worldchat
     */
    @Property(key = "gameserver.chat.talkdelay", defaultValue = "10")
    public static int TALK_DELAY;

    /**
     * Time Length of Duel Battles.
     */
    @Property(key = "gameserver.duel.length", defaultValue = "300")
    public static int DUEL_LENGTH;
}
