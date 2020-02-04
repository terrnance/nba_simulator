import java.util.ArrayList;

public class Team implements Comparable<Team> {

	//String - key
	private String theTeam;
	//List of files that contain this word
	private ArrayList<Player> teamplayers =new ArrayList<Player>();
	
	private float Rating;
	private  int Allstars =0;
	private float Average;
	private String Prefix;
	private float PG=0;
	private float SG=0;
	private float SF=0;
	private float PF=0;
	private float C=0;
	private String Conference;
	private String Division;
	private Player Best;
	private Player Scorer;
	private Player Rebounder;
	private Player Assister;
	private int Rotation;
	private int GamesPlayed=0;
	private int Wins=0;
	private int Losses=0;
	boolean Complete=false;
	public Team(){
		
	}
	public Team(String w){
		theTeam = w;
	}
	public void setTheName(String w){
		theTeam =w;
	}
	public String getTheName(){
		return theTeam.toString();
	}
	public void setConference(String c){
		Conference =c ;
	}
	public String getConference(){
		return Conference;
	}
	public void setDivision(String c){
		Division =c ;
	}
	public String getDivision(){
		return Division;
	}
	public void setAvg(float a) {
		Average =a;
	}
	public float getAvg() {
		return Average;
	}
	public void setAllstar(int a) {
		Allstars =a;
	}
	public int getAllstar() {
		return Allstars;
	}
	public void setPrefix(String p) {
		Prefix=p;
	}
	public String getPrefix() {
		return Prefix;
	}
	public void setAvgSG(float pg) {
		SG=pg;
	}
	public float getAvgSG(){
		return SG;
	}
	public void setAvgSF(float pg) {
		SF=pg;
	}
	public float getAvgSF(){
		return SF;
	}	
	public void setAvgPF(float pg) {
		PF=pg;
	}
	public float getAvgPF(){
		return PF;
	}	
	public void setAvgC(float pg) {
		C=pg;
	}
	public float getAvgC(){
		return C;
	}	
	public void setAvgPG(float pg) {
		PG=pg;
	}
	public float getAvgPG(){
		return PG;
	}
	public void setBest(Player b) {
		Best =b;
	}
	public Player getBest(){
		return Best;
	}
	public void setScorer(Player b) {
		Scorer =b;
	}
	public Player getScorer(){
		return Scorer;
	}
	public void setRebounder(Player b) {
		Rebounder =b;
	}
	public Player getRebounder(){
		return Rebounder;
	}
	public void setAssister(Player b) {
		Assister =b;
	}
	public Player getAssister(){
		return Assister;
	}
	public void setRotation(int r) {
		Rotation =r;
	}
	public int getRotation(){
		return Rotation;
	}
	public void setGamesPlayed() {
		GamesPlayed++;
		if (GamesPlayed>=82) {
			Complete=true;
		}
	}
	public int getGamesPlayed(){
		return GamesPlayed;
	}
	public boolean getComplete() {
		return Complete;
	}
	public void setWins() {
		Wins++;
	}
	public int getWins(){
		return Wins;
	}
	public void setLosses() {
		Losses++;
	}
	public int getLosses(){
		return Losses;
	}
	public void setPlayerslist(Player n){
		//Collections.sort(filelist);
		teamplayers.add(n);
		//filelist.
		//Collections.sort(filelist);
	}
	public void setPlayerslist(int p,Player n){
		//Collections.sort(filelist);
		teamplayers.add(p, n);

	}
	public void removePlayer(Player i) {
		teamplayers.remove(i);
	}


	public Player getPlayer(int i) {
		return teamplayers.get(i);
	}
	public void playerclearAll() {
		for (int h=0;h<leng();h++) {
			getPlayer(h).playerClear();
		}
	}
	public void clearStats() {
		for (int h=0;h<leng();h++) {
			getPlayer(h).setPoints(0);
			getPlayer(h).setRebounds(0);
			getPlayer(h).setAssists(0);
		}
	}
	public void clearWins() {
		Wins=0;
		Losses=0;
		GamesPlayed=0;
	}
	public void clearAlls() {
		playerclearAll();
		Wins=0;
		Losses=0;
		GamesPlayed=0;
		Complete=false;
	}
	public String getFilelist(){
		String f="";
		for(int i=0; i<teamplayers.size();i++)
			f+= teamplayers.get(i)+" ";
		return f;

	}
	public void clear() {
		teamplayers.clear();
	}
	public int leng() {
		return teamplayers.size();
	}
	public String toString(){
		return theTeam.toString()+" is a "+getFilelist();
	}
	
	public int compareTo(Team other){
		return theTeam.compareTo(other.theTeam);
	}
	
}