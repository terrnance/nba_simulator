import java.util.*;
import java.text.*;

public class Player implements Comparable<Player> {

	//String - key
	private String theWord;
	//List of files that contain this word
	private ArrayList<Integer> filelist =new ArrayList<Integer>();
	private String Upgraded="";
	private float Rating;
	private String Team;
	private String Position;
	private String OriginTeam;
	private String Year="";
	private boolean Injury =false;
	private String Allstar ="";
	private int Points;
	private int Rebounds;
	private int Assists;
	private int Scoring;
	private int Rebounding;
	private int Assisting;
	private int TotalPoints=0;
	private int TotalRebounds=0;
	private int TotalAssists=0;
	private int GamesPlayed=0;
	private float real_life_PPG=0;
	private float real_life_RPG=0;
	private float real_life_APG=0;
	private float real_life_ORTG=0;
	private float real_life_DRTG=0;
	private float real_life_SPG=0;
	private float real_life_BPG=0;
	private float real_life_MPG=0;
	private float real_life_UP=0;//Usage Rate
	public Player(){
		
	}
	public Player(String w){
		theWord = w;
	}
	public void setTheWord(String w){
		theWord =w;
	}
	public String getTheWord(){
		return theWord.toString();
	}
	public void setPosition(String r) {
		Position =r;
	}
	public String getPosition(){
		return Position;
	}
	public void setRating(float c) {
		Rating =c;
	}
	public float getRating() {
		return Rating;
	}
	public void setTeam(String p) {
		Team = p;
	}
	public String getTeam() {
		return Team;
	}
	public void setUpgraded(String p) {
			Upgraded= p;
	}
	public String getUpgraded() {
		return Upgraded;
	}
	public void setOrigin(String u) {
		OriginTeam=u;
	}
	public String getOrigin() {
		return OriginTeam;
	}
	public void setYear(String y) {
		Year =y;
	}
	public String getYear() {
		return Year;
	}
	public void setInjury(boolean i) {
			Injury=i;
	}
	public boolean getInjury() {
		return Injury;
	}
	public void setAllstar(String i) {
		Allstar=i;
	}
	public String getAllstar() {
		return Allstar;
	}
	public void setPoints(int p) {
		Points=p;
		TotalPoints+=p;
	}
	public int getPoints() {
		return Points;
	}
	public void setScoring(int s) {
		Scoring =s;
	}
	public int getScoring() {
		return Scoring;
	}
	public void setRebounds(int p) {
		Rebounds=p;
		TotalRebounds+=p;
	}
	public int getRebounds() {
		return Rebounds;
	}
	public void setRebounding(int s) {
		Rebounding =s;
	}
	public int getRebounding() {
		return Rebounding;
	}
	public void setAssists(int p) {
		Assists=p;
		TotalAssists+=p;
	}
	public int getAssists() {
		return Assists;
	}
	public void setAssisting(int s) {
		Assisting =s;
	}
	public int getAssisting() {
		return Assisting;
	}
	public void setGamesPlayed() {
		GamesPlayed++;
		
	}
	public int getGamesPlayed() {
		return GamesPlayed;
	}
	public void playerClear() {
		TotalPoints=0;
		TotalRebounds=0;
	 	TotalAssists=0;
	 	GamesPlayed=0;
	 	Points=0;
		Rebounds=0;
		Assists=0;
	}
	public float PointsPG() {
		if (GamesPlayed>0) {
			float d = (float) TotalPoints/GamesPlayed;
			 DecimalFormat df = new DecimalFormat("#.#");
		     
		     return Float.parseFloat((df.format(d)));
		}
		else {
			return 0;
		}
	}
	public float ReboundsPG() {
		if (GamesPlayed>0) {
			float d = (float)TotalRebounds/GamesPlayed;
			DecimalFormat df = new DecimalFormat("#.#");
		     
		    return Float.parseFloat((df.format(d)));
		}
		else {
			return 0;
		}
	}
	public float AssistsPG() {
		if (GamesPlayed>0) {
			float d = (float)TotalAssists/GamesPlayed;
			DecimalFormat df = new DecimalFormat("#.#");
		     
		    return Float.parseFloat((df.format(d)));
		}
		else {
			return 0;
		}
	}
	public void setPPG(float new_PPG) {
		real_life_PPG = new_PPG;
	}
	public float getPPG() {
		return real_life_PPG;
	}
	public void setRPG(float new_RPG) {
		real_life_RPG = new_RPG;
	}
	public float getRPG() {
		return real_life_RPG;
	}
	public void setAPG(float new_APG) {
		real_life_APG = new_APG;
	}
	public float getAPG() {
		return real_life_APG;
	}
	public void setORTG(float new_ORTG) {
		real_life_ORTG = new_ORTG;
	}
	public float getORTG() {
		return real_life_ORTG;
	}
	public void setDRTG(float new_DRTG) {
		real_life_DRTG = new_DRTG;
	}
	public float getDRTG() {
		return real_life_DRTG;
	}
	public void setSPG(float new_SPG) {
		real_life_SPG = new_SPG;
	}
	public float getSPG() {
		return real_life_SPG;
	}
	public void setBPG(float new_BPG) {
		real_life_BPG = new_BPG;
	}
	public float getBPG() {
		return real_life_BPG;
	}
	public void setMPG(float new_MPG) {
		real_life_MPG = new_MPG;
	}
	public float getMPG() {
		return real_life_MPG;
	}
	public void setUP(float new_UP) {
		real_life_UP = new_UP;
	}
	public float getUP() {
		return real_life_UP;
	}
	public void setFilelist(int n){
		//Collections.sort(filelist);
		filelist.add(n);
		//Collections.sort(filelist);
	}
	public void setFilelist(int p,int n){
		//Collections.sort(filelist);
		filelist.add(p, n);

	}
	public String getFilelist(){
		String f="";
		for(int i=0; i<filelist.size()-1;i++)
			f+= filelist.get(i)+" ";
		return f;

	}
	public String toString(){
		return theWord.toString()+" is a "+String.valueOf((int)Rating)+" overall";
	}
	public String toString2() {
		String str=theWord.toString()+" "+(int)Rating;
		if (Upgraded.equals("U")){
			str= str + " "+Upgraded;
		}
		return str;
	}
	public String toFinalString() {
		String str=theWord.toString()+" "+(int)Rating;
		if (Upgraded.equals("U")){
			str= str + " "+Upgraded;
		}
		str = str + " "+Scoring + " "+ Rebounding+ " "+ Assisting;
		return str;
	}
	public String toOldString() {
		String str=theWord.toString()+" "+getFilelist()+" "+OriginTeam+" \t"+(int)Rating;
		if (Upgraded.equals("U")){
			str= theWord.toString()+" "+String.valueOf((int)Rating)+" "+OriginTeam+" \t"+Upgraded;
		}
		return str;
	}
	public String Averages() {
		return theWord.toString()+" averaged "+PointsPG()+" ppg, "+ ReboundsPG()+" rpg, and " +AssistsPG()+" apg";
	}
	public String Totals() {
		return theWord.toString()+" totaled "+TotalPoints+" pts, "+ TotalRebounds+" rebs, and " +TotalAssists+" asts";
	}
	public int compareTo(Player other){
		return theWord.compareTo(other.theWord);
	}
	
}

