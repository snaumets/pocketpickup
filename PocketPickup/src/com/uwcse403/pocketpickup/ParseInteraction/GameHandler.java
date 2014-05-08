package com.uwcse403.pocketpickup.ParseInteraction;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.uwcse403.pocketpickup.Game;

public class GameHandler {
	
	public static void createGame(Game g) {
		ParseObject game = new ParseObject("Game");
		// fill in all the setters
		game.put("sport", "some sport");
		game.put("creator", g.creatorName);
		game.put("location", new ParseGeoPoint(g.gameLocation.latitude, g.gameLocation.longitude));
		game.put("date", g.gameDate);
		game.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					// successfully created game
				} else {
					// unable to create the game, alert user
				}
			}
		});
	} 
}



