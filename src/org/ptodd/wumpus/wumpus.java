/**
 * Hunt the Wumpus
 * 
 * Originally written by Gregory Yob in the early 1970s while at
 * Dartmouth, Hunt the Wumpus is an iconic early text-base adventure
 * game.
 * 
 * This version is adapted from the original BASIC program published
 * in "More BASIC Computer Games" from 1979 (although the program
 * itself is much older).  This version adapts the original to Java
 * syntax.  This is a purely procedural implementation not leveraging
 * any object-oriented design principles or language feature.  It
 * is designed to run from the console just as the original would
 * have worked.  As such, it is a good illustration of Java syntax
 * but not a particularly good example of a modern object-oriented
 * approach to the problem.  My intent is to simply resurrect the
 * original spirit, then build from that while developing my Java
 * skills.
 * 
 * @author  P. Todd Decker <ptdpublic@mac.com>
 * @version 1.0
 * @since   1.6
 * 
 **/

import java.util.*;
import java.io.*;

public class WumpusI {
	
	// Standard in, out, and error streams
	
	public static Scanner     in  = new Scanner(System.in);
	public static PrintWriter out = new PrintWriter(System.out, true);
	public static PrintWriter err = new PrintWriter(System.err, true);

	// Game objects
	//
	// Each object is represented by an integer.  The integer is used
	// as an index into the 'locationOf' array to determine where the
	// game object is located in the cave rooms.
	//
	// In some cases, the code is written to expect that HUNTER is always
	// stored in the first index ('0') of the array.  The index values for
	// the other objects could be changed without impacting program execution.
	
	public static final int NUM_OF_OBJECTS = 6;
	
	public static final int HUNTER = 0; // Don't change! HUNTER is expected to be index '0'
	public static final int WUMPUS = 1;
	public static final int PIT1   = 2;
	public static final int PIT2   = 3;
	public static final int BATS1  = 4;
	public static final int BATS2  = 5;
	
	// Action identifiers
	//
	// Used in the game event loop to identify the action that the
	// player has selected to take.
	
	public static final int ACTION_MOVE  = 1;
	public static final int ACTION_SHOOT = 2;
	public static final int ACTION_QUIT  = 3;

	// Game states
	//
	// Game state is passed throughout the supporting procedures and
	// is used for execution flow control.
	
	public static final int WUMPUS_DEAD = 1;
	public static final int HUNTER_DEAD = 2;
	public static final int CONTINUE    = 3;
	public static final int QUIT        = 4;
	
	// Cave definition data structure
	//
	// The cave itself is defined in the array 'cave[MAX_ROOMS][MAX_PATHS]'
	// Note that '0' is the index of the first element in a Java array
	// as such, reading and writing index values to and from the console
	// require adding or subtracting '1' so that the rooms are properly
	// numbered in human terms from room '1' to room 'MAX_ROOMS'.
	//
	// When looking at a particular cave room 'x', the paths out of the
	// room are represented as:  'cave[x][0]' through 'cave[x][MAX_PATHS-1]'.
	
	public static final int MAX_ROOMS = 20;
	public static final int MAX_PATHS = 3;
	
	int[][] cave = {{ 1, 4, 7},{ 0, 2, 9},{ 1, 3,11},{ 2, 4,13},{ 0, 3, 5},
	                { 4, 6,14},{ 5, 7,16},{ 0, 6, 8},{ 7, 9,17},{ 1, 8,10},
	                { 9,11,18},{ 2,10,12},{11,13,19},{ 3,12,14},{ 5,13,15},
	                {14,16,19},{ 6,15,17},{ 8,16,18},{10,17,19},{12,15,18}};

	// Game parameters
	//
	// Constants that set the game characteristics
	
	public static final int MAX_ARROWS      = 5;
	public static final int MIN_ARROW_RANGE = 1;
	public static final int MAX_ARROW_RANGE = 5;
	
	// Game state data structures
	//
	// The key data structures for the game itself beyond the 'cave' array
	//      'locationOf'   - Array containing the cave room location of each of the game objects
	//      'initialState' - A second array mirroring 'locationOf' for purposes of restoring the game
	//      'arrows'       - The number of arrows remaining in the hunter's quiver
	//      'status'       - The current game state
	
