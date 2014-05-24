package com.uwcse403.pocketpickup.ParseInteraction;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.game.Game;

/**
 * Callback for when the current user is creating a game
 *
 */

public class CreateGameAsCreatorCallback extends SaveCallbackWithArgs {
	/**Log tag for debugging**/
	private static final String LOG_TAG = "CreateGameAsCreatorCallback";
	
	/**
	 * Default constructor
	 * @param g - Game beig created
	 */
	public CreateGameAsCreatorCallback(Game g) {
		super(g);
	}
	
	@Override
	/**
	 * Called upon success or failure of the Parse database call associated with this callback
	 * @param createFail - Exception for when the creation fails. null when succesful
	 */
	public void done(ParseException createFail) {
			if (createFail == null) {
				// this will add the user who just created the game as an attendee
				GameHandler.joinGame(getGame(), true);
				ParseObject currentUser = ParseUser.getCurrentUser();
				ParseObject createdGame = GameHandler.getGameCreatedByCurrentUser(getGame());
				if (createdGame == null) {
					Log.e(LOG_TAG, "Did not find created game in database");
				}
				else {
					currentUser.add(DbColumns.USER_GAMES_ATTENDING, createdGame.getObjectId());
					currentUser.add(DbColumns.USER_GAMES_CREATED, createdGame.getObjectId());
				try {
					currentUser.save();
				} catch (ParseException updateUserFail) {
					Log.e(LOG_TAG, "Failed to updated games attending/created for user");
				}
			}
		} else {
			Log.e(LOG_TAG, "failed to save game: " + createFail.getCode() + " : " + createFail.getMessage());
		}
	}

}
