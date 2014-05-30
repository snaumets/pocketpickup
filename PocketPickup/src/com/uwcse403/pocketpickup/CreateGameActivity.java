package com.uwcse403.pocketpickup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.ParseInteraction.CreateGameAsCreatorCallback;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.ParseInteraction.SaveCallbackWithArgs;
import com.uwcse403.pocketpickup.fragments.DatePickerFragment;
import com.uwcse403.pocketpickup.fragments.TimePickerFragment;
import com.uwcse403.pocketpickup.game.Game;

public class CreateGameActivity extends Activity
								implements DatePickerDialog.OnDateSetListener, 
					   		     		   TimePickerDialog.OnTimeSetListener {
	public static final String LOG_TAG = "CreateGameActivity";
	public static final String CREATEGAME_LOCATION  = "creategame_location";
	public static final String CREATEGAME_LATITUDE  = "creategame_latitude";
	public static final String CREATEGAME_LONGITUDE = "creategame_longitude";
	public static final String CREATEGAME_GAMELIST = "creategame_gamelist";
	
	private static final long FIVE_MIN = 5 * 60 * 1000L;
	private static final long HOUR = 60 * 60 * 1000L;
	private static final long MILLIS_IN_DAY = 60 * 60 *24 * 1000L;
	private static final long MIN_IN_MILLIS = 60 * 1000;

	// Bundle IDs for persistent button names
	private static final String STATE_GAME_TIME = "cg_time";
	
	private Calendar mDate;
	private LatLng   mLatLng;
	private int      mDuration;
	private String   mSport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		long gameTime = (savedInstanceState == null) ? 0L : savedInstanceState.getLong(STATE_GAME_TIME);
		mDate = initDate(gameTime);
		
		final Bundle args = getIntent().getExtras();
		
		EditText editText = (EditText) findViewById(R.id.cg_location_text);
		editText.setText(args.getCharSequence(CREATEGAME_LOCATION));
		final double lat = args.getDouble(CREATEGAME_LATITUDE);
		final double lon = args.getDouble(CREATEGAME_LONGITUDE);
		mLatLng = new LatLng(lat, lon);
		
		setButtonLabels();
		
		// Initialize sports choices
		Spinner sportsSpinner = (Spinner) findViewById(R.id.cg_sports_spinner);
		ArrayList<String> sports = new ArrayList<String>(PocketPickupApplication.sportsAndObjs.keySet());
		ArrayAdapter<String> sportsAdapter = new ArrayAdapter<String>(this, R.drawable.spinner_center_item, sports);
		sportsAdapter.setDropDownViewResource(R.drawable.spinner_center_item);
		sportsSpinner.setAdapter(sportsAdapter);
		sportsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				mSport = (String) parent.getItemAtPosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
			
		});
		
		// Initialize duration choices
		Spinner durationSpinner = (Spinner) findViewById(R.id.cg_duration_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.duration_choices, 
				R.drawable.spinner_center_item);
		adapter.setDropDownViewResource(R.drawable.spinner_center_item);
		durationSpinner.setAdapter(adapter);
		durationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String durationStr = (String) parent.getItemAtPosition(pos);
				mDuration = Integer.parseInt(durationStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
			
		});
	}
	
	private Calendar initDate(long time) {
		Calendar c = Calendar.getInstance();
		if (time == 0L) {
			time = c.getTimeInMillis() + FIVE_MIN;
		}
		c.setTimeInMillis(time);
		return c;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			savedInstanceState.putLong(STATE_GAME_TIME, (mDate == null) ? 0L : mDate.getTimeInMillis());
		}
	}
	
	private void setButtonLabels() {
		((Button) findViewById(R.id.create_time_button)).setText(getTimeButtonString(mDate));
		((Button) findViewById(R.id.create_date_button)).setText(getDateButtonString(mDate));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_game, menu);
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
	 * Displays dialog for selecting game time.
	 * 
	 * @param v		view for dialog fragment
	 */
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	/**
	 * Displays dialog for selecting game date.
	 * 
	 * @param v		view for dialog fragment
	 */
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    
	    if (mDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_INIT, mDate.getTimeInMillis());
	    	args.putLong(DatePickerFragment.STATE_DATE_MIN, Calendar.getInstance().getTimeInMillis());
	    }

	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (mDate == null) {
			mDate = Calendar.getInstance();
		}
		mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mDate.set(Calendar.MINUTE, minute);
		((Button) findViewById(R.id.create_time_button)).setText(getTimeButtonString(mDate));
	}

	@Override
	public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
		if (mDate == null) {
			mDate = Calendar.getInstance();
		}
		mDate.set(year, monthOfYear, dayOfMonth);
		((Button) findViewById(R.id.create_date_button)).setText(getDateButtonString(mDate));
	}
	
	private String getDateButtonString(final Calendar date) {
		if (date == null) {
			return getResources().getString(R.string.select_date);
		} else {
			return DateFormat.getDateFormat(this).format(date.getTime());
		}
	}
	
	private String getTimeButtonString(final Calendar date) {
		if (date == null) {
			return getResources().getString(R.string.select_time);
		} else {
			return DateFormat.getTimeFormat(this).format(date.getTime());
		}
	}
	
	public void setLocation(View v) {
		Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
	}
	
	public void submitCreate(View v) {
		// Validate the create time
		Date date = new Date();
		long nowTime = date.getTime();
		if (mDate.getTimeInMillis() < nowTime) { // User tried to create a game back in time (expired)
			Toast.makeText(this, "Can't Create Game Starting In The Past, Please Fix The Create Time", Toast.LENGTH_LONG).show();
			return;
		}
		long startDateAndTime = mDate.getTimeInMillis();

		// the time of day in milliseconds from midnight
		long startTime = (startDateAndTime + PocketPickupApplication.GMT_OFFSET * HOUR)% MILLIS_IN_DAY; 
		// chop off anything after minutes
		startTime = (startTime / MIN_IN_MILLIS) * MIN_IN_MILLIS;
		
		// in case the game goes into the next day, mod by the number of milliseconds
		// in one day
		long endTime = (startTime + mDuration * HOUR) % MILLIS_IN_DAY;
		// chop off anything after minutes
		endTime = (endTime / MIN_IN_MILLIS) * MIN_IN_MILLIS;

		// the date represented as unix time milliseconds from 1970 to 12:00AM
		// on the selected start date
		long startDate = startDateAndTime  - startTime; 
		long endDate;
		// if the game spans two days, set the end date to the next day.
		// The only time this happens is if the previous calculations show that
		// the endTime is less than the startTime.
		//if ( startTime + (mDuration * HOUR) > MILLIS_IN_DAY) {
		if (endTime < startTime) {
			endDate = startDate + MILLIS_IN_DAY;
		} else {
			endDate = startDate;
		}
		
		EditText details = (EditText) findViewById(R.id.cg_details);
		String detailsText = details.getText().toString();
		details.setImeOptions(EditorInfo.IME_ACTION_DONE);
		details.setSingleLine();
		
		final Game createGame = new Game(ParseUser.getCurrentUser().getObjectId(), 
				mLatLng, startDate, endDate, startTime, endTime, 
				mSport, 2, detailsText);
		final ArrayList<Game> games = new ArrayList<Game>(); // will store only created game
		games.add(createGame);
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra(CREATEGAME_LATITUDE, mLatLng.latitude);
		returnIntent.putExtra(CREATEGAME_LONGITUDE, mLatLng.longitude);
		returnIntent.putParcelableArrayListExtra(CREATEGAME_GAMELIST, games);
		
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
		//setResult(Activity.RESULT_OK);
		//finish();
		GameHandler.createGame(createGame, null); 
		//GameHandler.createGame(createGame, new CreateGameAsCreatorCallback(createGame));
	}
	
	public void resetCreate(View v) {
		mDate = initDate(0);
		
		Spinner spinner = (Spinner) findViewById(R.id.cg_duration_spinner);
		spinner.setSelection(0);
		
		setButtonLabels();	
		
		EditText details = (EditText) findViewById(R.id.cg_details);
		details.setText("");
	}
}