	int[] locationOf   = new int[NUM_OF_OBJECTS];
	int[] initialState = new int[NUM_OF_OBJECTS];
	int   arrows;
	int   status;
	
	/**
	 * WumpusI Constructor
	 * 
	 * Offers instructions, sets up the game, then enters a play loop
	 * until the player decides enough is enough providing the chance
	 * to play the same configuration over again.
	 */
		
	private WumpusI() {
		
		out.println("Java Wumpus\n");
		
		if (singleUpperCaseCharPrompt("Instructions (Y-N)?")=='Y') {			
			instructions();
		} // if instructions
		
		setup();
		
		do {			
			out.println("\nHunt the Wumpus");			
			playGame();	
			finalStatus();						
		} while (playAgain());
		
		out.println("\nThank you for playing 'Hunt the Wumpus'!");

	} // constructor WumpusI

	/**
	 * Game event loop
	 * 
	 * Keep repeating so long as the game state is 'CONTINUE'.  Look
	 * around the current cave, take an action, check for hazards,
	 * rinse, and repeat.
	 */
	
	private void playGame() {

		status = CONTINUE;

		do {	
			look();
			status = takeAction();
			if (status==CONTINUE) status = checkHazards(status);
		} while (status==CONTINUE);

	} // method playGame

	/**
	 * Display final status
	 * 
	 * Based upon the game state, displays a final status message to
	 * the player.
	 */
	
	private void finalStatus() {

		switch (status) {
		case WUMPUS_DEAD:
			out.println("\nHee Hee Hee - The Wumpus'll getcha next time!!");
			break;
		case HUNTER_DEAD:
			out.println("\nHa Ha Ha - You Lose!");
			break;
		case QUIT:
			out.println("\nYou give up and are magically returned to safety in shame!");
			break;
		default:
			err.println("Something bad happend!");
		} // switch game state

	} // method finalStatus

	/**
	 * "Play Again?" query and game reset
	 * 
	 * Queries the player to see if they want to have another go at
	 * the game, then resets the game state back to its original settings.
	 * 
	 * @return 'True' if the player wants to play again
	 */

	private boolean playAgain() {

		if (singleUpperCaseCharPrompt("\nWould you like to play again with the same set-up (Y/N)?") == 'Y') {

			for (int j=0; j < NUM_OF_OBJECTS; j++) {
				locationOf[j] = initialState[j];
			} // for j
			
			arrows = MAX_ARROWS;
			
			return true;

		} // if same setup
		
		return false;
		
	} // method playAgain

	/**
	 * Take an action
	 * 
	 * Queries the player on the action that they want to take, then
	 * passes execution to the proper path based upon the action 
	 * identifier.
	 * 
	 * @return the game state after the action has been taken
	 */
	
	private int takeAction() {
	
		switch (getAction()) {
		case ACTION_MOVE:
			return move();
		case ACTION_SHOOT:
			return shoot();
		case ACTION_QUIT:
			return QUIT;
		default:
			err.println("Invalid action identifier returned from 'getAction' method");
			return QUIT;
		} // switch action identifier

	} // method takeAction

	/**
	 * Prompt for and return a single, upper case, character from the console
	 * 
	 * This helper method _does not_ check the validity of the character returned against
	 * what might have been expected.  It does assure that some character will be
	 * returned, repeating the prompt until one is provided.
	 * 
	 * @param msg a String containing the message to be displayed on the console as a prompt
	 * @return the character returned
	 */
	
	private char singleUpperCaseCharPrompt(String msg) {

		String response;
		
		do {
			out.printf("%s ", msg);
			while (!in.hasNextLine());
			response = in.nextLine().toUpperCase();
		} while (response.isEmpty());
		
		return response.charAt(0);

	} // method singleUpperCaseCharPrompt
	
