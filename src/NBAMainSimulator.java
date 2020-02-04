import java.io.File;
import java.io.IOException;
import java.util.*;
/*
 * This is the main file that should called when running the program
 * This is the Main Menu that allows the toggle between Head2Head Simulation, Roster Editing, and Latest Roster Download
 * If the proper directory have not been loaded, this program creates the files
 * Author: Terrance Nance <tnance@middlebury.edu>
 */
public class NBAMainSimulator  {
	
	static Scanner scan = new Scanner(System.in);
	public static ArrayList<Player> latest_roster_players = new ArrayList<Player>();
	public static ArrayList<Team>  latest_roster_teamcounter = new ArrayList<Team>();
	public static ArrayList<Player> editable_roster_players = new ArrayList<Player>();
	public static ArrayList<Team> editable_roster_teamcounter = new ArrayList<Team>();
	public static int active_roster = 1;
	public static int non_active_roster = 0;
	
	public static void main(String args []) throws Exception {
		System.out.println("\t\tWelcome to the NBA Simulator");
		checkFilesLoaded();
		String[] arguments = new String[2];
		arguments[0] = "latestRoster";
		arguments[1] = "editableRoster";
		NBAFileReadWrite.readFile("latestRoster",latest_roster_teamcounter,latest_roster_players);
		NBAFileReadWrite.readFile("editableRoster",editable_roster_teamcounter,editable_roster_players);
		while (true) {
			System.out.println("\t Active Roster: " +arguments[active_roster]);
			System.out.println("[0] About Page, [1] NBA Head 2 Head Sim, [2] NBA Roster Editor, [3] Get Latest Roster, [4] Change to "+arguments[non_active_roster]);
			System.out.print("You:");
			String choice = scan.next();
			if (choice.contains("0")) {
				aboutMe();
			}
			else if (choice.contains("1")) {
				NBAGame.main(arguments);
			}
			else if (choice.contains("2")) {
				boolean switch_active_roster = NBARoster.mainFunctionCall(arguments[active_roster]);
				if (switch_active_roster) {
					switchActiveRoster();
				}
			}
			else if (choice.contains("3")) {
				NBAWebScrap.mainFunctionCall(arguments);
			}
			else if (choice.contains("4")) {
				//Switches the active roster
				switchActiveRoster();
			}
			System.out.println("\t\t\tMain Menu\n");
		}
	}
	
	public static void aboutMe(){
		System.out.println("To learn more about the project view the github page: "+ "https://github.com/terrnance/nba_simulator");
	}
	//Switches the active roster that the user is currently using for simulation and roster editing
	public static void switchActiveRoster() {
		int tmp = active_roster;
		active_roster = non_active_roster;
		non_active_roster = tmp;
	}
	//Check that the files have been properly loaded so users can start interacting with program right away
	//If needed Creates the path to the 2 roster directories and add the team files to the directories
	public static void checkFilesLoaded() throws IOException {
		//Get the current path to latestRoster - create one if it currently does not exist
		String latest_roster_path = NBAFileReadWrite.getCurrentLatestRosterDirectory("latestRoster");
		if(NBAFileReadWrite.checkPathExistence(latest_roster_path)) {
			//Check that the contents are there
			File directory = new File(latest_roster_path);
			//Get all the files in the given directory
			File[] fList =directory.listFiles();
			//if there are no content in the files - write the contents
			if (fList.length < 30) {
				System.out.println("Inform Me 1");
				NBAWebScrap.getLatestUpdates();
			}
		}
		else {
			System.out.println("Error - creating path for latestRoster");
			System.out.println("Please add template latestRoster directory from github page");
		}
		//Get the current path to latestRoster - create one if it currently does not exist
		String editable_roster_path = NBAFileReadWrite.getCurrentLatestRosterDirectory("editableRoster");
		if(NBAFileReadWrite.checkPathExistence(editable_roster_path)) {
			//Check that the contents are there
			File directory = new File(latest_roster_path);
			//Get all the files in the given directory
			File[] fList =directory.listFiles();
			//if there are no content in the files - write the contents
			if (fList.length < 30) {
				System.out.println("Inform Me 2");
				NBAFileReadWrite.writeOutfile(false,"editableRoster",latest_roster_teamcounter);
			}
		}
		else {
			System.out.println("Error - creating path for editableRoster");
			System.out.println("Please add template editableRoster directory from github page");
		}
	}
}