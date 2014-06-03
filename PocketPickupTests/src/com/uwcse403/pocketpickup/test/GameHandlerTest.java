package com.uwcse403.pocketpickup.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.ParseInteraction.DbColumns;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.ParseInteraction.Translator;
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;

public class GameHandlerTest extends ApplicationTestCase<PocketPickupApplication>{
	/**Log tag label for logging**/
	public static final String LOG_TAG = "GameHandlerTest";
	
	/**Default starting location**/
	private LatLng SAMPLE_LOCATION = new LatLng(0, 0);
	/**Default starting day**/
	private long SAMPLE_START_DATE = 0L;
	/**Default ending day**/
	private long SAMPLE_END_DATE = 0L;
	/**Default starting time**/
	private long SAMPLE_START_TIME = 0L;
	/**Default ending time**/
	private long SAMPLE_END_TIME = 0L;
	/**Default ideal size**/
	private int SAMPLE_IDEAL_SIZE = 2;
	/**Default description. A random number unique to the test run is appended**/
	private String SAMPLE_DESCRIPTION = "GameHandlerTest ";
	/**Default user. Must be set in the constructor as the current user after Parse intitialization**/
	private String SAMPLE_USER = null;
	/**Default sport**/
	private String SAMPLE_SPORT = "Basketball";
	
	/**
	 * Default constructor.
	 * 
	 * Appends a random number to the description. This allows games created
	 * with this description to be uniquely identified. This allows for easily identifying test
	 * games in the game database.
	 */
	public GameHandlerTest() {
		super(PocketPickupApplication.class);
		Random r = new Random();
		this.SAMPLE_DESCRIPTION += r.nextLong();
	}
	