	/**
	 * Set up the game
	 * 
	 * Set up the game by placing the game objects into the cave system
	 * assuring that each cave room contains, at most, one object. 
	 * 
	 * The algorithm used to assure no two objects are placed into the
	 * same room is to loop through each of the game objects and, for 
	 * each one, pick a random room in which to place the object.  Before
	 * the objects location is recorded, we loop back down through all the
	 * game objects previously placed to assure that the newly selected
	 * room hasn't already been used.  If it has been, then we pick 
	 * another room and try all over again.  Once we have a good room,
	 * we set the object to that location and mirror the same information
	 * in 'initialState' so we can reset the game to the same configuration.
	 * Also, fill the hunter's quiver with the starting number of arrows.
	 */
	
	private void setup() {
		Random generator = new Random();		
		for (int j=0; j<NUM_OF_OBJECTS; j++) {
			int loc = generator.nextInt(MAX_ROOMS);     // random location
			for (int k=(j-1); k>=0; k--) {              // check prior locations to assure no duplicates
				if (locationOf[k]==loc) {               // found a duplicate, so try another location
					loc = generator.nextInt(MAX_ROOMS); // pick a new random location
					k = j;                              // k will be reduced by one in the for loop
				} // if location match
			} // for k
			locationOf[j] = loc;
			initialState[j] = loc;
		} // for j
		arrows = MAX_ARROWS;
	} // method setup
	
	/**
	 * Look around the cave room and take stock of things
	 * 
	 * Locate all the tunnels leading out of the cave room, sense for any game
	 * hazards, and check out how many arrows are left.
	 */
	
	private void look() {
		lookTunnels();
		senseHazards();
		quiverStatus();
	} // method look

	/**
	 * Identify all the tunnels leading out of the current room
	 * 
	 * Display the current location of the hunter and iterate through
	 * all the locations connected to it.
	 */
	
	private void lookTunnels() {
		out.printf("\nYou are in room %d.\n", locationOf[HUNTER]+1);
		out.print("Tunnels lead to");
		for (int j=0; j<MAX_PATHS; j++) {
			if (j==0) out.print(" ");
			if ((0<j)&&j<(MAX_PATHS-1)) out.print(", ");
			if (j==(MAX_PATHS-1)) out.print(", and ");
			out.print(cave[locationOf[HUNTER]][j]+1);
		} // for j
		out.println(".");
	} // method lookTunnels

	/**
	 * Sense any game hazards
	 * 
	 * For each game object, except the hunter (HUNTER = '0'), check each of the
	 * connecting cave rooms to see if that object exists in that room adjacent
	 * room.  If it does, then display a warning message on the console.
	 */
	
	private void senseHazards() {
		for (int j=1; j<NUM_OF_OBJECTS; j++) {
			for (int k=0; k<MAX_PATHS; k++) {
				if (cave[locationOf[HUNTER]][k]==locationOf[j]) {
					switch (j) {
					case WUMPUS:
						out.println("You smell a Wumpus!");
						break;
					case PIT1:
					case PIT2:
						out.println("You feel a draft!");
						break;
					case BATS1:
					case BATS2:
						out.println("You hear bats nearby!");
						break;
					default:
						err.println("Oops! Something unexpected happened!");
						break;
					} // switch game object
				} // if adjacent object
			} // for k
		} // for j
	} // method senseHazards()
	
	/**
	 * Display the status of the hunter's quiver
	 * 
	 * Display the number of arrows in the quiver onto the console
	 */

	private void quiverStatus() {
		if (arrows==0) {
			out.println("You have no more arrows!");
		} else if (arrows==1) {
			out.println("You only have one more arrow!");
		} else {
			out.printf("You have %d arrows.\n", arrows);
		} // if-else chain condition checks
	} // method quiverStatus
	
	/**
	 * Get the action the player wants to take
	 * 
	 * Repeats asking until a valid action is selected.
	 * 
	 * @return the action identifier corresponding to the selected action
	 */
	
	private int getAction() {
		do {
			switch (singleUpperCaseCharPrompt("\nShoot, move, or quit (S,M,Q)?")) {
			case 'S':
				return ACTION_SHOOT;
			case 'M':
				return ACTION_MOVE;
			case 'Q':
				return ACTION_QUIT;
			} // switch action identifier
		} while (true);
	} // method getAction
	
