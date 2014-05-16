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

public class AppInitTest extends ApplicationTestCase<PocketPickupApplication> {

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
		
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}

}
