package com.uwcse403.pocketpickup.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
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
	public static final String LOG_TAG = "GameHandlerTest";
	
	private static LatLng SAMPLE_LOCATION = new LatLng(0, 0);
	private static long SAMPLE_START_DATE = 0L;
	private static long SAMPLE_END_DATE = 0L;
	private static long SAMPLE_START_TIME = 0L;
	private static long SAMPLE_END_TIME = 0L;
	private static int SAMPLE_IDEAL_SIZE = 2;
	private static String SAMPLE_DESCRIPTION = "GameHandlerTest";
	private static String SAMPLE_USER = null;
	private static String SAMPLE_SPORT = "Basketball";
	private static Game SAMPLE_GAME = null;
	
	public GameHandlerTest() {
		super(PocketPickupApplication.class);
	}
	
	private Game getSampleGame() {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	private Game getGameWithLocation(LatLng loc) {
		return new Game(SAMPLE_USER, loc, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	@SuppressWarnings("unused")
	private Game getGameWithStartEnd(long start, long end) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	@SuppressWarnings("unused")
	private Game getGameWithSport(String sport) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, sport, SAMPLE_IDEAL_SIZE, SAMPLE_DESCRIPTION);
	}
	
	@SuppressWarnings("unused")
	private Game getGameWithIdealSize(int n) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, n, SAMPLE_DESCRIPTION);
	}
	
	private Game getGameWithDescription(String s) {
		return new Game(SAMPLE_USER, SAMPLE_LOCATION, SAMPLE_START_DATE, SAMPLE_END_DATE,
				null, null, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, s);
	}
	
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
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		assertTrue(isNetworkConnected());
		
		//Force sports to load so that the sports bimap guaranteed to be initialized
		getApplication().forceSportsLoading();
		Log.v(LOG_TAG, PocketPickupApplication.sportsAndObjs.keySet().toString());
		
		SAMPLE_GAME = getSampleGame();
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
	 * Adds a user to a game and tests to see if the user was indeed added to the Game object
	 * stored in the database.
	 */
	@SuppressWarnings("unchecked")
	public void testJoinAndLeaveGame() {
		// create a game description
		Random r = new Random(0);
		Long l = r.nextLong();
		// create a random description string to act as a lookup key for retrieving the object
		// directly from Parse instead of going through the app layer
		String randomDescription = Long.toString(l);
		Game game = getGameWithDescription(randomDescription);	
		GameHandler.createGame(game, null);
		// now add the current user to the game.
		GameHandler.joinGame(game, true);
		
		// now search for the game in the database and see if the current user's Parse objectId
		// is in the array in the 'players' column
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(DbColumns.GAME);
		query.whereEqualTo(DbColumns.GAME_DETAILS, randomDescription);
		List<ParseObject> result = null;
		try {
			result = query.find();
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
			fail("Failed to get query results in testJoinAndLeaveGame");
		}
		ParseObject justCreatedGame = result.get(0);
		ArrayList<String> players = (ArrayList<String>) justCreatedGame.get(DbColumns.GAME_PLAYERS);
		// fail if the players object is null, i.e., nothing to do with adding players to games
		// has been implemented
		assertTrue(players != null);
		// fail if no players were added to the game. 
		assertTrue(players.size() > 0);
		String id = null;
		id = players.get(0);
		// make sure the only member of the game is the current member
		assertEquals(SAMPLE_USER, id);
		// now leave the game
		GameHandler.leaveGame(game);
		// retrieve the game now that the user has left it 
		query = new ParseQuery<ParseObject>("Game");
		query.whereEqualTo(DbColumns.GAME_DETAILS, randomDescription);
		result = null;
		try {
			result = query.find();
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		justCreatedGame = result.get(0);
		players = (ArrayList<String>) justCreatedGame.get(DbColumns.GAME_PLAYERS);
		// Verify that the current user left the game
		assertEquals(0, players.size());
		// cleanup, delete the game from the database now that we are done testing
		GameHandler.removeGame(game);
	}
	
	public void testGetGamesAttending() {
		ArrayList<Game> myGames = GameHandler.getGamesCurrentUserIsAttending();
		assertTrue(myGames.size() > 0);
		assertTrue(myGames.contains(SAMPLE_GAME));
	}
	
	public void testGamesCreated() {
		ArrayList<Game> myGames = GameHandler.getGamesCreatedByCurrentUser();
		assertTrue(myGames.size() > 0);
		assertTrue(myGames.contains(SAMPLE_GAME));
	}
	
	public void testGamesCreatedAreAlsoAttended() {
		ArrayList<Game> myGames = GameHandler.getGamesCreatedByCurrentUser();
		ArrayList<Game> attendingGames = GameHandler.getGamesCurrentUserIsAttending();
		for(Game g : myGames) {
			assertTrue(attendingGames.contains(g));
		}
		assertTrue(myGames.contains(SAMPLE_GAME));
	}
	
	public void testGameCreatedInUserTable() {
		TestCase.assertTrue(true);
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}
}
