package com.uwcse403.pocketpickup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.game.Game;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

	public static final int HOUR_IN_MILLIS = 60 * 60 * 1000;

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
		long gameStart = game.startTime + game.mGameStartDate;
		long gameEnd = game.endTime + game.mGameEndDate;

		// Convert millisecond difference to hours, ms / (1000 ms/s) / (60
		// s/min) / (60 min/hr)
		long durationInMillis = gameEnd - gameStart;
		int durationInHours = (int) (durationInMillis / HOUR_IN_MILLIS);
		int gameDuration = durationInHours;
		String gameCreator = game.mCreator;
		int gameMinPlayers = game.mIdealGameSize;
		LatLng gameLocationLatLng = game.mGameLocation;

		TextView sport = (TextView) findViewById(R.id.gameSportTextView);
		sport.setText(gameType);

		Date startDate = new Date(gameStart);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEEE MMM d, yyyy hh:mm a", Locale.getDefault());
		String dateString = formatter.format(startDate);

		// String dateString = new Date(game.startTime +
		// game.mGameStartDate).toLocaleString();
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
		attendees.setText(countAttendees + attendeesUnit + " Joined");

		// Show the correct button
		if (!LoginActivity.user.mAttendingGames.contains(game)) { // user can
																	// join this
																	// game
			Button joinButton = (Button) findViewById(R.id.gameJoinButton);
			joinButton.setVisibility(View.VISIBLE);
		} else if ((LoginActivity.user.mCreatedGames.contains(game) || LoginActivity.user.mAttendingGames
				.contains(game)) && countAttendees == 1) { // delete game
			Button deleteButton = (Button) findViewById(R.id.gameDeleteButton);
			deleteButton.setVisibility(View.VISIBLE);
		} else if (LoginActivity.user.mAttendingGames.contains(game)
				&& countAttendees > 1) { // leave game
			Button leaveButton = (Button) findViewById(R.id.gameLeaveButton);
			leaveButton.setVisibility(View.VISIBLE);
		} else {
			// Sanity check, but should not show button in this case
		}
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
		boolean networkAvailable = checkNetwork();
		if (networkAvailable) {
			new JoinDialogTask().execute("");
		} else {
			displayNetworkErrorMessage();
		}
	}

	/**
	 * This method will update the backend that the user has deleted this game.
	 */
	public void deleteGameSubmit(View v) {
		boolean networkAvailable = checkNetwork();
		if (networkAvailable) {
			new DeleteDialogTask().execute("");
		} else {
			displayNetworkErrorMessage();
		}
	}

	/**
	 * This method will update the backend that the user has left this game.
	 */
	public void leaveGameSubmit(View v) {
		boolean networkAvailable = checkNetwork();
		if (networkAvailable) {
			new LeaveDialogTask().execute("");
		} else {
			displayNetworkErrorMessage();
		}
	}
	
	/**
	 * Returns whether or not the network can be accessed
	 */
	public boolean checkNetwork() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	    	return true;
	    }
	    return false;
	}
	
	/**
	 * displays a toast notifying the user that the network can't be accessed
	 */
	public void displayNetworkErrorMessage() {
		Toast.makeText(getApplicationContext(), "Network Disabled\nConnect To Network To Complete Operation", Toast.LENGTH_LONG).show();
	}
	
	/**
	 * This task will take show a progress dialog while the game is joined.
	 * After it is complete, it will dismiss the dialog and go back to the 
	 * MainActivity.
	 */
	private class JoinDialogTask extends AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(GameActivity.this, "", "Loading...", true);
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// add current user to the game passing false to signify that the game
			// the
			// user is joining was not created by him or herself. Still works if the
			// user
			// is the creator.
			JoinGameResult result = GameHandler.joinGame(game, false);
			Intent returnIntent = new Intent();
			returnIntent.putExtra(GAME, game);
			if (result == JoinGameResult.SUCCESS) {
				returnIntent.putExtra(GAME_RESULT, GAME_RESULT_JOINED);
			} else if (result == JoinGameResult.ERROR_JOINING) {
				returnIntent.putExtra(GAME_RESULT, GAME_RESULT_JOIN_FAILED);
			} else {
				returnIntent.putExtra(GAME_RESULT, GAME_RESULT_ALREADY_JOINED);
			}
			setResult(Activity.RESULT_OK, returnIntent);
			finish();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		   super.onProgressUpdate(values);
		}
 
		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}
	
	/**
	 * This task will take show a progress dialog while the game is left.
	 * After it is complete, it will dismiss the dialog and go back to the 
	 * MainActivity.
	 */
	private class LeaveDialogTask extends AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(GameActivity.this, "", "Loading...", true);
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			boolean result = GameHandler.leaveGame(game);
			Intent returnIntent = new Intent();
			returnIntent.putExtra(GAME, game);
			if (result) { // successfully left
				returnIntent.putExtra(GAME_RESULT, GAME_RESULT_LEFT);
			} else {
				returnIntent.putExtra(GAME_RESULT, GAME_RESULT_LEFT_FAILED);
			}
			setResult(Activity.RESULT_OK, returnIntent);
			finish();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		   super.onProgressUpdate(values);
		}
 
		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}
	
	/**
	 * This task will take show a progress dialog while the game is deleted.
	 * After it is complete, it will dismiss the dialog and go back to the 
	 * MainActivity.
	 */
	private class DeleteDialogTask extends AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(GameActivity.this, "", "Loading...", true);
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			GameHandler.removeGame(game);
			Intent returnIntent = new Intent();
			returnIntent.putExtra(GAME, game);
			returnIntent.putExtra(GAME_RESULT, GAME_RESULT_DELETED);
			setResult(Activity.RESULT_OK, returnIntent);
			finish();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		   super.onProgressUpdate(values);
		}
 
		@Override
		protected void onPostExecute(String result) {
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
	}
}