	/**
	 * Move the hunter to a new room
	 * 
	 * Implements the ACTION_MOVE action identifier.  Queries the player as to
	 * where the hunter should be moved.  Repeats the query until a valid reachable
	 * room is selected.  Then changes the location of the hunter to that room.
	 * 
	 * @return game state after moving to the new room (always CONTINUE)
	 */
	
	private int move() {
		out.printf("Where to? ");
		try {
			int loc = Integer.parseInt(in.nextLine()) - 1;
			if (isConnected(locationOf[HUNTER], loc)) {
				locationOf[HUNTER]=loc;
			} else {
				out.println("You can't get there from here!");
			} // if connected
		} catch (NumberFormatException e) {
			out.println("That's not a room number!");
		} // try-catch
		return CONTINUE;
	} // method move
	
	/**
	 * Check to see if one cave room is connected to another
	 * 
	 * Helper method to see if two rooms are connected to each other
	 * 
	 * @param from the room to leave from
	 * @param to the room to see if we can arrive in
	 * @return 'true' if we can get from room 'from' to room 'to'
	 */
	
	private boolean isConnected(int from, int to) {
		for (int j=0; j < MAX_PATHS; j++) {
			if (cave[from][j]==to) {
				return true;
			} // if connected
		} // for j
		return false;
	} // method isConnected
	
	/**
	 * Shoot an arrow from the hunter's bow
	 * 
	 * Implements the ACTION_SHOOT action identifier.  So long as the hunter
	 * has arrows, remove an arrow from the quiver, get the desired path of
	 * the arrow, then track the arrow as it moves from room to room returning
	 * the game status after the arrow flies.  If the game is still continuing
	 * after the arrow has finished its flight, awaken the wumpus so it has a
	 * chance to move to a new room.
	 * 
	 * 'arrowPath' ArrayList is used to store the path of the arrow
	 * 
	 * @return the game status after shooting an arrow and, perhaps, moving the wumpus
	 */
	
	private int shoot() {
		if (arrows>0) {
			arrows--;
			ArrayList<Integer> arrowPath = new ArrayList<Integer>();
			getPath(arrowPath, getRange());
			int status = trackArrow(arrowPath);
			if (status==CONTINUE) {
				return wumpusAwaken();
			} else {
				return status;
			} // if status CONTINUE
		} else {
			out.println("Unfortunately, you are out of arrows!");
			return CONTINUE;
		} // if arrows
	} // method shoot
	
	/**
	 * Get the range the hunter wants to shoot
	 * 
	 * Determines the number of rooms the hunter wants to shoot the arrow
	 * checking to assure the range is within the capabilities of the bow.
	 * 
	 * @return the range the hunter wants to shoot
	 */
	
	private int getRange() {
		int range = 0;
		do {

			// get a valid range from the hunter via the console
			
			out.printf("Number of rooms? ");
			try {
				range = Integer.parseInt(in.nextLine());
			} catch (NumberFormatException e) {
				out.println("Please enter a number!");
			} // try-catch
			
			// ensure that the entered range is valid
			
			if (range < MIN_ARROW_RANGE) {
				out.println("An arrow must be shoot a distance of at least one room!");
			} else if (range > MAX_ARROW_RANGE) {
				out.println("Your bow isn't strong enough to shoot an arrow that far!");
			} else {
				break;
			} // if-else chain condition checks
			
		} while (true); // intentional infinite loop, 'break' used to exit
		
		return range;
		
	} // method getRange
	
	/**
	 * Get a desired flight path for the arrow
	 * 
	 * Based upon how far the hunter wants to shoot (range _not_ checked here),
	 * get each of the desired rooms on the arrow's flight path.  Room and flight
	 * path validity is not checked.  However, the flight path cannot immediately
	 * turn back on itself (180 degree turn to come back from whence it came) and,
	 * if shooting only one room, the hunter cannot shoot into the same room in 
	 * which she stands.
	 * 
	 * @param arrowPath an integer array to be filled with the arrows path
	 * @param range the range of the arrow
	 */
	
