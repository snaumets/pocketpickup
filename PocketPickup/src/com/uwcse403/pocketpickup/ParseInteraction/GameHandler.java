package com.uwcse403.pocketpickup.ParseInteraction;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.game.Game;

public class GameHandler {
	public static final String LOG_TAG = "GameHandler";
	
	public static void createGame(Game g) {
		Log.v(LOG_TAG, "entering CreateGame()");
		ParseObject game = new ParseObject("Game");
		// fill in all the setters
		game.put("sport", "some sport");
		game.put("creator", g.creatorName);
		game.put("location", new ParseGeoPoint(g.gameLocation.latitude, g.gameLocation.longitude));
		game.put("startDate", g.gameStartDate);
		game.put("endDate", g.gameEndDate);
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
	
}



