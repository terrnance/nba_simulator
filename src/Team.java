import java.util.ArrayList;

/*
 * This file is used for creating, and editing the Team Object 
 * Most of the calls are Private Setter and Getter Functions
 * Author: Terrance Nance <tnance@middlebury.edu>
 */

public class Team implements Comparable<Team> {

	//String - key
	private String Team_Name; //Stores the team's name (i.e. Sacramento Kings, Memphis Grizzlies)
	//List of files that contain this word
	private ArrayList<Player> teamplayers =new ArrayList<Player>(); //Stores the players on the team
	private  int Number_Allstars =0; //Stores the number of allstars on the team
	private float Team_Average; //Stores the average rating of all the players on the team
	private String Prefix; //Stores the team's prefix (i.e. MIN, DET, HOU,PHO)
	private float PG_Average=0;//Stores the average rating of the players who play Point Guard on the team
	private float SG_Average=0;//Stores the average rating of the players who play Shooting Guard on the team
	private float SF_Average=0;//Stores the average rating of the players who play Small Forward on the team
	private float PF_Average=0;//Stores the average rating of the players who play Power Forward on the team
	private float C_Average=0;//Stores the average rating of the players who play Center on the team
	private String Conference;//Stores the conference that the team plays in
	private String Division; //Stores the division that the team plays in
	private Player Best_Rated_Player;//Stores the player who is best rated player on the team (Calculated in the program)
	private Player Best_Scoring_Player;//Stores the player who averages the most points on the team (Based on Real Life)
	private Player Best_Rebounding_Player;//Stores the player who averages the most rebounds on the team (Based on Real Life)
	private Player Best_Assisting_Player; //Stores the player who averages the most assists on the team (Based on Real Life)
	private int Rotation;//Indicates the number of players that typically play for a team in a a game
	private int GamesPlayed=0;//Indicates the number of games a team has played over the course of a season or playoffs
	private int Wins=0;//Indicates the number of wins a team has over the course of a season or playoffs
	private int Losses=0; //Indicates the number of losses a team has over the course of a season or playoffs
	boolean Games_Complete_82 =false; //Indicates whether the team has played a full season (82 games)
	private int Seed =0 ;//Shows the seeds of the team in the playoffs
	
	//Initializes the Team Object
	public Team(){
		
	}
	//Initializes the Team Object and set the team's name to the name given
	public Team(String name_given){
		Team_Name = name_given;
	}
	//Sets the team's name to the string given
	public void setTeamName(String name_given){
		Team_Name = name_given;
	}
	//Gets the team's fullname (i.e. Denver Nuggets, Philadelphia 76ers) 
	public String getTeamName(){
		return Team_Name.toString();
	}
	//Sets the team's conference to the conference given ("EAST" or "WEST")
	public void setConference(String conference_given){
		Conference = conference_given ;
	}
	//Gets the team's conference
	public String getConference(){
		return Conference;
	}
	//Sets the team's division to the division given ("Southeast" "Southwest" "Atlantic" "Pacific" "Northwest" "Central")
	public void setDivision(String division_given){
		Division = division_given;
	}
	//Gets the team's division
	public String getDivision(){
		return Division;
	}
	//Sets the team's average - which is the average of the player's ratings
	public void setAvg(float team_average_given) {
		Team_Average =team_average_given;
	}
	//Gets the team's average - which is the average of the player's ratings
	public float getAvg() {
		return Team_Average;
	}
	//Sets the number of allstars of the team
	public void setAllstar(int number_allstars_given) {
		Number_Allstars =number_allstars_given;
	}
	//Gets the number of allstars of the team
	public int getAllstar() {
		return Number_Allstars;
	}
	//Sets the team's prefix to the string given (i.e. UTA, WAS,IND)
	public void setPrefix(String team_prefix_given) {
		Prefix=team_prefix_given;
	}
	//Gets the team's prefix
	public String getPrefix() {
		return Prefix;
	}
	//Sets the number of players in the team's rotation
	public void setRotation(int players_team_rotation) {
		Rotation =players_team_rotation;
	}
	//Gets the number of players in the team's rotation
	public int getRotation(){
		return Rotation;
	}
	//Sets the average of the player's ratings who play Point Guard
	public void setAvgPG(float point_guard_average_given) {
		PG_Average=point_guard_average_given;
	}
	//Gets the average of the player's ratings who play Point Guard
	public float getAvgPG(){
		return PG_Average;
	}
	//Sets the average of the player's ratings who play Shooting Guard
	public void setAvgSG(float shooting_guard_average_given) {
		SG_Average=shooting_guard_average_given;
	}
	//Gets the average of the player's ratings who play Shooting Guard
	public float getAvgSG(){
		return SG_Average;
	}
	//Sets the average of the player's ratings who play Small Forward
	public void setAvgSF(float small_forward_average_given) {
		SF_Average=small_forward_average_given;
	}
	//Gets the average of the player's ratings who play Small Forward
	public float getAvgSF(){
		return SF_Average;
	}
	//Sets the average of the player's ratings who play Power Forward
	public void setAvgPF(float power_forward_average_given) {
		PF_Average=power_forward_average_given;
	}
	//Gets the average of the player's ratings who play Power Forward
	public float getAvgPF(){
		return PF_Average;
	}
	//Sets the average of the player's ratings who play Center
	public void setAvgC(float center_average_given) {
		C_Average=center_average_given;
	}
	//Gets the average of the player's ratings who play Center
	public float getAvgC(){
		return C_Average;
	}	
	//Sets the best player on the team based on their rating
	public void setBestPlayer(Player best_rated_player_given) {
		Best_Rated_Player =best_rated_player_given;
	}
	//Gets the best player on the team based on their rating
	public Player getBestPlayer(){
		return Best_Rated_Player;
	}
	//Sets the best scorer on the based on the ranking within the team
	public void setScorer(Player best_scoring_player) {
		Best_Scoring_Player =best_scoring_player;
	}
	//Gets the best scorer on the based on the ranking within the team
	public Player getScorer(){
		return Best_Scoring_Player;
	}
	//Sets the best rebounder on the based on the ranking within the team
	public void setRebounder(Player  best_rebounding_player) {
		Best_Rebounding_Player =best_rebounding_player;
	}
	//Gets the best rebounder on the based on the ranking within the team
	public Player getRebounder(){
		return Best_Rebounding_Player;
	}
	//Sets the best assister on the based on the ranking within the team
	public void setAssister(Player best_assisting_player) {
		Best_Assisting_Player =best_assisting_player;
	}
	//Gets the best assister on the based on the ranking within the team
	public Player getAssister(){
		return Best_Assisting_Player;
	}

