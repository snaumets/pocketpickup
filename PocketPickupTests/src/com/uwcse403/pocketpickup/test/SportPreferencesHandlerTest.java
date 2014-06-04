package com.uwcse403.pocketpickup.test;

import java.util.List;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.ParseInteraction.SportPreferencesHandler;

/**
 * Test suite for SportPreferencesHandler
 */
public class SportPreferencesHandlerTest extends ApplicationTestCase<PocketPickupApplication> {
	/**Log tag label for logging**/
	public static final String LOG_TAG = "SportPreferencesHandlerTest";
	/**Null callback so that we call setSportPreferences with thread blocking.
	 * Necessary to avoid the syntactic ambiguity of a null argument next to varargs
	 */
	public static final SaveCallback NO_CALLBACK = null;
	/**Identifying name for Soccer**/
	public static final String SOCCER = "Soccer";
	/**Identifying name for Basketball**/
	public static final String BASKETBALL = "Basketball";
	
	/**
	 * Default constructor
	 */
	public SportPreferencesHandlerTest() {
		super(PocketPickupApplication.class);
	}
	
	@Override
	/**{@inherit-doc}**/
	protected void setUp() throws Exception {
		super.setUp();
		createApplication();
		
		//Force sports to load so that the sports bimap guaranteed to be initialized
		getApplication().forceSportsLoading();
		Log.v(LOG_TAG, PocketPickupApplication.sportsAndObjs.keySet().toString());
	}
	
	@Override
	/**{@inherit-doc}**/
	protected void tearDown() {
		terminateApplication();
	}
	
	/**
	 * Tests that getSportPreferences returns a non-null list of sportPrefs
	 */
	public void testGetSportPreferences() {
		List<String> sportPrefs = SportPreferencesHandler.getSportPreferences();
		for (int i = 0; i < sportPrefs.size(); i++) {
			Log.v(LOG_TAG, sportPrefs.get(i));
		}
	}
	
	/**
	 * Tests that setSportPreferences returns with no error
	 */
	public void testSetSportPreferences() {
		SportPreferencesHandler.setSportPreferences(NO_CALLBACK, "Soccer");
	}
	
	/**
	 * Tests that changes made by setSportPreferences are visible by getSportPreferences
	 */
	public void testChangeSportPreferences() {
		SportPreferencesHandler.setSportPreferences(NO_CALLBACK, SOCCER, BASKETBALL);
		List<String> sportPrefs = SportPreferencesHandler.getSportPreferences();
		assertTrue(sportPrefs.size() == 2);
		assertTrue(sportPrefs.contains(SOCCER));
		assertTrue(sportPrefs.contains(BASKETBALL));
		
		//Empty the sport preferences
		String[] noSports = new String[0];
		SportPreferencesHandler.setSportPreferences(NO_CALLBACK, noSports);
		List<String> emptyPrefs = SportPreferencesHandler.getSportPreferences();
		assertTrue(emptyPrefs.isEmpty());
	}

	
}
