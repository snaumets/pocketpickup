package com.uwcse403.pocketpickup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.Game;

import android.app.Activity;
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
	
	private Game game;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		if (savedInstanceState == null) {
			
		}
		
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
		String detailsStr = (gameDetails.equals("") ? "None" : gameDetails);
		details.setText(detailsStr);
		
		TextView attendees = (TextView) findViewById(R.id.gameAttendeesTextView);
		int countAttendees = GameHandler.getCurrentNumberOfGameAttendees(game);
		String attendeesUnit = countAttendees == 1 ? " User" : " Users"; 
		attendees.setText(countAttendees + attendeesUnit + " Joined This Game");
		
		
		// TODO: check if this user has joined this game yet
		// Show the join button if the user hasnt joined this game yet
		Button joinButton = (Button) findViewById(R.id.gameJoinButton);
		joinButton.setVisibility(View.VISIBLE);
		
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
		if (result == JoinGameResult.SUCCESS) {
			Toast.makeText(this, "Successfully added to this game!", Toast.LENGTH_LONG).show();
		} else if (result == JoinGameResult.ERROR_JOINING) {
			Toast.makeText(getApplicationContext(), "Joining Game Failed", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "You are already an attendee", Toast.LENGTH_LONG).show();;
		}
		finish();
	}


}