	//ABOVE  - Setting and Getting the Team's basic attributes
	//BELOW  - Setting and Getting the Team's statistics
	
	//Increments the games played by the team and once the team reaches 82 - mark the season as completed
	public void setGamesPlayed() {
		GamesPlayed++;
		//Once playing 82 games mark season as complete
		if (GamesPlayed>=82) {
			Games_Complete_82=true;
		}
	}
	//Gets the number of games played by the team
	public int getGamesPlayed(){
		return GamesPlayed;
	}
	//Returns whether the team has played at least 82 games
	public boolean getComplete() {
		return Games_Complete_82;
	}
	//Increments the team's wins by one
	public void setWins() {
		Wins++;
	}
	//Returns the number of games the team won
	public int getWins(){
		return Wins;
	}
	//Increments the team's losses by one
	public void setLosses() {
		Losses++;
	}
	//Returns the number of games the team lost
	public int getLosses(){
		return Losses;
	}
	//Sets the team's playoffs seed to the seed given
	public void setSeed(int seed_given) {
		Seed = seed_given;
	}
	//Returns the team's playoff seed
	public int getSeed(){
		return Seed;
	}
	//Completely clears all of the player's stats over the course of a game, season, playoffs
	public void playerclearAll() {
		for (int h=0;h<leng();h++) {
			getPlayer(h).playerClear();
		}
	}
	//Clears all of the player individual games statistics 
	public void clearStats() {
		for (int h=0;h<leng();h++) {
			getPlayer(h).setPoints(0);
			getPlayer(h).setRebounds(0);
			getPlayer(h).setAssists(0);
		}
	}
	//clears the season wins,losses, and games played
	public void clearGames() {
		Wins=0;
		Losses=0;
		GamesPlayed=0;
		Games_Complete_82=false;
	}
	//Resets the stats of the season for both the team and the individual players on the roster
	public void clearAlls() {
		playerclearAll();
		clearGames();
	}
	//Adds a player to the team roster
	public void setPlayerslist(Player player_given){
		//Collections.sort(filelist);
		teamplayers.add(player_given);
		//filelist.
		//Collections.sort(filelist);
	}
	//Adds a player to the team roster at a specific address within the teamplayers arraylist
	public void setPlayerslist(int index_given,Player player_given){
		//Collections.sort(filelist);
		teamplayers.add(index_given, player_given);
	}
	//Removes a player from the team roster at the particular index in the team's arraylist of players
	public void removePlayer(Player index) {
		teamplayers.remove(index);
	}
	//Gets the player at the particular index in the team's arraylist of players
	public Player getPlayer(int index) {
		return teamplayers.get(index);
	}
	//Returns all the player's on the team
	public String getFilelist(){
		String player_list="";
		//Get the player's name and add it to the end of the string
		for(int i=0; i<teamplayers.size();i++)
			 player_list+= teamplayers.get(i).getPlayerName()+" ";
		return  player_list;
	}
	//Removes all of the players from the team
	public void clear() {
		teamplayers.clear();
	}
	//Returns the number of players on the team
	public int leng() {
		return teamplayers.size();
	}
	//Returns a string of the team name with the list of players on the team
	public String toString(){
		return Team_Name.toString()+" has "+getFilelist();
	}
	//Returns a string of the team and their seed
	public String playoffString(){
		String str = Team_Name.toString();
		if (Seed > 0) {
			str = Seed + " " + str;
		}
		return str;
	}
	//Compares the team's by name so that they can be ordered alphabetically by the cities
	public int compareTo(Team other){
		return Team_Name.compareTo(other.Team_Name);
	}
}