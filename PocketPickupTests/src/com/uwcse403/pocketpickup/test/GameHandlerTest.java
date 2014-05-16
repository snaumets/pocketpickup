package com.uwcse403.pocketpickup.test;

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
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;

public class GameHandlerTest extends ApplicationTestCase<PocketPickupApplication>{
	public static final String LOG_TAG = "GameHandlerTest";
	
	private static Game game;
	private static int randomIdealSize;
	public GameHandlerTest() {
		super(PocketPickupApplication.class);
		Random r = new Random();
		randomIdealSize =  r.nextInt();
		game = new Game(randomIdealSize);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		assertTrue(isNetworkConnected());
		if (!isNetworkConnected()) {
			fail();
		}
		// for some reason we need to wait a bit for the variables in PocketPickupApplication
		// to become initialized otherwise some of the tests fail.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		GameHandler.createDummyGame(game);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ParseQuery<ParseObject> q = ParseQuery.getQuery("Game"); 
		q.whereEqualTo("idealGameSize", randomIdealSize);
		List<ParseObject> results = null;
		try {
			results = q.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numResults = (results == null) ? 0 : results.size();
		// now delete the object
		for (int i = 0; i < numResults; i++) {
			try {
				results.get(i).delete();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertEquals(1, numResults);
	}
	
	/**
	 * This test case simulates a user looking for games within 5 miles of their current
	 * location. Two games are created, one at the same location as the current user 
	 * and one that is more than 5 miles away from the current user.
	 * The query should return only the game that is right at the current user's location
	 */
	public void testFindGameLocation() {
		Log.d(LOG_TAG, "sportsAndObjs is null: " + (PocketPickupApplication.sportsAndObjs == null));
		//ParseGeoPoint currentLocation = ParseGeoPoint. ask serge
		Game closeGame = new Game(getApplication().userObjectId, new LatLng(1,1), 
				1L, 2L, "Basketball", 2);
		Game farGame =  new Game(getApplication().userObjectId, new LatLng(10,10), 
				1L, 2L, "Basketball", 2);
		GameHandler.createGame(closeGame);
		GameHandler.createGame(farGame);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FindGameCriteria criteria = new FindGameCriteria(5L, new LatLng(1,1), 0L, Long.MAX_VALUE, 0L, 0L, "Basketball");
		List<Game> results = GameHandler.findGame(criteria);
		Log.d(LOG_TAG, "number of results: " + results.size());
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
		//Game gameDate = new Game(user, new LatLng(0, 0), 0L, 0L, sport, 0);
		//GameHandler.createGame(gameDate);
		//GameHandler.createDummyGameWithPointers(gameDate, user, sport);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}
}
