package com.uwcse403.pocketpickup.ParseInteraction;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.game.Game;

public class CreateGameAsCreatorCallback extends SaveCallbackWithArgs {
	private static final String LOG_TAG = "CreateGameAsCreatorCallback";

	public CreateGameAsCreatorCallback(Game g){
		super(g);
	}
	@Override
	public void done(ParseException createFail) {
		// this will add the user who just created the game as an attendee
			if (createFail == null) {
			GameHandler.joinGame(getGame(), true);
			} else {
				Log.e(LOG_TAG, "failed to save game: " + createFail.getCode() + " : " + createFail.getMessage());
			}
			ParseObject currentUser = ParseUser.getCurrentUser();
			ParseObject createdGame = GameHandler.getGameCreatedByCurrentUser(getGame());
			if (createdGame == null) {
				Log.e(LOG_TAG, "Did not find created game in database");
			}
			currentUser.add(DbColumns.USER_GAMES_ATTENDING, createdGame.getObjectId());
			currentUser.add(DbColumns.USER_GAMES_CREATED, createdGame.getObjectId());
			try {
				currentUser.save();
			} catch (ParseException updateUserFail) {
				Log.e(LOG_TAG, "Failed to updated games attending/created for user");
				
			}
		}

}
