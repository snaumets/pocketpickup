package com.uwcse403.pocketpickup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity {
	
	// Argument IDs
	public static final String GAME_TYPE = "gameType";
	public static final String GAME_DETAILS = "gameDetails";
	public static final String GAME_START_DATE = "gameStartDate";
	public static final String GAME_DURATION = "gameDurations";
	public static final String GAME_CREATOR = "gameCreator";
	public static final String GAME_MIN_PLAYERS = "gameMinPlayers";
	public static final String GAME_LOCATION_LAT = "gameLocationLat";
	public static final String GAME_LOCATION_LNG = "gameLocationLng";
	public static final String GAME = "game";
	public static final String GAME_RESULT = "gameResult";
	public static final int GAME_RESULT_JOINED = 0;
	public static final int GAME_RESULT_ALREADY_JOINED = 1;
	public static final int GAME_RESULT_JOIN_FAILED = 2;
	public static final int GAME_RESULT_LEFT = 3;
	public static final int GAME_RESULT_LEFT_FAILED = 4;
	public static final int GAME_RESULT_DELETED = 5;
	
	private Game game;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		// Initialize location text field from passed in location
		Bundle args = getIntent().getExtras();
		game = (Game) args.get(GAME);
		String gameType = game.mGameType;
		String gameDetails = game.mDetails;
		long gameStartDate = game.mGameStartDate;
		
		// Convert millisecond difference to hours, ms / (1000 ms/s) / (60 s/min) / (60 min/hr)
		int durationInHours = (int) (game.mGameEndDate - game.mGameStartDate) / 1000 / 60 / 60;
		int gameDuration = durationInHours;
		String gameCreator = game.mCreator;
		int gameMinPlayers = game.mIdealGameSize;
		LatLng gameLocationLatLng = game.mGameLocation;
		
		TextView sport = (TextView) findViewById(R.id.gameSportTextView);
		sport.setText(gameType);
		
		Date startDate = new Date(gameStartDate);
		SimpleDateFormat formatter = new SimpleDateFormat(
                "hh:mm a EE, MMM d, yyyy", Locale.getDefault());
		String dateString = formatter.format(startDate);
		TextView start = (TextView) findViewById(R.id.gameStartTextView);
		start.setText(dateString);
		
		
		String durationUnit = gameDuration > 1 ? " Hours" : " Hour";
		TextView duration = (TextView) findViewById(R.id.gameDurationTextView);
		duration.setText(gameDuration + durationUnit);
		
		TextView details = (TextView) findViewById(R.id.gameDetailsTextView);
		String detailsStr = gameDetails.equals("") ? "None" : gameDetails;
		details.setText(detailsStr);
		
		TextView attendees = (TextView) findViewById(R.id.gameAttendeesTextView);
		int countAttendees = GameHandler.getCurrentNumberOfGameAttendees(game);
		String attendeesUnit = countAttendees == 1 ? " User" : " Users"; 
		attendees.setText(countAttendees + attendeesUnit + " Joined This Game");
		
		// Show the correct button
		if (!LoginActivity.user.mAttendingGames.contains(game)) { // user can join this game
			Button joinButton = (Button) findViewById(R.id.gameJoinButton);
			joinButton.setVisibility(View.VISIBLE);
		} else if (LoginActivity.user.mCreatedGames.contains(game) && countAttendees == 1) { // delete game
			Button deleteButton = (Button) findViewById(R.id.gameDeleteButton);
			deleteButton.setVisibility(View.VISIBLE);
		} else if (LoginActivity.user.mAttendingGames.contains(game) && countAttendees > 1) { // leave game
			Button leaveButton = (Button) findViewById(R.id.gameLeaveButton);
			leaveButton.setVisibility(View.VISIBLE);
		} else {
			// Sanity check, but should not show button in this case
		}
		
		
		// Show the leave button if the user has already joined this game
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.		
		switch (item.getItemId()) {

		case android.R.id.home:
		    onBackPressed(); // This will not destroy and recreate main activity
		    return true;
		    
		default:
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * This method will update the backend that the user has joined this game.
	 */
	public void joinGameSubmit(View v) {
		// add current user to the game passing false to signify that the game the 
		// user is joining was not created by him or herself. Still works if the user
		// is the creator.
		JoinGameResult result = GameHandler.joinGame(game, false);
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GAME, game);
		if (result == JoinGameResult.SUCCESS) {
			Toast.makeText(this, "Successfully Added To Game!", Toast.LENGTH_LONG).show();
			returnIntent.putExtra(GAME_RESULT, GAME_RESULT_JOINED);
		} else if (result == JoinGameResult.ERROR_JOINING) {
			Toast.makeText(getApplicationContext(), "Joining Game Failed", Toast.LENGTH_LONG).show();
			returnIntent.putExtra(GAME_RESULT, GAME_RESULT_JOIN_FAILED);
		} else {
			Toast.makeText(getApplicationContext(), "You Are Already An Attendee", Toast.LENGTH_LONG).show();
			returnIntent.putExtra(GAME_RESULT, GAME_RESULT_ALREADY_JOINED);
		}
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}
	
	/**
	 * This method will update the backend that the user has deleted this game.
	 */
	public void deleteGameSubmit(View v) {
		GameHandler.removeGame(game);
		Toast.makeText(this, "Successfully Deleted Game", Toast.LENGTH_LONG).show();
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GAME, game);
		returnIntent.putExtra(GAME_RESULT, GAME_RESULT_DELETED);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}
	
	/**
	 * This method will update the backend that the user has left this game.
	 */
	public void leaveGameSubmit(View v) {
		boolean result = GameHandler.leaveGame(game);
		Intent returnIntent = new Intent();
		returnIntent.putExtra(GAME, game);
		if (result) { // successfully left
			Toast.makeText(this, "Successfully Left Game", Toast.LENGTH_LONG).show();
			returnIntent.putExtra(GAME_RESULT, GAME_RESULT_LEFT);
		} else {
			Toast.makeText(this, "Failed To Leave Game", Toast.LENGTH_LONG).show();
			returnIntent.putExtra(GAME_RESULT, GAME_RESULT_LEFT_FAILED);
		}
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}
}
