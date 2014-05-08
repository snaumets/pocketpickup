package com.uwcse403.pocketpickup;

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

import com.uwcse403.pocketpickup.fragments.DatePickerFragment;
import com.uwcse403.pocketpickup.fragments.TimePickerFragment;

public class FindGameActivity extends Activity
							  implements DatePickerDialog.OnDateSetListener, 
							   		     TimePickerDialog.OnTimeSetListener {
	
	private static final String STATE_START_TIME = "fg_start_time";
	private static final String STATE_END_TIME   = "fg_end_time";
	private static final String STATE_START_DATE = "fg_start_date";
	private static final String STATE_END_DATE   = "fg_end_date";
	private int mLastButtonId;
	
	/* TODO: These fields will be collected into a SearchCriteria object
	 *       when the Parse team builds it
	 */
	private String mStartTimeLabel; 
	private String mEndTimeLabel;
	private String mStartDateLabel;
	private String mEndDateLabel;
	
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
			mStartTimeLabel = savedInstanceState.getString(STATE_START_TIME); 
			mEndTimeLabel   = savedInstanceState.getString(STATE_END_TIME);
			mStartDateLabel = savedInstanceState.getString(STATE_START_DATE);
			mEndDateLabel   = savedInstanceState.getString(STATE_END_DATE);
		} else {
			mStartTimeLabel = getResources().getString(R.string.select_time_start);
			mEndTimeLabel   = getResources().getString(R.string.select_time_end);
			mStartDateLabel = getResources().getString(R.string.select_date_start);
			mEndDateLabel   = getResources().getString(R.string.select_date_end);
		}
		setButtonLabels();
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putString(STATE_START_TIME, mStartTimeLabel);
		savedInstanceState.putString(STATE_END_TIME,   mEndTimeLabel);
		savedInstanceState.putString(STATE_START_DATE, mStartDateLabel);
		savedInstanceState.putString(STATE_END_DATE,   mEndDateLabel);
	}
	
	private void setButtonLabels() {
		if (mStartTimeLabel != null) {
			((Button)findViewById(R.id.start_time_button)).setText(mStartTimeLabel);
			((Button)findViewById(R.id.end_time_button)).setText(mEndTimeLabel);
			((Button)findViewById(R.id.start_date_button)).setText(mStartDateLabel);
			((Button)findViewById(R.id.end_date_button)).setText(mEndDateLabel);
		}
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
	    newFragment.show(getFragmentManager(), "datePicker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		final Button dateButton = (Button)findViewById(mLastButtonId);
		String dateString = getFormattedDate(year, monthOfYear, dayOfMonth);
		
		if (dateButton.getId() == R.id.start_date_button) {
			mStartDateLabel = dateString;
		} else { // R.id.end_date_button
			mEndDateLabel = dateString;
		}
		dateButton.setText(dateString);
	}
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		final Button timeButton = (Button)findViewById(mLastButtonId);
		String timeString = getFormattedTime(hourOfDay, minute);
		
		if (timeButton.getId() == R.id.start_time_button) {
			mStartTimeLabel = timeString;
		} else { // R.id.end_time_button
			mEndTimeLabel = timeString;
		}
		timeButton.setText(timeString);
	}
	
	/**
	 * Formats hours and minute into date string
	 * @param hourOfDay		Hour for date string in range (1-24)
	 * @param minute        Minute for date string
	 * @return	Returns time string in default format of this phone.
	 */
	private String getFormattedTime(final int hourOfDay, final int minute) {
		final GregorianCalendar cal = new GregorianCalendar(0, 0, 0, hourOfDay, minute);
		return DateFormat.getTimeFormat(this).format(cal.getTime());
	}
	
	/**
	 * Formats year, month, and day into date string
	 * @param year			Year for date string
	 * @param monthOfYear   Month for date string in range (1-12)
	 * @param dayOfMonth    Day for date string in range (1-31)
	 * @return  Returns date string in default format of this phone.
	 */
	private String getFormattedDate(final int year, final int monthOfYear, final int dayOfMonth) {
	    final GregorianCalendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
	    return DateFormat.getDateFormat(this).format(cal.getTime());
	}
}