	/**
	 * Creates a game with default parameters
	 * @return Game with default parameters
	 */
	private Game getSampleGame() {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	/**
	 * Creates a game with a provided location
	 * @param loc - game location
	 * @return game with default parameters except with provided location.
	 */
	private Game getGameWithLocation(LatLng loc) {
		return new Game(SAMPLE_USER, loc, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	@SuppressWarnings("unused")
	/**
	 * Creates a game with default parameters except with a provided sport
	 * @param sport - game type
	 * @return game with default parameters except with a provided location
	 */
	private Game getGameWithSport(String sport) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, sport, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	@SuppressWarnings("unused")
	/**
	 * Creates a game with default parameters except with a provided ideal size
	 * @param n - game ideal size
	 * @return game with default parameters except with a provided ideal size
	 */
	private Game getGameWithIdealSize(int n) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, n, SAMPLE_DESCRIPTION);
	}
	
	/**
	 * Creates a game with default parameters except with a provided description
	 * @param s - game description
	 * @return game with default parameters except with a provided 
	 */
	private Game getGameWithDescription(String s) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, s);
	}
	
	/**
	 * Creates a game with default parameters except for a new random number in place of the description.
	 * @return game with default parameters except with a new, random description
	 */
	private Game getRandomGame() {
		Random r = new Random();
		return getGameWithDescription(Long.toString(r.nextLong()));
	}
	
	/**
	 * Asserts that the fields of each game are equal
	 * @param g1 - game that must have all fields equal to g2
	 * @param g2 - game that must have all fields equal to g1
	 */
	private void assertGamesAreEqual(Game g1, Game g2) {
		assertTrue(g2.mCreator.equals(g2.mCreator));
		assertTrue(g2.mDetails + " == " + g2.mDetails, g1.mDetails.equals(g2.mDetails));
		assertTrue(g1.mGameType.equals(g2.mGameType));
		assertTrue(g1.mGameLocation.equals(g2.mGameLocation));
		assertTrue(g1.mGameEndDate.equals(g2.mGameEndDate));
		assertTrue(g1.mGameStartDate.equals(g2.mGameStartDate));
		assertTrue(g1.mIdealGameSize == g2.mIdealGameSize);
	}
	
	@Override
	/**
	 * {@inherit-doc}
	 */
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		assertTrue(isNetworkConnected());
		
		//Force sports to load so that the sports bimap guaranteed to be initialized
		getApplication().forceSportsLoading();
		Log.v(LOG_TAG, PocketPickupApplication.sportsAndObjs.keySet().toString());
		
		SAMPLE_USER = ParseUser.getCurrentUser().getObjectId();
	}
	
	/**
	 * Checks to see if there is a network connection 
	 * @return true if there is wifi connection, false otherwise
	 */
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Checks that a game with the same description is contained in a list of Games
	 * @param list - list of games
	 * @param g - Game to check for being contained in list
	 * @return true if a game with g's description exists in list, else false
	 */
	private boolean contains(List<Game> list, Game g) {
		for(Game l : list) {
			if(l.mDetails.equals(g.mDetails)) return true;
		}
		return false;
	}
	
	/**
	 * Creates a PocketPickup.Game object then saves it to Parse as a ParseObject
	 * of type Game then queries the Parse database to see if the game was stored 
	 */
	public void testCreateGame() {
		Game gameToCreate = getSampleGame();
		GameHandler.createGame(gameToCreate, null);
		ParseQuery<ParseObject> q = ParseQuery.getQuery(DbColumns.GAME); 
		q.include(DbColumns.GAME_CREATOR);
		q.whereEqualTo(DbColumns.GAME_IDEAL_SIZE, SAMPLE_IDEAL_SIZE);
		q.whereEqualTo(DbColumns.GAME_DETAILS, SAMPLE_DESCRIPTION);
		q.whereEqualTo(DbColumns.GAME_START_DATE, SAMPLE_START_DATE);
		q.whereEqualTo(DbColumns.GAME_END_DATE, SAMPLE_END_DATE);
		List<ParseObject> results = null;
		try {
			results = q.find();
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
			fail("Query failed in testCreateGame");
		}
		int numResults = (results == null) ? 0 : results.size();
		assertEquals(numResults, 1);
		Game gameFound = Translator.parseGameToAppGame(results.get(0));
		this.assertGamesAreEqual(gameToCreate, gameFound);
		
		// now delete the object
		for (int i = 0; i < numResults; i++) {
			try {
				results.get(i).delete();
			} catch (ParseException e) {
				fail("Deletion failed in testCreateGame");
				Log.e(LOG_TAG, e.getMessage());
			}
		}
	}
	
	/**
	 * This test case simulates a user looking for games within 5 miles of their current
	 * location. Two games are created, one at the same location as the current user 
	 * and one that is more than 5 miles away from the current user.
	 * The query should return only the game that is right at the current user's location
	 */
	public void testFindGameLocation() {
		// Ensure that the sports bimap is available
		Log.d(LOG_TAG, "sportsAndObjs is null: " + (PocketPickupApplication.sportsAndObjs == null));
		
		//ParseGeoPoint currentLocation = ParseGeoPoint. ask serge
		Game closeGame = getSampleGame();
		Game farGame =  this.getGameWithLocation(new LatLng(0.001, 0.001));
		GameHandler.createGame(closeGame, null);
		GameHandler.createGame(farGame, null);
		
		ArrayList<String> gameTypes = new ArrayList<String>();
		gameTypes.add(SAMPLE_SPORT);
		FindGameCriteria criteria = new FindGameCriteria(50000L, SAMPLE_LOCATION, 0L, Long.MAX_VALUE, 0L, Long.MAX_VALUE, gameTypes);
		List<Game> results = GameHandler.findGame(criteria);
		assertTrue(results.size() > 0);
		assertEquals(closeGame.mGameLocation, results.get(0).mGameLocation);

		
		GameHandler.removeGame(farGame);
		GameHandler.removeGame(closeGame);
		
	}	
	
	/**
	 * Tests that a created game is added to the list of games attended by a user
	 */
	public void testGetGamesAttending() {
		Game toCreate = getRandomGame();
		GameHandler.createGame(toCreate, null);
		ArrayList<Game> myGames = GameHandler.getGamesCurrentUserIsAttending();
		assertTrue(myGames.size() > 0);
		assertTrue(contains(myGames, toCreate));
	}
	
	/**
	 * Tests that a created game is added to the list of games created by a user
	 */
	public void testGamesCreated() {
		Game toCreate = getRandomGame();
		GameHandler.createGame(toCreate, null);
		ArrayList<Game> myGames = GameHandler.getGamesCreatedByCurrentUser();
		assertTrue(myGames.size() > 0);
		assertTrue(contains(myGames, toCreate));
	}
	
	/**
	 * Tests that a created game is present in both the lists of games created and attending
	 */
	public void testGamesCreatedAreAlsoAttended() {
		Game toCreate = getRandomGame();
		GameHandler.createGame(toCreate, null);
		ArrayList<Game> myGames = GameHandler.getGamesCreatedByCurrentUser();
		ArrayList<Game> attendingGames = GameHandler.getGamesCurrentUserIsAttending();
		boolean foundInGamesCreated = false;
		boolean foundInGamesAttending = false;
		for(Game g : myGames) {
			if(g.mDetails.equals(toCreate.mDetails)) foundInGamesCreated = true;
		}
		for(Game g : attendingGames) {
			if(g.mDetails.equals(toCreate.mDetails)) foundInGamesAttending = true;
		}
		GameHandler.removeGame(toCreate);

		assertTrue(foundInGamesCreated);
		assertTrue(foundInGamesAttending);
	}
	
	@Override
	/**
	 * {@inherit-doc}
	 */
	protected void tearDown() {
		terminateApplication();
		try {
			super.tearDown();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed to tear down");
			e.printStackTrace();
		}
	}
}
