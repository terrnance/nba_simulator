import java.util.*;

/*
 * This file scraps the internet to get the latest NBA rosters and player statistics
 * The program creates the current team's and players in the NBA
 * Using the player's statistics, the program calculates the player's rating 
 * 
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBARatingCalculator{

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
	private static float calculateDefensiveRating(Player current_player) {
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
	private static float calculateOffensiveRating(Player current_player) {
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
	public static void setLeagueWidePlayerRankings(ArrayList<Team> given_team_arraylist) {
		for(int team_index=0;team_index<given_team_arraylist.size();team_index++) {
			setTeamPlayerRankings(team_index,given_team_arraylist);
		}
	}
	
	//Given a team, update the players on the team so they are ranked statistically within their team
	//Player receiving a ranking for how well they rank respectively in scoring, rebounding, and assisting
	public static void setTeamPlayerRankings(int team_index,ArrayList<Team> given_team_arraylist) {
		//Each of the following arrays are used to order the players by a respective stat
		//Higher rating are placed higher in the array
		//EX: if Player A scores more points than Player B on the team - Player A will have a higher index in the points_rankings
		int[] points_rankings = new int[given_team_arraylist.get(team_index).leng()];
		int[] rebounds_rankings = new int[given_team_arraylist.get(team_index).leng()];
		int[] assists_rankings = new int[given_team_arraylist.get(team_index).leng()];
		for (int player_index =0; player_index<given_team_arraylist.get(team_index).leng();player_index++) {
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
					if (given_team_arraylist.get(team_index).getPlayer(points_rankings[switch_index]).getPPG()>given_team_arraylist.get(team_index).getPlayer(points_rankings[switch_index-1]).getPPG()) {
						int player_index_moving_up = points_rankings[switch_index];
						points_rankings[switch_index] = points_rankings[switch_index-1];
						points_rankings[switch_index-1] =player_index_moving_up;
					}
					//if Player A at lower index has higher RPG than Player B at higher index --> move Player A up
					if (given_team_arraylist.get(team_index).getPlayer(rebounds_rankings[switch_index]).getRPG()>given_team_arraylist.get(team_index).getPlayer(rebounds_rankings[switch_index-1]).getRPG()) {
						int player_index_moving_up = rebounds_rankings[switch_index];
						rebounds_rankings[switch_index] = rebounds_rankings[switch_index-1];
						rebounds_rankings[switch_index-1] =player_index_moving_up;
					}
					//if Player A at lower index has higher APG than Player B at higher index --> move Player A up
					if (given_team_arraylist.get(team_index).getPlayer(assists_rankings[switch_index]).getAPG()>given_team_arraylist.get(team_index).getPlayer(assists_rankings[switch_index-1]).getAPG()) {
						int player_index_moving_up = assists_rankings[switch_index];
						assists_rankings[switch_index] = assists_rankings[switch_index-1];
						assists_rankings[switch_index-1]= player_index_moving_up;
					}
				}
			}
		}
		//Set the scoring rankings for all the players on the teams
		for (int points_index=0;points_index<points_rankings.length;points_index++) {
			given_team_arraylist.get(team_index).getPlayer(points_rankings[points_index]).setScoring(points_index+1);
		}
		//Set the rebounding rankings for all the players on the teams
		for (int rebounds_index=0;rebounds_index<points_rankings.length;rebounds_index++) {
			given_team_arraylist.get(team_index).getPlayer(rebounds_rankings[rebounds_index]).setRebounding(rebounds_index+1);
		}
		//Set the assisting rankings for all the players on the teams
		for (int assists_index=0;assists_index<points_rankings.length;assists_index++) {
			given_team_arraylist.get(team_index).getPlayer(assists_rankings[assists_index]).setAssisting(assists_index+1);
		}
	}
	
}