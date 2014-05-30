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
	
	private LatLng SAMPLE_LOCATION = new LatLng(0, 0);
	private long SAMPLE_START_DATE = 0L;
	private long SAMPLE_END_DATE = 0L;
	private long SAMPLE_START_TIME = 0L;
	private long SAMPLE_END_TIME = 0L;
	private int SAMPLE_IDEAL_SIZE = 2;
	private String SAMPLE_DESCRIPTION = "GameHandlerTest ";
	private String SAMPLE_USER = null;
	private String SAMPLE_SPORT = "Basketball";
	
	public GameHandlerTest() {
		super(PocketPickupApplication.class);
		Random r = new Random();
		this.SAMPLE_DESCRIPTION += r.nextLong();
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
				SAMPLE_START_TIME, SAMPLE_END_TIME, SAMPLE_SPORT, SAMPLE_IDEAL_SIZE, s);
	}
	
	private Game getRandomGame() {
		Random r = new Random();
		return getGameWithDescription(Long.toString(r.nextLong()));
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
	
	public void testGetGamesAttending() {
		Game toCreate = getSampleGame();
		GameHandler.createGame(toCreate, null);
		ArrayList<Game> myGames = GameHandler.getGamesCurrentUserIsAttending();
		assertTrue(myGames.size() > 0);
		assertTrue(contains(myGames, toCreate));
	}
	private boolean contains(List<Game> list, Game g) {
		for(Game l : list) {
			if(l.mDetails.equals(g.mDetails)) return true;
		}
		return false;
	}
	
	public void testGamesCreated() {
		Game toCreate = getRandomGame();
		GameHandler.createGame(toCreate, null);
		ArrayList<Game> myGames = GameHandler.getGamesCreatedByCurrentUser();
		assertTrue(myGames.size() > 0);
		assertTrue(contains(myGames, toCreate));
	}
	
	public void testGamesCreatedAreAlsoAttended() {
		Random r = new Random();
		Game toCreate = getGameWithDescription(Long.toString(r.nextLong()));
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
	
	public void testGameCreatedInUserTable() {
		TestCase.assertTrue(true);
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}
}
