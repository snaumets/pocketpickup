package com.uwcse403.pocketpickup.test;

import java.util.List;
import java.util.Random;

import android.test.ApplicationTestCase;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.Game;

public class GameHandlerTest extends ApplicationTestCase{
	
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
		doTests();
	}
	
	protected void doTests() {
		createGame();
		
	}
	
	/**
	 * Creates a PocketPickup.Game object then saves it to Parse as a ParseObject
	 * of type Game then queries the Parse database to see if the game was stored 
	 */
	protected void createGame() {
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
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}
}
