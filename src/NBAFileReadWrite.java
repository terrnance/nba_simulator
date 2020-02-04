import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/*
 * Functions are called for reading and writing the files
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBAFileReadWrite  {
	
	static Scanner scan = new Scanner(System.in);
	
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
				current_player.setPlayerName(full_name);
				current_player.setTeam(prefix);
				current_team.setPlayerslist(current_player);
				//If Player is an Allstar - set the player's Allstar attribute
				if(fr.hasNext("A")){
					fr.next();
					current_player.setAllstar(true);
				}
				//If Player has been Upgraded -set the player's Upgraded attribute
				if(fr.hasNext("U")){
					fr.next();
					current_player.setUpgraded(true);
				}
				//If Player is a Rookie -set the player's Rookie attribute
				if(fr.hasNext("R")){
					fr.next();
					current_player.setRookieStatus(true);
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
					if (current_player.getInjury()==false&& current_player.getRating()>65) {
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
	
	//Checks whether a given path exists
	//If path exists return true, Else return false
	public static boolean checkPathExistence(String path_given) {
		String path = path_given;
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
			    		    		return false;
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
		    return true;
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
				String filename = given_team_rosters.get(team_index).getTeamName()+string_offset+team_index+".txt";

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
						out.write(given_team_rosters.get(team_index).getPlayer(player_index).toString()+"\n");
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