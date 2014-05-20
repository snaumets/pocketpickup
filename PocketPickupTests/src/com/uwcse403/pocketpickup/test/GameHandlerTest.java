package com.uwcse403.pocketpickup.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

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
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;

public class GameHandlerTest extends ApplicationTestCase<PocketPickupApplication>{
	public static final String LOG_TAG = "GameHandlerTest";
	
	private static Game game;
	private static int randomIdealSize;
	private static String SAMPLE_USER = "GUzx6W5p2b";
	private static String SAMPLE_SPORT = "Basketball";
	
	public GameHandlerTest() {
		super(PocketPickupApplication.class);
		Random r = new Random();
		randomIdealSize =  r.nextInt();
		game = new Game(SAMPLE_USER, new LatLng(0, 0), 0L, 1L, SAMPLE_SPORT, randomIdealSize, "");
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		assertTrue(isNetworkConnected());
		if (!isNetworkConnected()) {
			fail();
		}
		//Force sports to load so that the sports bimap guaranteed to be initialized
		getApplication().forceSportsLoading();
		Log.v(LOG_TAG, PocketPickupApplication.sportsAndObjs.keySet().toString());
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
		GameHandler.createGame(game, null);
		ParseQuery<ParseObject> q = ParseQuery.getQuery("Game"); 
		q.whereEqualTo("idealGameSize", randomIdealSize);
		List<ParseObject> results = null;
		try {
			results = q.find();
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		int numResults = (results == null) ? 0 : results.size();
		// now delete the object
		for (int i = 0; i < numResults; i++) {
			try {
				results.get(i).delete();
			} catch (ParseException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
		}
		assertEquals(1, numResults);
	}
	
	/**
	 * This test case simulates a user looking for 
	 */
	
	/**
	 * This test case simulates a user looking for games within 5 miles of their current
	 * location. Two games are created, one at the same location as the current user 
	 * and one that is more than 5 miles away from the current user.
	 * The query should return only the game that is right at the current user's location
	 */
	public void testFindGameLocation() {
		Log.d(LOG_TAG, "sportsAndObjs is null: " + (PocketPickupApplication.sportsAndObjs == null));
		//ParseGeoPoint currentLocation = ParseGeoPoint. ask serge
		Game closeGame = new Game(ParseUser.getCurrentUser().getObjectId(), new LatLng(1,1), 
				1L, 2L, "Basketball", 2, "shirts vs skins");
		Game farGame =  new Game(ParseUser.getCurrentUser().getObjectId(), new LatLng(10,10), 
				1L, 2L, "Basketball", 2, "thugs shooting hoops");
		GameHandler.createGame(closeGame, null);
		GameHandler.createGame(farGame, null);
		ArrayList<String> gameTypes = new ArrayList<String>();
		gameTypes.add("Basketball");
		FindGameCriteria criteria = new FindGameCriteria(5L, new LatLng(1,1), 0L, Long.MAX_VALUE, 0L, 0L, gameTypes);
		List<Game> results = GameHandler.findGame(criteria);
		GameHandler.removeGame(farGame);
		GameHandler.removeGame(closeGame);
		//assertTrue(results.size() == 1);
		assertEquals(closeGame.mGameLocation, results.get(0).mGameLocation);
	}
	/**
	 * Adds a user to a game and tests to see if the user was indeed added to the Game object
	 * stored in the database.
	 */
	public void testJoinGame() {
		// create a game
		Random r = new Random();
		long l = r.nextLong();
		// create a random description string to act as a lookup key for retrieving the object
		// directly from Parse instead of going through the app layer
		String randomDescription = Long.toString(l);
		Game game = new Game(ParseUser.getCurrentUser().getObjectId(), new LatLng(1,1), 
				1L, 2L, "Basketball", 2, randomDescription);
		GameHandler.createGame(game, null);
		// now add the current user to the game.
		GameHandler.joinGame(game);
		// now search for the game in the database and see if the current user's Parse objectId
		// is in the array in the 'players' column
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Game");
		query.whereEqualTo(DbColumns.GAME_DETAILS, randomDescription);
		List<ParseObject> result = null;
		try {
			result = query.find();
		} catch (ParseException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		// cleanup, delete the game from the database now that we have retrieved it
		//GameHandler.removeGame(game);
		ParseObject justCreatedGame = result.get(1);
		ArrayList<String> players = (ArrayList<String>) justCreatedGame.get(DbColumns.GAME_PLAYERS);
		// fail if the players object is null, i.e., nothing to do with adding players to games
		// has been implemented
		assertTrue(players != null);
		
		// fail if no players were added to the game. 
		
		assertTrue(players.size() > 0);
		String id = null;
		id = players.get(0);
		
		
		//Not implemented
		assertEquals(ParseUser.getCurrentUser().getObjectId(), id);
	}
	
	/**
	 * Uploads a game
	 */
	public void testDate() {
		ParseObject user = GameHandler.getAUser();
		ParseObject sport = GameHandler.getASport();
		if(user == null || sport == null) {
			Log.e("GameHandlerTest", "Failed to get a sample user or sport");
			fail();
		}
		Game gameDate = new Game(ParseUser.getCurrentUser().getObjectId(), new LatLng(0, 0), 0L, 1L, SAMPLE_SPORT, randomIdealSize, "");
		GameHandler.createGame(gameDate, null);
		List<ParseObject> uploaded = GameHandler.getGame(gameDate);
		assertTrue(uploaded.size() != 0);
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}
}
