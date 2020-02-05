import java.util.*;
import java.text.*;

/*
 * This file is used for creating, and editing the Player Object 
 * Most of the calls are Private Setter and Getter Functions
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class Player implements Comparable<Player> {
	//The Player's basic attributes
	private String Player_Name; ///Stores the player's name
	private ArrayList<Integer> filelist =new ArrayList<Integer>(); //Stores the previous ratings of the player
	private boolean Upgraded= false; //Indicates whether the player has been upgraded by the user
	private float Rating; //Indicates the player's rating
	private String Team;//Indicates the player's current team (Usually a team prefix i.e "CLE","DAL","MIA")
	private String Position; //Indicates the player's position
	private String Origin_Team; //Indicates the first team that player played on
	private int Scoring_Ranking;	//Player's real life PPG ranked within their team (1) Highest PPG (2) Lower PPG
	private int Rebounding_Ranking;//Player's real life RPG ranked within their team (1) Highest RPG (2) Lower RPG
	private int Assisting_Ranking;//Player's real life APG ranked within their team (1) Highest APG (2) Lower APG
	private boolean Rookie_Status= false; //Indicates whether the player is a rookie
	private boolean Injury_Status =false;//Indicates whether the player is injured
	private boolean Allstar =false; //Indicates whether the player is an allstar
	//Simulated Statistics
	private int Points; //Stores the player's points scored in a game
	private int Rebounds;//Stores the player's rebounds scored in a game
	private int Assists;//Stores the player's assists scored in a game
	private int TotalPoints=0;//Stores the player's total points scored over the course of a season, or playoffs
	private int TotalRebounds=0;//Stores the player's total rebounds scored over the course of a season, or playoffs
	private int TotalAssists=0;//Stores the player's total assists scored over the course of a season, or playoffs
	private int GamesPlayed=0;//Stores the player's total games played over the course of a season, or playoffs
	//Real life attributes
	private float real_life_PPG=0;// Real Life Points Per Game
	private float real_life_RPG=0;// Real Life Rebounds Per Game
	private float real_life_APG=0;// Real Life Assists Per Game
	private float real_life_ORTG=0;// Real Life Offensive Rating
	private float real_life_DRTG=0;// Real Life Defensive Rating
	private float real_life_SPG=0;// Real Life Steals Per Game
	private float real_life_BPG=0;// Real Life Blocks Per Game
	private float real_life_MPG=0;// Real Life Minutes Per Game
	private float real_life_UP=0;// Real Life Usage Rate/Percentage
	
	//Initializes the Player Object
	public Player(){
		
	}
	//Initializes the Player Object and set the player's name to the name given
	public Player(String given_name){
		Player_Name = given_name;
	}
	//Sets the player's name to the string given
	public void setPlayerName(String name_given){
		Player_Name = name_given;
	}
	//Gets the player's name
	public String getPlayerName(){
		return Player_Name.toString();
	}
	//Sets the player's position to the position given
	public void setPosition(String position_given) {
		Position = position_given;
	}
	//Gets the player's position
	public String getPosition(){
		return Position;
	}
	//Sets the player's rating to the rating given
	public void setRating(float rating_given) {
		Rating = rating_given;
	}
	//Gets the player's rating
	public float getRating() {
		return Rating;
	}
	//Sets the player's team to the string given 
	//String given to this function are usually the team's prefix (i.e. ATL,BOS,SAS,OKC)
	public void setTeam(String team_prefix_given) {
		Team = team_prefix_given;
	}
	//Gets the player's team prefix
	public String getTeam() {
		return Team;
	}
	//Sets the player's ranking when it comes to scoring within their team
	public void setScoring(int scoring_ranking_given) {
		Scoring_Ranking =scoring_ranking_given;
	}
	//Gets the player's ranking when it comes to scoring within their team
	public int getScoring() {
		return Scoring_Ranking;
	}
	//Sets the player's ranking when it comes to rebounding within their team
	public void setRebounding(int rebounding_ranking_given) {
		Rebounding_Ranking =rebounding_ranking_given;
	}
	//Gets the player's ranking when it comes to rebounding within their team
	public int getRebounding() {
		return Rebounding_Ranking;
	}
	//Sets the player's ranking when it comes to assisting within their team
	public void setAssisting(int assisting_ranking_given) {
		Assisting_Ranking =assisting_ranking_given;
	}
	//Gets the player's ranking when it comes to assisting within their team
	public int getAssisting() {
		return Assisting_Ranking;
	}
	//Sets the status of whether the player has been upgraded by the user
	//Boolean is either equal to true (Upgraded) or false (Not upgraded)
	public void setUpgraded(boolean upgraded_text) {
			Upgraded= upgraded_text;
	}
	//Gets the status of whether the player has been upgraded by the user
	public boolean getUpgraded() {
		return Upgraded;
	}
	//Sets the player's first team that they played on when they made their NBA Debut
	public void setOrigin(String origin_team_given) {
		Origin_Team=origin_team_given;
	}
	//Gets the player's first team that they player on when they made their NBA Debut
	public String getOrigin() {
		return Origin_Team;
	}
	//Sets the player's rookie status
	//Boolean given is either true (Rookie) or false (Not Rookie)
	public void setRookieStatus(boolean rookie_status_given) {
		Rookie_Status = rookie_status_given;
	}
	//Gets the player's rookie status
	public boolean getRookieStatus() {
		return Rookie_Status;
	}
	//Sets the player's injury status
	//Boolean given is true (Injured) or false (Not Injured)
	public void setInjury(boolean injury_status_given) {
			Injury_Status =injury_status_given;
	}
	//Get's the player's injury status
	public boolean getInjury() {
		return Injury_Status;
	}
	//Sets the player's allstar status
	//Boolean given is either true (Allstar) or false (Not Allstar)
	public void setAllstar(boolean allstar_status_given) {
		Allstar=allstar_status_given;
	}
	//Gets the Allstar of a player
	public boolean getAllstar() {
		return Allstar;
	}
	
	//ABOVE - Setting and Getting the player attributes
	//BELOW - Setting and Getting the player statistics
	
	//Sets the player's points over the course of the game, season, playoffs
	public void setPoints(int points_given) {
		Points=points_given; //Game 
		TotalPoints+=points_given; //Season, Playoffs
	}
	//Gets the player's points over the course of the game
	public int getPoints() {
		return Points;
	}
	//Sets the player's rebounds over the course of the game, season, playoffs
	public void setRebounds(int p) {
		Rebounds=p;//Game 
		TotalRebounds+=p;//Season, Playoffs
	}
	//Gets the player's rebounds over the course of the game
	public int getRebounds() {
		return Rebounds;
	}
	//Sets the player's assists over the course of the game, season, playoffs
	public void setAssists(int p) {
		Assists=p;//Game 
		TotalAssists+=p;//Season, Playoffs
	}
	//Gets the player's assists over the course of the game
	public int getAssists() {
		return Assists;
	}
	//Increments the player's game played by 1
	public void setGamesPlayed() {
		GamesPlayed++;//Season, Playoffs
		
	}
	//Gets the number of games the player has played during the season
	public int getGamesPlayed() {
		return GamesPlayed;
	}
	//Calculates the players simulated PPG based on the points they have scored per game
	public float PointsPG() {
		if (GamesPlayed>0) {
			//If the player has played at least 1 game calculate the player's PPG
			float d = (float) TotalPoints/GamesPlayed;
			 DecimalFormat df = new DecimalFormat("#.#");
		     return Float.parseFloat((df.format(d)));
		}
		else {
			//Else if the player has not played any games 
			return 0;
		}
	}
	//Calculates the players simulated RPG based on the rebounds they have grabbed per game
	public float ReboundsPG() {
		if (GamesPlayed>0) {
			//If the player has played at least 1 game calculate the player's RPG
			float d = (float)TotalRebounds/GamesPlayed;
			DecimalFormat df = new DecimalFormat("#.#");
		     
		    return Float.parseFloat((df.format(d)));
		}
		else {
			//Else if the player has not played any games 
			return 0;
		}
	}
	//Calculates the players simulated APG based on the assists they have dished per game
	public float AssistsPG() {
		if (GamesPlayed>0) {
			//If the player has played at least 1 game calculate the player's APG
			float d = (float)TotalAssists/GamesPlayed;
			DecimalFormat df = new DecimalFormat("#.#");
		     
		    return Float.parseFloat((df.format(d)));
		}
		else {
			//Else if the player has not played any games 
			return 0;
		}
	}
	//Clears the player's stats over the course of a game, season, playoffs
	public void playerClear() {
		TotalPoints=0;
		TotalRebounds=0;
	 	TotalAssists=0;
	 	GamesPlayed=0;
	 	Points=0;
		Rebounds=0;
		Assists=0;
	}

	//ABOVE - Setting and Getting player statistics for simulation
	//BELOW - Initializing the player's PPG,RPG,APG,SPG,BPG,MPG,UP,ORTG,DRTG for calculating the player's rating

	//Sets the player's real life PPG after scraping or user manually inputting PPG
	public void setPPG(float new_PPG) {
		real_life_PPG = new_PPG;
	}
	//Gets the player's real life PPG
	public float getPPG() {
		return real_life_PPG;
	}
	//Sets the player's real life RPG after scraping or user manually inputting RPG
	public void setRPG(float new_RPG) {
		real_life_RPG = new_RPG;
	}
	//Gets the player's real life RPG
	public float getRPG() {
		return real_life_RPG;
	}
	//Sets the player's real life APG after scraping or user manually inputting APG
	public void setAPG(float new_APG) {
		real_life_APG = new_APG;
	}
	//Gets the player's real life APG
	public float getAPG() {
		return real_life_APG;
	}
	//Sets the player's real life ORTG after scraping or user manually inputting ORTG
	public void setORTG(float new_ORTG) {
		real_life_ORTG = new_ORTG;
	}
	//Gets the player's real life ORTG
	public float getORTG() {
		return real_life_ORTG;
	}
	//Sets the player's real life DRTG after scraping or user manually inputting DRTG
	public void setDRTG(float new_DRTG) {
		real_life_DRTG = new_DRTG;
	}
	//Gets the player's real life DRTG
	public float getDRTG() {
		return real_life_DRTG;
	}
	//Sets the player's real life SPG after scraping or user manually inputting SPG
	public void setSPG(float new_SPG) {
		real_life_SPG = new_SPG;
	}
	//Gets the player's real life SPG
	public float getSPG() {
		return real_life_SPG;
	}
	//Sets the player's real life BPG after scraping or user manually inputting BPG
	public void setBPG(float new_BPG) {
		real_life_BPG = new_BPG;
	}
	//Gets the player's real life BPG
	public float getBPG() {
		return real_life_BPG;
	}
	//Sets the player's real life MPG after scraping or user manually inputting MPG
	public void setMPG(float new_MPG) {
		real_life_MPG = new_MPG;
	}
	//Gets the player's real life MPG
	public float getMPG() {
		return real_life_MPG;
	}
	//Sets the player's real life UP after scraping or user manually inputting UP
	public void setUP(float new_UP) {
		real_life_UP = new_UP;
	}
	//Gets the player's real life UP
	public float getUP() {
		return real_life_UP;
	}
	//Adds the given rating to the player list of rating so that they can keep track of their progress
	public void setFilelist(int player_rating_given){
		//Collections.sort(filelist);
		filelist.add(player_rating_given);
		//Collections.sort(filelist);
	}
	//Adds the given rating to the player list of rating at a given index so that they can keep track of their progress
	public void setFilelist(int index_given,int player_rating_given){
		//Collections.sort(filelist);
		filelist.add(index_given, player_rating_given);
	}
	//Gets the list of ratings that the player progress from
	public String getFilelist(){
		String rating_list="";
		//Get the rating and add it to the end of the string
		for(int i=0; i<filelist.size()-1;i++)
			rating_list+= filelist.get(i)+" ";
		return rating_list;
	}
	
	//BELOW - String returns that tell info about the player
	
	//Returns the string used for the team files 
	//Returns the player's name, rating, upgraded status, scoring ranking, rebound ranking and assisting ranking
	public String toString() {
		String str=Player_Name.toString()+" "+(int)Rating;
		if (Allstar){
			str= str + " "+"A";
		}
		if (Upgraded){
			str= str + " "+"U";
		}
		if (Rookie_Status){
			str= str + " "+"R";
		}
		if (Injury_Status){
			str= str + " "+"I";
		}
		str = str + " "+Scoring_Ranking+ " "+ Rebounding_Ranking+ " "+ Assisting_Ranking;
		return str;
	}
	
	//Returns the player's name with their rating
	public String toRatingStatement(){
		return Player_Name.toString()+" is a "+String.valueOf((int)Rating)+" overall";
	}
	//Returns the player's name with their rating and if they have been upgraded
	public String toUpgradedIndicator() {
		String str=Player_Name.toString()+" "+(int)Rating;
		if (Upgraded){
			str= str + " "+"U";
		}
		return str;
	}
	//Returns the player's name, previous ratings, current rating, origin team , and upgraded status
	public String toOldString() {
		String str=Player_Name.toString()+" "+getFilelist()+" "+Origin_Team+" \t"+(int)Rating;
		if (Upgraded){
			str= Player_Name.toString()+" "+String.valueOf((int)Rating)+" "+Origin_Team+" \t"+"U";
		}
		return str;
	}
	//Returns the player's statistics after a game
		public String showGameStatistics() {
			return Player_Name.toString()+" had "+Points+"pts "+ Rebounds+"rebs " +Assists+"asts";
		}
	//Returns the player's simulated averages over the course of the season, playoffs.
	public String Averages() {
		return Player_Name.toString()+" averaged "+PointsPG()+" ppg, "+ ReboundsPG()+" rpg, and " +AssistsPG()+" apg";
	}
	//Returns the player's simulated totals over the course of the season, playoffs.
	public String Totals() {
		return Player_Name.toString()+" totaled "+TotalPoints+" pts, "+ TotalRebounds+" rebs, and " +TotalAssists+" asts";
	}
	//Compares player with each other by name so that they can later be arranged in alphabetical order by first name
	public int compareTo(Player other_player_given){
		return Player_Name.compareTo(other_player_given.Player_Name);
	}	
}

