package com.uwcse403.pocketpickup.test;

import java.util.Date;

import org.junit.Test;

import android.test.ApplicationTestCase;

import com.google.android.gms.maps.model.LatLng;
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
	
	
	protected void doTests() {
		//alwaysPass();
		//alwaysFail();
		Game g = new Game("isaiah", new LatLng(0,0), new Date(), "bball");
		GameHandler.createGame(g);
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}

}
