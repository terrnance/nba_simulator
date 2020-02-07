import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

/*
 * This file is used for simulating head to head competition between teams
 * Users can simulate regular season games, playoff games, complete seasons, team specific seasons
 * User can use either the latest updated roster or their editable roster
 *
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBAGame {
	static Scanner scan = new Scanner(System.in);
	static ArrayList<Player> players = new ArrayList<Player>();
	static ArrayList<Team> teamcounter = new ArrayList<Team>();
	
	public static void mainFunctionCall(String directory_name) throws IOException, Exception  {
		//Initializes the components needed for the head to head simulation
		initializeHead2Head(directory_name);
		System.out.println("\n \t \t \t \t Head 2 Head Simulation ");
		int choice = 1;
		while (choice != 0) {
			System.out.println("[0] Return to Main Menu, [1] Game, [2] Season, [3] Team Season, [4] Playoffs");
			choice = scan.nextInt();
			if (choice==1) {
				//Simulates a NBA Game based on the intensity given
				System.out.println("\n"+"[1] Regular Season Intensity, [2] Playoff Intensity");
				int play = scan.nextInt();
				boolean isPlayoffs = (play == 2);
				teamAvgVs(null,null,true,true,isPlayoffs);
			}
			else if (choice==2) {
				//Simulates a regular NBA regular season with playoffs
				CompleteSeasonSim();
			}
			else if (choice==3) {
				//Simulates a season of one particular team
				TeamSeasonSim();
			}
			else if (choice==4) {
				//Simulates the playoffs
				Playoffs(0,null);
			}
		}
	}
	
	//Sets the current roster and players that are being used to play
	//Initializes the averages and best scorers for the teams
	//Initializes the team leaders in Points, Rebounds, Assists
	//Counts the number of allstars on the team
	private static void initializeHead2Head(String directory_name) {
		System.out.println("Active Roster: "+directory_name);
		if (directory_name == "latestRoster") {
			//Sets the simulation roster to the latest roster
			teamcounter = NBAMainSimulator.latest_roster_teamcounter;
			players = NBAMainSimulator.latest_roster_players;
		}
		
		else {
			//Sets the simulation roster to the editable roster
			teamcounter = NBAMainSimulator.editable_roster_teamcounter;
			players = NBAMainSimulator.editable_roster_players;
		}
		for (int team_index= 0; team_index<teamcounter.size();team_index++) {
			//Get the averages for all the teams & Gets the number of Allstars on the team
			getTeamAverage(teamcounter.get(team_index),false);
			//Get the stat leaders for Points, Rebounds, assists
			getTeamStatLeaders(teamcounter.get(team_index));
		}
		
	}
		//Gets the best (non injured) player from the team being searched
		//Returns a string containing the best player's name and overall.
		//Returns the last player in the roster if the entire team is injured
		public static String getBestplayer(int team_index_given) {
			if (teamcounter.get(team_index_given).leng() > 0) {
				int best_player_index=0;
				//Get Best Player who is not injured, if all players are injured return last player
				while (teamcounter.get(team_index_given).getPlayer(best_player_index).getInjury()==true && best_player_index < teamcounter.get(team_index_given).leng()) {
					best_player_index++;
				}
				//Return last player if all players are injured
				if (best_player_index >= teamcounter.get(team_index_given).leng()) {
					return teamcounter.get(team_index_given).getPlayer(best_player_index-1).toRatingStatement();
				}
				else {
					//Once finding first non-injured player - find best non-injured player
					for (int new_player_index = best_player_index+1; new_player_index< teamcounter.get(team_index_given).leng();new_player_index++) {
						if (teamcounter.get(team_index_given).getPlayer(best_player_index).getInjury()==false && teamcounter.get(team_index_given).getPlayer(new_player_index).getInjury()==false) {
							//Compare the ratings if the new player has a higher rating - make new player Best Player
							int compare_value = Float.compare(teamcounter.get(team_index_given).getPlayer(new_player_index).getRating(), teamcounter.get(team_index_given).getPlayer(best_player_index).getRating());
						     if(compare_value > 0) {
						    	  	best_player_index=new_player_index;
						     }
						}
					}
					//Sets the team's best player as the player given
					teamcounter.get(team_index_given).setBestPlayer(teamcounter.get(team_index_given).getPlayer(best_player_index));
					// Returns the player's name and their overall
					return teamcounter.get(team_index_given).getPlayer(best_player_index).toRatingStatement();
				}
			}
			//If there are no player on the roster
			return "There is no best player - check team roster";
		}
		
		//Gets the best scorer, rebounder, and assister from the team being searched.
		//Returns the last player in the roster for every category if the entire team is injured
		private static void getTeamStatLeaders(Team team_given) {
			if (team_given.leng()>0) {
				//Get the index of the first noninjured player
				int first_non_injured_index=0;
				while(team_given.getPlayer(first_non_injured_index).getInjury()==true) {
					first_non_injured_index++;
				}
				//If the entire roster is injured - make the last player in roster best Scorer, Rebounder, Assister
				if (first_non_injured_index >= team_given.leng()) {
					(team_given).setScorer((team_given).getPlayer(first_non_injured_index-1));
					(team_given).setRebounder((team_given).getPlayer(first_non_injured_index-1));
					(team_given).setAssister((team_given).getPlayer(first_non_injured_index-1));
				}
				else {
					//Else if the entire is not injured
					int best_scorer_index = first_non_injured_index;
					int best_rebounder_index = first_non_injured_index;
					int best_assister_index = first_non_injured_index;
					//Find the best scorer, best rebounder, best assister
					for (int current_player_index = first_non_injured_index+1; current_player_index< (team_given).leng();current_player_index++) {
						//If the current player is not injured - compare the rankings
						if ((team_given).getPlayer(first_non_injured_index).getInjury()==false && (team_given).getPlayer(current_player_index).getInjury()==false) {
							//Find the best scorer 
							if (team_given.getPlayer(current_player_index).getScoring()>0) {
								//If the current player has higher scoring ranking than current best scorer - make current player best scorer
								int compare_value = Float.compare((team_given).getPlayer(current_player_index).getScoring(), (team_given).getPlayer(best_scorer_index).getScoring());
							     if(compare_value < 0) {
							    	 	best_scorer_index=current_player_index;
							     }
							}
							//Find the best rebounder 
							if (team_given.getPlayer(current_player_index).getRebounding()>0) {
								//If the current player has higher rebounding ranking than current best rebounder - make current player best rebounder
								int compare_value = Float.compare((team_given).getPlayer(current_player_index).getRebounding(), (team_given).getPlayer(best_rebounder_index).getRebounding());
							     if(compare_value < 0) {
							    	 	best_rebounder_index=current_player_index;
							     }
							}
							//Find the best assister
							if (team_given.getPlayer(current_player_index).getAssisting()>0) {
								//If the current player has higher assisting ranking than current best assister - make current player best assister
								int compare_value = Float.compare((team_given).getPlayer(current_player_index).getAssisting(), (team_given).getPlayer(best_assister_index).getAssisting());
							     if(compare_value < 0) {
							    	 	best_assister_index=current_player_index;
							     }
							}
							
						}
					}
					//Set the Best Scorer
					(team_given).setScorer((team_given).getPlayer(best_scorer_index));
					//Set the Best Rebounder
					(team_given).setRebounder((team_given).getPlayer(best_rebounder_index));
					//Set the Best Assister
					(team_given).setAssister((team_given).getPlayer(best_assister_index));
				}
			}
			else {
				//If there are no player on the roster
				System.out.println("There is no best player - check team roster");
			}
		}
		
		// Calculates and Returns the overall average rating of the team in a String.
		// Also calulcates the average rating of the team without the rookies.
		// Averages the rating of the players on the team
		// included_injured determines whether injured players are included
		// Counts the number of allstars on the team
		// Player must have a rating higher than 0 to be counted in average
		private static String getTeamAverage(Team team_given,boolean included_injured) {
			float number_players=0;
			float total_ratings = 0;
			float non_rookie_number_players=0;
			float non_rookie_total_ratings = 0;
			int number_allstars =0;//Stores the number of Allstars on the team
			//Get average of player's rating who are not injured on the team
			for(int player_index=0;player_index<team_given.leng();player_index++) {
				//Player must have have actual rating or played real life game
				if (team_given.getPlayer(player_index).getRating() > 0) {
					//Makes sure player is not injured unless allowing injured
					if(!team_given.getPlayer(player_index).getInjury() || included_injured) {
						total_ratings+=team_given.getPlayer(player_index).getRating();
						number_players++;
						//Count the total number of Allstars on the Team
						if (team_given.getPlayer(player_index).getAllstar()) {
							number_allstars++;
						}
						//Calculate the averages for players who are not rookie
						if(!team_given.getPlayer(player_index).getRookieStatus()) {
							non_rookie_number_players+=team_given.getPlayer(player_index).getRating();
							non_rookie_total_ratings++;
						}
					}
				}
			
			}
			//Sets the number of allstars on the team
			team_given.setAllstar(number_allstars);
			//Calculates, Sets, and Returns the team average
			float team_average= total_ratings/number_players;
			float non_rookie_team_average= non_rookie_total_ratings/non_rookie_number_players;
			String average = ("Average for the "+team_given.getTeamName()+" = "+ team_average+"\n");
			average= average +"Average without Rookies for the "+team_given.getTeamName()+ " = "+non_rookie_team_average+"\n";
			team_given.setAvg(team_average);
			return average;	
		}
		
		//Allows the user to heal or injure a player before a game simulation
		//If a user select a player the player will be healed or injured depending on prior status before action was taken
		private static void healInjuryBeforeGame(Team team1, Team team2) {
			boolean change_injury_status = true;
			while (change_injury_status ==true) {
				System.out.println("[0] Continue to Game , [1] Heal/Injure Player");
				int user_choice = scan.nextInt();
				if (user_choice ==0) {
					change_injury_status=false;
				}
				else {
					System.out.println("[0] Continue to Game , [1] "+team1.getTeamName()+", [2] "+team2.getTeamName());
					user_choice = scan.nextInt();
					if (user_choice ==1 ) {
						injury(team1,true);
					}
					else if (user_choice ==2){
						injury(team2,true);
					}
				}
			}
		}
		
		//Calculates a team's initial score by using their team average and Random
		//Returns the team's score
		private static int calculateInitialTeamScore(Team team_given) {
			Random rand = new Random(); 
			int team_score = rand.nextInt(((int)team_given.getAvg()*2));
			team_score+=team_given.getAvg();
			team_score=team_score/2;
			if (team_score<70) {
				team_score+=rand.nextInt(30);
				team_score+=rand.nextInt(30);
			}
			return team_score;
		}
		
		//Returns a random player index from the given team for simulating NBA game
		//Finds the potential X Factor in the game
		private static int findXPlayer(Team team_given) {
			Random rand = new Random(); 
			int wild_card_player_index = rand.nextInt(team_given.leng());
			while (team_given.getPlayer(wild_card_player_index).getInjury()){
				wild_card_player_index = rand.nextInt(team_given.leng());
			}
			return wild_card_player_index;
		}
		
		//Compares the positional averages for two teams and whichever team has the highest
		//team position average gets a boost 
		//Called for PG, SG, SF, PF, and C matchups
		//Returns an array containing the boost for each team
		private static int[] positionMatchup(String position_abbreviation,float team1_position_average,float team2_position_average, String team1_name, String team2_name, boolean print_extra) {
			int[] boost = new int[]{0,0};
			//If the team position average of team 1 is higher give team 1 the boost
			if(team1_position_average>team2_position_average) {
				boost[0] = (int)(team1_position_average/8);
				//See who wins the matchup 
				if (print_extra)
				System.out.println("Advantage "+team1_name+" for "+position_abbreviation);
			}
			//If the team position average of team 2 is higher give team 2 the boost
			else if(team2_position_average>team1_position_average) {
				boost[1] = (int)(team2_position_average/8);
				//See who wins the matchup 
				if (print_extra)
				System.out.println("Advantage "+team2_name+" for "+position_abbreviation);
			}
			return boost;
		}
		
		//Queries the user to pick two players
		//The user must pick two teams or quit
		//If the user quits the Team array will be filled with null
		//Returns a Team [] of the team's that user chose
		private static Team[] choose2Teams(Team home_team, Team away_team) {
			//User chooses team 1 if team given is null
			while (home_team == null) {
				home_team = teamChoice(teamcounter);
				if (home_team ==null){
					//If the user does not pick an actual team -- give user option to quit
					System.out.println("[0] Quit, [1] Continue");
					int quit_query = scan.nextInt();
					if (quit_query ==0) {
						home_team=null;
						away_team=null;
					}
				}
			}
			//If the user has not quit get the 2nd team
			if (home_team != null) {
				//User chooses team 2 if team given is null
				while (away_team == null) {
					away_team = teamChoice(teamcounter);
					if (away_team==null){
						//If the user does not pick an actual team -- give user option to quit
						System.out.println("[0] Quit, [1] Continue");
						int quit_query = scan.nextInt();
						if (quit_query ==0) {
							home_team=null;
							away_team=null;
						}
					}
					//Once the user selected two valid teams inform the user
					else {
						System.out.println("Home Team: "+home_team.getTeamName());
						System.out.println("Away Team: "+away_team.getTeamName());
					}
				}
			}
			Team [] teams_selected = new Team[]{home_team,away_team};
			return teams_selected;
		}
		
		//Calculates the new score based on the X Factor, Home, and Allstar, Bonuses
		//X Factor Bonus: Two players are randomly selected one player - the one with the higher rating will help team get boost
		//Home Bonus: Home team gets a random number added to the score
		//Allstar Bonus: Team gets bonus for having allstars
		//Returns the new statistics for the teams
		private static int [][] getInitialScore(Team team1, Team team2, int [] team1stats, int[] team2stats) {
			Random rand = new Random(); 
			//Finds a random X Factor player
			int t1rand = findXPlayer(team1);
			int t2rand = findXPlayer(team2);
			//Check which X Factor player has the higher rating
			if (team1.getPlayer(t1rand).getRating() < team2.getPlayer(t2rand).getRating()) {
				//If Player on team 2 has higher rating - give team score boost
				team2stats[0] += (int) (team2.getPlayer(t2rand).getRating()/4);
				team1stats[0] += (int) (team1.getPlayer(t1rand).getRating()/16);
			}
			else {
				//If Player on team 1 has higher rating - give team score boost
				team1stats[0] += (int) (team1.getPlayer(t1rand).getRating()/4);
				team2stats[0] += (int) (team2.getPlayer(t2rand).getRating()/16);
			}
			//Home Team Bonus
			team1stats[0] += rand.nextInt(20);
			//Allstar Bonus
			team1stats[0]+= rand.nextInt((team1.getAllstar()+1)*12);
			team2stats[0]+= rand.nextInt((team2.getAllstar()+1)*12);
			//Return new stats
			int [] [] new_stats = new int [][] {team1stats,team2stats};
			return new_stats;
		}
		
		//Comparing the different position matchups - bonuses are added for a team if they win
		//a position matchup
		//PG,SG,SF,PF,C matchups are examined boosts to team assists and rebounds are affected differently based on position
		//Returns the new statistics for the teams
		private static int [][] getMatchupBonuses(Team team1, Team team2, int [] team1stats, int[] team2stats, boolean print_extra) {
			Random rand = new Random(); 
			//Matchups by Position
			matchups(team1);
			matchups(team2);
			//Point Guards
			int [] score_update =positionMatchup("PGs",team1.getAvgPG(),team2.getAvgPG(),team1.getTeamName(),team2.getTeamName(),print_extra);
			team1stats[0] += score_update[0];
			team2stats[0] += score_update[1];
			team1stats[2] += score_update[0];
			team2stats[2] += score_update[1];
			//Shooting Guards
			score_update =positionMatchup("SGs",team1.getAvgSG(),team2.getAvgSG(),team1.getTeamName(),team2.getTeamName(),print_extra);
			team1stats[0] += score_update[0];
			team2stats[0] += score_update[1];
			team1stats[2] += score_update[0];
			team2stats[2] += score_update[1];
			//Small Forwards
			score_update =positionMatchup("SFs",team1.getAvgSF(),team2.getAvgSF(),team1.getTeamName(),team2.getTeamName(),print_extra);
			team1stats[0] += score_update[0];
			team2stats[0] += score_update[1];
			team1stats[2]+= (score_update[0]/ 2);
			team2stats[2] += (score_update[1] / 2);
			team1stats[1] += (score_update[0]/ 2);
			team2stats[1]  += (score_update[1] / 2);
			//Power Forwards
			score_update =positionMatchup("PFs",team1.getAvgPF(),team2.getAvgPF(),team1.getTeamName(),team2.getTeamName(),print_extra);
			team1stats[0] += score_update[0];
			team2stats[0] += score_update[1];
			team1stats[1]  += score_update[0];
			team2stats[1]  += score_update[1];
			//Centers
			score_update =positionMatchup("Cs",team1.getAvgC(),team2.getAvgC(),team1.getTeamName(),team2.getTeamName(),print_extra);
			team1stats[0] += score_update[0];
			team2stats[0] += score_update[1];
			team1stats[1]  += score_update[0];
			team2stats[1]  += score_update[1];
			//Boost the rebounds
			team2stats[1] +=(rand.nextInt(10)+rand.nextInt(5));
			team1stats[1]+=(rand.nextInt(10)+rand.nextInt(5));
			//Scoring balance
			if (team1stats[0]>100) {
				team1stats[0] -= rand.nextInt(40);
				team1stats[0] -= rand.nextInt(15);
				team1stats[0]-=5;
			}
			if (team2stats[0]>100) {
				team2stats[0] -= rand.nextInt(30);
				team2stats[0] -= rand.nextInt(15);
				team2stats[0]-=5;
			}
			//Return new stats
			int [] [] new_stats = new int [][] {team1stats,team2stats};
			return new_stats;
		}
		
		//Simulates games between two teams 
		//User has the ability to change the injured status of players
		//Score is determined by team averages, matchups, and unpredictableness
		//Final box score is also shown after the game
		private static void teamAvgVs(Team home_team_given, Team away_team_given,boolean change_injured_status,boolean print_extra,boolean isPlayoffs) {
			Team [] teams_chosen = choose2Teams(home_team_given,away_team_given);
			Team team1 = teams_chosen[0];
			Team team2 = teams_chosen[1];
			//User have the opportunity to heal injuries before the game
			//Currently disabled
			if (change_injured_status) {
				healInjuryBeforeGame(team1, team2);
			}
			int team1_points = calculateInitialTeamScore(team1);
			int team2_points = calculateInitialTeamScore(team2);
			int team1_rebounds =40;
			int team2_rebounds=40;
			int team1_assists =0;
			int team2_assists=0;
			//0th index is Points, 1st index is Rebounds, 2nd index is Assists
			int [] team1_stats = new int[]{team1_points, team1_points,team1_rebounds,team1_assists};
			//0th index is Points, 1st index is Rebounds, 2nd index is Assists
			int [] team2_stats = new int[]{team2_points, team2_points,team2_rebounds,team2_assists};
			//Calculates the initial team score
			int [][] final_stats = getInitialScore(team1,team2,team1_stats,team2_stats);
			if (print_extra)
				System.out.println("\n"+team1.getTeamName()+" have "+team1.getAllstar()+" Allstars");
			if (print_extra)
				System.out.println(team2.getTeamName()+" have "+team2.getAllstar()+" Allstars \n");
			//Calculate the matchup bonsuses and adds them to the teams' statistics
			final_stats = getMatchupBonuses(team1,team2,final_stats[0],final_stats[1],print_extra);
			//Walk through the simulation quarter by quarter recalculating when necessary
			final_stats = quarterWalkThrough(team1,team2,final_stats[0],final_stats[1],print_extra);
			//Walks through the fourth quarter and overtime - allows comebacks if the score is reasonable beforehand
			final_stats = roundOutStatistics(team1,team2,final_stats[0],final_stats[1],print_extra);
			//Wraps up the game by showing the player and team statistics, and clearing the box score
			closeGame(team1,team2,final_stats[0],final_stats[1],print_extra, isPlayoffs);
		}
		
		//Adjusts the game score and rebounds for the first three quarters of the game 
		//Shows the score after every quarter
		//Returns the new statistics for the teams
		private static int [][] quarterWalkThrough(Team team1, Team team2, int [] team1stats, int[] team2stats, boolean print_extra) {
			Random rand = new Random(); 
			//Quarter Loop
			int current_quarter =1;
			//Determines how much of the score we should show
			double [] quarter_effects = new double[] {1,4,2,1.41421356237};
			while (current_quarter <4) {
				if (print_extra) {
					System.out.println("\t \t \t \t \t  \t   Q"+current_quarter);
					System.out.println("\t \t \t \t\t  \t"+team1.getPrefix()+": "+(int)(team1stats[0]/quarter_effects[current_quarter]));
					System.out.println("\t \t \t \t\t\t"+team2.getPrefix()+": "+(int)(team2stats[0]/quarter_effects[current_quarter]));
					System.out.println("Press Any Key to Continue");
					System.out.print("You:");
					scan.next();
				}
				//Adjust the statisticss so that there is unpredictableness 
				current_quarter++;
				if (team1stats[0]>team2stats[0]){
					//team1stats[0] -= (((int)(team1stats[0]/quarter_effects[current_quarter])-(int)(team2stats[0]/quarter_effects[current_quarter]))/2);
					team2stats[0] += 3;
				}
				else if(team2stats[0]>team1stats[0]) {
					//team2stats[0] -= (((int)(team2stats[0]/quarter_effects[current_quarter])-(int)(team1stats[0]/quarter_effects[current_quarter]))/2);
					team1stats[0]+=3;
				}
				if (team1stats[0]-team2stats[0]>20) {
					team2stats[0]+=rand.nextInt(6);
				}
				else if (team2stats[0]-team1stats[0]>20) {
					team1stats[0]+=rand.nextInt(6);
				}
				//HalfTimeCheck
				if (current_quarter ==2) {
					//Scoring balance 2
					if (team1stats[0]>105) {
						team1stats[0] -= rand.nextInt(20);
						team1stats[0]-=10;
					}
					else {
						team2stats[1]+=rand.nextInt(5);
						team1stats[1]+=rand.nextInt(3);
					}
					if (team2stats[0]>105) {
						team2stats[0] -= rand.nextInt(10);
						team2stats[0]-=10;
					}
					else {
						team1stats[1]+=rand.nextInt(5);
						team2stats[1]+=rand.nextInt(3);
					}
					if (team1.getAvg()>team2.getAvg()) {
						team1stats[0]+=5;
						team1stats[0]+=rand.nextInt(3*(int)(1+team1.getAvg()-team2.getAvg()));
					}
					if (team2.getAvg()>team1.getAvg()) {
						team2stats[0]+=5;
						team2stats[0]+=rand.nextInt(3*(int)(1+team2.getAvg()-team1.getAvg()));
					}
				}
			}
			System.out.println("\t \t \t \t \t  \t Final");
			//Return new stats
			int [] [] new_stats = new int [][] {team1stats,team2stats};
			return new_stats;
		}
		
		//Generates the proper ratio of points to assists for a team in a game
		//Returns the new assists total
		private static int assistsPointsRatio(int assists_given, int points_given) {
			Random rand = new Random();
			assists_given +=points_given/6;
			assists_given+=rand.nextInt(points_given/9);
			if (assists_given>35) {
				assists_given/=2;
				assists_given+=5;
				assists_given-=rand.nextInt(8);
			}
			return assists_given;
		}
		//Generates a more appropriate value for rebounds by a team in a game
		//Returns the new rebounds total
		public static int fillOutRebounds(int rebounds_given) {
			Random rand = new Random();
			if (rebounds_given>=50) {
				rebounds_given/=2;
				rebounds_given+=5;
				rebounds_given-=rand.nextInt(8);
			}
			rebounds_given-=rand.nextInt(7);
			return rebounds_given;
		}
		
		//Simulates the final quarter of the game and the overtime periods
		//Helps teams make comebacks and print over the final winners and losers
		//Rounds out the teams' assists and rebounds
		//Allows increments the team wins and losses
		//Returns the new statistics for the teams
		public static int[][] roundOutStatistics(Team team1, Team team2, int [] team1stats, int[] team2stats, boolean print_extra) {
			Random rand = new Random();
			//Comeback if the score is not a blowout
			if (team1stats[0]-team2stats[0]>10&&team1stats[0]-team2stats[0]<21) {
				team2stats[0]+=rand.nextInt(15);
			}
			if (team2stats[0]-team1stats[0]>10&&team2stats[0]-team1stats[0]<21) {
				team1stats[0]+=rand.nextInt(15);
			}
			//If the score is Close one 
			if (Math.abs(team1stats[0]-team2stats[0])<8) {
				team2stats[0]+=rand.nextInt(10);
				team1stats[0]+=rand.nextInt(10);
			}
			//If the score is too low add points to both teams
			if (team2stats[0]+team1stats[0]<160) {
				int score_bonus = (rand.nextInt(10) + 8);
				team1stats[0]+=score_bonus;
				team2stats[0]+=score_bonus;
			}
			//If the score is too high subtract points from both teams
			else if (team1stats[0]+team2stats[0]>245) {
				team1stats[0]-=(rand.nextInt(8) + 10);
				team2stats[0]-= (rand.nextInt(8) + 10);
			}
			//Assists
			team1stats[2] = assistsPointsRatio(team1stats[2],team1stats[0]);
			team2stats[2] = assistsPointsRatio(team2stats[2],team2stats[0]);
			if (team1stats[2] <35) {
				team2stats[1]+=rand.nextInt(10);
			}
			if (team2stats[2] <35) {
				team1stats[1]+=rand.nextInt(10);
			}
			//Rebounds
			team1stats[1]= fillOutRebounds(team1stats[1]);
			team2stats[1]= fillOutRebounds(team2stats[1]);
		
			//If Both teams are tied after regulation keep playing until a team wins
			int overtime = 1;
			while (team1stats[0]==team2stats[0]) {
				System.out.println("\t \t \t \t \t OVERTIME"+overtime+" "+team1.getPrefix()+": "+team1stats[0]+" "+team2.getPrefix()+": "+team2stats[0]+"\n");	
				team1stats[0]+=rand.nextInt(12);
				team2stats[0]+=rand.nextInt(12);
				team1stats[2]+=rand.nextInt(3);
				team2stats[2]+=rand.nextInt(3);
				overtime++;
			}
			//If Team 1 wins - print and increment team 1 wins, and team 2 losses
			if (team1stats[0]>team2stats[0]) {
				team1stats[2]+=rand.nextInt(3);
				printWinner(team2,team1,team2stats[0],team1stats[0],print_extra);
			}
			//If Team 2 wins - print and increment team 2 wins, and team 1 losses
			else if (team2stats[0]>team1stats[0]) {
				team2stats[2]+=rand.nextInt(3);
				printWinner(team2,team1,team2stats[0],team1stats[0],print_extra);
			}
			//Return new stats
			int [] [] new_stats = new int [][] {team1stats,team2stats};
			return new_stats;
		}
		
		//Prints the winner of the game and increments the games played by the teams
		//Sets the Wins and Losses for the winning and losing team respectively
		private static void printWinner(Team winning_team, Team losing_team,int winning_score, int losing_score,boolean print_extra) {
			if (print_extra) {
				System.out.println("\n \t \t"+winning_team.getTeamName()+" wins over "+losing_team.getTeamName()+ " by a score of "+ winning_score+ " to "+losing_score);
			}
			//Increment winning team wins
			winning_team.setWins();
			//Increment losing team losses
			losing_team.setLosses();
			//Increment both teams games played
			winning_team.setGamesPlayed();
			losing_team.setGamesPlayed();
		}
		
		//Prints the box scores for both teams and clear the statistics
		//Prints the individual player box scores and prints the individual team rebounds, assists
		//Clears the player individual statistics for the game
		private static void closeGame(Team team1, Team team2, int [] team1stats, int[] team2stats, boolean print_extra, boolean isPlayoffs) {
			//Prints the box score of team 1
			if (print_extra) {
				System.out.println("\n  Box Score of "+team1.getTeamName());
			}
			boxScore(team1,team1stats[0],team1stats[1],team1stats[2],print_extra, isPlayoffs);
			//Prints the box score of team 2
			if (print_extra) {
				System.out.println("\n  Box Score of "+team2.getTeamName());
			}
			boxScore(team2,team2stats[0],team2stats[1],team2stats[2],print_extra, isPlayoffs);
			//Prints out the player of the game for the winning team
			if (team1stats[0]>team2stats[0] || print_extra) {
				//If team 1 won print stats of player of the game on this team
				getPlayerOfGame(team1);
			}
			else if (team1stats[0]<team2stats[0] || print_extra){
				//If team 2 won print stats of player of the game on this team
				getPlayerOfGame(team2);
			}
			
			//Shows the team statistics
			if (print_extra) {
				System.out.println("\n"+team1.getTeamName()+" collected "+team1stats[1]+"rebs "+team1stats[2]+"asts");
				System.out.println(team2.getTeamName()+" collected "+team2stats[1]+"rebs "+team2stats[2]+"asts");	
			}
			//Shows the final score
			System.out.println("\t \t \t \t \t FINAL "+team1.getPrefix()+": "+team1stats[0]+" "+team2.getPrefix()+": "+team2stats[0]+"\n");
			//Clear the team's game stats
			team1.clearStats();
			team2.clearStats();
		}
		
		//Prints out the statistics of the player who had the best statistics on the given team
		private static void getPlayerOfGame(Team team_given) {
			Player best_performer = team_given.getPlayer(0);
			for (int j=1;j<team_given.leng();j++) {
				if (team_given.getPlayer(j).getPoints()+team_given.getPlayer(j).getRebounds()+team_given.getPlayer(j).getAssists()>best_performer.getPoints()+best_performer.getRebounds()+best_performer.getAssists()) {
					best_performer = team_given.getPlayer(j);
				}
			}
			System.out.println("\t \t \t \t"+best_performer.showGameStatistics());
		}
		
		//Gets a player's rating from the given team
		//Given a team, the user selects a player on the team
		//Returns the selected player's rating
		//If a valid player is not given - return 0
		//include_injured determines if the player selected can be injured
		public static int getPlayerRating(Team team_given, boolean include_injured) {
			//Lists the players on the team for the user to select
			for (int player_index=0; player_index<(team_given).leng();player_index++) {
				System.out.println(player_index+" "+(team_given).getPlayer(player_index).getPlayerName());
			}
			System.out.print("Which player: ");
			int player_choice_index = scan.nextInt();
			//Check that the given player is a valid player on the team
			if (player_choice_index<(team_given).leng()&&player_choice_index>=0) {
				//Check that the selected player is not injured
				if (!team_given.getPlayer(player_choice_index).getInjury()||include_injured){
					//If the player is not injured return the player's rating
					return (int) (team_given).getPlayer(player_choice_index).getRating();
				}
				else {
					//If the selected player is injured inform the user
					System.out.println((team_given).getPlayer(player_choice_index).getPlayerName()+" is Injured!");
				}
			}
			//If the player given is not valid - inform the user
			else {
				System.out.println("Such player does not exist!");
			}
			return 0;
		}
		
		//Prints the season averages for players on the given team who played well for the team
		//Printed Players combined PPG, RPG, APG must be greater than 9
		//Printed Players must also not have missed over 30 games
		private static void printSeasonAverages(Team team_given) {
			for (int player_index=0;player_index<team_given.leng();player_index++) {
				Player best_player = team_given.getPlayer(player_index);
				//If the player scored during season
				if (best_player.getScoring()>0) {
					//If the player did not miss more than 30 games during the season
					if (best_player.getGamesPlayed()>0&&team_given.getGamesPlayed()-best_player.getGamesPlayed()<=30) {
						//Print Special Box Score if player played extraordinarily
						if (best_player.PointsPG()+best_player.ReboundsPG()+best_player.AssistsPG()>9) {
							System.out.println(best_player.Averages());
						}
					}
				}
			}
		}
		
		//Prompt the user to select how many teams they would like in the playoffs
		//Number the user selects are rounded if not 2, 4, 8, 16
		//Returns and prints the rounded number
		private static int numPlayoffTeams() {
			System.out.print("Enter Number of Teams in Playoffs: ");
			int user_inputted_number_teams = scan.nextInt();
			if (user_inputted_number_teams  >8) {
				user_inputted_number_teams = 16;
			}
			else if (user_inputted_number_teams  >4) {
				user_inputted_number_teams = 8;
			}
			else if (user_inputted_number_teams  >2) {
				user_inputted_number_teams = 4;
			}
			else if (user_inputted_number_teams  <2) {
				user_inputted_number_teams = 2;
			}
			System.out.println("Number of Teams in Playoffs: "+ user_inputted_number_teams);
			return user_inputted_number_teams;
		}
		
		//Adds teams to the playoff arraylist and returns teh playoff arraylist
		//number_teams determines how many teams a user can input
		private static ArrayList<Team> userInputPlayoffTeams(ArrayList<Team> playoff_array_list, int number_teams){
			for( int team_counter =0; team_counter< number_teams; team_counter++) {
				//Add a given number of teams to the playoff arraylist
				Team a = teamChoice(teamcounter);
				if (playoff_array_list.contains(a)) {
					//If the team is already in the playoffs
					System.out.println(a.getTeamName()+" is already in the playoffs");
					//Reset the counter
					team_counter--;
				}
				else {
					//If the team is not already in the playoffs add the team to the playoff arraylist
					playoff_array_list.add(a);
				}
			}
			return playoff_array_list;
		}
		
		//Simulates a 7 game playoff series between two teams
		//Given two teams first team to win 4 games moves on
		private static void runPlayoffSeries(Team top_team, Team bottom_team) {
			//Print the two teams facing off
			System.out.println("\n"+top_team.getTeamName());
			System.out.println(bottom_team.getTeamName());
			//Reset the stats of the team for the matchup
			top_team.clearGames();
			bottom_team.clearGames();
			//Ask the user if they are ready to continuce
			System.out.println("\n \t \t Press Any Key to Continue");
			scan.next();
			int games_played = 0;
			//Play out a 7 game series
			while (top_team.getWins() < 4 && bottom_team.getWins() < 4){
				games_played++;
				System.out.println("\t \t \t \t \t \tGame "+games_played);
				//Make top team the Home team for Games 1,2,5 and 7.
				if (games_played == 0 || games_played == 1 || games_played == 4 || games_played == 6) {
					teamAvgVs(top_team,bottom_team,false,false, true);
				}
				//Make bottom team the Home team for Games 3,4, 6.
				else {
					teamAvgVs(bottom_team,top_team,false,false, true);
				}
			}
		}

		//Prints the number of wins and losses for a team
		//Prints the player who contributed significantly to their team
		private static void printRoundStatistics(ArrayList<Team> teams_given) {
			//Prints all the team wins and losses for a round
			for (int team_index = 0; team_index < teams_given.size();team_index++ ) {
				Team team_to_print = teams_given.get(team_index);
				showTeamStanding(false,team_to_print);
			}
		}
		
		//Simulates the Playoffs 
		//Option 1: User enters the teams they wish to simulate with
		//  -> users enter number of teams and teams - play until one winning team is found
		//Option 2: The teams have already been given and play until one winning team is found
		//All series are best of 7 games
		//Number of teams allowed in playoffs are 2,4,8,16.
		//Statistics are printed after every round
		//Function is called recursively on itself to get winner
		private static void Playoffs(int number_teams, ArrayList<Team> current_round ) {
			if (number_teams == 0) {
				//If user is creating Playoffs ask user how many teams in playoffs
				number_teams = numPlayoffTeams();
			}
			//If there was no list of teams given add team
			if (current_round==null) {
				current_round = new ArrayList<Team>();
				userInputPlayoffTeams(current_round,number_teams);
			}
			//If there are no teams given (Add teams in playoffs)
			if (current_round.size() <= 0){
				userInputPlayoffTeams(current_round,number_teams);
			}
			ArrayList<Team> next_round = new ArrayList<Team>();
			int top_seed_index = 0;
            int bottom_seed_index = 1;
			Team top_team = null;
			Team bottom_team= null;
			//Run the current round of the playoffs
			for(int i = 0; i < current_round.size()/2; i++) {
				 //Get the teams for the current matchup
				top_team = current_round.get(top_seed_index);
				bottom_team = current_round.get(bottom_seed_index);
				//Simulate playoff series between two teams
				runPlayoffSeries(top_team,bottom_team);
				//Top team wins add the team to the next round
				if (top_team.getWins()>bottom_team.getWins()){
					next_round.add(top_team);
					System.out.println(top_team.getTeamName()+ " beats "+ bottom_team.getTeamName());
					System.out.println(top_team.getPrefix()+ ": "+top_team.getWins()+ " "+ bottom_team.getPrefix()+": "+bottom_team.getWins());
				}
				//Bottom team wins add the team to the next round
				if (bottom_team.getWins()>top_team.getWins()){
					next_round.add(bottom_team);
					System.out.println(bottom_team.getTeamName()+ " beats "+ top_team.getTeamName());
					System.out.println(bottom_team.getPrefix()+ ": "+bottom_team.getWins()+ " "+ top_team.getPrefix()+": "+top_team.getWins());
				}
				//Increment to the next two teams
				top_seed_index = top_seed_index + 2;
				bottom_seed_index = bottom_seed_index + 2;
			}
			System.out.println("\n \t \t Press Any Key to Continue");
			scan.next();
			if (current_round.size() == 2) {
				System.out.println("\n \t Finals Statistics");
			}
			else {
				System.out.println("\n \t Round Statistics");
			}
			//Prints the team and player statistics
			printRoundStatistics(current_round);
			//If there is still more rounds to be played recursivel call the Playoffs function
			if (next_round.size() > 1){
				Playoffs(number_teams/2,next_round);
			}
			//Else if there is only one team left - inform the user and get the Team's MVP
			else {
				System.out.println(next_round.get(0).getTeamName()+ " are the Champions!!!");
				System.out.println("The Finals MVP is "+ teamMVP(next_round.get(0)).Averages());
			}
			//Reset the player's and the team's statistics
			resetTeamsPlayers();
		}
		
		
		//Find the Most Valuable Player for given team based on their statistics
		//Returns Player with the highest combined PPG, RPG, APG on the team
		private static Player teamMVP(Team team_given) {
			Player most_valuable_player = team_given.getPlayer(0);
			for (int player_index=1;player_index<team_given.leng();player_index++) {
				Player current_player = team_given.getPlayer(player_index);
				//If the new player has a higher combined PPG, RPG, APG than current MVP -- make new player MVP
				if (most_valuable_player.PointsPG()+most_valuable_player.ReboundsPG()+most_valuable_player.AssistsPG()<current_player.PointsPG()+current_player.ReboundsPG()+current_player.AssistsPG()) {
					most_valuable_player = current_player;
				}
			}
			return most_valuable_player;
		}
		
		//Shows the full standings in the league over the course of the season
		//Prints the team's win and the statistics of the top performers on the season
		private static void showFullStandings(boolean heal_injured) throws IOException {
			for (int i=0;i<teamcounter.size();i++) {
				showTeamStanding(heal_injured,teamcounter.get(i));
			}
		}
		
		//Shows the team standings over the course of the season
		//Prints the top performers on their team
		//Top performers are considered people who average either over 11PPG, over 3RPG,and/or over 3APG
		private static void showTeamStanding(boolean heal_injured,Team team_previously_selected) {
			Team team_selected = team_previously_selected;
			if (team_selected == null) {
				//If the user has not already chosen a team
				//Users selects the team whose stats will be shown
				team_selected = teamChoice(teamcounter);
			}
			if (team_selected!=null) {
				System.out.println(team_selected.getPrefix()+ " won " +team_selected.getWins()+" games & lost " +team_selected.getLosses()+" games");
				for (int player_index=0;player_index<team_selected.leng();player_index++) {
					//Only statistics of player's who really contributed statistically to the team
					if (team_selected.getPlayer(player_index).PointsPG()>9||team_selected.getPlayer(player_index).ReboundsPG()>3||team_selected.getPlayer(player_index).AssistsPG()>3) {
						System.out.println(team_selected.getPlayer(player_index).Averages());
					}
					if (heal_injured) {
						//If true - Heal all of the player's that are injured on the team
						if (team_selected.getPlayer(player_index).getInjury()==true) {
							team_selected.getPlayer(player_index).setInjury(false);}
						}
				}
				System.out.println("\n");
			}
		}
		
		//Completes a Season Simulation for a Particular Team Given
		private static void TeamSeasonSim() throws IOException {
			Random rand = new Random();
			//Select a team to simulation
			Team user_selected_team = teamChoice(teamcounter);
			if (user_selected_team != null) {
				//Simulate the Season
				for (int game_counter=0; game_counter<=82;game_counter++) {
					//Get a random team
					int random_opponent_team_index = rand.nextInt(30);
					Team opponent_team = teamcounter.get(random_opponent_team_index);
					//If the opponent is the not the actual team selected and have not played 4 games
					if (!user_selected_team.equals(opponent_team) && opponent_team.getGamesPlayed() < 4){
						//Play only 2 games if team is other conference
						if (!opponent_team.getConference().equals(user_selected_team.getConference())) {
							if (opponent_team.getGamesPlayed() < 2) {
								//Have the play against each other if they have not played at least 2 games against each other
								teamAvgVs(user_selected_team,opponent_team,false,false,false);
							}
							else {
								//If they have played more than 2 games, reset the counter
								game_counter--;
							}
						}
						//Else if there are in the same conference play up to 4 games
						else {
							//Playing only 4 games if teams is within division 
							if (opponent_team.getDivision().equals(user_selected_team.getDivision())){
								game_counter--;
								//Play the entire series between the two teams
								while (opponent_team.getGamesPlayed() < 4 && game_counter<=82) {
									teamAvgVs(user_selected_team,opponent_team,false,false,false);
									game_counter++;
								}
							}
							//Playing up to 4 games within conference
							else {
								teamAvgVs(user_selected_team,opponent_team,false,false,false);
							}
						}
					}
					//Else if the opponent team has played 4 games or opponent team is user selected team - reset counter
					else {
						game_counter--;
					}
				}
				//Get the player statistics and team statistics
				printSeasonAverages(user_selected_team);
				System.out.println(user_selected_team.getTeamName()+ " won " +user_selected_team.getWins()+" games \n");
				int choice=2;
				//Show the standings if the user is interested
				while (choice==2) {
					System.out.println("[0] Continue, [1] Show Full Standings, [2] Show a Specific Team's Standing");
					choice = scan.nextInt();
					if (choice ==1) {
						showFullStandings(false);
					}
					if (choice==2) {
						showTeamStanding(false,null);
					}
				}
				//Reset the player's and the team's statistics
				resetTeamsPlayers();
			}
	
		}
		
		//Simulates a complete season including the playoffs
		private static void CompleteSeasonSim() throws IOException{
			Random rand = new Random();
			Team teamA = null;
			Team teamB = null;
			//Add the teams to team list
			ArrayList<Team> teamlist = new ArrayList<Team>();
			for( int i =0; i< teamcounter.size(); i++) {
				teamlist.add(teamcounter.get(i));
			}
			int team_index1=0;
			int team_index2=0;
			int team_played_82 =0;//Stores the number of teams that have played 82 games
			team_index2 = rand.nextInt(teamcounter.size());
			//Simulates games until all teams have played 82 games
			while (team_played_82<teamcounter.size()&&teamlist.size()>1){
				teamB = teamlist.get(team_index1);
				teamA = teamlist.get(team_index2);
				//Get a team that has not played a complete season
				while (teamB.getComplete()) {
					//Keep going until you get a new team
					team_index1 = rand.nextInt(teamlist.size());
					teamB = teamlist.get(team_index1);
				}
				//Get a team that has not played a complete season
				while (teamA.getComplete()) {
					//Keep going until you get a new team
					team_index2 = rand.nextInt(teamlist.size());
					teamA = teamlist.get(team_index2);
				}
				//If both team have not played 82 games
				if (teamA.getGamesPlayed()<82&&teamB.getGamesPlayed()<82) {
					//If both teams are not the same
					if (!teamA.equals(teamB)) {
						teamAvgVs(teamA,teamB,false,false,false);
						//Check if team A has played 82 games - if remove team A from list of available teams to play
						if (teamA.getGamesPlayed()==82) {
							teamlist.remove(teamA);
							team_played_82++;
						
						}
						//Check if team B has played 82 games - if remove team B from list of available teams to play
						if (teamB.getGamesPlayed()==82) {
							teamlist.remove(teamB);
							team_played_82++;
						}
					}
					//If there is still teams who have not played 82 games - find the next team to play game
					if (teamlist.size()>0) {
						team_index2 = rand.nextInt(teamlist.size());
						team_index1 = rand.nextInt(teamlist.size());
					}
				}
			}
			//Prints the list of team averages over the course of the season
			for (int i=0; i<teamcounter.size();i++) {
				System.out.println("\n");
				printSeasonAverages(teamcounter.get(i));
			}
			//Before preceding to the Playoffs asking the user if they would like to heal all injuries
			System.out.println("[0] Continue to Playoffs, [1] Heal Injuries, then continue to playoffs");
			int choice = scan.nextInt();
			//transitions from the Season to the Playoffs
			ArrayList<Team> playoff_seeding = seasonToPlayoffs(teamcounter);
			//Runs the Playoffs
			showFullStandings(choice==1);
			Playoffs(16,playoff_seeding);
			//Reset the player's and the team's statistics
			resetTeamsPlayers();
		}
		
		//Resets the player's and the team's statistics
		private static void resetTeamsPlayers() {
			for (int i=0;i<teamcounter.size();i++) {
				teamcounter.get(i).clearAlls();
			}
		}
		
		//Finds the 16 teams with highest record and adds them to the playoffs seeding arraylist
		//Returns a teamarraylist of the teams in bracket order i.e. "2nd Place" v.s. "15th Place"
		private static ArrayList<Team> seasonToPlayoffs(ArrayList<Team> given_team_arraylist){
			ArrayList<Team> rounds = new ArrayList<Team>();
			for (int playoff_spots = 0; playoff_spots < 16; playoff_spots++) {
				//Find the 16 best teams in the league (No matter conference)
				int current_team_index = 0;
				Team current_team = given_team_arraylist.get(current_team_index);
				while (rounds.contains(current_team)){
					//Find the next team not already in the playoffs
					current_team_index++;
					current_team = given_team_arraylist.get(current_team_index);
				}
				//Find the team with the next highest wins who is not already in the playoffs
				for (int new_team_index = current_team_index+1; new_team_index  <given_team_arraylist.size();new_team_index ++) {
					//If the team is not already in the playoffs
					if (!rounds.contains(given_team_arraylist.get(new_team_index ))){
						//If the team has a higher number of wins than current highest team - make team current highest win team
						if (current_team.getWins() < given_team_arraylist.get(new_team_index ).getWins()){
							current_team = given_team_arraylist.get(new_team_index );
						}
					}
				}
				rounds.add(current_team);
			}
			//Seeds the team so that the best teams in the playoffs are playing the worst teams i.e. "1st Place" v.s. "16th Place"
			ArrayList<Team> playoff_seeding = new ArrayList<Team>();
			while (rounds.size() > 0) {
				 playoff_seeding.add(rounds.remove(0));
				 playoff_seeding.add(rounds.remove(rounds.size()-1));
			}
			return playoff_seeding;
		}
		
		//Calculates the boxScore for the game 
		//Calculates the player's individual points, rebounds, assists
		private static void boxScore(Team team_given, int points, int rebounds, int assists, boolean show_special_boxscore, boolean isPlayoffs) {
			int team_rotation = 9;
			//Assign Rotations to each team
			if (team_given.getRotation()>0) {
				team_rotation= team_given.getRotation();
			}
			//Calculate the Player's Points
			calculateBoxScorePoints(team_given,team_rotation,points,isPlayoffs);
			//Calculate the Player's Rebounds
			calculateBoxScoreRebounds(team_given,team_rotation,rebounds,isPlayoffs);
			//Calculate the Player's Assists
			calculateBoxScoreAssists(team_given,team_rotation,assists,isPlayoffs);
			//Sets the player's games played
			setPlayersGamesPlayed(team_given,team_rotation,show_special_boxscore);
		}
		
		//Increments the games played for the top scorers in the rotation for the team given
		//Shows the special boxscore if a player played extremely well statistically
		private static void setPlayersGamesPlayed(Team team_given,int team_rotation, boolean show_special_boxscore) {
			Player best_player = team_given.getScorer();
			Player previous_player=new Player();
			int following_scoring_ranking=2;
			//Increment the games played for  all the players who played
			for (int player_index=0;player_index<team_rotation;player_index++) {
				if (!previous_player.equals(best_player)){
					best_player.setGamesPlayed();
					//Print Special Box Score if Player played extraordinarily
					if (best_player.getPoints()+best_player.getRebounds()+best_player.getAssists()>14) {
						if (show_special_boxscore) {
							System.out.println(best_player.getPlayerName()+" had "+best_player.getPoints()+"pts "+best_player.getRebounds()+ "rebs "+best_player.getAssists()+"asts");
						}
					}
				}
				previous_player=best_player;
				best_player = bestScorer(team_given,following_scoring_ranking,best_player);
				following_scoring_ranking++;
			}
		}
		
		//Given the total points of the game
		//The functions calculates all of the individual player points who are in the rotation
		private static void calculateBoxScorePoints(Team team_given, int team_rotation,int total_points, boolean isPlayoffs) {
			Random rand = new Random();
			Player best_scorer = team_given.getScorer();
			int current_player_points=0;
			int bonus_indicator=0;//Used when boosting the stats
			int following_points_ranking=2;
			//Calculate the pointss for the top players in rotation who are not injured
			for (int player_in_rotation=0;player_in_rotation<team_rotation;player_in_rotation++) {
				//If the player is not injured
				if (!best_scorer.getInjury()) {
					if (total_points > 3) {
						current_player_points = rand.nextInt(total_points/3);
						current_player_points += rand.nextInt(total_points/3);
						current_player_points += rand.nextInt(total_points/3);
						bonus_indicator=0;
						//Make sure the player does not already have value set and they are points left
						if (best_scorer.getPoints()==0&&current_player_points>0) {
							//If the player is an allstar and is in the top 3 in PPG within their team
							if (best_scorer.getAllstar()&&best_scorer.getScoring()<4) {
								//If it is the points
								if (isPlayoffs) {
									current_player_points += rand.nextInt(10);
								}
								current_player_points+=4;
								current_player_points+=rand.nextInt(4);
							}
							current_player_points+=(best_scorer.getRating()/4.5);
							bonus_indicator++;
							//If the player is an rookie
							if(best_scorer.getRookieStatus()&&current_player_points>80) {
								current_player_points-=rand.nextInt(5);
								current_player_points-=rand.nextInt(15);
								if(current_player_points>=75) {
									current_player_points-=rand.nextInt(3);
									current_player_points-=rand.nextInt(5);
								}
							}
							//Calculate the player's points scored in the game
							current_player_points = current_player_points/3+bonus_indicator;
							//If the player is not an allstar and has scored more than 12 points
							if ((!best_scorer.getAllstar())&&current_player_points>12){
								current_player_points -= rand.nextInt(4);
								if (current_player_points>=20){
									current_player_points-=3;
									current_player_points-=rand.nextInt(3);
									//If the player is in top 2 in PPG 
									if (best_scorer.getScoring()>3&&current_player_points>14) {
										current_player_points-=rand.nextInt(4);
									}
									//Else if the player is not in top 5 in PPG 
									else if (best_scorer.getScoring()<4&&current_player_points>20) {
										current_player_points-=3;
									}
								}
							}
							if (current_player_points<0) {
								current_player_points=0;
							}
							best_scorer.setPoints(current_player_points);
							//Decrement the player's points from the total points left
							total_points-=current_player_points;
						}
						//Find the next best scorer
						best_scorer= bestScorer(team_given,following_points_ranking,best_scorer);
						following_points_ranking++;
						}
					//If there are only a few points they assign them to the next best scorer
					//and exit the loop
					else if (total_points > 0 && total_points > 4) {
						if (best_scorer.getPoints()==0&&current_player_points>0) {
							best_scorer.setPoints(total_points);
							total_points = 0;
							player_in_rotation = team_rotation;
						}
					}
				}
				else {
					//Reset the counter if player is injured
					player_in_rotation--;
					//Find the next best scorer
					best_scorer= bestScorer(team_given,following_points_ranking,best_scorer);
					following_points_ranking++;
				}
	
			}
		}
		
		//Given the total rebounds of the game
		//The functions calculates all of the individual player rebounds who are in the rotation
		private static void calculateBoxScoreRebounds(Team team_given, int team_rotation,int total_rebounds, boolean isPlayoffs) {
			Random rand = new Random();
			Player best_rebounder = team_given.getRebounder();
			int current_player_rebounds=0;
			int bonus_indicator=0;//Used when boosting the stats
			int following_rebound_ranking=2;
			//Calculate the rebounds for the top players in rotation who are not injured
			for (int player_in_rotation=0;player_in_rotation<team_rotation;player_in_rotation++) {
				//If the player is not injured
				if (!best_rebounder.getInjury()) {
					if (total_rebounds>0) {
						bonus_indicator=0;
						current_player_rebounds = rand.nextInt(total_rebounds/3);
						current_player_rebounds += rand.nextInt(total_rebounds/3);
						current_player_rebounds += rand.nextInt(total_rebounds/4);
						//Make sure the player does not already have value set
						if (best_rebounder.getRebounds()==0) {
							//If the player is an allstar and is in the top 3 in RPG within their team
							if (best_rebounder.getAllstar()&&best_rebounder.getRebounding()<4) {
								//If it is the playoffs
								if (isPlayoffs) {
									current_player_rebounds += rand.nextInt(5);
								}
								current_player_rebounds+=rand.nextInt(4);
								//If the player is a center or a power forward
								if (best_rebounder.getPosition().contains("C")||best_rebounder.getPosition().contains("PF")) {
									current_player_rebounds+=rand.nextInt(4);
								}
							}
							//If the player is in the top 2 in RPG within their team
							if(best_rebounder.getRebounding()<3) {
								current_player_rebounds+=2;
								//If the player is a center
								if (best_rebounder.getPosition().contains("C")){
									current_player_rebounds+=(best_rebounder.getRating()/8.5);
								}
								//If the player is power forward
								else if (best_rebounder.getPosition().contains("PF")){
									current_player_rebounds+=(best_rebounder.getRating()/10);
								}
								//Else if the player is a small forward, shooting guard, point guard
								else {
									current_player_rebounds+=(best_rebounder.getRating()/12);
								}
								bonus_indicator=1;
							}
							//If the player is a rookie
							if(best_rebounder.getRookieStatus()&&current_player_rebounds>=24) {
								current_player_rebounds-=rand.nextInt(3);
								current_player_rebounds-=rand.nextInt(5);
								if (current_player_rebounds>=27) {
									current_player_rebounds-=5;
									current_player_rebounds-=rand.nextInt(3);
								}
							}
							//Calculate the actual rebounds for the player
							current_player_rebounds = current_player_rebounds/(3+bonus_indicator);
							//If the player is a center and 1st in RPG on the team
							if (best_rebounder.getRebounding()==1&&best_rebounder.getPosition().contains("C")) {
								current_player_rebounds+=rand.nextInt(3);
							}
							if (current_player_rebounds<0) {
								current_player_rebounds=0;
							}
							best_rebounder.setRebounds(current_player_rebounds);
							//Decrement the total amount of rebounds
							total_rebounds-=current_player_rebounds;
						}
					}
				}
				else {
					//Reset the counter if player is injured
					player_in_rotation--;
				}
				//Find the next best rebounder on the team
				best_rebounder= bestRebounder(team_given,following_rebound_ranking,best_rebounder);
				following_rebound_ranking++;
			}
		}
		
		//Given the total assists of the game
		//The functions calculates all of the individual player assists who are in the rotation
		private static void calculateBoxScoreAssists(Team team_given, int team_rotation,int total_assists, boolean isPlayoffs) {
			Random rand = new Random();
			Player best_assister = team_given.getAssister();
			int current_player_assists=0;
			int bonus_indicator=0;//Used when boosting the stats
			int following_assist_ranking =2;
			for (int player_in_rotation=0;player_in_rotation<team_rotation;player_in_rotation++) {
				//If the player is not injured
				if (!best_assister.getInjury()) {
					if (total_assists>2) {
						bonus_indicator=0;
						current_player_assists = rand.nextInt(total_assists/2);
						current_player_assists += rand.nextInt(total_assists/3);
						current_player_assists += rand.nextInt(total_assists/3);
						if (best_assister.getAssists()==0) {
							//If the player is an allstar and is in the top 3 in APG within their team
							if (best_assister.getAllstar()&&best_assister.getAssisting()<4) {
								//If it is the playoffs boost the stats
								if (isPlayoffs) {
									current_player_assists += rand.nextInt(5);
								}
								current_player_assists+=rand.nextInt(4);
								current_player_assists+=rand.nextInt(2);
								//If the player is guard and the team's leading assister
								if (best_assister.getAssisting()==1&&best_assister.getPosition().contains("G")) {
									current_player_assists+=2;
									current_player_assists+=rand.nextInt(5);
								}
							}
							//If the player is in the top 2 in APG within their team
							if (best_assister.getAssisting()<3) {
								current_player_assists+=2;
								current_player_assists+=(best_assister.getRating()/10);
								//If the player is a guard
								if (best_assister.getPosition().contains("G")) {
									current_player_assists+=rand.nextInt(3);
								}
								bonus_indicator=1;
							}
							//Calculate the player's assists for the game
							current_player_assists = current_player_assists/(3+bonus_indicator);
							best_assister.setAssists(current_player_assists);
							//Decrements their assists from the total assists
							total_assists-=current_player_assists;
						}
					}
				}
				else {
					//Reset the counter if player is injured
					player_in_rotation--;
				}
				//Find the next bestAssister
				best_assister= bestAssister(team_given,following_assist_ranking,best_assister);
				following_assist_ranking++;
			}
		}
		
		//Gets best scorer from given team who is ranked by the 
		//given number and is also not the Player given
		private static Player bestScorer(Team team_given,int rank_given, Player player_given) {
			for(int player_index=0;player_index<team_given.leng();player_index++) {
				if (team_given.getPlayer(player_index).getScoring()==rank_given) {
					//Makes sure player is not injured
					if (team_given.getPlayer(player_index).getInjury()==false) {
						//Makes sure player is not given Player
						if (!player_given.equals(team_given.getPlayer(player_index))){
							player_given=team_given.getPlayer(player_index);
						}
					}
				}
			}
			return player_given;
		}
		
		//Gets best rebounder from given team who is ranked by the 
		//given number and is also not the Player given
		private static Player bestRebounder(Team team_given,int rank_given, Player player_given) {
			for(int player_index=0;player_index<team_given.leng();player_index++) {
				if (team_given.getPlayer(player_index).getRebounding()==rank_given) {
					//Makes sure player is not injured
					if (team_given.getPlayer(player_index).getInjury()==false) {
						//Makes sure player is not given Player
						if (!player_given.equals(team_given.getPlayer(player_index))){
							player_given=team_given.getPlayer(player_index);
						}
					}
				}
			}
			return player_given;
		}
		
		//Gets best assister from given team who is ranked by the 
		//given number and is also not the Player given
		private static Player bestAssister(Team team_given,int rank_given, Player player_given) {
			for(int player_index=0;player_index<team_given.leng();player_index++) {
				if (team_given.getPlayer(player_index).getAssisting()==rank_given) {
					//Makes sure player is not injured
					if (team_given.getPlayer(player_index).getInjury()==false) {
						//Makes sure player is not given Player
						if (!player_given.equals(team_given.getPlayer(player_index))){
							player_given=team_given.getPlayer(player_index);
						}
					}
				}
			}
			return player_given;
		}
		
		//Gets best player from given team who is not injured and has lower
		//or equal to given rating (rating1) and is not the Player given
		public static Player findBestPlayer(Team team_given,int rating_given,Player player_given) {
			int best_player_index=0;
			//The Best Player must not be injured, can not be given player, and must have higher rating than rating given
			while (team_given.getPlayer(best_player_index).getInjury()==true||(team_given).getPlayer(best_player_index).equals(player_given)||(team_given).getPlayer(best_player_index).getRating()>rating_given) {
				best_player_index++;
			}
			for (int new_best_player_index =1; new_best_player_index< (team_given).leng();new_best_player_index++) {
				//Find the best player who has the highest rating, but is not injured or player given
				if ((team_given).getPlayer(best_player_index).getInjury()==false && (team_given).getPlayer(new_best_player_index).getInjury()==false) {
					//Check rating is higher than rating given
					if (rating_given<(team_given).getPlayer(new_best_player_index).getRating()) {
						//Check player is not player given
						if (!player_given.equals((team_given).getPlayer(new_best_player_index))){
							//Compare the ratings
							int d = Float.compare((team_given).getPlayer(new_best_player_index).getRating(), (team_given).getPlayer(best_player_index).getRating());
							//If higher - make this player - new best player
							if(d > 0) {
								best_player_index=new_best_player_index;
							}
						}
					}
				}
			}
			//If there is no best or better player return the player given
			if (best_player_index >= team_given.leng()) {
				return player_given;
			}
			return (team_given).getPlayer(best_player_index);
		}
		
		//Injures a Player from the given team
		private static void injury(Team team_given, boolean revert_status) {
			//Lists all the player on the given team
			for (int player_index=0; player_index<team_given.leng();player_index++) {
				System.out.println(player_index+" "+(team_given).getPlayer(player_index).getPlayerName());
			}
			System.out.print("Which player: ");
			//Prompts the player to choose a player to injury
			int player_index_choice = scan.nextInt();
			//If a valid player is given - make the player injured
			if (player_index_choice<(team_given).leng()&&player_index_choice>=0) {
				if (revert_status) {
					team_given.getPlayer(player_index_choice).setInjury(!team_given.getPlayer(player_index_choice).getInjury());
					//Print the new status of the player
					if (team_given.getPlayer(player_index_choice).getInjury()) {
						//If now injured - inform user player is now injured
						System.out.println("Success, "+team_given.getPlayer(player_index_choice).getPlayerName() + " is injured");
					}
					else {
						//If now healed - inform user player is now healed
						System.out.println("Success, "+team_given.getPlayer(player_index_choice).getPlayerName() + " is healed");
					}
					
				}
				else {
					team_given.getPlayer(player_index_choice).setInjury(true);
				}
			}
			else {
				System.out.println("Such player does not exist!");
			}
		}
		
		//Calculates the averages for every team based on position 
		//Average the rating of the players who play the particular position on the team
		private static void matchups(Team team_given) {
			String [] position_chart = new String[5];
			position_chart[0]="PG";
			position_chart[1]="SG";
			position_chart[2]="SF";
			position_chart[3]="PF";
			position_chart[4]="C";
			float total_ratings_combined=0;
			float number_of_players=0;
			float position_average=0;
			//Calculates the average of player rating for each position
			for (int position_index=0;position_index<position_chart.length;position_index++) {
				total_ratings_combined=0;
				number_of_players=0;
				position_average=0;
				for (int player_index=0;player_index<team_given.leng();player_index++) {
					//If the player is not injured
					if (team_given.getPlayer(player_index).getInjury()==false && team_given.getPlayer(player_index).getRating() > 0) {
						//If the player plays the current position being calculated
						if (team_given.getPlayer(player_index).getPosition().equals(position_chart[position_index])){
							//Add the player to the totals
							total_ratings_combined += team_given.getPlayer(player_index).getRating();
							number_of_players++;
						}
					}
				}
				//Calculates the average for the position 
				position_average=total_ratings_combined/number_of_players;
				//Set the Average for Points Guards on the team
				if (position_index==0) {
					if (position_average>0) {
						team_given.setAvgPG(position_average);
					}
					else {
						team_given.setAvgPG(0);
					}
				}
				//Set the Average for Shooting Guards on the team
				if (position_index==1) {
					if (position_average>0) {
						team_given.setAvgSG(position_average);
					}
					else {
						team_given.setAvgSG(0);
					}
				}
				//Set the Average for Small Forwards on the team
				if (position_index==2) {
					if (position_average>0) {
						team_given.setAvgSF(position_average);
					}
					else {
						team_given.setAvgSF(0);
					}
				}
				//Set the Average for Power Forwards on the team
				if (position_index==3) {
					if (position_average>0) {
						team_given.setAvgPF(position_average);
					}
					else {
						team_given.setAvgPF(0);
					}
				}
				//Set the Average for Centers on the team
				if (position_index==4) {
					if (position_average>0) {
						team_given.setAvgC(position_average);
					}
					else {
						team_given.setAvgC(0);
					}
				}
			}
			
		}
		
		//Lists teams for user to choose from
		//Returns the team - if a real team is chosen
		private static Team teamChoice(ArrayList<Team> given_team_arraylist) {
			NBACommonFunctions.showTeams(given_team_arraylist);
			Team team1=null;
			int team_choice  = scan.nextInt();
			if(NBACommonFunctions.checkValidityOfTeam(given_team_arraylist, team_choice)){
				team1 =given_team_arraylist.get(team_choice);
			}
			return team1;
		}
		
		public static void gif() throws MalformedURLException{
			//URL url = new URL("https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwjywrmJ2a3YAhVDOCYKHSNSCMAQjRwIBw&url=http%3A%2F%2Fwww.phillyvoice.com%2Fjoel-embiid-rookie-recap-gif-each-his-31-games-and-then-some%2F&psig=AOvVaw1dqyrOyiO9HG_bgZDdxaFr&ust=1514584495549551");
			//URL url = new URL("file://terrancenanceDocuments/FreeTime/CSFun/embiidblock.gif");
			//URL url = new URL("https://media.giphy.com/media/3o6YglDndxKdCNw7q8/source.mp4");
			//URL url = new URL("http://www.freeallimages.com/wp-content/uploads/2014/09/animated-gif-images-2.gif");
			String file = ("/Users/terrancenance/Documents/Free Time/CSfun/embiidblock.gif");
			URL url = new URL(file);
			JLabel l = new JLabel(new ImageIcon(url));
			JOptionPane.showMessageDialog(null, l);
			Icon icon = new ImageIcon(url);
			//Icon icon 
		    JLabel label = new JLabel(icon);

		    JFrame f = new JFrame("Animation");
		    f.getContentPane().add(label);
		    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    f.pack();
		   // f.setLocationRelativeTo(null);
		    f.setLocation((int)(Math.random()*1000),(int)(Math.random()*1000));
		   // f.setResizable(false);
		    f.setVisible(true);
		}
		
}

