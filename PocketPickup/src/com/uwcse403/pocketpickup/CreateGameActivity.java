package com.uwcse403.pocketpickup;

import java.util.Calendar;

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

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.fragments.DatePickerFragment;
import com.uwcse403.pocketpickup.fragments.TimePickerFragment;

public class CreateGameActivity extends Activity
								implements DatePickerDialog.OnDateSetListener, 
					   		     		   TimePickerDialog.OnTimeSetListener {
	public static final String CREATE_GAME_TIME = "cg_game_time";
	public static final String CREATEGAME_LOCATION  = "creategame_location";
	public static final String CREATEGAME_LATITUDE  = "creategame_latitude";
	public static final String CREATEGAME_LONGITUDE = "creategame_longitude";

	// Bundle IDs for persistent button names
	private static final String STATE_GAME_TIME = "cg_time";
	
	private Calendar mDate;
	private LatLng   mLatLng;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_game);
		
		long gameTime = (savedInstanceState == null) ? 0L : savedInstanceState.getLong(CREATE_GAME_TIME);
		mDate = initDate(gameTime);
		
		final Bundle args = getIntent().getExtras();
		final double lat = args.getDouble(CREATEGAME_LATITUDE);
		final double lon = args.getDouble(CREATEGAME_LONGITUDE);
		mLatLng = new LatLng(lat, lon);
		
		setButtonLabels();
	}
	
	private Calendar initDate(long time) {
		Calendar c = Calendar.getInstance();
		if (time != 0L) {
			c.setTimeInMillis(time);
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
			savedInstanceState.putLong(STATE_GAME_TIME, ( (mDate == null) ? 0L : mDate.getTimeInMillis() ));
		}
	}
	
	private void setButtonLabels() {
		((Button)findViewById(R.id.create_time_button)).setText(getTimeButtonString(mDate));
		((Button)findViewById(R.id.create_date_button)).setText(getDateButtonString(mDate));
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
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	/**
	 * Displays dialog for selecting game date
	 * 
	 * @param v		view for dialog fragment
	 */
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    Bundle args = new Bundle();
	    
	    if (mDate != null) {
	    	args.putLong(DatePickerFragment.STATE_DATE_INIT, mDate.getTimeInMillis());
	    	args.putLong(DatePickerFragment.STATE_DATE_MIN, mDate.getTimeInMillis());
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
		((Button)findViewById(R.id.create_time_button)).setText(getTimeButtonString(mDate));
	}

	@Override
	public void onDateSet(DatePicker arg0, int year, int monthOfYear, int dayOfMonth) {
		if (mDate == null) {
			mDate = Calendar.getInstance();
		}
		mDate.set(year, monthOfYear, dayOfMonth);
		((Button)findViewById(R.id.create_date_button)).setText(getDateButtonString(mDate));
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
