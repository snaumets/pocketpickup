package com.uwcse403.pocketpickup.ParseInteraction;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.PocketPickupApplication;
import com.uwcse403.pocketpickup.game.Game;
/**
 * Translates between PocketPickup Game objects (for use communicating 
 * between activities in the app) and ParseObject Game objects to be
 * stored in the database
 * @author imathieu
 *
 */
public class Translator {
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
		String gameType = game.getString(DbColumns.SPORT_NAME);
		int idealGameSize = game.getInt(DbColumns.GAME_IDEAL_SIZE);
		return new Game(creatorId, location, gameStartDate, gameEndDate, gameType, idealGameSize);
	}
	
	/**
	 * 
	 * @param game object to be converted into a ParseObject game object
	 * @return Game object ready to be stored in the Parse database
	 */
	public static ParseObject appGameToParseGame(Game game) {
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
			throw new IllegalStateException();
		}
		ParseGeoPoint location = new ParseGeoPoint(game.mGameLocation.latitude, game.mGameLocation.longitude);
		g.put(DbColumns.GAME_LOCATION, location);
		g.put(DbColumns.GAME_START_DATE, game.mGameStartDate);
		g.put(DbColumns.GAME_END_DATE, game.mGameEndDate);
		g.put(DbColumns.GAME_IDEAL_SIZE, game.mIdealGameSize);
		g.put(DbColumns.GAME_SPORT, PocketPickupApplication.sportsAndObjs.get(game.mGameType));
		return g;
	}
}