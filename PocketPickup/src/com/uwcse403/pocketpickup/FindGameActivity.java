package com.uwcse403.pocketpickup;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.fragments.DatePickerFragment;
import com.uwcse403.pocketpickup.fragments.TimePickerFragment;
import com.uwcse403.pocketpickup.game.FindGameCriteria;

public class FindGameActivity extends Activity
							  implements DatePickerDialog.OnDateSetListener, 
							   		     TimePickerDialog.OnTimeSetListener {
	// Argument IDs
	public static final String FINDGAME_LOCATION  = "findgame_location";
	public static final String FINDGAME_LATITUDE  = "findgame_latitude";
	public static final String FINDGAME_LONGITUDE = "findgame_longitude";
	
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
		}
		setButtonLabels();
		
		// Initialize radius choices
		Spinner radiusSpinner = (Spinner)findViewById(R.id.radius_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.radius_choices, 
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		radiusSpinner.setAdapter(adapter);
		radiusSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String radiusStr = (String)parent.getItemAtPosition(pos);
				mRadius = Integer.parseInt(radiusStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
			
		});
		
		// Initialize location text field from passed in location
		Bundle args = getIntent().getExtras();
		EditText editText = (EditText)findViewById(R.id.fg_location_text);
		editText.setText(args.getCharSequence(FINDGAME_LOCATION));

		final double lat = args.getDouble(FINDGAME_LATITUDE);
		final double lon = args.getDouble(FINDGAME_LONGITUDE);
		mLatLng = new LatLng(lat, lon);
	}
	
	private Calendar initDate(long time) {
		Calendar c = Calendar.getInstance();
		if (time != 0L) {
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
			savedInstanceState.putLong(STATE_START_TIME, ( (mStartTime == null) ? 0L : mStartTime.getTimeInMillis() ));
			savedInstanceState.putLong(STATE_END_TIME,   ( (mEndTime == null)   ? 0L : mEndTime.getTimeInMillis() ));
			savedInstanceState.putLong(STATE_START_DATE, ( (mStartDate == null) ? 0L : mStartDate.getTimeInMillis() ));
			savedInstanceState.putLong(STATE_END_DATE,   ( (mEndDate == null)   ? 0L : mEndDate.getTimeInMillis() ));
		}
	}
	
	private void setButtonLabels() {
		((Button)findViewById(R.id.start_time_button)).setText(getTimeButtonString(mStartTime));
		((Button)findViewById(R.id.end_time_button)).setText(getTimeButtonString(mEndTime));
		((Button)findViewById(R.id.start_date_button)).setText(getDateButtonString(mStartDate));
		((Button)findViewById(R.id.end_date_button)).setText(getDateButtonString(mEndDate));
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Displays dialog for selecting game time
	 * 
	 * @param v		view for dialog fragment
	 */
	public void showTimePickerDialog(View v) {
		mLastButtonId = v.getId();
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	/**
	 * Displays dialog for selecting game date
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
	    }
	    
	    if (v.getId() == R.id.start_date_button && mEndDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_MAX, mEndDate.getTimeInMillis());
	    } else if (v.getId() == R.id.end_date_button && mStartDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_MIN, mStartDate.getTimeInMillis());
	    }
	    
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void resetSearchForms(View v) {
		mStartDate = null;
		mStartTime = null;
		mEndDate = null;
		mEndTime = null;
		
		Spinner spinner = (Spinner)findViewById(R.id.radius_spinner);
		spinner.setSelection(0);
		
		setButtonLabels();
	}
	
	/**
	 * This method is called when the 'Find Game' button is clicked to submit the form and send the search
	 * criteria to the backend.
	 * @param v
	 */
	public void submitSearch(View v) {
		/* validate */
		
		/* Create FindGameCriteria object and send */
		long msInDay = 1000 * 60 * 60 * 24; // 1000 ms/s * (60 s/min) * (60 min/hour) * (24 hr/day)
		// create a long representing the date or time only by doing some simple arithmetic
		long startDate = mStartDate != null ? mStartDate.getTimeInMillis() / msInDay * msInDay : 0;
		long endDate = mEndDate != null ? mEndDate.getTimeInMillis() / msInDay * msInDay : 0;
		long startTime = mStartTime != null ? mStartTime.getTimeInMillis() % msInDay : 0;
		long endTime = mEndTime != null ? mEndTime.getTimeInMillis() % msInDay : 0;
		
		FindGameCriteria criteria = new FindGameCriteria(mRadius, mLatLng, startDate, endDate, startTime, endTime, "");
		// pass this criteria object to the game handler
		
		setResult(Activity.RESULT_OK);
		finish();
		//Toast.makeText(this, "r: " + mRadius, Toast.LENGTH_LONG).show();
	}
	
	public void showSportsPreferencesDialog(View v) {
		Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
	}

	public void setLocation(View v) {
		Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		final Button dateButton = (Button)findViewById(mLastButtonId);
		
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
		final Button timeButton = (Button)findViewById(mLastButtonId);
		
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
}
