package kubaska.AutoChannelRank;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import de.stefan1200.jts3servermod.BotConfigurationException;
import de.stefan1200.jts3servermod.interfaces.HandleBotEvents;
import de.stefan1200.jts3servermod.interfaces.HandleTS3Events;
import de.stefan1200.jts3servermod.interfaces.JTS3ServerMod_Interface;
import de.stefan1200.jts3servermod.interfaces.LoadConfiguration;
import de.stefan1200.jts3serverquery.JTS3ServerQuery;
import de.stefan1200.jts3serverquery.TS3ServerQueryException;
import de.stefan1200.util.ArrangedPropertiesWriter;

public class AutoChannelRank implements HandleBotEvents, HandleTS3Events, LoadConfiguration{

	private JTS3ServerMod_Interface modClass = null;
	private JTS3ServerQuery queryLib = null;
	private String pluginPrefix = "";
	
	private String PLUGIN_VERSION = "1.0.0";
	
	private int CONFIG_CHANNEL_ID = -1;
	private int CONFIG_RANK_ID = -1;
	private long CONFIG_TIME_SPENT = -1;
	private String CONFIG_MESSAGE_RANK_ASSIGNED = "";
	private String CONFIG_MESSAGE_NEED_TO_WAIT = "";
	private String CONFIG_MESSAGE_ALREADY_HAVE_RANK = "";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public void handleTS3Events(String eventType, HashMap<String,String> eventInfo){
		if (eventType.equalsIgnoreCase("notifyclientmoved")){
			if (eventInfo.get("ctid").equals(Integer.toString(CONFIG_CHANNEL_ID))){
				//retrieve full client info
				HashMap<String, String> fullClientInfo = null;
				fullClientInfo = queryLib.doCommand("clientinfo clid=" + eventInfo.get("clid"));
				//get needed values
				HashMap<String, String> formatted = queryLib.parseLine(fullClientInfo.get("response"));
				int clientID = Integer.parseInt(eventInfo.get("clid"));
				String clientUniqueID = queryLib.decodeTS3String(formatted.get("client_unique_identifier"));
				String clientGroups = formatted.get("client_servergroups");
				String createdAt = formatted.get("client_created");
				//split groups
				List<String> clientGroup = Arrays.asList(clientGroups.split(","));

				if(clientGroup.contains(Integer.toString(CONFIG_RANK_ID))){
					try {
						queryLib.kickClient(clientID, true, "");
						queryLib.sendTextMessage(clientID, JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, CONFIG_MESSAGE_ALREADY_HAVE_RANK);
					} catch (TS3ServerQueryException e) {
						e.printStackTrace();
					}
				} else {
					long currentTimestamp = System.currentTimeMillis() / 1000L;
					
					if(currentTimestamp - Integer.parseInt(createdAt) > CONFIG_TIME_SPENT){
						try {
							queryLib.doCommand("servergroupaddclient sgid=" + CONFIG_RANK_ID + " cldbid=" + modClass.getClientDBID(clientUniqueID));
							queryLib.sendTextMessage(clientID, JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, CONFIG_MESSAGE_RANK_ASSIGNED);
							queryLib.kickClient(clientID, true, "");
						} catch (TS3ServerQueryException e) {
							System.out.println("Error while assigning rank. STACKTRACE:");
							e.printStackTrace();
						}
					} else {
						try {
							queryLib.kickClient(clientID, true, "");
							queryLib.sendTextMessage(clientID, JTS3ServerQuery.TEXTMESSAGE_TARGET_CLIENT, CONFIG_MESSAGE_NEED_TO_WAIT);
						} catch (TS3ServerQueryException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void initClass(JTS3ServerMod_Interface modClass, JTS3ServerQuery queryLib, String prefix) {
		this.queryLib = queryLib;
		this.modClass = modClass;
		this.pluginPrefix = prefix.trim();
	}

	public void handleOnBotConnect() {
		
	}

	public void handleAfterCacheUpdate() {
		// TODO Auto-generated method stub
	}

	public void activate() {
		System.out.println("AutoChannelRank v." + PLUGIN_VERSION + " activated");
	}

	public void disable() {
		// TODO Auto-generated method stub
	}

	public void unload() {
		// TODO Auto-generated method stub
	}

	public boolean multipleInstances() {
		return false;
	}

	public int getAPIBuild() {
		return 4;
	}

	public String getCopyright() {
		return "AutoChannelRank by kubaska";
	}

	public String[] botChatCommandList(HashMap<String, String> eventInfo, boolean isFullAdmin, boolean isAdmin) {
		// TODO Auto-generated method stub
		return null;
	}

	public String botChatCommandHelp(String command) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean handleChatCommands(String msg, HashMap<String, String> eventInfo, boolean isFullAdmin,
			boolean isAdmin) {
		// TODO Auto-generated method stub
		return false;
	}

	public void initConfig(ArrangedPropertiesWriter config) {
		config.addKey(pluginPrefix + "_channel_id", "ID of channel that will give rank upon joining.");
		config.addKey(pluginPrefix + "_rank_id", "ID of rank that should be given.");
		config.addKey(pluginPrefix + "_time_spent", "Time which user have to spend on the server before obtaining rank in seconds. (default: 3 days)", "259200");
		
		config.addKey(
				pluginPrefix + "_message_rank_assigned",
				"Custom message when server assigns rank to user.",
				"Thanks for being part of our community."
				);
		
		config.addKey(pluginPrefix + "_message_need_to_wait",
				"Custom message when user need to wait before getting rank.",
				"Hey! You need to wait at least 3 days before getting this rank."
				);
		
		config.addKey(pluginPrefix + "_message_already_have_rank",
				"Custom message when user tries to join channel already having specified rank.",
				"Hey! You already have this rank. Shoo!"
				);
	}

	public boolean loadConfig(ArrangedPropertiesWriter config, boolean slowMode)
			throws BotConfigurationException, NumberFormatException {
		try{
			CONFIG_CHANNEL_ID = Integer.parseInt(config.getValue(pluginPrefix + "_channel_id"));
			CONFIG_RANK_ID = Integer.parseInt(config.getValue(pluginPrefix + "_rank_id"));
			CONFIG_TIME_SPENT = Integer.parseInt(config.getValue(pluginPrefix + "_time_spent"));
		} catch (NumberFormatException e){
			System.out.println(pluginPrefix + ": Something is wrong with channel_id, rank_id or time_spent value(s) in config.");
			System.out.println("STACKTRACE:");
			e.printStackTrace();
			return false;
		}
		
		try {
			CONFIG_MESSAGE_RANK_ASSIGNED = config.getValue(pluginPrefix + "_message_rank_assigned");
			CONFIG_MESSAGE_NEED_TO_WAIT = config.getValue(pluginPrefix + "_message_need_to_wait");
			CONFIG_MESSAGE_ALREADY_HAVE_RANK = config.getValue(pluginPrefix + "_message_already_have_rank");
			
			if(CONFIG_MESSAGE_RANK_ASSIGNED.isEmpty())
				throw new BotConfigurationException("Invalid message in field " + pluginPrefix + "_message_rank_assigned");
			if(CONFIG_MESSAGE_NEED_TO_WAIT.isEmpty())
				throw new BotConfigurationException("Invalid message in field " + pluginPrefix + "_message_need_to_wait");
			if(CONFIG_MESSAGE_ALREADY_HAVE_RANK.isEmpty())
				throw new BotConfigurationException("Invalid message in field " + pluginPrefix + "_message_already_have_rank");
			
		} catch (BotConfigurationException e){
			System.out.println("STACKTRACE:");
			e.printStackTrace();
		}
		return true;
	}

	public void setListModes(BitSet listOptions) {
		// TODO Auto-generated method stub
		
	}
}
