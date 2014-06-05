package com.uwcse403.pocketpickup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.fragments.DatePickerFragment;
import com.uwcse403.pocketpickup.fragments.TimePickerFragment;
import com.uwcse403.pocketpickup.game.FindGameCriteria;
import com.uwcse403.pocketpickup.game.Game;

public class FindGameActivity extends Activity
							  implements DatePickerDialog.OnDateSetListener, 
							   		     TimePickerDialog.OnTimeSetListener {
	private static final long HOUR = 60 * 60 * 1000L;
	private static final long MS_IN_DAY = 1000 * 60 * 60 * 24; // 1000 ms/s * (60 s/min) * (60 min/hour) * (24 hr/day)
	private static final long MIN_IN_MILLIS = 60 * 1000;
	
	// Argument IDs
	public static final String FINDGAME_LOCATION  = "findgame_location";
	public static final String FINDGAME_LATITUDE  = "findgame_latitude";
	public static final String FINDGAME_LONGITUDE = "findgame_longitude";
	public static final String FINDGAME_RESULTS   = "findgame_results";
	public static final String FINDGAME_RADIUS    = "findgame_radius";
	
	// Bundle IDs for persistent button names
	private static final String STATE_START_TIME = "fg_start_time";
	private static final String STATE_END_TIME   = "fg_end_time";
	private static final String STATE_START_DATE = "fg_start_date";
	private static final String STATE_END_DATE   = "fg_end_date";
	
	// ID for identifying the last pressed button
	private int    mLastButtonId;
	private LatLng mLatLng;
	
	private Calendar mStartTime; 
	private Calendar mEndTime;
	private Calendar mStartDate;
	private Calendar mEndDate;
	private int      mRadius;
	
	private ArrayList<String> availableSports;
	// arraylist to save the selected sports' indexes
	private ArrayList<Integer> selectedSports;
	// arraylist to save initial preferred sports of the user
	private ArrayList<Integer> preferredSports;
	// boolean array to keep track of selections
	private boolean[] preferred;
	
	private AlertDialog sportsDialog;
		
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_game);
		
		// Restore button labels if necessary, otherwise init
		if (savedInstanceState != null) {
			mStartTime = initDate(savedInstanceState.getLong(STATE_START_TIME));
			mEndTime = initDate(savedInstanceState.getLong(STATE_END_TIME));
			mStartDate = initDate(savedInstanceState.getLong(STATE_START_DATE));
			mEndDate = initDate(savedInstanceState.getLong(STATE_END_DATE));
		} else {
			setStartState();
		}
		setButtonLabels();
		
		// Initialize radius choices
		Spinner radiusSpinner = (Spinner) findViewById(R.id.radius_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radius_choices, 
				R.drawable.spinner_center_item);
		adapter.setDropDownViewResource(R.drawable.spinner_center_item);
		radiusSpinner.setAdapter(adapter);
		radiusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String radiusStr = (String) parent.getItemAtPosition(pos);
				mRadius = Integer.parseInt(radiusStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
			
		});

		// A list of the sports to display as options to check
		availableSports = new ArrayList<String>(PocketPickupApplication.sportsAndObjs.keySet());
		
		// arraylist of initial preferred sports
		preferredSports = new ArrayList<Integer>();
		
		// arraylist to keep the selected items' indexes
		selectedSports = new ArrayList<Integer>();
		
		boolean preferredSportsSet = LoginActivity.user.isPreferredSportsInitialized();
		
		// Make the equivalent CharSequence array of sports that the dialog uses to initialize
		CharSequence[] sports = new CharSequence[availableSports.size()];
		preferred = new boolean[availableSports.size()];
		String sportStr = null;
		for (int i = 0; i < sports.length; i++) {
			sportStr = availableSports.get(i);
			sports[i] = (CharSequence) sportStr;
			
			
			// set initial state of checked options according to user's preferred sports
			if (preferredSportsSet && LoginActivity.user.mPreferredSports.contains(sportStr)) {
				preferred[i] = true;
				selectedSports.add(i);
				preferredSports.add(i);
			} else {
				preferred[i] = false;
			}
		}

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Sports To Search For");
        builder.setMultiChoiceItems(sports, preferred,
        		new DialogInterface.OnMultiChoiceClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int indexSelected,
			         boolean isChecked) {
        		
				if (isChecked) {
					// If the user checked the item, add it to the set of selected items
					if (!selectedSports.contains(indexSelected)) {
						selectedSports.add(indexSelected);
					}
				} else if (selectedSports.contains(indexSelected)) {
					// Else, if the item is already in the set, remove it
					selectedSports.remove(Integer.valueOf(indexSelected));
				}
        	}
        })
		// Set the action buttons
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// When user clicked on Ok
				if (selectedSports.isEmpty()) {
					// user submitted without any checkboxes selected
					// All sports will still be searched for.
					Button button = (Button) findViewById(R.id.search_pref_button);
					button.setText(R.string.all_sports);
				} else if (selectedSports.equals(preferredSports)) { 
					Button button = (Button) findViewById(R.id.search_pref_button);
					button.setText(R.string.preferred_sports);
				} else {
					Button button = (Button) findViewById(R.id.search_pref_button);
					button.setText(R.string.selected_sports);
				}
			}
		});
        
        sportsDialog = builder.create(); //AlertDialog dialog; create like this outside onClick

		// Initialize location text field from passed in location
		Bundle args = getIntent().getExtras();
		EditText editText = (EditText) findViewById(R.id.fg_location_text);
		editText.setText(args.getCharSequence(FINDGAME_LOCATION));

		final double lat = args.getDouble(FINDGAME_LATITUDE);
		final double lon = args.getDouble(FINDGAME_LONGITUDE);
		mLatLng = new LatLng(lat, lon);
	}
	
	private Calendar initDate(long time) {
		Calendar c = Calendar.getInstance();
		if (time != -1L) {
			c.setTimeInMillis(time);
		} else {
			c = null;
		}
		return c;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			savedInstanceState.putLong(STATE_START_TIME, (mStartTime == null) ? 0L : mStartTime.getTimeInMillis());
			savedInstanceState.putLong(STATE_END_TIME,   (mEndTime == null)   ? 0L : mEndTime.getTimeInMillis());
			savedInstanceState.putLong(STATE_START_DATE, (mStartDate == null) ? 0L : mStartDate.getTimeInMillis());
			savedInstanceState.putLong(STATE_END_DATE,   (mEndDate == null)   ? -1L : mEndDate.getTimeInMillis());
		}
	}
	
	private void setButtonLabels() {
		((Button) findViewById(R.id.start_time_button)).setText(getTimeButtonString(mStartTime));
		((Button) findViewById(R.id.end_time_button)).setText(getTimeButtonString(mEndTime));
		((Button) findViewById(R.id.start_date_button)).setText(getDateButtonString(mStartDate));
		((Button) findViewById(R.id.end_date_button)).setText(getDateButtonString(mEndDate));
		
		boolean preferredSportsSet = LoginActivity.user.isPreferredSportsInitialized();
		
		// Set the text correctly on the sports filtering button
		if (preferredSportsSet && !LoginActivity.user.mPreferredSports.isEmpty()) { // Set sports button to 'Preferred Sports'
			Button button = (Button) findViewById(R.id.search_pref_button);
			button.setText(R.string.preferred_sports);
		} else { // Not yet initialized, set button to 'All Sports'
			Button button = (Button) findViewById(R.id.search_pref_button);
			button.setText(R.string.all_sports);
		}
	}
	
	private void setStartState() {
		mStartTime = Calendar.getInstance();
		mStartTime.set(Calendar.HOUR_OF_DAY, 0);
		mStartTime.set(Calendar.MINUTE, 0);
		
		mEndTime = Calendar.getInstance();
		mEndTime.set(Calendar.HOUR_OF_DAY, 23);
		mEndTime.set(Calendar.MINUTE, 59);
		
		Calendar c = Calendar.getInstance();
		long nowDate = c.getTimeInMillis();
		mStartDate = initDate(nowDate);
		mEndDate = null;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_game, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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
		mLastButtonId = v.getId();
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	/**
	 * Displays dialog for selecting game date.
	 * 
	 * @param v		view for dialog fragment
	 */
	public void showDatePickerDialog(View v) {
		mLastButtonId = v.getId();
	    DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    
	    Calendar initDate = (v.getId() == R.id.start_date_button) ? mStartDate : mEndDate;
	    if (initDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_INIT, initDate.getTimeInMillis());
	    	// The minimum will be set to the current day, and overwritten if necessary
	    	args.putLong(DatePickerFragment.STATE_DATE_MIN, Calendar.getInstance().getTimeInMillis());
	    }
	    
	    if (v.getId() == R.id.start_date_button && mEndDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_MAX, mEndDate.getTimeInMillis());
	    } else if (v.getId() == R.id.end_date_button && mStartDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_MIN, mStartDate.getTimeInMillis());
	    }
	    
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	/**
	 * This will reset the form inputs and associated state to default values.
	 * @param v		The view from which the button was called
	 */
	public void resetSearchForms(View v) {
		setStartState();
		
		Spinner spinner = (Spinner) findViewById(R.id.radius_spinner);
		spinner.setSelection(0);
		
		ListView choices = sportsDialog.getListView();
		for (int i = 0; i < choices.getCount(); ++i) {
			if (preferredSports.contains(i)) { // was user's preferred sport
				choices.setItemChecked(i, true);
				preferred[i] = true;
			} else { // user didnt prefer this sport
				choices.setItemChecked(i, false);
				preferred[i] = false;
			}
		}
		selectedSports.clear();
		selectedSports.addAll(preferredSports);
		
		setButtonLabels();
	}
	
	/**
	 * This method is called when the 'Find Game' button is clicked to submit the form and send the search
	 * criteria to the backend.
	 * @param v
	 */
	public void submitSearch(View v) {
		new DialogTask().execute("");
	}
	
	public void showSportsPreferencesDialog(View v) {
		sportsDialog.show();
	}

	public void setLocation(View v) {
		//Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		final Button dateButton = (Button) findViewById(mLastButtonId);
		
		if (mLastButtonId == R.id.start_date_button) {
			if (mStartDate == null) {
				mStartDate = Calendar.getInstance();
			}
			mStartDate.set(year, monthOfYear, dayOfMonth);
			dateButton.setText(getDateButtonString(mStartDate));
		} else { // R.id.end_date_button
			if (mEndDate == null) {
				mEndDate = Calendar.getInstance();
			}
			mEndDate.set(year,  monthOfYear, dayOfMonth);
			dateButton.setText(getDateButtonString(mEndDate));
		}
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		final Button timeButton = (Button) findViewById(mLastButtonId);
		
		if (timeButton.getId() == R.id.start_time_button) {
			if (mStartTime == null) {
				mStartTime = Calendar.getInstance();
			}
			mStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			mStartTime.set(Calendar.MINUTE, minute);
			timeButton.setText(getTimeButtonString(mStartTime));
		} else { // R.id.end_time_button
			if (mEndTime == null) {
				mEndTime = Calendar.getInstance();
			}
			mEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			mEndTime.set(Calendar.MINUTE, minute);
			timeButton.setText(getTimeButtonString(mEndTime));
		}
		// default ID doesn't matter
	}
	
	private String getDateButtonString(final Calendar date) {
		if (date == null) {
			return getResources().getString(R.string.select_end_date);
		} else {
			return DateFormat.getDateFormat(this).format(date.getTime());
		}
	}
	
	private String getTimeButtonString(final Calendar date) {
		return DateFormat.getTimeFormat(this).format(date.getTime());
	}
	
	/**
	 * This task will take show a progress dialog while the games are found.
	 * After it is complete, it will dismiss the dialog and go back to the 
	 * MainActivity.
	 */
	private class DialogTask extends AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(FindGameActivity.this, "", "Loading...", true);
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			// Create FindGameCriteria object and send
			// create a long representing the date or time only by doing some simple arithmetic
			long startDateAndTime = mStartTime.getTimeInMillis();
			long startTime = (startDateAndTime + PocketPickupApplication.GMT_OFFSET*HOUR)% MS_IN_DAY; 
			// chop off anything after minutes
			startTime = (startTime / MIN_IN_MILLIS) * MIN_IN_MILLIS;
			long endTime = startTime + (mEndTime.getTimeInMillis() - startDateAndTime);
			// chop off anything after minutes
			endTime = (endTime / MIN_IN_MILLIS) * MIN_IN_MILLIS;
			long startDate = ((mStartDate.getTimeInMillis() / MS_IN_DAY) )* MS_IN_DAY;
			startDate -= PocketPickupApplication.GMT_OFFSET*HOUR;
			long endDate = -1;
			if (mEndDate != null) {
				endDate = ((mEndDate.getTimeInMillis() / MS_IN_DAY) )* MS_IN_DAY;
				endDate -= PocketPickupApplication.GMT_OFFSET*HOUR;	
			}
			ArrayList<String> gameTypes = new ArrayList<String>();
			// Add all of the sports that the user selected to search for.
			// If the set is empty, then the user didnt use the dialog so
			// add all available sports so they can be search for.
			Set<String> mSports = new HashSet<String>();
			if (selectedSports.isEmpty()) {
				// No sports were selected in the dialog, add all available ones
				mSports.addAll(availableSports);
			} else {
				// At least one sport was selected, add only those
				for (int i = 0; i < selectedSports.size(); i++) {
					int selectedIndex = selectedSports.get(i);
					mSports.add(availableSports.get(selectedIndex));
				}
			}
			gameTypes.addAll(mSports);
			FindGameCriteria criteria = new FindGameCriteria(mRadius, mLatLng, startDate, endDate, startTime, endTime, gameTypes);
			final ArrayList<Game> searchResults = new ArrayList<Game>();
			searchResults.addAll(GameHandler.findGame(criteria));
			Intent returnIntent = new Intent();
			returnIntent.putExtra(FINDGAME_RADIUS, mRadius);
			returnIntent.putExtra(FINDGAME_LATITUDE, mLatLng.latitude);
			returnIntent.putExtra(FINDGAME_LONGITUDE, mLatLng.longitude);
			returnIntent.putParcelableArrayListExtra(FINDGAME_RESULTS, searchResults);
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
