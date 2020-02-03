import java.io.*;

import javax.swing.*;
import java.awt.*;

import java.util.*;
import java.lang.*;
import java.net.*;
public class NBAWebScrap{
	
	static Scanner scan = new Scanner(System.in);
	static ArrayList<Player> latest_roster_players = new ArrayList<Player>();
	static ArrayList<Team>  latest_roster_teamcounter = new ArrayList<Team>();
	static ArrayList<Player> editable_roster_players = new ArrayList<Player>();
	static ArrayList<Team> editable_roster_teamcounter = new ArrayList<Team>();
	static Dictionary<String,ArrayList<Player>> players_on_team_list = new Hashtable<String,ArrayList<Player>>();
	
	public static void main(String args []) throws IOException {
		int current_roster = 1;//Initially Load Editable Roster
		String myfile = args[current_roster];
		readDirectory(myfile);
		int choice = -1;
		while (choice != 0) {
			//Lists the options when using the Editable Roster
			if (current_roster ==1) {
				System.out.println("\t\t\tCurrently Viewing: Editable Roster");
				System.out.println("[0] Exit the Program, [1] Switch to Latest Roster,[2] Edit Personal Roster, [3] Save Personal Roster, [4] Play with Roster ");
			}
			//Lists the options when using the Latest Roster
			else {
				System.out.println("\t\t\tCurrently Viewing: Latest Roster");
				System.out.println("[0] Exit the Program, [1] Switch to Editable Roster, [2] View Latest Roster, [3] Get Latest Team Rosters, [4] Overwrite Personal Roster, [5] Play with Roster ");
			}
			//Prompt the User
			System.out.print("You:");
			choice = scan.nextInt();
			if (choice ==1) {
				//Called when using wishes to switch rosters
				if (current_roster ==1) {
					//Editable Roster --> Latest Roster
					current_roster = 0;
				}
				else {
					//Latest Roster --> Editable Roster
					current_roster = 1;
				}
				readDirectory(args[current_roster]);
			}
			if (choice ==2) {
				System.out.println("Coming Soon");
			}
			if (choice ==3 && current_roster ==0) {
				//Scraps the internet to get the latest player statistics and team rosters
				getLatestUpdates();
				
				//Prompts the user if they would like to overwrite editable roster with latest roster
				System.out.print("[0] Ignore, [1] Overwriet Personal Roster with Latest Roster");
				int roster_choice = scan.nextInt();
				if (roster_choice ==1) {
					choice = 4;
				}
				
			}
			else if (choice == 3 && current_roster==1) {
				//Saves the Editable Roster
				writeOutfile(false,"editableRoster",editable_roster_teamcounter);
				System.out.print("Editable Roster has been saved");
			}
			if (choice ==4  && current_roster ==0) {
				//Overwrites the editable roster
				writeOutfile(false,"editableRoster",latest_roster_teamcounter);
				System.out.print("Editable Roster has been Overwritten");
				readDirectory(args[1]);
			}
			if (choice ==5  && current_roster ==0) {
				System.out.println("Coming Soon");
			}
		}
	}
	
	//Called to get the latest rosters and player statistics
	//Scraps the internet for latest statistics
	//Recreates the teams();
	//Calculates every player's rating
	//Set the player's scoring, rebounding, and assisting rating within their team
	//Writes those changes to the latestRoster directory
	public static void getLatestUpdates() throws IOException{
		latest_roster_teamcounter.clear();
		latest_roster_players.clear();
		players_on_team_list = new Hashtable<String,ArrayList<Player>>();
		//Scrap the internet to get all the current players and create the Player Objects
		webScrap();
		//Puts the players on their respective teams
		buildTeams();
		//Put the teams in alphabetical order
		rearrangeTeams();
		//Scrap the internet to get the statistics of the current season
		getStatisticsWebScrap();
		//Set the player's scoring, rebounding, and assisting rating within their team
		setLeagueWidePlayerRankings();
		//Rearranges the player's on their given teams so that higher rated players are presented higher
		organizeTeams(latest_roster_teamcounter);
		//Writes those changes to the latestRoster directory
		writeOutfile(false,"latestRoster",latest_roster_teamcounter);
		System.out.println("\n \t COMPLETED - RETRIEVED LATEST TEAM ROSTERS \n");
	}
	
