package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.parse.ParseUser;
import com.uwcse403.pocketpickup.PocketPickupApplication;

public class SportPreferencesHandler {
	public static String LOG_TAG = "SportPreferencesHandler";
	/**
	 * Replaces the String list of sport preferences with the Strings provided as arguments 
	 * @param varargs of the String names of the sport preferences
	 */
	public static void setSportPreferences(String... strings) {
		// first make sure these are valid sports by comparing them agains the BiMap
		Set<String> sportsNames = PocketPickupApplication.sportsAndObjs.keySet();
		List<String> newSportPreferences = new ArrayList<String>();
		for (int i = 0; i < strings.length; i++) {
			if (!sportsNames.contains(strings[i])) {
				Log.e(LOG_TAG, "not a recognized sport name: " + strings[i]);
				throw new IllegalArgumentException("not a recognized sport name: " + strings[i]);
			} else {
				newSportPreferences.add(strings[i]);
			}
		}
		// now update the sport preferences
		ParseUser currentUser = ParseUser.getCurrentUser();
		currentUser.put(DbColumns.USER_SPORT_PREFERENCES, newSportPreferences);
		currentUser.saveInBackground();
	}
	
	public static List<String> getSportPreferences() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		List<String> sports =currentUser.getList(DbColumns.USER_SPORT_PREFERENCES);
		return sports;
	}

}
