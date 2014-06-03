package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.JoinGameResult;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;
/**
 * Interfaces between local application storage and cloud storage.
 * 
 * This implementation is dependent on the Parse library
 */
public final class GameHandler {
	/** Label for debugging tag label. **/
	public static final String LOG_TAG = "GameHandler";
	/** Default callback for saving in background. **/
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
	 * Adds a game to the database of available games.
	 * 
	 * @param g - contains user settings for the game
	 */
	public static void createGame(Game g) {
		createGame(g, DEFAULT_SAVE_CALLBACK);
	} 

	/**
	 * Creates a game 
	 * 
	 * A game is added to the database and User is registered as having created and joined the game. 
	 * @param g - game to create
	 * @param cb - function to complete after saving (null for thread blocking, non-blocking
	 * otherwise)
	 */
	public static void createGame(Game g, SaveCallback cb) {
		Log.v(LOG_TAG, "entering CreateGame(Game, SaveCallback)");
		ParseObject game = Translator.appGameToNewParseGame(g);
		game.put(DbColumns.GAME_IS_VALID, true);
		if (cb != null) {
			game.saveInBackground(cb);	
		} else {
			try {
				game.save();
				joinGameJustCreatedByCurrentUserWithCallback(g, null);
			} catch (ParseException e) {
				// Failed to save game with waiting
				Log.e(LOG_TAG, "error saving game with waiting: " + e.getCode() + ": " + e.getMessage());
				//Do not update User table if we failed to update the Games table
				return;
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
		
		game.saveInBackground(DEFAULT_SAVE_CALLBACK);
	}
	
	/**
	 * Gets a game that meets the provided criteria. Only works for games the user has created
	 * because the query uses the current ParseUser's objectId to find the corresponding game.
	 * 
	 * @param g - target game to get
	 * @return the Parse Game object corresponding to the app Game object passed as a 
	 * parameter
	 * @requires the game already exist in the database
	 * @throws IllegalStateException if the game does not already exist in the database
	 */
	public static ParseObject getGameCreatedByCurrentUser(Game g) {
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
		if (objects.size() != 1) {
			String errorString = "found " + objects.size()  + " games that met" 
					+ " the description of the input game but there should be exactly one";
			Log.e(LOG_TAG, errorString);
			throw new IllegalStateException(errorString);
		}
		return objects.get(0);
	}
	
	/**
	 * Queries the Parse databse using the objectId in the provided Game
	 * 
	 * @param app Game object 
	 * @return the Parse Game object that the app Game object passed as a parameter represents. 
	 */
	public static ParseObject getGameUsingId(Game g) {
		return getGameUsingId(g.id);
	}
	
	/**
	 * Queries the database for the game corresponding to the provided id
	 * 
	 * @param id - Game ObjectID
	 * @return Game ParseObject
	 */
	public static ParseObject getGameUsingId(String id) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(DbColumns.GAME);
		ParseObject game = null;
		if(id == null) {
			String error = "Tried to find game with game object"
					+ " that has a null id";
			Log.e(LOG_TAG, error);
			throw new IllegalArgumentException(error);
		}
		try {
			game = query.get(id);
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "error retreiving game with id " + id + ": " + e.getCode() + " : " + e.getMessage());
		}
		return game;
	}
	