	//Scraps ESPN's database of players by position to all the player, their current teams, and their position
	//By position - go to the website that stores the player info and grab the list of players at position
	//Use a regular expression to get only the players and their team
	//Creates the Player Objects
	public static void webScrap() throws IOException  {
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
	public static String webScrapPosition(String position_fullname, String position_shortname) throws IOException {
		System.out.println("Retreiving the "+position_fullname);
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
        System.out.println(position_fullname+" have been retrieved");
        return players_list;
	}
	
	//Called by webScrap()
	//Given a HTML line containing a list of players and their positions - call a regular expression
	//to get only the player's name and their current team
	//Return the list of player's name and current teams
	public static String[] getPlayersFromString(String players_list,String position_given) {
		//Shorten the string
		int index = players_list.indexOf("COLLEGE");
		String newString = players_list.substring(index);
		//Helpful tool towards building regular expressions --> https://regex101.com
		//The only expression to only get player's names and current teams">[a-zA-Z]*[ ]*[a-zA-Z.,]* [a-zA-Z.]*[ ]*[a-zA-Z]*<"
		System.out.println("Generating the list of "+position_given);
		//Call the regular expression to dismiss all the excess information
		String[] new_list = newString.split("[<\\/a><\\/td><td>]*<\\/td><\\/tr><tr class=\"[a-z]* player-[0-9]*-[0-9]*\"><td><a href=\"http:\\/\\/www.espn.com\\/nba\\/player\\/_\\/id\\/[0-9]*\\/[a-z.']*-[a-z']*[-]*[a-z]*[.]*\">|<\\/a><\\/td><td><a href=\"http:\\/\\/www.espn.com\\/nba\\/team\\/_\\/name\\/[a-z]*\\/[a-z]*-[76]*[a-z]*[-]*[a-z]*\">|<\\/a><\\/td><td>[A-Z]{1}[a-zA-Z]*|<\\/a><\\/td><td><\\/td><\\/tr><tr class=\"stathead\"><td colspan=\"3\">[A-Z]");
		System.out.println(position_given+" have been generated");
		return new_list;
	}
	
	//Called by webScrap()
	//Given a list of player's names and current teams - create the player object for player given
	public static void generatePlayers(String[] players_list,String position_fullname, String position_shortname) {
		System.out.println("Creating "+position_fullname);
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
		System.out.println(position_fullname+" Created");
	}
	
	//Called by generatePlayers()
	//Adds the players to the dictionary players_on_team_list - so that they are grouped with their team mates
	public static void addPlayerTeamList(Player player_given, String full_team_name, String team_prefix) {
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
	public static String generatePlayerName(String player_name) {
		String[] names = player_name.split(", ");
		return names[1] + " "+ names[0];
	}
	//Called by generatePlayers()
	//Get the team prefix of the given full team name - return the team prefix 
	//i.e. "Atlanta Hawks" to "ATL"
	public static String getTeamPrefix(String team_given) {
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
	public static void createTeam(String full_team_name,String team_prefix) {
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
	public static void buildTeams() {
		System.out.println("Add players to their respective teams");
		for (int team_index=0;team_index<latest_roster_teamcounter.size();team_index++) {
			String team_prefix = latest_roster_teamcounter.get(team_index).getPrefix();
			//Adds all the players under the team prefix to the team's player list
			for (int player_index=0; player_index<players_on_team_list.get(team_prefix).size();player_index++) {
				latest_roster_teamcounter.get(team_index).setPlayerslist(players_on_team_list.get(team_prefix).get(player_index));
			}
			
		}
		System.out.println("Players added to their respective teams");
	}
	
	//Rearranges the teams so that they are alphabetical order by full team name
	//i.e. Atlanta Hawks will have index 0 and Washington Wizards will have index 29
	public static void rearrangeTeams() {
		ArrayList<Team> tmp_team_counter = new ArrayList<Team>();
		//Add all the teams to the temporary team array
		for(int i = 0; i < latest_roster_teamcounter.size(); i++) {
			tmp_team_counter.add(latest_roster_teamcounter.get(i));
		}
		//Reorder the teams in the temporary array so that they are in alphabetical order;
		for (int i = 0; i < latest_roster_teamcounter.size(); i++) {
			for (int j = i + 1; j < latest_roster_teamcounter.size(); j++) {
				//Swap the teams placement of the teams in the array if the order is incorrect
				if (tmp_team_counter.get(i).getTheName().compareTo(tmp_team_counter.get(j).getTheName())>0) {
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
	public static void getStatisticsWebScrap() throws IOException {
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
	public static String[] extractPlayerStats(String newString) {
		//This is the old regular expression >[0-9]{1}[0-9]*[.]*[0-9]*<
		String[] player_attribute_list = newString.split("(<\\/td>){0,1}<td class=\"column-[0-9]*\">");
		return player_attribute_list;
	}
	
	//Called by getStatisticsWebScrap
	//Given a string array that contains a list of player info (i.e. name, team, ppg, rpg)
	//Assign the statistics to the player's properties
	//Calculate the player's rating based on their statistics
	public static void setPlayerAttributes(String[] given_player_attribute_list) {
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
			if (given_player_attribute_list[2].equals(players_on_team_list.get(team_prefix).get(player_index).getTheWord())) {
				assignRealStatistics(given_player_attribute_list,team_prefix,player_index);
				//Once the player is found - exit for loop
				player_index = players_on_team_list.get(team_prefix).size();
			}

		}
	}
	
	//Called by setPlayerAttributes
	//Assigns the MPG,UP,PPG,RPG,APG,SPG,BPG,ORTG,DRTG to the given player
	//Calculates the player rating
	public static void assignRealStatistics(String[] given_player_attribute_list,String team_prefix, int player_index) {
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
		calculatePlayerRating(current_player);
	}
	
	//Called by assignRealStatistics
	//Given a string extract the float value from the string
	//Returns float value extracted - else returns 0
	public static float getFloatValue(String number_in_string_form) {
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
	public static float parseDRTG(String number_in_string_form) {
		if ((number_in_string_form.length()>0) && (!number_in_string_form.equals("</td>"))) {
			//Get rid of the "</td>" at the end of string and return the float
			int index = number_in_string_form.indexOf("<");
			return Float.parseFloat(number_in_string_form.substring(0, index));
		}
		else {
			return 0;
		}
	}
	
	//Called by assignRealStatistics
	//Calculates the Player's defensive rating and offensive rating
	//Assigns the combination of these ratings with the player's actual statistics to get the rating
	public static void calculatePlayerRating(Player current_player) {
		float defensive_rating = calculateDefensiveRating(current_player);
		float offensive_rating = calculateOffensiveRating(current_player);
		int rating = (int) (Math.floor((((defensive_rating-0)*.65) + ((offensive_rating+0)*3)+(current_player.getRPG()/4)+(current_player.getAPG()+current_player.getPPG()/(current_player.getMPG()/36))+((current_player.getMPG()+12)*2))/2.75));
		//System.out.println(current_player.getTheWord()+" Overall Rating is "+ rating);
		current_player.setRating(rating);
	}
	
	//Called by calculateDefensiveRating
	//Using the defensive statistics -> DRTG, SPG, BPG, RPG with MPG -- calculate the given player's defensive rating
	//Returns a float of the rating
	public static float calculateDefensiveRating(Player current_player) {
		//Defensive Portion
		//DRTG: Min-88, Max-118 -->  Defensive Player of the Year - 88-95; All Defensive 1st Team - 95.1-101; Above Average 101.1-105;  Average 105.1-109; Below Average 109.1 - 118 
		//SPG: Min-0.1, Max-3.15 --> All-Time-Great - 2.55-3.15 ; League-Leader - 1.75-2.54; Above Average -  1-1.74; Average - 0.5-.99; Below Average - 0.1-.49
		//BPG: Min-0.1, Max-3.75 --> All-Time-Great - 3.15-3.75; League-Leader - 2-3.15; Above Average - .75-1.99; Average - .4-.74; Below Average - 0.1-.39
		float defensive_rating = (118 - current_player.getDRTG());
		if (current_player.getPosition().contains("G")||current_player.getPosition().equals("SF")){
			defensive_rating+=(20 * current_player.getSPG()) + (15 * current_player.getBPG());
		}
		else {
			defensive_rating+=(17 * current_player.getSPG()) + (18 * current_player.getBPG());
		}	
		defensive_rating=((defensive_rating / current_player.getMPG()) * 36) / 2;
		defensive_rating+=((118 - current_player.getDRTG()) / 4);
		defensive_rating+=(current_player.getRPG() / 15);
		while (defensive_rating > 100) {
			defensive_rating /= 2;
		}
		if (defensive_rating > 75) {
			defensive_rating /= 2;
		}
		if (defensive_rating > 50 && current_player.getMPG()< 18) {
			defensive_rating /= 2;
		}
		if (defensive_rating < 28) {
			defensive_rating = 28;
		}
		//System.out.println(current_player.getTheWord()+" Defensive Rating is "+defensive_rating);
		return defensive_rating;
	}
	
	//Called by calculateOffensiveRating
	//Using the offensive statistics -> ORTG, UP, PPG, APG with MPG -- calculate the given player's offensive rating
	//Returns a float of the rating
	public static float calculateOffensiveRating(Player current_player) {
		//Offensive Portion
		//ORTG: Smalls-Min: 80  Bigs-Max:135 --> All-Time-Great - 
		float offensive_rating = (current_player.getUP() * current_player.getORTG()) / 100;
		offensive_rating+=(current_player.getAPG()*15);
		offensive_rating+=((current_player.getPPG()*30) / (current_player.getUP()+1));
		offensive_rating= (offensive_rating /current_player.getMPG())* 36;
		offensive_rating=(offensive_rating/(current_player.getUP() +1 )) *(current_player.getORTG()/(current_player.getUP()+1));
		offensive_rating+=(current_player.getMPG() -(36-current_player.getMPG()));
		offensive_rating = (offensive_rating+(current_player.getPPG()+(current_player.getAPG()*4)));
		offensive_rating = offensive_rating / ((current_player.getAPG()+current_player.getPPG()) / current_player.getUP());
		offensive_rating /=3;
		while (offensive_rating >40 && current_player.getMPG()<24) {
			offensive_rating-=(24-current_player.getMPG());
		}
		if (offensive_rating > 45 && current_player.getORTG()< 115) {
			offensive_rating /= 1.75;
		}
		if (offensive_rating  > current_player.getUP()* 3) {
			offensive_rating -=current_player.getUP();
		}
		if (offensive_rating < 28) {
			offensive_rating = 28;
		}
		//System.out.println(current_player.getTheWord()+" Offensive Rating is "+offensive_rating);
		return offensive_rating;
	}
	
	//For every team, update the players so that they are ranked statistically within their team
	//Player receiving a ranking for how well they rank respectively in scoring, rebounding, and assisting
	public static void setLeagueWidePlayerRankings() {
		for(int team_index=0;team_index<latest_roster_teamcounter.size();team_index++) {
			setTeamPlayerRankings(team_index);
		}
	}
	
	//Given a team, update the players on the team so they are ranked statistically within their team
	//Player receiving a ranking for how well they rank respectively in scoring, rebounding, and assisting
	public static void setTeamPlayerRankings(int team_index) {
		//Each of the following arrays are used to order the players by a respective stat
		//Higher rating are placed higher in the array
		//EX: if Player A scores more points than Player B on the team - Player A will have a higher index in the points_rankings
		int[] points_rankings = new int[latest_roster_teamcounter.get(team_index).leng()];
		int[] rebounds_rankings = new int[latest_roster_teamcounter.get(team_index).leng()];
		int[] assists_rankings = new int[latest_roster_teamcounter.get(team_index).leng()];
		for (int player_index =0; player_index<latest_roster_teamcounter.get(team_index).leng();player_index++) {
			//If first player being examined simply put them in respective arrays
			if (player_index ==0) {
				points_rankings[0] =(player_index);
				rebounds_rankings[0] =(player_index);
				assists_rankings[0] =(player_index);
			}
			else {
				//Add the player to the end of the array
				points_rankings[player_index] =(player_index);
				rebounds_rankings[player_index] =(player_index);
				assists_rankings[player_index] =(player_index);
				//Check the stats of the player and move the player's index up in the array if player has more of particular stat
				for (int switch_index = player_index;switch_index >0;switch_index--) {
					//if Player A at lower index has higher PPG than Player B at higher index --> move Player A up
					if (latest_roster_teamcounter.get(team_index).getPlayer(points_rankings[switch_index]).getPPG()>latest_roster_teamcounter.get(team_index).getPlayer(points_rankings[switch_index-1]).getPPG()) {
						int player_index_moving_up = points_rankings[switch_index];
						points_rankings[switch_index] = points_rankings[switch_index-1];
						points_rankings[switch_index-1] =player_index_moving_up;
					}
					//if Player A at lower index has higher RPG than Player B at higher index --> move Player A up
					if (latest_roster_teamcounter.get(team_index).getPlayer(rebounds_rankings[switch_index]).getRPG()>latest_roster_teamcounter.get(team_index).getPlayer(rebounds_rankings[switch_index-1]).getRPG()) {
						int player_index_moving_up = rebounds_rankings[switch_index];
						rebounds_rankings[switch_index] = rebounds_rankings[switch_index-1];
						rebounds_rankings[switch_index-1] =player_index_moving_up;
					}
					//if Player A at lower index has higher APG than Player B at higher index --> move Player A up
					if (latest_roster_teamcounter.get(team_index).getPlayer(assists_rankings[switch_index]).getAPG()>latest_roster_teamcounter.get(team_index).getPlayer(assists_rankings[switch_index-1]).getAPG()) {
						int player_index_moving_up = assists_rankings[switch_index];
						assists_rankings[switch_index] = assists_rankings[switch_index-1];
						assists_rankings[switch_index-1]= player_index_moving_up;
					}
				}
			}
		}
		//Set the scoring rankings for all the players on the teams
		for (int points_index=0;points_index<points_rankings.length;points_index++) {
			latest_roster_teamcounter.get(team_index).getPlayer(points_rankings[points_index]).setScoring(points_index+1);
		}
		//Set the rebounding rankings for all the players on the teams
		for (int rebounds_index=0;rebounds_index<points_rankings.length;rebounds_index++) {
			latest_roster_teamcounter.get(team_index).getPlayer(rebounds_rankings[rebounds_index]).setRebounding(rebounds_index+1);
		}
		//Set the assisting rankings for all the players on the teams
		for (int assists_index=0;assists_index<points_rankings.length;assists_index++) {
			latest_roster_teamcounter.get(team_index).getPlayer(assists_rankings[assists_index]).setAssisting(assists_index+1);
		}
	}

	//Rearranges the ratings of the team so that all the best rated players at every 
	//position are positioned first
	public static void organizeTeams(ArrayList<Team> given_league_rosters) {
		String[] fixed_basketball_positions =new String[5];
		fixed_basketball_positions[0]="PG";
		fixed_basketball_positions[1]="SG";
		fixed_basketball_positions[2]="SF";
		fixed_basketball_positions[3]="PF";
		fixed_basketball_positions[4]="C";
		ArrayList<Player> position_players_array = new ArrayList<Player>();//Stores the players who play same position on the team
		ArrayList<Player> new_team_players_array = new ArrayList<Player>();//Stores all players on one team in the correct order
		for (int team_index=0; team_index< given_league_rosters.size();team_index++) {
			//For all teams, go through each position and reorder players by rating
			for (int position_index=0; position_index<5;position_index++) {
				for (int team_player_index=0; team_player_index<given_league_rosters.get(team_index).leng();team_player_index++) {
					//Get all the players who play the current viewed position
					if(given_league_rosters.get(team_index).getPlayer(team_player_index).getPosition().equals(fixed_basketball_positions[position_index])) {
						position_players_array.add(given_league_rosters.get(team_index).getPlayer(team_player_index));
					}
				}
				//Reorder the list of players of by rating so that higher rated players at position are listed first
				if(position_players_array.size()>1) {
					//Reorder the array so that higher rated players are presented first the array
					for (int h=position_players_array.size()-1; h>0;h--) {
						//Switch the ordering if the current player has a higher rating than player in preceding index
						if(position_players_array.get(h).getRating()>position_players_array.get(h-1).getRating()) {
							Player tmp = position_players_array.get(h-1);
							position_players_array.set(h-1, position_players_array.get(h));
							position_players_array.set(h,tmp);
							//Reset the position - in the case that the multiple people need to be rearranged
							h=position_players_array.size()-1;
						}
					}
					//After reordering the players by rating -- add the players to the final "new_team_players_array" array
					for (int i=0; i< position_players_array.size();i++) {
						new_team_players_array.add(position_players_array.get(i));
					}
				}
				//If only one player at the position -- add the player to the final "new_team_players_array" array
				else if  (position_players_array.size()==1){
					new_team_players_array.add(position_players_array.get(0));
				}
				//Clear the array
				position_players_array.clear();
			}
			//Clear all the current players on the team
			given_league_rosters.get(team_index).clear();
			//(Re)Add the players in their new sorted order to the team.
			for (int u=0;u<new_team_players_array.size();u++) {
				given_league_rosters.get(team_index).setPlayerslist(new_team_players_array.get(u));
			}
			//Clear the array
			new_team_players_array.clear();
		}
	}
	
	//Reads the team files based on the directory name given
	//Uses readFile
	//Reads each of the team's players and ratings and rankings
	//Creates the team and players objects
	public static void readDirectory(String directory_name) throws IOException {
			//Based on the directory_name given update/initialize the appropriate ArrayList of Players and Teams
			if (directory_name.equals("latestRoster")) {
				//Clear the previous entries
				latest_roster_teamcounter.clear();
				latest_roster_players.clear();
				//Read the team files
				readFile(directory_name,latest_roster_teamcounter,latest_roster_players);
			}
			// Else read the files for the Editable Roster
			else {
				//Clear the previous entries
				editable_roster_teamcounter.clear();
				editable_roster_players.clear();
				//Read the team files
				readFile(directory_name,editable_roster_teamcounter,editable_roster_players);
			}
	}

	//Reads the files in the given directory and assigns each player their
	//rating, Assigns players to their respective teams, Assigns players
	//to the players index, takes in account injures, the year of the player.
	//if they have been updated in NBA 2k18, their position and if they are
	//an all star talent.
public static void readFile(String direct, ArrayList<Team> given_teamcounter, ArrayList<Player> given_players)throws IOException {
	File directory = new File(direct);
	//Get all the files in the given directory
	File[] fList =directory.listFiles();
	File file;
	//for each file in the directory
	for (int i =0; i<fList.length;i++){
		file = fList[i];
		// If the file is not hidden --> process the file...
		//For every team - create/initialize the team and players
		if(!file.isHidden()){
			int boundary_index;//Used often to get the endIndex for substring
			//Get the Full Team Name i.e. "AtlantaHawks01" --> "Atlanta Hawks"
			boundary_index = file.getName().lastIndexOf('0');
			String full_team_name = file.getName().substring(0, boundary_index);
			if (full_team_name.contains("0")) {
				boundary_index = full_team_name.lastIndexOf('0');
				full_team_name = full_team_name.substring(0, boundary_index);
			}
			//Create the team
			Team current_team = new Team (full_team_name);
			given_teamcounter.add(current_team);
			int team_rotation =0;
			Scanner fr = new Scanner((file));
			//Get the team's prefix
			String prefix =fr.next();
			prefix= prefix.substring(0, 3);
			//Get the team's conference
			String conference = fr.next();
			//Get the team's division
			String division= fr.next();
			current_team.setDivision(division);
			//Set the team's attributes
			current_team.setPrefix(prefix);
			current_team.setConference(conference);
			//Get the first position that some players of the team play
			String current_position =fr.next();
			boundary_index = current_position.lastIndexOf(':');
			current_position = current_position.substring(0, boundary_index);
			//For every player - create the player and get their name, position, rating, and team rankings
			while(fr.hasNext()){
				if (!fr.hasNext("SG:")&&!fr.hasNext("SF:")&&!fr.hasNext("PF:")&&!fr.hasNext ("C:")){
				String first_name=fr.next();
				String last_name= fr.next();
				String last_name_extension ="";
				//Check if the player has an extension to the end of the name - if so add to their full name
				if (!fr.hasNextInt()) {
					last_name_extension = " " + fr.next();
				}
				String full_name = first_name + " "+ last_name + last_name_extension;
				//Create the new player
				Player current_player = new Player (full_name);
				//Add the Player to players index
				given_players.add(current_player);
				//Get the player's rating
				float player_rating=fr.nextInt();
				//Set the Player Attributes
				current_player.setFilelist((int)player_rating);
				current_player.setPosition(current_position.toUpperCase());
				current_player.setRating(player_rating);
				current_player.setTheWord(full_name);
				current_player.setTeam(prefix);
				current_team.setPlayerslist(current_player);
				//If Player is an Allstar - set the player's Allstar attribute
				if(fr.hasNext("A")){
					current_player.setAllstar(fr.next());
				}
				//If Player has been Upgraded -set the player's Upgraded attribute
				if(fr.hasNext("U")){
					current_player.setUpgraded(fr.next());
				}
				//If Player is a Rookie -set the player's Rookie attribute
				if(fr.hasNext("R")){
					current_player.setYear(fr.next());
				}
				//If Player is Injured -set the player's Injured attribute
				if (fr.hasNext("I")) {
					fr.next();
					current_player.setInjury(true);
				}
				//Set the Player Ranking within their team when it comes to Points
				if (fr.hasNextInt()) {
					current_player.setScoring(fr.nextInt());
					//If the player is not injured increment and update the number of player in team's rotation
					if (current_player.getInjury()==false) {
						team_rotation++;
						current_team.setRotation(team_rotation);
					}
				}
				//Set the Player Ranking within their team when it comes to Rebounds
				if (fr.hasNextInt()) {
					current_player.setRebounding(fr.nextInt());
				}
				//Set the Player Ranking within their team when it comes to Assists
				if (fr.hasNextInt()) {
					current_player.setAssisting(fr.nextInt());
				}
			}
			//If we are about to change the current position to either SG,SF,PF,C - update the constant position
			if (fr.hasNext("SG:")||fr.hasNext("SF:")||fr.hasNext("PF:")||fr.hasNext("C:")){
				current_position = fr.next();
				boundary_index = current_position.lastIndexOf(':');
				current_position = current_position.substring(0, boundary_index);
			}
		}
		fr.close();
	}
}
	
}

	//Called if the user wishes to enter their own desired path for the team files
	//User enters their desired path
	//The function walks through the path - creating the directories that do not exist along the way
	//Returns the string path entered by the user
	public static String getDesiredPath() {
		//Create the path for the file
		System.out.println("Please seperate your directories with \"/\"");
		System.out.println("Enter the desired path to put the team files: ");
		//User enters the desired path
	    scan.nextLine();
	    String path = scan.nextLine();
	    //Add the extra "/" at the beginning at the end if the user forgets to include team
	    if (path.length()>0) {
	    	  if (path.charAt(0)!='/') {
		    		path = "/" + path;
		    }
		    if (path.charAt(path.length()-1)!='/') {
		    		path = path + "/";
		    }
	    }
	    //Get the individual directories
	    String[] individual_directories = path.split("/");
	    //Given the path, walk through each directory check that the every directory exists
	    if (individual_directories.length > 0) {
	        String add_path = "/" + individual_directories[0];
		    for (int i =1; i<=individual_directories.length;i++) {
		    		if (add_path.length()>0) {
			    		//Check that the directory exists if not create the directory
		    			File path_file = new File(add_path);
		    		    if(!path_file.exists()){
		    		    		path_file.mkdir();
		    		    }
		    		}
		    		//Add to the path - to check the existence of the following directory
		    		//as long there is a another directory
		    		if (i<individual_directories.length) {
		    			if (!individual_directories[i].equals("")) {
			    			add_path = add_path + "/" +individual_directories[i];
			    		}
		    		}
		    		
		    }
	    }
	    return path;
	}
	
	//Called by writeOutfile
	//Gets the User current path and adds the directory_name directory to the path
	//If the directory directory_name does not exist - create the directory
	//Returns the string of the path
	public static String getCurrentLatestRosterDirectory(String directory_name) {
		//Get the Current Path
		String showPath = new File("").getAbsolutePath();
		//Create the final path
		showPath = showPath + "/" + directory_name;
		//Check that the path exists - if not created the directory directory_name
		File path_file = new File(showPath.substring(0));
	    if(!path_file.exists()){
	    		path_file.mkdir();
	    }
	    showPath = showPath+"/";
	    return showPath;
	}
	
	//Writes the out each individual team's roster and ratings and outputs
	//them into a directory for the NBAGame class
	//targeted_path - determines whether the user gets to enter their desired path (true - user may enter own path)
	//if targeted_path is false - directory_name will be name of the directory team files are stored
	//Creates a file for each team
	public static void writeOutfile(boolean targeted_path,String directory_name,ArrayList<Team> given_team_rosters) throws IOException{
		String path;
		if (targeted_path) {
			//Get the user's desired path for the team files
		   path = getDesiredPath();
		}
		else {
			//Else -get the current path and add the final directory_name to the path
			path = getCurrentLatestRosterDirectory(directory_name);
		}
		//If the there is an error with the rosters given
		if (given_team_rosters.size() == 0) {
			System.out.println("Download Latest Rosters");
		}
		else {
			for (int team_index=0;team_index<given_team_rosters.size();team_index++) {
				//Every file should begin with the user team name followed by a "0" and the team's index;
				//EX: AtlantaHawks01.txt
				String string_offset = "0";
				//Create the filename for the team
				String filename = given_team_rosters.get(team_index).getTheName()+string_offset+team_index+".txt";

			    File file = new File(path+filename);
			    //Check that the path exists before writing the files out
			    if (new File (path).isDirectory()) {
			    		BufferedWriter out = new BufferedWriter(new FileWriter (file));
					//Write the initial team info
					out.write(given_team_rosters.get(team_index).getPrefix()+(given_team_rosters.get(team_index).leng()-3)+"\n");
					out.write(given_team_rosters.get(team_index).getConference()+"\n"+given_team_rosters.get(team_index).getDivision()+"\n"+"\n");
					//Write the first position
					out.write(given_team_rosters.get(team_index).getPlayer(0).getPosition()+":\n");
					//Write all the players on the team with their name, rating, upgrade setting, and rookie status
					for(int player_index=0; player_index<given_team_rosters.get(team_index).leng(); player_index++) {
						if (player_index>0) {
							//If the player plays a different position from the prior player -- write the new position
							if (!given_team_rosters.get(team_index).getPlayer(player_index).getPosition().equals(given_team_rosters.get(team_index).getPlayer(player_index-1).getPosition())) {
								out.write("\n"+given_team_rosters.get(team_index).getPlayer(player_index).getPosition()+":\n");
							}
						}
						//Write out the current player with the name, rating, upgraded status,rankings
						out.write(given_team_rosters.get(team_index).getPlayer(player_index).toFinalString()+"\n");
					}
					out.close();
			    }
			    //If the path does not exists
			    else {
			    		System.out.println("Please given a real path");
			    		return;
			    }
			}
			System.out.println("\t COMPLETED ---- ROSTER HAS BEEN WRITTEN\n");
		}
	}
	
}