	private void getPath(ArrayList<Integer> arrowPath, int range) {

		int nextRoom;
		for (int j = 0; j < range; j++) {
			
			// get a room number on the flight path

			out.printf("Room %d ? ", j+1);
			try {
				nextRoom = Integer.parseInt(in.nextLine())-1;
			} catch (NumberFormatException e) {
				out.println("Please enter a number!");
				j--;
				continue;
			} // try-catch
			
			// assure that it is valid and add it to the flight path if it is

			if (locationOf[HUNTER] == nextRoom) {
				out.println("You cannot try to commit suicide!");
				j--;
				continue;
			} else if ((arrowPath.size() > 1) && (arrowPath.get(arrowPath.size()-2) == nextRoom)) {
				out.println("Your arrows are not that crooked!");
				j--;
				continue;
			} else {
				arrowPath.add(nextRoom);
			} // if-else chaing condition checks

		} // for j
		
	} // method getPath

	
	/**
	 * Track the flight of the arrow
	 * 
	 * The flight of the arrow itself is managed through a recursive algorithm.
	 * The arrow tries to fly along the path the hunter selected ('guided flight');
	 * however, if it is impossible to get to the next location specified on the path,
	 * the arrow switches to random flight.  'guidedArrowFlightIntoRoom' recursively
	 * handles guided flight and 'randomArrowFlightIntoRoom' handles random flight.
	 * 
	 * We start things out by using guided flight to try to get from the current room
	 * where the hunter is located into the first room on the arrow's flight path.
	 * Since the arrow hasn't yet flown, the prior room to this one is set to a 
	 * sentinel value of '-1'.
	 * 
	 * @param arrowPath an ArrayList of the desired path of the arrow
	 * @return the game state after the arrow has flown its course
	 */
	
	private int trackArrow(ArrayList<Integer> arrowPath) {
		return guidedArrowFlightIntoRoom(-1, locationOf[HUNTER], arrowPath.remove(0), arrowPath);
	} // method trackArrow
	
	/**
	 * Guided arrow flight
	 * 
	 * Recursively handles guided flight of an arrow.  After checking to see if the arrow
	 * can get from where it is ('currentRoom') into the nextRoom, it sees if the arrow is going
	 * to hit either the hunter or the wumpus.  If it is, then we're done and return back down
	 * the recursive calling stack the appropriate _DEAD game status.  If we didn't hit anything
	 * and the 'nextRoom' is the last room on the arrow's flight path, then we're also done and
	 * CONTINUE on.  But, if we didn't hit anything and we still have more flying to do, then
	 * recursively call 'guidedArrowFlightIntoRoom' to take the arrow a little further.   If,
	 * however, when we are unable to get from currentRoom into the nextRoom then we ignore where
	 * we were supposed to go and, instead, flip over to random flight.
	 * 
	 * @param priorRoom the room the arrow was in prior to the room it is currently in, set
	 *                  set to '-1' if just starting our flight.
	 * @param currentRoom the room the arrow is currently in or starting its flight from
	 * @param nextRoom the room we are trying to get to using guided flight
	 * @param arrowPath the remaining flight path of the arrow (ever decreasing in size)
	 * @return the game state after the arrow gets to its final destination
	 */
	
	private int guidedArrowFlightIntoRoom(int priorRoom, int currentRoom, int nextRoom, ArrayList<Integer> arrowPath) {
		if (isConnected(currentRoom, nextRoom)) {
			if (locationOf[HUNTER]==nextRoom) {
				out.println("\nOh, no! You were hit by your own arrow!");
				return HUNTER_DEAD;
			} else if (locationOf[WUMPUS]==nextRoom) {
				out.println("\nWhap! Your arrow hit a wumpus!");
				return WUMPUS_DEAD;
			} else if (arrowPath.size()==0) {
				out.println("\nYou missed!");
				return CONTINUE;
			} else {
				return guidedArrowFlightIntoRoom(currentRoom, nextRoom, arrowPath.remove(0), arrowPath);
			} // if-else chain condition checks
		} else {
			return randomArrowFlightIntoRoom(priorRoom, currentRoom, arrowPath.size());
		} // if isConnected
	} // method guidedArrowFlightIntoRoom
	
