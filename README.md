# nba_simulator

## Terrance Nance <tnance@middlebury.edu> 

I created this project in order to extract the current NBA rosters and player statistics so that I could simulate real NBA games. I used Java to create these files. Users are able to edit the roster by adding, removing, and trading players from teams. Users can also simulate the NBA postseason and simulate the NBA season.

### Key Abbreviations
 ##### PPG - Points Per Game
 ##### RPG - Rebounds Per Game
 ##### APG - Assists Per Game
 ##### SPG - Steals Per Game
 ##### BPG - Blocks Per Game
 ##### MPG - Minutes Per Game
 ##### UP - Usage Rate
 ##### ORTG - Offensive Rating
 ##### DRTG - Defensive Rating

## Things about the simulator

In order to scrap, I am using the websites: 

http://www.espn.com/nba/players/_/position/pg 
https://www.nbastuffer.com/2019-2020-nba-player-stats/
https://www.cbssports.com/nba/injuries/
https://www.espn.com/nba/story/_/id/28292088/nba-all-star-game-2020-rosters-schedule-news-how-watch

For the first website, I am getting the current team rosters.
For the second website, I am getting the every player's statistics.
For the third website, I am getting the current injuries.
For the fourth website, I am getting the current allstars.

Based on the player's PPG, RPG, APG, ORTG, UP, and MPG - I calcuate the player's offensive rating.
Based on the player's SPG, BPG, DRTG and MPG - I calcuate the player's defensive rating.
I use the offensive rating and defensive rating in combination with the actual player's PPG, RPG, APG, MPG to calculate the player's rating.

Afterwards, I used the team player's rating to calculate the scores of games.

In addition, users can simulate games will both the lastest rosters and their own customizable rosters.

### File format

I am using text file to store the team and player info.

The format of the generally file goes:

 #### TeamPrefix(Rotation)
 #### TeamConference
 #### TeamDivision

 #### Position
 #### PlayerName PlayerRating PlayerRankingPoints PlayerRankingRebounds PlayerRankingsAssists

U stands for Upgraded by User, R stands for Rookie, A stands for Allstar, I stands for Injured

## Things you need to use the simulator

The main function is NBAMainSimulator.java

Currently, You will need to have the two rosters have the "latestRoster" and "editableRoster" directories readily available to run. The editableRoster can be edited by users, but the latestRoster can only be updated by scrapping the latest NBA rosters and statistics. For the first boot, the directories will be created and filled with the correct files automatically.


## Why I took on this project

I really enjoy watching and following the NBA. I always come up with trade ideas and free agent ideas that I think can help out an NBA team. I always wanted to know if my ideas would actually help the team or backfire. So, I decided to take on this project to explore my curiosity. I also thought that this would be a great opportunity to grow my programming skills.

## Future Work

I plan on creating an app so that others can simulate games on the go. I might also potentially update the algorithms used for calculating a player's rating and creating the box score.

## Acknowledgments

### NBA 
### ESPN - http://www.espn.com/nba/players/_/position/pg https://www.espn.com/nba/story/_/id/28292088/nba-all-star-game-2020-rosters-schedule-news-how-watch
### NBA Stufferr - https://www.nbastuffer.com/2019-2020-nba-player-stats/
### CBS Sports - https://www.cbssports.com/nba/injuries/


## What I am using 
Eclipse IDE for Java Developers

Version: Oxygen.2 Release (4.7.2)
