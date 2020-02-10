import java.io.*;
import java.util.*;

/*
 * The files allows the user to edit,view the team rosters and players 
 * Users can only manipulate the rosters in the editableRoster directory
 * 
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBARoster  {
	
	static Scanner scan = new Scanner(System.in);
	static ArrayList<Player> editable_roster_players = NBAMainSimulator.editable_roster_players;
	static ArrayList<Team> editable_roster_teamcounter = NBAMainSimulator.editable_roster_teamcounter;
	static ArrayList<Player> latest_roster_players = NBAMainSimulator.latest_roster_players;
	static ArrayList<Team>  latest_roster_teamcounter = NBAMainSimulator.latest_roster_teamcounter;
	private static int roster_cap = 17;

	public static boolean mainFunctionCall(String directory_name) throws IOException {
		System.out.println("\n"+"\t\t"+"Welcome the the NBA Roster Editor"+"\n");
		int choice = 10;
		while (choice>0){
			//If the latestRoster is the current roster only give user option to view rosters
			if (directory_name == "latestRoster") {
				choice = runLatestRosterOptions(directory_name);
				//If true Changes the Active Roster
				if (choice == 2) {
					return true;
				}
			}
			//If the editableRoster is the current roster give the user full access to the team rosters
			else {
				choice = runEditableRosterOptions(directory_name);
				//If true Changes the Active Roster
				if (choice == 7) {
					return true;
				}
			}
		}
		return false;
	}
	
	//If the latestRoster is the active roster 
	//Inform the user and give the user the options to view the team rosters or swithc to editableRoster
	//Returns user's choice if they wish to exit or switch the active roster
	private static int runLatestRosterOptions(String directory_name) {
		int choice = 1;
		while (choice == 1) {
			System.out.println("\t Active Roster: "+directory_name);
			System.out.println("[0] Return to Main Menu, [1] View Team, [2] Switch Active roster to editableRoster");
			System.out.print("You:");
			//Prompts the User
			choice = scan.nextInt();
			//User can view the players and their ratings on the current team
			if (choice ==1) {
				viewTeam(latest_roster_teamcounter);
			}
		}
		return choice;
	}

	//If the editable is the active roster
	//Inform the user and give the user the options to edit the player and teams on the roster
	//Returns the user's choice if they wish to exit or switch the active roster
	private static int runEditableRosterOptions(String directory_name) throws IOException {
		organizeTeam();
		int choice = 1;
		while (choice > 0) {
			System.out.println("\t Active Roster: "+directory_name);
			System.out.println("[0] Return to Main Menu, [1] Edit Player Rating, [2] Add A Player, [3] Remove A Player, [4] Trade A Player");
			System.out.println("[5] Assign Player to Team, [6] View A Team, [7] Switch Active Roster to latest Roster");
			System.out.println("[8] Permanently save roster changes");
			System.out.print("You:");
			choice = scan.nextInt();
			if (choice==1) {
				//User wants to edit a player
				editPlayer();
			}
			else if (choice==2) {
				//User wants to add a free agent or created player to a team
				addPlayer();
			}
			else if (choice==3) {
				//User wants to remove a player from a team
				removePlayer();
			}
			else if (choice==4) {
				//User wants to make trade between two teams
				tradeTwoTeams();
			}
			else if (choice==5) {
				//User wants to assign a player to team
				assignNewTeam();
			}
			else if (choice==6) {
				//User wants to view roster of team
				viewTeam(editable_roster_teamcounter);
			}
			/*
			 else if (choice==7) {
				deleteTeam();
			}
			else if (choice==8) {
				addTeam();
			}
			*/
			else if (choice == 7) {
				//Users wants to switch active roster
				return choice;
			}
			else if (choice == 8) {
				//Users wants to save changes after quitting program
				System.out.println("[0] Cancel Changes [1] Permantetly Save Roster Changes");
				choice = scan.nextInt();
				if (choice ==1) {
					NBAFileReadWrite.writeOutfile(false,"editableRoster",editable_roster_teamcounter);
				}
			}
		/*	
		 * else if (choice == 100 ) {
				for (int j=0; j<editable_roster_teamcounter.size();j++) {	
					System.out.println(getAverage(j));
				}
			}
			*/
			organizeTeam();
		}
		return choice;
	}
	//Called by the main function when printing the averages
	// Gets the average rating of the team and returns it in a String.
	//Gets both the average ratings of the all the players on the team and the non rookies on the team
	public static String getAverage(int team_index)  throws IOException {
		float all_average=0;
		float all_ratings = 0;
		float total_players=0;
		float no_rookie_average=0;
		float total_non_rookie_players=0;
		float no_rookie_ratings = 0;
		//Gather the ratings of all the players 
		for(int i=0;i<editable_roster_teamcounter.get(team_index).leng();i++) {
			float current_player_rating = editable_roster_teamcounter.get(team_index).getPlayer(i).getRating();
			if (current_player_rating > 0) {
				all_ratings+=current_player_rating;
				total_players++;
				//Gather the ratings of all the players who are not rookies
				if(!editable_roster_teamcounter.get(team_index).getPlayer(i).getRookieStatus()) {
					no_rookie_ratings+=current_player_rating;
					total_non_rookie_players++;
				}
			}
		}
		//Calculate averages
		all_average = all_ratings/total_players;
		no_rookie_average=no_rookie_ratings/total_non_rookie_players;
		String average = ("Average for the "+editable_roster_teamcounter.get(team_index).getTeamName()+" = "+ all_average+"\n");
		editable_roster_teamcounter.get(team_index).setAvg(all_average);
		average = average +"Average without the rookies for the "+editable_roster_teamcounter.get(team_index).getTeamName()+ " = "+no_rookie_average+"\n";
		return average;	
	}
	
	//Capitalizes the first character in the string and Lowercases the rest of the characters
	private static String TitleCase(String word_given) {
		String last_characters = word_given.substring(1).toLowerCase();
		String first_character = word_given.substring(0, 1).toUpperCase();
		return first_character.concat(last_characters);
	}
	
	//Prompts the user to manually input the Player's name for editing, adding, removing, and trading players
	private static String manual() {
		String player ="";
		System.out.print("Player's First Name: ");
		String first = scan.next();
		System.out.print("Player's Last Name: ");
		String last = scan.next();
		first = TitleCase(first);
		last = TitleCase(last);
		player = first + " "+last;
		return player;
	}
	
	//Checks the whether the player name given is already in the roster of players
	//Returns the index of the player in the roster list if the player is found
	private static int checkValidityOfPlayer(String player_name) {
		int player_index;
		//Find the player based on the players Arraylist
		for ( player_index =0; player_index<editable_roster_players.size();player_index++) {
			if(player_name.toLowerCase().equals(editable_roster_players.get(player_index).getPlayerName().toLowerCase())) {
				return player_index;
			}
		}
		//If the player is not found --> inform the user
		if (player_index==editable_roster_players.size()){
			System.out.println("Such player does not exist!");
		}
		return -1;
	}
	
	//Lists the players for one team for editing, removing based on the player's Arraylist.
	//Return the player's index
	private static int teamChoice() {
		//Lists the team with their given index
		NBACommonFunctions.showTeams(editable_roster_teamcounter);
		//User chooses the team
		int choice_team  = scan.nextInt();
		if (choice_team<editable_roster_teamcounter.size() && choice_team>=0) {
			//If the number select matches an index of a team, then show the players of the team
			for (int player_index=0; player_index<editable_roster_players.size();player_index++) {
				//Shows the given player's name with their associated index
				//Only show the players whose team matches the prefix of the selected team
				if (editable_roster_players.get(player_index).getTeam() == editable_roster_teamcounter.get(choice_team).getPrefix()) {
					System.out.println(player_index+" "+editable_roster_players.get(player_index).getPlayerName());
				}
			}
			System.out.print("Which player: ");
			//User selects the player
			int choice_player = scan.nextInt();
			//Return the player's index if the integer chosen is appropriate
			if (choice_player<editable_roster_players.size() && choice_player>=0) {
				return choice_player;
			}
			else {
				System.out.println("Such player does not exist!");
			}
		}
		//If the team index given by user does not match index of team 
		else {
			System.out.println("Unfortunately, this team does not exist.");
		}
		return -1;
	}
	
	//Lists every player in the roster 
	//Returns the player's index chosen by the user
	private static int listAll() {
		//Shows all the players and their index
		for (int play_index=0; play_index<editable_roster_players.size();play_index++) {
			System.out.println(play_index+" "+editable_roster_players.get(play_index).getPlayerName());
		}
		System.out.print("Which player: ");
		//Users chooses the player based on their index given
		int choice_player = scan.nextInt();
		if (choice_player<editable_roster_players.size() && choice_player>=0) {
			//If the chosen index corresponds to a player return the index
			return choice_player;
		}
		else {
			System.out.println("Unfortunately, "+choice_player+" is out of range");
		}
		return -1;
	}
	
	//Assign a player that currently on team to a new team
	//The selected player can also be a free agent and get assigned to a team
	//User selects the player and their new team in the function
	private static void assignNewTeam() {
		int player_index = editPlayerOptions();
		if (player_index != -1) {
			Player player_selected = editable_roster_players.get(player_index);
			boolean enter_team = true;
			//Continue prompting the user to select a new team for the team until
			//the user either quits or chooses a new team
			while (enter_team) {
				NBACommonFunctions.showTeams(editable_roster_teamcounter);
				//User selects the new team that the player is on
				System.out.print("What is "+player_selected.getPlayerName()+"'s new team: ");
				int choice_team = scan.nextInt();
				//If the team given by the user is valid
				if (NBACommonFunctions.checkValidityOfTeam(editable_roster_teamcounter,choice_team)) {
					//If the new team's roster is less than 15 before the transaction -- move the player
					if (editable_roster_teamcounter.get(choice_team).leng() < 15) {
						//Get the player's previous team so that they are removed from old team's roster
						int previous_team_index = -1;
						for (int team_index = 0; team_index < editable_roster_teamcounter.size(); team_index++) {
							if (editable_roster_teamcounter.get(team_index).getPrefix().equals(player_selected.getTeam())) {
								previous_team_index = team_index;
							}
						}
						//If the player is not a free agent - remove the player from the old team roster
						if (previous_team_index != -1) {
							editable_roster_teamcounter.get(previous_team_index).removePlayer(player_selected);
						}
						//If the player's new team is different from current team -- assign player to new team
						if (previous_team_index != choice_team) {
							//Change the player's team
							player_selected.setTeam(editable_roster_teamcounter.get(choice_team).getPrefix());
							//Add the player to the team's roster
							editable_roster_teamcounter.get(choice_team).setPlayerslist(player_selected);
							System.out.println(player_selected.getPlayerName()+" has been added to the "+editable_roster_teamcounter.get(choice_team).getTeamName());
							enter_team=false;
						}
						//If the user chooses the player's current team -- inform the user and prompt them to continue
						else {
							System.out.println(editable_roster_teamcounter.get(choice_team).getTeamName()+" is  their current team!");
							if(continuePrompt("Quit","Choose a new team")) {
								enter_team = false;
							}
						}
					}
					else {
						System.out.println("Too many players on the "+editable_roster_teamcounter.get(choice_team).getTeamName()+" roster");
						if(continuePrompt("Quit","Choose a new team")) {
							enter_team = false;
						}
					}
				}
				//If the team the user chooses is invalid -- inform the user and prompt them to continue
				else {
					if(continuePrompt("Quit","Choose a new team")) {
						enter_team = false;
					}
				}
			}
			
		}
	}
	
	//Rearranges the ratings of the team so that all the best rated players at every 
	//position are positioned first
	private static void organizeTeam() {
		String[] fixed_basketball_positions =new String[5];
		fixed_basketball_positions[0]="PG";
		fixed_basketball_positions[1]="SG";
		fixed_basketball_positions[2]="SF";
		fixed_basketball_positions[3]="PF";
		fixed_basketball_positions[4]="C";
		ArrayList<Player> position_players_array = new ArrayList<Player>();//Stores the players who play same position on the team
		ArrayList<Player> new_team_players_array = new ArrayList<Player>();//Stores all players on one team in the correct order
		for (int team_index=0; team_index< editable_roster_teamcounter.size();team_index++) {
			//For all teams, go through each position and reorder players by rating
			for (int position_index=0; position_index<5;position_index++) {
				for (int team_player_index=0; team_player_index<editable_roster_teamcounter.get(team_index).leng();team_player_index++) {
					//Get all the players who play the current viewed position
					if(editable_roster_teamcounter.get(team_index).getPlayer(team_player_index).getPosition().equals(fixed_basketball_positions[position_index])) {
						position_players_array.add(editable_roster_teamcounter.get(team_index).getPlayer(team_player_index));
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
			editable_roster_teamcounter.get(team_index).clear();
			//(Re)Add the players in their new sorted order to the team.
			for (int u=0;u<new_team_players_array.size();u++) {
				editable_roster_teamcounter.get(team_index).setPlayerslist(new_team_players_array.get(u));
			}
			//Clear the array
			new_team_players_array.clear();
		}
	}
	
	//Lists the options for selecting a player
	//Returns the index of the selected player
	private static int editPlayerOptions() {
		int player_index=-1;
		System.out.println("[0] Quit Mode, [1] Type In Manually, [2] List Players from Specific Team, [3] List All Players");
		//User chooses an option
		int choice = scan.nextInt();
		if (choice==1) {
			String player =manual();
			player_index = checkValidityOfPlayer(player);
		}
		else if (choice==2) {
			player_index=teamChoice();
		}
		else if (choice==3){
			player_index=listAll();
		}
		return player_index;
	}
	
	//Asks for the the player's first and last name, and if the player is 
	//real then it prompts the user to edit the player 
	//Users can change the rating of the player, allstar status, injury status, rookie status, and position of the players
	private static void editPlayer() {
		boolean edit_player = true;
		//Returns the selected player's index
		int player_index = editPlayerOptions();
		//If an actual player is not given --> quit
		if (player_index==-1){
			System.out.println("\t\t\tQuiting Edit Player Mode \n");
			edit_player=false;
		}
		else {
			System.out.println("Player Select is: "+ editable_roster_players.get(player_index).getPlayerName());
		}
		//If a real player is given, prompt the user to change the selected player's rating, injury status, position, allstar status
		if (edit_player) {
			updatePlayer(player_index);
		}
	}
	
	//Users are prompted to the change the attributes of the given player
	//Users can change the rating of the player, allstar status, injury status, rookie status, and position of the players
	//Changes to the player will be saved after editing
	//However user's changes will not be permanently change until they actually save the roster
	private static void updatePlayer(int player_index) {
		Player player_selected = editable_roster_players.get(player_index);
		boolean continue_editing = true;
		//Continue prompting the user to change the attributes of the given player until they exit edit player mode
		while (continue_editing) {
			String allstar_string = " Make Allstar ";
			if (player_selected.getAllstar()) {
				allstar_string = " Take Away Allstar Status ";
			}
			String rookie_string = " Make Rookie ";
			if (player_selected.getRookieStatus()) {
				rookie_string = " Take Away Rookie Status ";
			}
			String injury_string = " Injure "+player_selected.getPlayerName();
			if (player_selected.getInjury()) {
				injury_string = " Heal "+player_selected.getPlayerName();
			}
			//List the options the user has available with the current player
			System.out.println("[0] Exit Edit Player Mode, [1] Change "+player_selected.getPlayerName()+" "+(int)player_selected.getRating()+" Rating,  [2] "+injury_string);
			System.out.println(", [3] "+allstar_string+", [4] "+rookie_string+", [5] Change Position from "+player_selected.getPosition());
			//Prompt the user with the choice
			int choice = scan.nextInt();
			if (choice == 0 ) {
				//If the user wishes to exit the player
				continue_editing = false;
			}
			else if (choice == 1) {
				//If the user wishes to change the player's rating
				changePlayerRating(player_index,0);
			}
			else if (choice ==2 ) {
				//If the user wishes to change the player's injury status
				flipInjuryStatus(player_index);
			}
			else if (choice == 3) {
				//If the user wishes to change the player's allstar status
				flipAllstarStatus(player_index);
			}
			else if (choice == 4) {
				//If the user wishes to change the player's rookie status
				flipRookieStatus(player_index);
			}
			else if (choice == 5) {
				//If the user wishes to change the player's position
				selectNewPosition(player_index);
			}
		}
	}
	
	//Flips the given player's injury status to be from healed to injured and from injured to healed
	//Informs the user of the new injury status of the player
	private static void flipInjuryStatus(int player_index) {
		//Flips the given player's injury status
		editable_roster_players.get(player_index).setInjury(!editable_roster_players.get(player_index).getInjury());
		if (editable_roster_players.get(player_index).getInjury()) {
			//Inform user player is now injured
			System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now injured");
		}
		else {
			//Inform user player is now healed
			System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now healed");
		}
	}
	
	//Flips the given player's allstar status to be from allstar to non-allstar and from allstar to non-allstar
	//Informs the user of the new allstar status of the player
	private static void flipAllstarStatus(int player_index) {
		//Flips the given player's allstar status
		editable_roster_players.get(player_index).setAllstar(!editable_roster_players.get(player_index).getAllstar());
		if (editable_roster_players.get(player_index).getAllstar()) {
			//Inform user player is now an allstar
			System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now an allstar");
		}
		else {
			//Inform user player is now not an allstar
			System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now not at an allstar");
		}
	}
	
	//Flips the given player's rookie status to be from rookie to non-rookie and from non-rookie to rookie
	//Informs the user of the new rookie status of the player
	private static void flipRookieStatus(int player_index) {
		//Flips the given player's rookie status
		editable_roster_players.get(player_index).setRookieStatus(!editable_roster_players.get(player_index).getRookieStatus());
		if (editable_roster_players.get(player_index).getRookieStatus()) {
			//Inform user player is now a rookie
			System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now a rookie");
		}
		else {
			//Inform user player is now not a rookie
			System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now not at a rookie");
		}
	}
	
	//Users selected a new position for the player given
	//if the user chooses the old position for the player - no changes will be made to the player
	//if the user chooses a new position for the the player - will the player's position will change
	private static void selectNewPosition(int player_index) {
		System.out.println(" [0] Cancel , [1] PG, [2] SG, [3] SF, [4] PF, [C] 5");
		int choice = scan.nextInt();
		if (choice > 0 && choice < 6) {
			//If the user chooses a position and does not cancel
			String [] position_array = {"N/A","PG","SG","SF","PF","C"};
			String new_position = position_array[choice];
			if (player_index > -1) {
				//If the user chooses a new position for the player - change the position and inform the user of success
				if (!new_position.equals(editable_roster_players.get(player_index).getPosition())) {
					editable_roster_players.get(player_index).setPosition(new_position);
					System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is now a "+editable_roster_players.get(player_index).getPosition());
					return;
				}
			}
			else {
				//If a valid player index is not given
				System.out.println("Please give a valid player");
			}
		}
		//If the user does not give a valid choice or chooses the old position of the player
		System.out.println(editable_roster_players.get(player_index).getPlayerName()+" is still a "+editable_roster_players.get(player_index).getPosition());
	}
	
	//Edits the given players rating if the new rating is higher than their previous rating
	//If the new rating is higher ask the user to save their changes
	private static void changePlayerRating(int player_index, int choice) {
		//Continue to prompt the user to given a rating for the select player until they quit or update the rating.
		while (choice ==0) {
			System.out.print("Rating: ");
			//User gives a rating
			int rating = scan.nextInt();
			if (rating >=100) {
				System.out.println("Rating must be less than 100");
			}
			//If the rating given is higher than previous rating --> ask user to save changes and update rating
			else if (editable_roster_players.get(player_index).getRating()<rating) {
				System.out.println("Success, "+editable_roster_players.get(player_index).getPlayerName() +" will have a new rating of "+rating);
				System.out.println("[0] Cancel changes,[1] Confirm changes");
				choice = scan.nextInt();
				if (choice == 1) {
					editable_roster_players.get(player_index).setRating(rating);
					editable_roster_players.get(player_index).setUpgraded(true);
					System.out.println(editable_roster_players.get(player_index).getPlayerName() +" has a new rating of "+rating);
				}
			}
			//If the rating is no higher than the previous rating --> inform the user and ask them to either submit a higher rating or exit
			else {
				System.out.println("Unfortunately his rating did not change input higher rating than "+editable_roster_players.get(player_index).getRating());
				System.out.println("Enter [0] to edit the rating of "+editable_roster_players.get(player_index).getPlayerName()+", or Enter [1] to exit");
				choice = scan.nextInt();
			}
		}
	}

	//Asks for Team prefix and adds players to the team with their rating
	//Users have the option to add a new (user-created) player or a existing player from free agency 
	private static void addPlayer() {
		NBACommonFunctions.showTeams(editable_roster_teamcounter);
		int team_index = scan.nextInt(); 
		if(NBACommonFunctions.checkValidityOfTeam(editable_roster_teamcounter,team_index)){
			//If Roster is full don't add new player 
			//Roster can not be more than 15 players
			if (editable_roster_teamcounter.get(team_index).leng()>=roster_cap) {
				System.out.println("Roster is full!");
			}
			else {
				System.out.println("[1] Add Brand New Player, [2] Add Player from Free Agency");
				int choice = scan.nextInt(); 
				//Create a new player and add the new player to the team
				if (choice == 1) {
					createNewPlayer(team_index);
				}
				//Add a free agent to the team
				else if (choice == 2) {
					addFromFreeAgency(team_index);
				}
				else {
					System.out.println("Unsuccessfully added a new player to"+editable_roster_teamcounter.get(team_index).getTeamName());
				}
			}
		}
	}
	
	//Adds a brand new created player to a team (team_index is provided by user)
	//Prompts the user to input the new player's name, rating, position
	//Called by addPlayer()
	private static void createNewPlayer(int team_index) {
		//Add brand new created player 
		String player=manual();//Get the new player's name
		System.out.print("Rating: ");
		int new_player_rating = scan.nextInt(); //Get the new player's rating
		System.out.print("Position: ");
		String position =scan.next(); //Get the new player's position
		Player tmp = new Player (player);
		editable_roster_players.add(tmp);
		//Set the properties of the new player
		tmp.setPosition(position.toUpperCase());
		tmp.setRating(new_player_rating);
		tmp.setPlayerName(player);
		tmp.setTeam(editable_roster_teamcounter.get(team_index).getPrefix());
		//Add the player to given team
		editable_roster_teamcounter.get(team_index).setPlayerslist(tmp);
		System.out.println("Success, "+tmp.getPlayerName()+" has been added and has a rating of "+tmp.getRating());
	}
	
	//Adds a player from free agency to a team (team_index is provided by user)
	//Prompts the user to choose the free agent
	//If the choosen player is a free agency, the player is added to the team
	//Called by addPlayer()
	private static void addFromFreeAgency(int team_index) {
		//Show all the current free agents
		for (int all_players_index =0; all_players_index < editable_roster_players.size(); all_players_index++) {
			if (editable_roster_players.get(all_players_index).getTeam() == "FA") {
				System.out.println(all_players_index + " "+ editable_roster_players.get(all_players_index));
			}
		}
		//User selects a free agent
		System.out.print("Select a player from free agency :");
		int player_index = scan.nextInt(); 
		//Add the free agent to the team
		if (player_index < editable_roster_players.size() && player_index >=0) {
			//If the player is actually a free agent add the player to the team
			if (editable_roster_players.get(player_index).getTeam() == "FA") {
				editable_roster_teamcounter.get(team_index).setPlayerslist(editable_roster_players.get(player_index));;
				editable_roster_players.get(player_index).setTeam(editable_roster_teamcounter.get(team_index).getPrefix());
				System.out.println(editable_roster_players.get(player_index).getPlayerName() + " has been added to the "+editable_roster_teamcounter.get(team_index).getTeamName());
			}
			//If the player is not a free agent
			else {
				System.out.println(editable_roster_players.get(player_index).getPlayerName() + " is not a free agent");
			}
		}
		else {
			System.out.println(player_index + " is out of range");
		}
	}
	
	//Removes a inputed and valid player from the team roster and entire roster if the user wishes.
	//Team must have at least 9 players afterwards in order to effectively remove a player
	private static void removePlayer() {
		int player_index = editPlayerOptions();
		if (player_index == -1) {
			System.out.println("Unfortunately, this player does not exist!");
		}
		else {
			removePlayerFromTeam(player_index);
		}
	}
	
	//Prompts the user if they want to remove the selected player from the team
	//Additionally prompts the user if they want to delete the player from the roster
	//Ensures that at least 9 players will remain on the roster afterwards
	private static void removePlayerFromTeam(int player_index) {
		String player = editable_roster_players.get(player_index).getPlayerName();
		for (int j=0; j<editable_roster_teamcounter.size();j++) {
			if (editable_roster_players.get(player_index).getTeam().equals(editable_roster_teamcounter.get(j).getPrefix())) {
				//If the roster has at least 10 players - allow user to remove player
				if (editable_roster_teamcounter.get(j).leng() > 9) {
					System.out.println("Are you sure you want to remove "+player+" from the team");
					if (confirmChanges()) {
						//Remove the player from the team
						editable_roster_teamcounter.get(j).removePlayer(editable_roster_players.get(player_index));
						//Update the player not to be on the team
						editable_roster_players.get(player_index).setTeam("FA");
						System.out.println("Success, "+player+" was been removed from the team");
						//Prompt the user to remove the player from the roster
						removePlayerFromRoster(player_index, player);
					}
				}
				else {
					//If the roster will have at least than 9 players on the roster
					System.out.println("Team must have at least 9 players on the team");
				}
				
			}
		}
	
	}
	
	//Prompts the user if they want to delete the player from the roster
	private static void removePlayerFromRoster(int player_index, String player) {
		System.out.println("[0] Continue, [1] Delete "+player+" completely from roster");
		int choice= scan.nextInt();
		if (choice ==1) {
			if (confirmChanges()) {
				editable_roster_players.remove(player_index);
				System.out.println("Success, "+player+" has been deleted from roster");
			}
		}
	}
	
	//Prompts the user to complete option 1 or option 2
	//Returns the boolean of the user's decisions.
	//true- if the user wishes to chooses option 1 , false - if the user chooses option 2
	private static boolean continuePrompt(String option1, String option2) {
		System.out.println("[0] "+option1+", [1] "+option2);
		int end_choice = scan.nextInt();
		if (end_choice == 0) {
			return true;
		}
		return false;
	}
	
	private static void tradeTwoTeams() {
		boolean quit_trade = false;
		Team team1= null;
		Team team2= null;
		int index_team1=-1;
		int index_team2=-1;
		//Pick the two teams that will be conducting a trade
		int inteam =1;
		while (quit_trade==false&&inteam<3) {
			NBACommonFunctions.showTeams(editable_roster_teamcounter);
			//User selects a team
			int choice_team  = scan.nextInt();
			//If the team index is valid -- make the team one of the traders
			if (NBACommonFunctions.checkValidityOfTeam(editable_roster_teamcounter,choice_team)) {
				if (inteam==1) {
					team1 = editable_roster_teamcounter.get(choice_team);
					index_team1 = choice_team;
					System.out.println(team1.getTeamName() + " have been selected!");
				}
				if (inteam==2) {
					team2 = editable_roster_teamcounter.get(choice_team);
					index_team2 = choice_team;
					System.out.println(team2.getTeamName() + " have been selected!");
				}
				inteam++;
			}
			//If the team index is not valid -- ask the user if they want to reenter or exit
			else {
				if(!continuePrompt("Enter a different team","Exit Trade")) {
					quit_trade = true;
				}
			}
			//If the user chooses a duplicate team -- ask the user if they want to reenter or exit
			if (inteam==3 && index_team1==index_team2) {
				System.out.println("Duplicate Teams!");
				if(!continuePrompt("Enter a different team","Exit Trade")) {
					quit_trade = true;
				}
				else {
					inteam=2;
				}
			}
		}
		
		boolean trade_request_complete=false;
		ArrayList<Player> team1array = new ArrayList<Player>();
		ArrayList<Player> team2array = new ArrayList<Player>();
		//Select the players to be traded for both teams 
		if (quit_trade == false) {
			team1array = selectPlayers(team1, "Next Team");
			team2array = selectPlayers(team2, "Make Trade");
			trade_request_complete=true;
		}
		
		//If all the players for the team have been selected - check the roster size of each team after the team
		//and trade the players
		if(trade_request_complete) {
			//Check the potential new roster size of team 1 if greater than 15 --> send error message
			if (team1.leng()+team2array.size()-team1array.size()>roster_cap) {
				System.out.println("Trade Failure, "+team1.getTeamName()+" is acquiring too many players.");
			}
			//Check the potential new roster size of team 2 if greater than 15 --> send error message
			else if (team2.leng()+team1array.size()-team2array.size()>roster_cap) {
				System.out.println("Trade Failure, "+team2.getTeamName()+" is acquiring too many players.");
			}
			//Else if both team rosters will have the appropriate sizes --> make the trade
			else{
				tradePlayers(team1,team2,team1array,team2array);
			}
		}
	}
	
	//Users select the player(s) from the given team
	//They can select multiple players
	//Returns the list of players the user select to be traded;
	private static ArrayList<Player> selectPlayers(Team team_given,String option2) {
		boolean continue_select_players = true;
		ArrayList<Player> players_leaving_team = new ArrayList<Player>();
		//Continue prompting the user to select players until they quit
		while (continue_select_players) {
			//Show all the players of the given team
			for (int player_index=0; player_index<team_given.leng();player_index++) {
				System.out.println(player_index+" "+team_given.getPlayer(player_index).getPlayerName());
			}
			System.out.print("Which player: ");
			//User chooses a player
			int choice = scan.nextInt();
			//If the user choose a valid player index
			if(choice<team_given.leng()&&choice>=0) {
				//If the user has not already selected the player -- add the player and prompt the user 
				//to either add another player or quit
				if (!players_leaving_team.contains(team_given.getPlayer(choice))) {
					players_leaving_team.add(team_given.getPlayer(choice));
					System.out.println("Got it!, [0] Add another player, or [1] "+option2);
					int choice1 = scan.nextInt();
					if (choice1==1) {
						continue_select_players = false;
					}
				}	
				//If the user has already selected the player -- inform the user and prompt the user 
				//to either add another player or remove the previously selected player
				else {
					System.out.println("Duplicate Player!");
					System.out.println("[0] Add another player, or [1] Remove Player from Trade");
					int choice2 = scan.nextInt();
					if (choice2==1) {
						Player removed_player = team_given.getPlayer(choice);
						players_leaving_team.remove(team_given.getPlayer(choice));
						System.out.println(removed_player.getPlayerName() +" removed from Trade");
					}
				}
			}
			//If the user does not choose a valid player and has more than one player -- allow user to exit
			else if (players_leaving_team.size() > 0){
				System.out.println("[0] Add another player, or [1] "+option2);
				int choice1 = scan.nextInt();
				if (choice1==1) {
					continue_select_players = false;
				}
			}
		}
		return players_leaving_team;
	}
	
	//Trades the players from the team1 to team 2 and team 2 to team 1
	//Given the list of the players being traded from each team
	//As long as the team rosters won't have more than 15 players after the trade
	private static void tradePlayers(Team team1, Team team2, ArrayList<Player> team1array,ArrayList<Player> team2array) {
		if(team1.leng()+team2array.size()-team1array.size()<=roster_cap&&team2.leng()+team1array.size()-team2array.size()<=roster_cap){
			//Trade all the players from team 1 to team 2
			String new_players_team1= movePlayersToNewTeam(team1,team2,team1array);
			//Trade all the players from team 2 to team 1
			String new_players_team2= movePlayersToNewTeam(team2,team1,team2array);
			//Prints the results of the trade
			System.out.println(("Players traded to "+team2.getTeamName()+" are "+new_players_team1+"."));
			System.out.println(("Players traded to "+team1.getTeamName()+" are "+new_players_team2+"."));
			System.out.println("Trade Complete!");
		}
		else {
			System.out.println("Oops, Something went wrong!");
		}
	}
	
	//Move the players of the players_to_newteam array to the newteam
	//Removes the players from their old team and trades the player to new team
	//Returns a string of the players traded to their new team
	private static String movePlayersToNewTeam(Team oldteam, Team newteam, ArrayList<Player> players_to_newteam) {
		//List of new players being traded to the new team
		String new_players_team =players_to_newteam.get(0).getPlayerName(); 
		for (int current_player_index=0;current_player_index<players_to_newteam.size();current_player_index++) {
			//Add the player's name to the string of players being traded to team 2
			if (current_player_index>0) {
				new_players_team= new_players_team+ ", "+players_to_newteam.get(current_player_index).getPlayerName();
			}
			//Change the player's team
			players_to_newteam.get(current_player_index).setTeam(newteam.getPrefix());
			//Add the player to team 2
			newteam.setPlayerslist(players_to_newteam.get(current_player_index));
			//Remove the player from team 1
			oldteam.removePlayer(players_to_newteam.get(current_player_index));
		}
		return new_players_team;
	}
	
	//Prompts the user to confirm their changes
	private static boolean confirmChanges() {
		//Asks the user to save their changes
		System.out.println("[0] Cancel changes,[1] Confirm changes");
		int choice = scan.nextInt();
		if (choice == 1) {
			return true;
		}
		return false;
	}
	
	//Deletes a Team from the roster
	//Prompts the user to confirm their changes before deleting a team
	public static void deleteTeam() {
		NBACommonFunctions.showTeams(editable_roster_teamcounter);
		int team_index = scan.nextInt();
		//If team index given is appropriate
		if(NBACommonFunctions.checkValidityOfTeam(editable_roster_teamcounter,team_index)) {
			String team_to_delete = editable_roster_teamcounter.get(team_index).getTeamName();
			System.out.println(team_to_delete+" will be deleted!");
			//Asks the user to save their changes
			if (confirmChanges()) {
				editable_roster_teamcounter.remove(team_index);
				System.out.println(team_to_delete+" deleted!");
			}
		}
	}

	//Views the current players of the Team
	private static void viewTeam(ArrayList<Team> given_team_arraylist) {
		NBACommonFunctions.showTeams(given_team_arraylist);
		int team_index = scan.nextInt();
		//If team index given is appropriate
		if(NBACommonFunctions.checkValidityOfTeam(editable_roster_teamcounter,team_index)) {
			//Lists all the players on the team
			for (int j=0;j<given_team_arraylist.get(team_index).leng();j++) {
				System.out.println(given_team_arraylist.get(team_index).getPlayer(j).toRatingStatement());
			}
		}
	}
	
	//Adds new team to the roster by requesting the city, mascot, prefix
	//from the user, and players
	public static void addTeam() {
		Team newteam = createTeamBackground();
		int team_index =-1;
		//Get the team index
		for (int tmp_index=0;tmp_index<editable_roster_teamcounter.size();tmp_index++) {
			if (editable_roster_teamcounter.get(tmp_index).getPrefix()==newteam.getPrefix()) {
				team_index=tmp_index;
			}
		}
		boolean complete =false;
		boolean full_roster =false;
		while (complete ==false) {
			if (newteam.leng()>=15) {
				System.out.println("Roster is Full! Either Remove Player or Finish Team!");
				full_roster = true;
			}
			System.out.println("[1] Type New Player, [2] Add Current Players, [3] Edit Players, [4] View Team, [5] Remove A Player [6] Complete");
			int choice = scan.nextInt();
			//Type in a New Player
			if (choice ==1 &&full_roster ==false) {
				createNewPlayer(team_index);
			}
			//Add a Current Player if roster is not full
			else if (choice ==2 &&!full_roster) {
				int player_index = listAll();
				if (player_index!=-1) {
					newteam.setPlayerslist(editable_roster_players.get(player_index));
					editable_roster_players.get(player_index).setTeam(newteam.getPrefix());
					System.out.println("Success, "+editable_roster_players.get(player_index).getPlayerName()+" has a rating of "+editable_roster_players.get(player_index).getRating()+" has been added to the "+newteam.getTeamName());
				}
			}
			//Edit Players from New Team
			else if (choice ==3) {
				if (newteam.leng()>0) {
					for(int u=0;u<newteam.leng();u++) {
						System.out.println(u+" "+newteam.getPlayer(u));
					}
					int teamplayer = scan.nextInt();
					if (teamplayer >= newteam.leng()||teamplayer<0) {
						System.out.println("Please choose a player!");
					}
					else {
						System.out.print("Rating: ");
						int new_rating = scan.nextInt();
						if (new_rating >=100) {
							System.out.println("Rating must be less than 100");
						}
						else if (newteam.getPlayer(teamplayer).getRating()<new_rating) {
							newteam.getPlayer(teamplayer).setRating(new_rating);
							System.out.println("Success, "+newteam.getPlayer(teamplayer).getPlayerName() +" has a new rating of "+new_rating);
						}
						else {
							System.out.println("Unfortunately the rating did not change");
						}
					}
				}
				else {
					System.out.println("No Players inputted to "+newteam.getTeamName()+ " yet!");
				}
			}
			//View the Team's current roster
			else if (choice==4) {
				for (int new_team_player_index=0;new_team_player_index<newteam.leng();new_team_player_index++) {
					System.out.println(newteam.getPlayer(new_team_player_index).toString());
				}
			}
			//Remove a player from the team
			else if (choice==5) {
				if (newteam.leng()>0) {
					for(int u=0;u<newteam.leng();u++) {
						System.out.println(u+" "+newteam.getPlayer(u));
					}
					int to_remove_player = scan.nextInt();
					
					if (to_remove_player >= newteam.leng()||to_remove_player<0) {
						System.out.println("Please choose a player!");
						//System.out.println("Unfortunately, this player does not exist!");
					}
					else {
						Player player= newteam.getPlayer(to_remove_player);
						System.out.println(player.getPlayerName()+" will deleted from the team");
						if (confirmChanges()) {
							newteam.removePlayer(newteam.getPlayer(to_remove_player));
							player.setTeam("FA");
							full_roster = false;
							System.out.println("Success, "+player.getPlayerName()+" has been deleted from team");
						}
					}
				}
				else {
					System.out.println("No Players inputted to "+newteam.getTeamName()+ " yet!");
				}
			}
			//Officially add the Team
			else {
				complete=true;
			}
		}
		
	}
	
	//Creates the initial team 
	//Prompts the user to input the team info (City, Mascot, Prefix)
	//Adds the newteam to the teamcounter and returns the team
	private static Team createTeamBackground() {
		System.out.println("Team City: ");
		String city = scan.next();
		System.out.println("Team Mascot: ");
		String mascot = scan.next();
		String teamname = TitleCase(city) + " "+ TitleCase(mascot);
		Team newteam = new Team(teamname);
		System.out.println(teamname+"'s Prefix: ");
		String teamprefix = scan.next();
		newteam.setPrefix(teamprefix.toUpperCase());
		newteam.setTeamName(teamname);
		editable_roster_teamcounter.add(newteam);
		return newteam;
	}
	
}
