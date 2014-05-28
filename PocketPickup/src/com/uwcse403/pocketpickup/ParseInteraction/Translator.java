package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.game.Game;
/**
 * Translates between PocketPickup Game objects (for use communicating 
 * between activities in the app) and ParseObject Game objects to be
 * stored in the database.
 *
 */
public final class Translator {
	private Translator() {
		
	}
	
	
	public static final String LOG_TAG = "Translator";
	/**
	 * 
	 * @param game ParseObject Game object
	 * @return Game object corresponding to the Game object stored in the Parse database
	 */
	public static Game parseGameToAppGame(ParseObject game) {
		String creatorId = game.getObjectId();
		ParseGeoPoint parseLocation = (ParseGeoPoint) game.get(DbColumns.GAME_LOCATION);
		LatLng location = new LatLng(parseLocation.getLatitude(), parseLocation.getLongitude());
		Long gameStartDate = game.getLong(DbColumns.GAME_START_DATE);
		Long gameEndDate = game.getLong(DbColumns.GAME_END_DATE);
		//String gameType = PocketPickupApplication.objIdAndObjs.get(game.getString(DbColumns.GAME_SPORT)).getString(DbColumns.SPORT_NAME);
		ParseObject parseSportObj = game.getParseObject(DbColumns.GAME_SPORT);
		Log.v(LOG_TAG, "objectid: " + parseSportObj.getObjectId());
		Log.v(LOG_TAG, "parse sport obj hash: " + parseSportObj.hashCode());
		Log.v(LOG_TAG, "bimap contains it: " + PocketPickupApplication.sportsAndObjs.inverse().containsKey(parseSportObj));
		String gameType = PocketPickupApplication.sportsAndObjs.inverse().get(parseSportObj);
		int idealGameSize = game.getInt(DbColumns.GAME_IDEAL_SIZE);
		String gameDetails = game.getString(DbColumns.GAME_DETAILS);
		String objectId = game.getObjectId();
		return new Game(creatorId, location, gameStartDate, gameEndDate, 
				gameType, idealGameSize, gameDetails, objectId);
	}
	
	/**
	 * 
	 * @param game object to be converted into a new ParseObject to be saved to the database.
	 * This method SHOULD NOT be called if there is already a Game object in the database. This
	 * is only for creation of new Game objects
	 * @return Game object ready to be stored in the Parse database
	 */
	public static ParseObject appGameToNewParseGame(Game game) {
		// first check to see if this Game object corresponds to a game created by the current
		// user. If so, this means we don't have to fetch the user info from the Parse servers
		// because we can just call ParseUser.getCurrentUser()
		ParseObject g = new ParseObject("Game");
		String creatorId = game.mCreator;
		Log.v(LOG_TAG, "app game creator: " + creatorId);
		Log.v(LOG_TAG, "current ParseUser objectId: " + PocketPickupApplication.userObjectId);
		if (creatorId.equals(PocketPickupApplication.userObjectId)) {
			g.put(DbColumns.GAME_CREATOR, ParseUser.getCurrentUser());
		} else {
			// TODO implement this, if it necessary. Currently it will never be used
			// if it is called, throw an exception
			//throw new IllegalStateException();
		}
		ParseGeoPoint location = new ParseGeoPoint(game.mGameLocation.latitude, game.mGameLocation.longitude);
		g.put(DbColumns.GAME_LOCATION, location);
		g.put(DbColumns.GAME_START_DATE, game.mGameStartDate);
		g.put(DbColumns.GAME_END_DATE, game.mGameEndDate);
		g.put(DbColumns.GAME_IDEAL_SIZE, game.mIdealGameSize);
		g.put(DbColumns.GAME_SPORT, PocketPickupApplication.sportsAndObjs.get(game.mGameType));
		g.put(DbColumns.GAME_DETAILS, game.mDetails);
		return g;
	}
	
	/**
	 * @param app Game object that corresponds to a ParseObject Game object that is currently in
	 * the database. This method is used for UPDATING EXISTING GAMES.
	 * @requires Game object have a valid id field
	 * @return the ParseObject Game object from the database if found, else returns null.
	 */
	public static ParseObject appGameToExistingParseGame(Game game) {
		if (game.id == null) {
			throw new IllegalArgumentException("Game object must have non-null id field");
		}
		ParseQuery<ParseObject> gameQuery = ParseQuery.getQuery("Game");
		gameQuery.whereEqualTo(LOG_TAG, game.id);
		List<ParseObject> result = null;
		try {
			result = gameQuery.find();
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.getMessage());
			return null;
		}
		if (result.size() == 1) {
			return result.get(0);
		} else {
			Log.e(LOG_TAG, "expected to find exactly one game but found " + result.size());
			return null;
		}
	}
}