	/** 
	 * Removes the App game object g from the Parse database. 
	 * Also deletes the associated Attends relations. 
	 * @param g - target game to be deleted
	 */
	public static void removeGame(Game g) {
		ParseObject game = null;
		if(g.id != null) game = getGameUsingId(g);
		else game = getGameCreatedByCurrentUser(g);
		// mark the game as invalid
		game.put(DbColumns.GAME_IS_VALID, false);
		Log.v(LOG_TAG, "marked game as invalid");
		game.saveInBackground();
		// set the attends relation to invalid. There should be just one
		ParseQuery<ParseObject> attendsQuery = ParseQuery.getQuery("Attends");
		attendsQuery.whereEqualTo(DbColumns.ATTENDS_GAME, game);
		attendsQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(objects != null) {
					int numAttendsRelations = objects.size();
					if (numAttendsRelations != 1) {
						Log.w(LOG_TAG, "expected exactly one Attends relation to mark as invalid but found " + numAttendsRelations);
					}
					for (int i = 0; i < objects.size(); i++) {
						ParseObject toMarkInvalid = objects.get(i);
						Log.v(LOG_TAG, "marking invalid the Attends relationship with objectId: " + toMarkInvalid.getObjectId());
						toMarkInvalid.put(DbColumns.ATTENDS_IS_VALID, false);
						toMarkInvalid.saveInBackground();
					}
				}
			}
		});
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
		// there are two cases: one when the end date IS specified and one where the
		// end date IS NOT specified
		query.whereGreaterThanOrEqualTo(DbColumns.GAME_START_DATE, criteria.mStartDate);
		query.whereGreaterThanOrEqualTo(DbColumns.GAME_START_TIME, criteria.mStartTime);
		query.whereLessThanOrEqualTo(DbColumns.GAME_START_TIME, criteria.mEndTime);
		if (criteria.mEndDate != -1L) {
			query.whereLessThanOrEqualTo(DbColumns.GAME_START_DATE, criteria.mEndDate);
		} 
		// only look for valid games
		query.whereEqualTo(DbColumns.GAME_IS_VALID, true);
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
	 * @return a ParseObject representing the first Sport in the Sport table
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
	 * 
	 * @param g the game to join. Must be the game that the current user just created
	 */
	public static void joinGameJustCreatedByCurrentUser(Game g) {
		joinGameJustCreatedByCurrentUserWithCallback(g, DEFAULT_SAVE_CALLBACK);
	}
	
	/**
	 * 
	 * @param g the game to join. Must be the game that the current user just created
	 */
	public static void joinGameJustCreatedByCurrentUserWithCallback(Game g, SaveCallback cb) {
		ParseObject game = getGameCreatedByCurrentUser(g);
		ParseObject attends = new ParseObject("Attends");
		attends.put(DbColumns.ATTENDS_ATTENDEE, ParseUser.getCurrentUser());
		attends.put(DbColumns.ATTENDS_GAME, game);
		attends.put(DbColumns.ATTENDS_JOINED_AT, System.currentTimeMillis());
		attends.put(DbColumns.ATTENDS_IS_VALID, true);
		if (cb != null)
			attends.saveInBackground(cb);
		else {
			try {
				attends.save();
			} catch (ParseException e) {
				Log.e(LOG_TAG, "Failed to save game without callback");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Adds the current user to a game. 
	 * @param g the Game to join
	 * @return true if the user was added to the game, false otherwise
	 */
	public static JoinGameResult joinGame(Game g, boolean currentUserIsGameCreator) {
		ParseObject game = null;
		if (currentUserIsGameCreator) {
			game = getGameCreatedByCurrentUser(g);
		} else {
			game = getGameUsingId(g); 
		}
		// first check to see if they are already attending this game
		ParseQuery<ParseObject> alreadyAttendingCheck = ParseQuery.getQuery("Attends");
		alreadyAttendingCheck.whereEqualTo(DbColumns.ATTENDS_GAME, game);
		alreadyAttendingCheck.whereEqualTo(DbColumns.ATTENDS_ATTENDEE, ParseUser.getCurrentUser());
		List<ParseObject> results = null;
		try {
			results = alreadyAttendingCheck.find();
		} catch (ParseException e1) {
			// could not check to see if user has already joined
			e1.printStackTrace();
			return JoinGameResult.ERROR_JOINING;
		}
		if (results.size() == 0) {
			// not attending this game yet, so add the user
			ParseObject attends = new ParseObject("Attends");
			attends.put(DbColumns.ATTENDS_ATTENDEE, ParseUser.getCurrentUser());
			attends.put(DbColumns.ATTENDS_GAME, game);
			attends.put(DbColumns.ATTENDS_JOINED_AT, System.currentTimeMillis());
			attends.put(DbColumns.ATTENDS_IS_VALID, true);
			try {
				attends.save();
				return JoinGameResult.SUCCESS;
			} catch (ParseException e) {
				e.printStackTrace();
				return JoinGameResult.ERROR_JOINING;
			}
		} else if (results.size() == 1) {
			// there are two cases here: they once were joining but they left, or they have already
			// joined the game
			ParseObject attends = results.get(0);
			boolean alreadyJoined = attends.getBoolean(DbColumns.ATTENDS_IS_VALID);
			if (alreadyJoined) {
				return JoinGameResult.ALREADY_ATTENDING;
			} else {
				// set the flag to true to show they are attending now
				attends.put(DbColumns.ATTENDS_IS_VALID, true);
				attends.saveInBackground();
				return JoinGameResult.SUCCESS;
			}
		} else {
			// database is in an inconsistent state
			Log.e(LOG_TAG, "database inconsistency: user is joined this game " + results.size() + " times.");
			return JoinGameResult.ALREADY_ATTENDING;
		}
	}

	/**
	 * Removes the current user from the game.
	 * @param g the Game that the current user intends to leave
	 * @return true if the user was successfully removed or if the user was never a member of
	 * the game to begin with. Returns false if there was an error 
	 */
	public static boolean leaveGame(Game g) {
		// just delete the Attends object
		ParseQuery<ParseObject> attendsRelationToLeaveQuery = ParseQuery.getQuery("Attends");
		attendsRelationToLeaveQuery.whereEqualTo(DbColumns.ATTENDS_ATTENDEE, ParseUser.getCurrentUser());
		ParseObject gameToLeave = Translator.appGameToExistingParseGame(g);
		attendsRelationToLeaveQuery.whereEqualTo(DbColumns.ATTENDS_GAME, gameToLeave);
		try {
			List<ParseObject> objects = attendsRelationToLeaveQuery.find();
			int numResults = objects.size();
			if (numResults == 1) {
				ParseObject attendsRelation = objects.get(0);
				attendsRelation.put(DbColumns.ATTENDS_IS_VALID, false);
				attendsRelation.saveInBackground();
				return true;
			} else {
				Log.e(LOG_TAG, "expected exactly 1 result but got: " + numResults);
				return false;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Returns the number of players for a game
	 * 
	 * @param app Game object 
	 * @return number of attendees for this game.
	 * @requires the Game object have a non-null id
	 * @throws IllegalArgumentException if the Game object has a null id
	 */
	public static int getCurrentNumberOfGameAttendees(Game g) {
		if (g.id == null) {
			throw new IllegalArgumentException("Game object must have and id that is not null");
		}
		ParseObject game = getGameUsingId(g);
		if (game != null) {
			ParseQuery<ParseObject> attendeesQuery = ParseQuery.getQuery("Attends"); 
			attendeesQuery.whereEqualTo(DbColumns.ATTENDS_GAME, game);
			attendeesQuery.whereEqualTo(DbColumns.ATTENDS_IS_VALID, true);
			try {
				int count = attendeesQuery.count();
				Log.v(LOG_TAG, "number of attendees for game with id " + g.id + ": " + count);
				return count;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	/**
	 * Gets a List of valid games that have been created by the user. Valid means they are not
	 * over yet. 
	 * 
	 * @param userId - ID of the user to query
	 * @return List of Game objects
	 */
	public static ArrayList<Game> getGamesCreatedByCurrentUser() {
		ParseQuery<ParseObject> gamesCreatedQuery = ParseQuery.getQuery("Game");
		gamesCreatedQuery.whereEqualTo(DbColumns.GAME_CREATOR, ParseUser.getCurrentUser());
		gamesCreatedQuery.whereEqualTo(DbColumns.GAME_IS_VALID, true);
		List<ParseObject> results = null;
		try {
			results = gamesCreatedQuery.find();
			ArrayList<Game> games = new ArrayList<Game>();
			for (ParseObject game : results) {
				Game g = Translator.parseGameToAppGame(game);
				Log.v(LOG_TAG, "game with id: " + g.id);
				games.add(g);
			}
			return games;
		} catch (ParseException e) {
			Log.e(LOG_TAG, "unable to retreive games created by user");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets list of game the user current user is attending.
	 * @return List of game objects representing what the games is user attending
	 */
	public static ArrayList<Game> getGamesCurrentUserIsAttending() {
		ParseQuery<ParseObject> gamesAttendingQuery = ParseQuery.getQuery("Attends");
		gamesAttendingQuery.whereEqualTo(DbColumns.ATTENDS_ATTENDEE, ParseUser.getCurrentUser());
		gamesAttendingQuery.include(DbColumns.ATTENDS_GAME);
		gamesAttendingQuery.whereEqualTo(DbColumns.ATTENDS_IS_VALID, true);
		try {
			List<ParseObject> results = gamesAttendingQuery.find();
			ArrayList<Game> games = new ArrayList<Game>();
			for (ParseObject attends : results) {
				ParseObject parseGame = attends.getParseObject(DbColumns.ATTENDS_GAME);
				if (parseGame != null) {
					games.add(Translator.parseGameToAppGame(parseGame));
				} else {
					// this means there is an inconsistency in the database
					Log.w(LOG_TAG, "An Attends relation refers to a nonexistent Game object." +
								" Attends relation objectId: " + attends.getObjectId());
				}
			}
			return games;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}

