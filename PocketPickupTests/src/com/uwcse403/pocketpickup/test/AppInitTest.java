package com.uwcse403.pocketpickup.test;

import java.util.Date;

import org.junit.Test;

import android.test.ApplicationTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.Game;

public class AppInitTest extends ApplicationTestCase {

	public AppInitTest() {
		super(PocketPickupApplication.class);
	}

	@Test
	public void alwaysPass() {
		assertTrue(true);
	}
	
	@Test void alwaysFail() {
		assertTrue(false);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		doTests();
	}
	
	
	protected void doTests() throws ParseException {
		//alwaysPass();
		//alwaysFail();
		
		Game g = new Game(new ParseObject("isaiah"), new LatLng(0,0), (long) 0, (long) 0, new ParseObject("bball"), 9);
		GameHandler.createGame(g);
		ParseObject p = new ParseObject("test");
		p.save();
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}

}
