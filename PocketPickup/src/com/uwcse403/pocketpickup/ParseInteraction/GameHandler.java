package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;
/**
 * Interfaces between local application storage and cloud storage.
 * 
 * This implementation is dependent on the Parse library
 */
public class GameHandler {
	/**Label for debugging tag label**/
	public static final String LOG_TAG = "GameHandler";
	/**Default callback for saving in background**/
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
	 * Adds a game to the database of available games
	 * 
	 * @param g - contains user settings for the game
	 */
	public static void createGame(Game g) {
		createGame(g, DEFAULT_SAVE_CALLBACK);
	} 
	
	public static void createGame(Game g, SaveCallback cb) {
		Log.v(LOG_TAG, "entering CreateGame(Game, SaveCallback)");
		ParseObject game = Translator.appGameToParseGame(g);
		if(cb != null) {
			game.saveInBackground(cb);	
		}
		else {
			try {
				game.save();
			} catch (ParseException e) {
				// Failed to save game with waiting
				Log.e(LOG_TAG, "error saving game with waiting: " + e.getCode() + ": " + e.getMessage());
			}
		}
	}
	
	/**
	 * Adds a game to the database of available games
	 * Unlike {@link createGame}, this creates a game with default preferences, except
	 * for the ideal game size provided by the {@link Game} argument.
	 * 
	 * Used for debugging purposes
	 * 
	 * @param g - supplies the ideal game size
	 */
	public static void createDummyGame(Game g) {
		Log.v(LOG_TAG, "entering createDummyGame()");
		ParseObject game = new ParseObject("Game");
		// fill in all the setter	
		game.put(DbColumns.GAME_IDEAL_SIZE, g.mIdealGameSize);
		
		game.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					// successfully created game
					Log.v(LOG_TAG, "Successfully saved game");
				} else {
					// unable to create the game, alert user
					Log.e(LOG_TAG, "error saving game: " + e.getCode() + ": " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Adds a game to the database of available games
	 * Unlike {@link createGame}, this creates a game with default preferences, except
	 * for the ideal game size provided by the {@link Game} argument and the creator
	 * and gameType provided by the ParseObject arguments.
	 * 
	 * Used for debugging purposes
	 * 
	 * @param g - supplies the ideal game size
	 * @param user - specifies the creator
	 * @param sport - specifies the gameType
	 */
	public static void createDummyGameWithPointers(Game g, ParseObject user, ParseObject sport) {
		Log.v(LOG_TAG, "entering createDummyGame()");
		ParseObject game = new ParseObject("Game");
		// fill in all the setter	
		game.put(DbColumns.GAME_IDEAL_SIZE, g.mIdealGameSize);
		game.put(DbColumns.GAME_CREATOR, user);
		game.put(DbColumns.GAME_SPORT, sport);
		
		game.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					// successfully created game
					Log.v(LOG_TAG, "Successfully saved game");
				} else {
					// unable to create the game, alert user
					Log.e(LOG_TAG, "error saving game: " + e.getCode() + ": " + e.getMessage());
				}
			}
		});
	}

	/**
	 * Gets a game that meets the provided criteria. Only works for games the user has created.
	 * 
	 * @param g - target game to get
	 * @return
	 */
	public static List<ParseObject> getGame(Game g) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
		query.whereEqualTo(DbColumns.GAME_CREATOR, ParseUser.getCurrentUser());
		query.whereEqualTo(DbColumns.GAME_IDEAL_SIZE, g.mIdealGameSize);
		query.whereEqualTo(DbColumns.GAME_START_DATE, g.mGameStartDate);
		query.whereEqualTo(DbColumns.GAME_END_DATE, g.mGameEndDate);
		ParseGeoPoint location = new ParseGeoPoint(g.mGameLocation.latitude, g.mGameLocation.longitude);
		// it seems as though ParseGeoPoints need to be compared like doubles, as in
		// two ParseGeoPoints are equal if they are within a very small distance from each other
		query.whereWithinMiles(DbColumns.GAME_LOCATION, location, .0001);
		query.whereEqualTo(DbColumns.GAME_DETAILS, g.mDetails);
		List<ParseObject> objects = null;
		try {
			objects = query.find();
		} catch (ParseException e) {
			//Failed to see results of the query
			Log.e(LOG_TAG, "failed to collect query results in removeGame()");
		}
		if (objects == null) {
			//Failed to find the game to delete
			//This is an error because the user should only be able to delete
			//games that exist
			Log.e(LOG_TAG, "no objects found to delete");
		}
		return objects;
	}
	
	/** 
	 * Removes the App game object g from the Parse database.
	 * 
	 * ONLY WORKS IF THE CURRENT USER CREATED THE GAME BEING DELETED
	 * @param g - target game to be deleted
	 */
	public static void removeGame(Game g) {
		List<ParseObject> objects = getGame(g);
		for (int i = 0; i < objects.size(); i++) {
			try {
				objects.get(i).delete();
				Log.v(LOG_TAG, "deleting object: " + i);
			} catch (ParseException e) {
				//Failed to delete a game
				Log.e(LOG_TAG, "failed to delete a target game in removeGame()");
			}
		}
	}

	/**
	 * Finds a list of games based on user criteria.
	 * 
	 * The return type has to be an ArrayList because of how parcelling works.
	 * 
	 * @param criteria: FindGameCriteria object containing search criteria
	 */
	public static ArrayList<Game> findGame(FindGameCriteria criteria) {
		ArrayList<String> sports = criteria.mGameTypes;
		if (sports.size() == 0) {
			throw new IllegalArgumentException("must include a non-empty list of sport types to findGame()");
		}
		ArrayList<ParseQuery<ParseObject>> sportTypes = new ArrayList<ParseQuery<ParseObject>>();
		for (int i = 0; i < sports.size(); i++) {
			ParseObject sport = PocketPickupApplication.sportsAndObjs.get(sports.get(i));
			ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Game");
			q.whereEqualTo(DbColumns.GAME_SPORT, sport);
			sportTypes.add(q);
		}
		// first create a compound query that returns games of the desired sport
		ParseQuery<ParseObject> query = ParseQuery.or(sportTypes);
		query.include(DbColumns.GAME_SPORT);
		// get the location of the query, i.e., the center of the circle
		LatLng loc = criteria.mSearchLocation;
		ParseGeoPoint center = new ParseGeoPoint(loc.latitude, loc.longitude);

		// limit to games within the specified radius
		query.whereWithinMiles(DbColumns.GAME_LOCATION, center, criteria.mRadius);
		// limit games to those that fall within the times entered
		// if the user did not specify any date/time constraints, show all games
		// that will be happening in the future
		if (criteria.mEndDate == 0 && criteria.mEndTime == 0) {
			/* TODO uncomment this once criteria validation is implemented to enforce
			 * start date/time to be initialized to the current time
			if (criteria.mStartDate == 0 || criteria.mStartTime == 0) {
				throw new IllegalStateException("search criteria start date or start time" +
							" cannot be zero");
			}
			*/
			query.whereGreaterThan(DbColumns.GAME_START_DATE, criteria.mStartDate + criteria.mStartTime);
		} else {
			query.whereGreaterThan(DbColumns.GAME_START_DATE, criteria.mStartDate + criteria.mStartTime);
			query.whereLessThan(DbColumns.GAME_END_DATE, criteria.mEndDate + criteria.mEndTime);
		}

		List<ParseObject> results = null;
		try {
			results = query.find();
		} catch (ParseException e) {
			// Failed to get search results
			Log.e(LOG_TAG, "Failed to get search results in findGame()");
		}
		Log.v(LOG_TAG, "number of Games that match query: " + (results != null ? results.size() : 0));
		ArrayList<Game> matchingGames = new ArrayList<Game>();
		for (int i = 0; i < results.size(); i++) {
			ParseObject result = results.get(i);
			Log.v(LOG_TAG, "objectId of match: " + result.getObjectId());
			matchingGames.add(Translator.parseGameToAppGame(result));
		}
		return matchingGames;
	}
	

	/**
	 * Gets the first User in the User table. For debugging purposes
	 * @return ParseObject representing the first user in the User table
	 */
	public static ParseObject getAUser() {
		ParseQuery<ParseObject> uQuery = new ParseQuery<ParseObject>("_User");
		try {
			List<ParseObject> users = uQuery.find();
			return users.get(0);
		} catch (ParseException e) {
			// Failed to get any entry from the _User table
			Log.e(LOG_TAG, "Failed to get any row from the _User table in getAUser()");
		}
		return null;
	}
	
	/**
	 * Gets the first sport in the Sort table. For debugging purposes.
	 * @return a PArseObject representing the first Sport in the Sport table
	 */
	public static ParseObject getASport() {
		ParseQuery<ParseObject> uQuery = new ParseQuery<ParseObject>("Sport");
		try {
			List<ParseObject> sports = uQuery.find();
			return sports.get(0);
		} catch (ParseException e) {
			// Failed to get any entry in the Sport table
			Log.e(LOG_TAG, "Failed to get any row from the Sport table in getASport()");
		}
		return null;
	}
	
	/**
	 * Gets the Game ParseObject with the specified id. Returns null if no game matches the id
	 * @param id - id of desired game
	 * @return ParseObject representing game with id or null
	 */
	public static ParseObject getGameById(String id) {
		ParseQuery<ParseObject> idQuery = new ParseQuery<ParseObject>("Game");
		try {
			return idQuery.get(id);
		} catch (ParseException e) {
			Log.e(LOG_TAG, "Failed to get game by ID: " + id);
		}
		return null;
	}
	/**
	 * Adds the current user to a game 
	 * @param g the Game to join
	 * @return true if the user was added to the game, false otherwise
	 */
	public static boolean joinGame(Game g) {
		// TODO: not implemented in Beta release
		ParseObject game = Translator.appGameToParseGame(g);
		ArrayList<String> players = (ArrayList<String>) game.get(DbColumns.GAME_PLAYERS);
		if (players == null) {
			//JSONArray newPlayersArray = new JSONArray();
			//newPlayersArray.put(ParseUser.getCurrentUser().getObjectId());
			//game.add(DbColumns.GAME_PLAYERS, newPlayersArray);
			game.add(DbColumns.GAME_PLAYERS, Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
		} else {
			players.add(ParseUser.getCurrentUser().getObjectId());
		}
		try {
			game.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}



