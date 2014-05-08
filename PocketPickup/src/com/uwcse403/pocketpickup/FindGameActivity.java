package com.uwcse403.pocketpickup;

import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.uwcse403.pocketpickup.fragments.DatePickerFragment;
import com.uwcse403.pocketpickup.fragments.TimePickerFragment;

public class FindGameActivity extends Activity
							  implements DatePickerDialog.OnDateSetListener, 
							   		     TimePickerDialog.OnTimeSetListener {
	
	// Bundle IDs for persistent button names
	private static final String STATE_START_TIME = "fg_start_time";
	private static final String STATE_END_TIME   = "fg_end_time";
	private static final String STATE_START_DATE = "fg_start_date";
	private static final String STATE_END_DATE   = "fg_end_date";
	
	// ID for identifying the last pressed button
	private int mLastButtonId;
	
	/* TODO: These fields will be collected into a SearchCriteria object
	 *       when the Parse team builds it
	 */
	private Date mStartTime; 
	private Date mEndTime;
	private Date mStartDate;
	private Date mEndDate;
	
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
			mStartTime = new Date(savedInstanceState.getLong(STATE_START_TIME)); 
			mEndTime   = new Date(savedInstanceState.getLong(STATE_END_TIME));
			mStartDate = new Date(savedInstanceState.getLong(STATE_START_DATE));
			mEndDate   = new Date(savedInstanceState.getLong(STATE_END_DATE));
		}
		setButtonLabels();
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putLong(STATE_START_TIME, mStartTime.getTime());
		savedInstanceState.putLong(STATE_END_TIME,   mEndTime.getTime());
		savedInstanceState.putLong(STATE_START_DATE, mStartDate.getTime());
		savedInstanceState.putLong(STATE_END_DATE,   mEndDate.getTime());
	}
	
	private void setButtonLabels() {
		((Button)findViewById(R.id.start_time_button)).setText(getTimeButtonString(mStartTime));
		((Button)findViewById(R.id.end_time_button)).setText(getTimeButtonString(mEndTime));
		((Button)findViewById(R.id.start_date_button)).setText(getDateButtonString(mStartDate));
		((Button)findViewById(R.id.end_date_button)).setText(getDateButtonString(mEndDate));
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
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
	    
	    Date initDate = (v.getId() == R.id.start_date_button) ? mStartDate : mEndDate;
	    if (initDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_INIT, initDate.getTime());
	    }
	    
	    if (v.getId() == R.id.start_date_button && mEndDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_MAX, mEndDate.getTime());
	    } else if (v.getId() == R.id.end_date_button && mStartDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_MIN, mStartDate.getTime());
	    }
	    
	    newFragment.setArguments(args);
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	public void submitSearch(View v) {
		/* validate */
		
		/* Create FindGameCriteria object and send */
		Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
	}
	
	public void showSportsPreferencesDialog(View v) {
		Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		final Button dateButton = (Button)findViewById(mLastButtonId);
		Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
		
		if (dateButton.getId() == R.id.start_date_button) {
			mStartDate = date;
		} else { // R.id.end_date_button
			mEndDate = date;
		}
		// default ID doesn't matter
		dateButton.setText(getDateButtonString(date));
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		final Button timeButton = (Button)findViewById(mLastButtonId);
		Date time = new GregorianCalendar(0, 0, 0, hourOfDay, minute).getTime();
		
		if (timeButton.getId() == R.id.start_time_button) {
			mStartTime = time;
		} else { // R.id.end_time_button
			mEndTime = time;
		}
		// default ID doesn't matter
		timeButton.setText(getTimeButtonString(time));
	}
	
	private String getDateButtonString(final Date date) {
		if (date == null) {
			return getResources().getString(R.string.select_date);
		} else {
			return DateFormat.getDateFormat(this).format(date);
		}
	}
	
	private String getTimeButtonString(final Date date) {
		if (date == null) {
			return getResources().getString(R.string.select_time);
		} else {
			return DateFormat.getTimeFormat(this).format(date);
		}
	}
}