	/**
	 * Random arrow flight
	 * 
	 * Recursively handles the random flight of an arrow.  Unlike guided flight, we don't know where the
	 * arrow should be going so the first thing we do is pick a room at random that we can get to from 
	 * where we are; however, we check to assure that the arrow doesn't do an 180 turn and double back
	 * on itself.  Once we have a random room, we check and see if the arrow is going to hit anything
	 * in that randomly selected room.  If so, we return as appropriate.  If not, we make sure that we
	 * have any distance left to go (we might not [note: could probably check this before bothering to
	 * pick a room to go to, but done this we to mirror the guided flight method]).  If we do have 
	 * further to go, then recursively call 'randomArrowFlightIntoRoom' to fly the arrow further down
	 * its range.
	 * 
	 * @param priorRoom the room the arrow was in prior to the room it is currently in
	 * @param currentRoom the room the arrow is currently in
	 * @param remainingRange the remaining range of the arrow (flight path doesn't matter anymore)
	 * @return the game state after the arrow gets to its final destination
	 */
	
	private int randomArrowFlightIntoRoom(int priorRoom, int currentRoom, int remainingRange) {

		// select a random connected room other than the one we came from
		
		Random generator = new Random();
		int nextRoom;
		do {
			nextRoom = cave[currentRoom][generator.nextInt(MAX_PATHS)];
		} while (nextRoom==priorRoom);
		
		// check conditions, recursively continue flight if appropriate
		
		if (locationOf[HUNTER]==nextRoom) {
			out.println("\nOh, no! You were hit by your own arrow!");
			return HUNTER_DEAD;
		} else if (locationOf[WUMPUS]==nextRoom) {
			out.println("\nWhap! Your arrow hit a wumpus!");
			return WUMPUS_DEAD;
		} else if (remainingRange==0) {
			out.println("\nYou missed!");
			return CONTINUE;
		} else {
			return randomArrowFlightIntoRoom(currentRoom, nextRoom, remainingRange - 1);
		} // if-else chain condition checks

	} // method randomArrowFlightIntoRoom
	

	/**
	 * Check for the effect of any hazards
	 * 
	 * Check the room the hunter is currently in for the existence of any hazards.
	 * If any exist, take the appropriate action and return game state.
	 * 
	 * @param status current game state (passed so it can be preserved)
	 * @return game state after checking for hazards
	 */
	
	private int checkHazards(int status) {
		if (locationOf[HUNTER]==locationOf[WUMPUS]) {
			return wumpusAction();
		} else if ((locationOf[HUNTER]==locationOf[BATS1])||(locationOf[HUNTER]==locationOf[BATS2])) {
			return batAction();
		} else if ((locationOf[HUNTER]==locationOf[PIT1])||(locationOf[HUNTER]==locationOf[PIT2])) {
			return pitAction();
		} else {
			return status;
		} // if-else chain condition checks
	} // method checkHazards
	
	/**
	 * Wumpus bumped into
	 * 
	 * Wakes up the wumpus if it was bumped into
	 * 
	 * @return game state after wumpus bumped into
	 */
	
	private int wumpusAction() {
		out.println("\nOops! You bumped in a Wumpus!");
		return wumpusAwaken();
	} // method wumpusAction
	
	/**
	 * Wumpus awoke
	 * 
	 * Wumpus was awaken from sleep so it takes action.  Pick a random
	 * path to move to from 0 to MAX_PATHS.  If 0 to MAX_PATHS - 1 (which
	 * when there are 3 paths will occur 75% of the time, then we move to
	 * the room connected to by the selected path.  Otherwise (25% of the time
	 * in a 3 MAX_PATHS game), the wumpus stays where it is.  If the wumpus
	 * moves, and happens to move into the room where the hunter is located,
	 * then it immediately attacks.
	 * 
	 * @return game state after wumpus wakes up and, perhaps, moves
	 */
	
