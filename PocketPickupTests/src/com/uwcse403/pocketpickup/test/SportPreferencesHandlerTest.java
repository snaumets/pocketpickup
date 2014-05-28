package com.uwcse403.pocketpickup.test;

import java.util.List;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.ParseInteraction.SportPreferencesHandler;

public class SportPreferencesHandlerTest extends ApplicationTestCase<PocketPickupApplication> {
	public static final String LOG_TAG = "SportPreferencesHandlerTest";
	
	public SportPreferencesHandlerTest() {
		super(PocketPickupApplication.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		
		//Force sports to load so that the sports bimap guaranteed to be initialized
		getApplication().forceSportsLoading();
		Log.v(LOG_TAG, PocketPickupApplication.sportsAndObjs.keySet().toString());
	}
	
	@Override
	protected void tearDown() {
		terminateApplication();
	}
	
	public void testSetSportPreferences() {
		SportPreferencesHandler.setSportPreferences("Soccer");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> sportPrefs = SportPreferencesHandler.getSportPreferences();
		for (int i = 0; i < sportPrefs.size(); i++) {
			Log.v(LOG_TAG, sportPrefs.get(i));
		}
	}
	
	
}
