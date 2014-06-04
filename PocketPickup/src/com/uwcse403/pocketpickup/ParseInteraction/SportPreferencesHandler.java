package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.PocketPickupApplication;

public class SportPreferencesHandler {
	public static String LOG_TAG = "SportPreferencesHandler";
	private static final SaveCallback DEFAULT_SAVE_CALLBACK = new SaveCallback() {
		public void done(ParseException e) {
			if (e == null) {
				// successfully created game
				Log.v(LOG_TAG, "Successfully saved game");
			} else {
				// unable to create the game, alert user
				Log.e(LOG_TAG, "error saving game: " + e.getCode() + ": " + e.getMessage());
			}
		}
	};
	
	/**
	 * Replaces the String list of sport preferences with the Strings provided as arguments 
	 * @param varargs of the String names of the sport preferences
	 */
	public static void setSportPreferences(String... strings) {
		setSportPreferences(DEFAULT_SAVE_CALLBACK, strings);
	}
	
	/**
	 * Performs the same action as setSportPreferences with a provided callback
	 * @param cb - Callback to perform after save completion
	 * @param strings - varargs of the String names of the sport preferences
	 */
	public static void setSportPreferences(SaveCallback cb, String... strings) {
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
		if(cb != null) {
			currentUser.saveInBackground(cb);
		} else {
			try {
				currentUser.save();
			} catch (ParseException e) {
				Log.e(LOG_TAG, "Failed to save sport preferences");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns a list of the current user's sport preferences
	 * @return List of Strings representing preferred sports
	 */
	public static List<String> getSportPreferences() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		List<String> sports =currentUser.getList(DbColumns.USER_SPORT_PREFERENCES);
		return sports;
	}

}