	private int wumpusAwaken() {
		out.println("The Wumpus woke up!");

		// pick a random path or one more than are possible
		// move to the room connected by the path if appropriate
		
		Random generator = new Random();
		int newLoc = generator.nextInt(MAX_PATHS+1);
		if (newLoc < MAX_PATHS) {
			locationOf[WUMPUS] = cave[locationOf[WUMPUS]][newLoc];
			out.println("The Wumpus is moving to a new room!");
		} // if moving
		
		// see if the hunter encounters some bad luck
		
		if (locationOf[HUNTER] == locationOf[WUMPUS]) {
			out.println("The Wumpus attacks you!");
			return HUNTER_DEAD;
		} else {
			return CONTINUE;
		} // if hunter check
		
	} // method wumpusAwaken

	/**
	 * Bats take action
	 * 
	 * Bats move the hunter to any random room in the cave.  Once there,
	 * recursively checkHazards again and return the game state
	 * 
	 * @return game state after bats randomly dropped hunter
	 */
	
	private int batAction() {
		Random generator = new Random();
		locationOf[HUNTER] = generator.nextInt(MAX_ROOMS);
		out.println("\nZap! A superbat snatched you!  Elsewhere for you!");
		return checkHazards(CONTINUE);
	} // method batAction
	
	/**
	 * Pit takes action
	 * 
	 * Not much to say about this--hutner dead, end of game
	 * 
	 * @return game state after falling into a pit
	 */
	
	private int pitAction() {
		out.println("\nYyyiiiiieeeeee .... you fell into a pit!");
		return HUNTER_DEAD;
	} // method pitAction
	
	/**
	 * Display the instructions on the console
	 */
	
	private void instructions() {
		out.printf("Welcome to 'Hunt the Wumpus'\n\n");
		out.printf("The Wumpus lives in a cave of %d rooms.  Each room\n", MAX_ROOMS);
		out.printf("has %d tunnels leading to other rooms.  (Look at a\n", MAX_PATHS);
		out.printf("dodecahedron to see how this works-If you don't know\n");
		out.printf("what a dodecahedron is, ask someone)\n\n");
		out.printf("Hazards:\n\n");
		out.printf("Bottomless Pits - Two rooms have bottomless pits in them\n");
		out.printf("if you go there, you fall into the pit (and lose!)\n\n");
		out.printf("Superbats - Two other rooms hae super bats. If you go\n");
		out.printf("there, a bat grabs you and takes you to some other room\n");
		out.printf("at random (which might be troublesome)\n\n");
		out.printf("Wumpus - The wumpus is not bothered by the hazards (he\n");
		out.printf("has sucker feet and is too big for a bat to lift).\n");
		out.printf("Usually he is asleep.  Two things that wake him up:\n");
		out.printf("your entering his room, or your shooting an arrow.\n");
		out.printf("If the wumpus wakes, he moves (P=.75) one room or\n");
		out.printf("stays still (P=.25). After that, if he is where you are\n");
		out.printf("he eats you up (and you lose!)\n\n");
		out.printf("You - Each turn you may move or shoot a crooked arrow.\n");
		out.printf("Moving: You can go one room (thru one tunnel).\n");
		out.printf("Arrows: You have 5 arrows.  You lose when you run out.\n");
		out.printf("Each arrow can go from 1 to 5 rooms.  You aim by telling\n");
		out.printf("the computer the rooms you want the arrow to go to.  If\n");
		out.printf("the arrow can't go that way (i.e. no tunnel) it moves at\n");
		out.printf("random to the next room. If the arrow hits the Wumpus,\n");
		out.printf("you win.  If it hits you, you lose.\n\n");
		out.printf("Warnings:\n\n");
		out.printf("When you are one room away from the Wumpus or a hazard,\n");
		out.printf("the computer says:\n");
		out.printf("\tWumpus - 'I smell a wumpus!'\n");
		out.printf("\tBat - 'Bats nearby.'\n");
		out.printf("\tPit - 'I feel a draft'\n\n");
	} // method instructions
	
	/**
	 * Static main method.  Program entry point 
	 * 
	 * @param args not used
	 */
	
	public static void main(String[] args) {
		new WumpusI();
	} // static method main

} // class WumpusI
