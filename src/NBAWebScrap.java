import java.io.*;
import java.util.*;
import java.net.*;
/*
 * This file scraps the internet to get the latest NBA rosters and player statistics
 * The program creates the current team's and players in the NBA
 * Using the player's statistics, the program calculates the player's rating 
 * 
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBAWebScrap{
	
	static Scanner scan = new Scanner(System.in);
	static ArrayList<Player> latest_roster_players = NBAMainSimulator.latest_roster_players;
	static ArrayList<Team>  latest_roster_teamcounter = NBAMainSimulator.latest_roster_teamcounter;
	static ArrayList<Player> editable_roster_players = NBAMainSimulator.editable_roster_players;
	static ArrayList<Team> editable_roster_teamcounter = NBAMainSimulator.editable_roster_teamcounter;
	
	static Dictionary<String,ArrayList<Player>> players_on_team_list = new Hashtable<String,ArrayList<Player>>();
	
	public static void mainFunctionCall(String args []) throws IOException {
		getLatestUpdates();
		//Prompt the user to overwrite their editable roster with latest roster
		System.out.println("[0] Return to Main Menu, [1] Overwrite Personal Roster with Latest Roster");
		System.out.print("You:");
		int roster_choice = scan.nextInt();
		if (roster_choice ==1) {
			NBAFileReadWrite.writeOutfile(false,"editableRoster",latest_roster_teamcounter);
			System.out.println("----------------------------------------------------------");
			System.out.println("\t Editable Roster has been Overwritten \n");
			//Loads the new editable roster
			readDirectory(args[1]);
		}
	}
	
	//Called to get the latest rosters and player statistics
	//Scraps the internet for latest statistics
	//Recreates the teams();
	//Calculates every player's rating
	//Set the player's scoring, rebounding, and assisting rating within their team
	//Writes those changes to the latestRoster directory
	public static void getLatestUpdates() throws IOException{
		System.out.println("\t    RETRIEVING LATEST TEAM ROSTERS");
		System.out.println("\t     Thank you for your patience");
		latest_roster_teamcounter.clear();
		latest_roster_players.clear();
		players_on_team_list = new Hashtable<String,ArrayList<Player>>();
		//Scrap the internet to get all the current players and create the Player Objects
		webScrap();
		//Puts the players on their respective teams
		fillOutRosters();
		//Put the teams in alphabetical order
		rearrangeTeams();
		//Scrap the internet to get the statistics of the current season
		getStatisticsWebScrap();
		//Set the player's scoring, rebounding, and assisting rating within their team
		NBARatingCalculator.setLeagueWidePlayerRankings(latest_roster_teamcounter);
		//Rearranges the player's on their given teams so that higher rated players are presented higher
		NBACommonFunctions.organizeTeams(latest_roster_teamcounter);
		//Writes those changes to the latestRoster directory
		NBAFileReadWrite.writeOutfile(false,"latestRoster",latest_roster_teamcounter);
		System.out.println("\n \t COMPLETED - RETRIEVED LATEST TEAM ROSTERS \n");
	}
	
	//Scraps ESPN's database of players by position to all the player, their current teams, and their position
	//By position - go to the website that stores the player info and grab the list of players at position
	//Use a regular expression to get only the players and their team
	//Creates the Player Objects
	private static void webScrap() throws IOException  {
		String[] shortname_positions = new String[]{"pg","sg","sf","pf","c"};
		String[] longname_positions = new String[]{"Point Guards","Shooting Guards","Small Forwards","Power Forwards","Centers"};
		for (int position_index = 0; position_index < 5; position_index++) {
			//Get the line of HTML that contains players at given position from ESPN web database
			String individual_position_list = webScrapPosition(longname_positions[position_index],shortname_positions[position_index]);
			//Use a regular expression to only get player's names and current team
			String[] individual_position_array = getPlayersFromString(individual_position_list,longname_positions[position_index]);
			//Create all player objects
			generatePlayers(individual_position_array,longname_positions[position_index],shortname_positions[position_index]);
		}
	}
	
	//Called by webScrap()
	//Reads the HTML from the URL: "http://www.espn.com/nba/players/_/position/pg"
	//Finds the line of code containing the actual player info
	//Return the line
	private static String webScrapPosition(String position_fullname, String position_shortname) throws IOException {
		URL url = new URL("http://www.espn.com/nba/players/_/position/"+position_shortname);
        // Get the input stream through URL Connection
        URLConnection connection = url.openConnection();
        InputStream inputstream = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
        String line = null;
        String players_list = "";
        // Find the line containing the list of players and save it
        while ((line = br.readLine()) != null) {
        		if (line.contains("http://www.espn.com/nba/player/_/id/")) {
        			players_list = line;
        		}
        }
        return players_list;
	}
	
	//Called by webScrap()
	//Given a HTML line containing a list of players and their positions - call a regular expression
	//to get only the player's name and their current team
	//Return the list of player's name and current teams
	private static String[] getPlayersFromString(String players_list,String position_given) {
		//Shorten the string
		int index = players_list.indexOf("COLLEGE");
		String newString = players_list.substring(index);
		//Helpful tool towards building regular expressions --> https://regex101.com
		//The only expression to only get player's names and current teams">[a-zA-Z]*[ ]*[a-zA-Z.,]* [a-zA-Z.]*[ ]*[a-zA-Z]*<"
		//Call the regular expression to dismiss all the excess information
		String[] new_list = newString.split("[<\\/a><\\/td><td>]*<\\/td><\\/tr><tr class=\"[a-z]* player-[0-9]*-[0-9]*\"><td><a href=\"http:\\/\\/www.espn.com\\/nba\\/player\\/_\\/id\\/[0-9]*\\/[a-z.']*-[a-z']*[-]*[a-z]*[.]*\">|<\\/a><\\/td><td><a href=\"http:\\/\\/www.espn.com\\/nba\\/team\\/_\\/name\\/[a-z]*\\/[a-z]*-[76]*[a-z]*[-]*[a-z]*\">|<\\/a><\\/td><td>[A-Z]{1}[a-zA-Z]*|<\\/a><\\/td><td><\\/td><\\/tr><tr class=\"stathead\"><td colspan=\"3\">[A-Z]");
		return new_list;
	}
	
	//Called by webScrap()
	//Given a list of player's names and current teams - create the player object for player given
	private static void generatePlayers(String[] players_list,String position_fullname, String position_shortname) {
		int index = 0;
		while (index< players_list.length) {
			//Check that the content is not irrelevant information
			if ((!players_list[index].contains("COLLEGE"))||(!players_list[index].equals(""))) {
				//Check that the current index points to a name
				if (players_list[index].contains(",")) {
					//Create the new player object
					Player new_player = new Player(generatePlayerName(players_list[index]));
					new_player.setPosition(position_shortname.toUpperCase());
					//Get the current team of the player - skip over irrelevant information
					while (players_list[index+1].contains("COLLEGE")||players_list[index+1].equals("")) {
						index++;
					}
					String team_prefix =getTeamPrefix(players_list[index+1]);
					new_player.setTeam(team_prefix);
					addPlayerTeamList(new_player,players_list[index+1],team_prefix);
					new_player.setRating(0);
					//System.out.println(new_player.getTheWord()+" "+new_player.getTeam());
					index++;
					latest_roster_players.add(new_player);
				}
			}
			index++;
		}
	}
	
	//Called by generatePlayers()
	//Adds the players to the dictionary players_on_team_list - so that they are grouped with their team mates
	private static void addPlayerTeamList(Player player_given, String full_team_name, String team_prefix) {
		//Get the list of players for given team
		ArrayList<Player> team_player_list = players_on_team_list.get(team_prefix);
		//If the team has not been intialized - create the Arraylist of Players and add player to list
		if (team_player_list == null) {
			team_player_list = new ArrayList<Player>();
			team_player_list.add(player_given);
			//Create new team
			createTeam(full_team_name,team_prefix);
		}
		//Else - simply add player to the list
		else {
			team_player_list.add(player_given);
		}
		players_on_team_list.put(team_prefix, team_player_list);
	}

	//Called by generatePlayers()
	//Change the name from "LASTNAME, FIRSTNAME" to "FIRSTNAME LASTNAME" - return new name
	private static String generatePlayerName(String player_name) {
		String[] names = player_name.split(", ");
		return names[1] + " "+ names[0];
	}
	
	//Called by generatePlayers()
	//Get the team prefix of the given full team name - return the team prefix 
	//i.e. "Atlanta Hawks" to "ATL"
	private static String getTeamPrefix(String team_given) {
		String[] split_name = team_given.split(" ");
		if (split_name.length==3) {
			if (split_name[0].equals("Portland")) {
				return "POR";
			}
			if (split_name[0].equals("Oklahoma")) {
				return "OKC";
			}
			return split_name[0].substring(0,1)+split_name[1].substring(0,1)+split_name[2].substring(0,1);
		}
		else if (team_given.equals("Brooklyn Nets")) {
			return "BKN";
		}
		else if (team_given.contains("LA Clippers")) {
			return "LAC";
		}
		else {
			return team_given.substring(0, 3).toUpperCase();
		}
	}
	
	//Called by addPlayerTeamList()
	//Creates the new team with their attributes (Team Name, Team Prefix, Team Division, Team Conference);
	//Adds the newly created team to the teamcounter database
	private static void createTeam(String full_team_name,String team_prefix) {
		Team new_team_object = new Team(full_team_name);
		new_team_object.setPrefix(team_prefix);
		if (team_prefix.equals("ORL")||team_prefix.equals("CHA")||team_prefix.equals("MIA")||team_prefix.equals("WAS")||team_prefix.equals("ATL")) {
			new_team_object.setDivision("Southeast");
			new_team_object.setConference("EAST");
		}
		else if (team_prefix.equals("BKN")||team_prefix.equals("BOS")||team_prefix.equals("PHI")||team_prefix.equals("NYK")||team_prefix.equals("TOR")) {
			new_team_object.setDivision("Atlantic");
			new_team_object.setConference("EAST");
		}	
		else if (team_prefix.equals("CHI")||team_prefix.equals("CLE")||team_prefix.equals("IND")||team_prefix.equals("MIL")||team_prefix.equals("DET")) {
			new_team_object.setDivision("Central");
			new_team_object.setConference("EAST");
		}
		else if (team_prefix.equals("LAC")||team_prefix.equals("LAL")||team_prefix.equals("SAC")||team_prefix.equals("PHO")||team_prefix.equals("GSW")) {
			new_team_object.setDivision("Pacific");
			new_team_object.setConference("WEST");
		}
		else if (team_prefix.equals("DEN")||team_prefix.equals("MIN")||team_prefix.equals("OKN")||team_prefix.equals("POR")||team_prefix.equals("UTA")) {
			new_team_object.setDivision("Northwest");
			new_team_object.setConference("WEST");
		}	
		else if (team_prefix.equals("DAL")||team_prefix.equals("HOU")||team_prefix.equals("MEM")||team_prefix.equals("NOP")||team_prefix.equals("SAS")) {
			new_team_object.setDivision("Southwest");
			new_team_object.setConference("WEST");
		}	
		latest_roster_teamcounter.add(new_team_object);
	}
	
	//Puts the players on their respective teams
	//For every team add the players in their respective players_on_team_list ArrayList
	private static void fillOutRosters() {
		for (int team_index=0;team_index<latest_roster_teamcounter.size();team_index++) {
			String team_prefix = latest_roster_teamcounter.get(team_index).getPrefix();
			//Adds all the players under the team prefix to the team's player list
			for (int player_index=0; player_index<players_on_team_list.get(team_prefix).size();player_index++) {
				latest_roster_teamcounter.get(team_index).setPlayerslist(players_on_team_list.get(team_prefix).get(player_index));
			}
		}
	}
	
	//Rearranges the teams so that they are alphabetical order by full team name
	//i.e. Atlanta Hawks will have index 0 and Washington Wizards will have index 29
	private static void rearrangeTeams() {
		ArrayList<Team> tmp_team_counter = new ArrayList<Team>();
		//Add all the teams to the temporary team array
		for(int i = 0; i < latest_roster_teamcounter.size(); i++) {
			tmp_team_counter.add(latest_roster_teamcounter.get(i));
		}
		//Reorder the teams in the temporary array so that they are in alphabetical order;
		for (int i = 0; i < latest_roster_teamcounter.size(); i++) {
			for (int j = i + 1; j < latest_roster_teamcounter.size(); j++) {
				//Swap the teams placement of the teams in the array if the order is incorrect
				if (tmp_team_counter.get(i).getTeamName().compareTo(tmp_team_counter.get(j).getTeamName())>0) {
					Team temp = tmp_team_counter.get(i);
					tmp_team_counter.set(i,tmp_team_counter.get(j));
					tmp_team_counter.set(j,temp);
				}
			}
		}
		//Put the teams in the new sorted order in the actual array
		for(int i = 0; i < latest_roster_teamcounter.size(); i++) {
			latest_roster_teamcounter.set(i, tmp_team_counter.get(i));
		}
	}

	//Scraps the internet to get the current player statistics 
	//Using the nbastuffer website, get all the player averages
	//Update the Player Objects to have the stats
	private static void getStatisticsWebScrap() throws IOException {
		URL url = new URL("https://www.nbastuffer.com/2019-2020-nba-player-stats/");
        // Get the input stream through URL Connection
        URLConnection connection = url.openConnection();
        InputStream inputstream = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
        String line = null;
        // Find the line containing the list of players and extract the statistic information
        while ((line = br.readLine()) != null) {
        		if (line.contains("column-1") && (!line.contains("RANK"))) {
        			String [] player_attribute_list = extractPlayerStats(line);
        			setPlayerAttributes(player_attribute_list);
        		}
        }
	}
	
	//Called by getStatisticsWebScrap
	//Given a string containing player info such as name, team, ppg, rpg
	//Call a regular expression in order to put these items in different indexes of a string array
	private static String[] extractPlayerStats(String newString) {
		//This is the old regular expression >[0-9]{1}[0-9]*[.]*[0-9]*<
		String[] player_attribute_list = newString.split("(<\\/td>){0,1}<td class=\"column-[0-9]*\">");
		return player_attribute_list;
	}
	
	//Called by getStatisticsWebScrap
	//Given a string array that contains a list of player info (i.e. name, team, ppg, rpg)
	//Assign the statistics to the player's properties
	//Calculate the player's rating based on their statistics
	private static void setPlayerAttributes(String[] given_player_attribute_list) {
		String team_prefix = given_player_attribute_list[3].toUpperCase();
		if (team_prefix.equals("SAN")) {
			team_prefix = "SAS";
		}
		else if (team_prefix.equals("NOR")) {
			team_prefix = "NOP";
		}
		else if (team_prefix.equals("BRO")) {
			team_prefix = "BKN";
		}
		else if (team_prefix.equals("GOL")) {
			team_prefix = "GSW";
		}
		//1 - Find the player object 2- assign the values to the player 3- calculate the player rating
		//Values assigned are MPG,UP,PPG,RPG,APG,SPG,BPG,ORTG,DRTG
		for (int player_index = 0; player_index<players_on_team_list.get(team_prefix).size();player_index++) {
			if (given_player_attribute_list[2].equals(players_on_team_list.get(team_prefix).get(player_index).getPlayerName())) {
				assignRealStatistics(given_player_attribute_list,team_prefix,player_index);
				//Once the player is found - exit for loop
				player_index = players_on_team_list.get(team_prefix).size();
			}

		}
	}
	
	//Called by setPlayerAttributes
	//Assigns the MPG,UP,PPG,RPG,APG,SPG,BPG,ORTG,DRTG to the given player
	//Calculates the player rating
	private static void assignRealStatistics(String[] given_player_attribute_list,String team_prefix, int player_index) {
		Player current_player = players_on_team_list.get(team_prefix).get(player_index);
		current_player.setMPG(getFloatValue(given_player_attribute_list[7]));
		current_player.setUP(getFloatValue(given_player_attribute_list[9]));
		current_player.setPPG(getFloatValue(given_player_attribute_list[19]));
		current_player.setRPG(getFloatValue(given_player_attribute_list[20]));
		current_player.setAPG(getFloatValue(given_player_attribute_list[22]));
		current_player.setSPG(getFloatValue(given_player_attribute_list[24]));
		current_player.setBPG(getFloatValue(given_player_attribute_list[25]));
		current_player.setORTG(getFloatValue(given_player_attribute_list[28]));
		current_player.setDRTG(parseDRTG(given_player_attribute_list[29]));
		//Calculates the Player rating based on the statistics inputed
		NBARatingCalculator.calculatePlayerRating(current_player);
	}
	
	//Called by assignRealStatistics
	//Given a string extract the float value from the string
	//Returns float value extracted - else returns 0
	private static float getFloatValue(String number_in_string_form) {
		if ((number_in_string_form.length()>0) && (!number_in_string_form.equals("<\\/td>"))) {
			return Float.parseFloat(number_in_string_form);
		}
		else {
			return 0;
		}
	}
	
	//Called by assignRealStatistics
	//Given a string extract the float value from the string
	//At the end of the DRTG the chars </td> were attached at the end, so this function disregards the extra chars
	//Returns float value extracted - else returns 0
	private static float parseDRTG(String number_in_string_form) {
		if ((number_in_string_form.length()>0) && (!number_in_string_form.equals("</td>"))) {
			//Get rid of the "</td>" at the end of string and return the float
			int index = number_in_string_form.indexOf("<");
			return Float.parseFloat(number_in_string_form.substring(0, index));
		}
		else {
			return 0;
		}
	}
	
	//Reads the team files based on the directory name given
	//Initially the format was different format such as nbanotes - so use the readOldFile for older files
	//Else use the readFile
	//Reads each of the team's players and ratings and rankings
	//Creates the team and players objects
	public static void readDirectory(String directory_name) throws IOException {
			//Based on the directory_name given update/initialize the appropriate ArrayList of Players and Teams
			if (directory_name.equals("latestRoster")) {
				//Clear the previous entries
				latest_roster_teamcounter.clear();
				latest_roster_players.clear();
				//Read the team files
				NBAFileReadWrite.readFile(directory_name,latest_roster_teamcounter,latest_roster_players);
			}
			// Else read the files for the Editable Roster
			else {
				//Clear the previous entries
				editable_roster_teamcounter.clear();
				editable_roster_players.clear();
				//Read the team files
				NBAFileReadWrite.readFile(directory_name,editable_roster_teamcounter,editable_roster_players);
			}
	}
}
