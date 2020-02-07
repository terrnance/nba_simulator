import java.util.*;

/*
 * This file stores some of the most common function calls that are used
 * 
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBACommonFunctions{
	
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
							h=position_players_array.size();
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
	
	//Lists all the teams
	public static void showTeams(ArrayList<Team> given_team_arraylist) {
		for (int i=0;i<given_team_arraylist.size();i++) {
			System.out.println(i+" "+given_team_arraylist.get(i).getTeamName());
		}
		System.out.print("Which Team: ");
	}
	
	//Checks the whether the team index given is appropriate -- 
	//Must be greater than 0 and less than the teamcounter size
	//Returns a boolean
	public static boolean checkValidityOfTeam(ArrayList<Team> given_team_arraylist, int team_index) {
		boolean valid = team_index < given_team_arraylist.size() && team_index >= 0;
		//If not true --> inform the user
		if (!valid) {
			System.out.println("Please choose a team!");
		}
		return valid;
	}
	
	//Calculates the number of players in the team's rotation
	//Player must be have a rating higher than 65 and not be injured to be considered part of the team rotation 
	//Updates the team's rotation
	public static void getRotation(Team team_given) {
		int team_rotation = 0;
		for (int player_index=0;player_index<team_given.leng();player_index++) {
			Player current_player = team_given.getPlayer(player_index);
			//If the player is not injured increment and update the number of player in team's rotation
			if ((!current_player.getInjury())&& current_player.getRating()>65) {
				team_rotation++;
			}
		}
		team_given.setRotation(team_rotation);
	}
}
