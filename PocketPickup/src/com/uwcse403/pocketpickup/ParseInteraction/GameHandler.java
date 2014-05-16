package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;

public class GameHandler {
	public static final String LOG_TAG = "GameHandler";
	
	public static void createGame(Game g) {
		Log.v(LOG_TAG, "entering CreateGame()");
		ParseObject game = Translator.appGameToParseGame(g); 
		game.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					Log.v(LOG_TAG, "Successfully saved game");
					// successfully created game
				} else {
					// unable to create the game, alert user
					Log.e(LOG_TAG, "error saving game: " + e.getCode() + ": " + e.getMessage());
				}
			}
		});
	} 
	
	public static void createDummyGame(Game g) {
		Log.v(LOG_TAG, "entering createDummyGame()");
		ParseObject game = new ParseObject("Game");
		// fill in all the setter	
		game.put(DbColumns.GAME_IDEAL_SIZE, g.mIdealGameSize);
		
		game.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					Log.v(LOG_TAG, "Successfully saved game");
					// successfully created game
				} else {
					// unable to create the game, alert user
					Log.e(LOG_TAG, "error saving game: " + e.getCode() + ": " + e.getMessage());
				}
			}
		});
	}
	
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
					Log.v(LOG_TAG, "Successfully saved game");
					// successfully created game
				} else {
					// unable to create the game, alert user
					Log.e(LOG_TAG, "error saving game: " + e.getCode() + ": " + e.getMessage());
				}
			}
		});
	}
	/**
	 * 
	 * @param id the Parse objectId of the game to remove
	 */
	public static void removeGame(String id) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
		query.getInBackground(id, new GetCallback<ParseObject>() {
			public void done(ParseObject object, ParseException e) {
				if(e == null) {
					Log.v(LOG_TAG, "Successfully got object to delete");
					object.deleteInBackground(new DeleteCallback() {
						public void done(ParseException pe) {
							if(pe == null) {
								Log.v(LOG_TAG, "Successfully deleted object");
							}
							else {
								Log.v(LOG_TAG, "Failed to delete object");
							}
						}
					});
				}
				else {
					Log.v(LOG_TAG, "Failed to get object for deletion");
					return;
				}
			}
		});
	}

	/**
	 * 
	 * @param criteria: FindGameCriteria object containing search criteria
	 */
	public static ArrayList<Game> findGame(FindGameCriteria criteria) {
		// get the location of the query, i.e., the center of the circle
		LatLng loc = criteria.mSearchLocation;
		ParseGeoPoint center = new ParseGeoPoint(loc.latitude, loc.longitude);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Game");
		ParseObject gameType = PocketPickupApplication.sportsAndObjs.get(criteria.mGameType);
		// look for games of the desired sport
		query.whereEqualTo(DbColumns.GAME_SPORT, gameType);
		// limit to games within the specified radius
		query.whereWithinMiles(DbColumns.GAME_LOCATION, center, criteria.mRadius);
		// limit games to those that fall within the times entered
		query.whereGreaterThan(DbColumns.GAME_START_DATE, criteria.mStartDate + criteria.mStartTime);
		query.whereLessThan(DbColumns.GAME_END_DATE, criteria.mEndDate + criteria.mEndTime);

		List<ParseObject> results = null;
		try {
			results = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (results == null) {
			// no games match search criteria
			return null;
		}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return null;
	}
}



