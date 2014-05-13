package com.uwcse403.pocketpickup.ParseInteraction;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.game.Game;
import com.uwcse403.pocketpickup.game.Game2;

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
	
	public static void createGame(Game2 g) {
		Log.v(LOG_TAG, "entering CreateGame()");
		ParseObject game = new ParseObject("Game");
		// fill in all the setter	
		game.put("idealGameSize", g.getIdealSize());
		game.put("creator", g.getCreator());
		game.put("sport", g.getGameType());
		game.put("location", g.getLocation());
		game.put("startDate", g.getStartDate());
		game.put("endDate", g.getEndDate());
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
	
	public static void createDummyGame(Game2 g) {
		Log.v(LOG_TAG, "entering CreateGame()");
		ParseObject game = new ParseObject("Game");
		// fill in all the setter	
		game.put("idealGameSize", g.getIdealSize());
		
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
}



