package com.uwcse403.pocketpickup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.parse.ParseUser;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.Game;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

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
		String gameType = args.getString(GAME_TYPE);
		String gameDetails = args.getString(GAME_DETAILS);
		long gameStartDate = args.getLong(GAME_START_DATE);
		int gameDuration = args.getInt(GAME_DURATION);
		String gameCreator = args.getString(GAME_CREATOR);
		int gameMinPlayers = args.getInt(GAME_MIN_PLAYERS);
		double gameLocationLat = args.getDouble(GAME_LOCATION_LAT);
		double gameLocationLng = args.getDouble(GAME_LOCATION_LNG);
		
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
		attendees.setText(countAttendees + " Users Joined This Game");
		
		
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
		// TODO: actually join the game, uncomment when the function works
		if(GameHandler.joinGame(game, false)) {
			Toast.makeText(this, "Successfully added to this game!", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "Joining Game Failed", Toast.LENGTH_LONG).show();
		}
		finish();
	}
}
