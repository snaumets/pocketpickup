package com.uwcse403.pocketpickup.ParseInteraction;

/**
 * Stores the column names used in Parse as constants because using
 * Parse.put("key", "value") will create a new column with name "key"
 * if one does not already exist. This means that if you call the put()
 * method on a Parse object but misspell the key, Parse will create a new
 * column corresponding to that misspelled key. Thus, column names should
 * ALWAYS be referred to using the constants declared here.
 * 
 * @author imathieu
 *
 */
public class DatabaseColumnNames {

	// column names of the Game class
	public static final String GAME_CREATOR = "creator";
	public static final String GAME_SPORT = "sport";
	public static final String GAME_LOCATION = "location";
	public static final String GAME_PLAYERS = "player";
	public static final String GAME_IDEAL_SIZE = "idealGameSize";
	
	// column names of the User class
	public static final String USER_GENDER = "gender";
	public static final String USER_BIRTHDAY = "birthday";
	public static final String USER_SPORT_PREFERENCES = "sportPreferences";
	
	// column names of the Sport class
	public static final String SPORT_NAME = "name";
	

}
