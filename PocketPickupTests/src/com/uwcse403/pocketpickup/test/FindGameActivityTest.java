package com.uwcse403.pocketpickup.test;

import java.util.Calendar;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.robotium.solo.Solo;
import com.uwcse403.pocketpickup.FindGameActivity;

public class FindGameActivityTest 
extends ActivityInstrumentationTestCase2<FindGameActivity> {
	private Activity        mActivity;
	private Instrumentation mInstrumentation;
	
	// UI Components
	private EditText mLocationText;
	private Button   mStartTimeButton;
	private Button   mEndTimeButton;
	private Button   mStartDateButton;
	private Button   mEndDateButton;
	private Spinner  mRadiusSpinner;
	private Button   mSportsButton;
	private Button   mResetButton;
	private Button   mSubmitButton;
	
	// Default Values
	private CharSequence mDefLocationText;
	private double       mDefLatitude;
	private double       mDefLongitude;
	
	public FindGameActivityTest() {
		super(FindGameActivity.class);
	}
	
	@Override
	protected void setUp() {
		mDefLocationText = "FG LOCATION";
		mDefLatitude  = 50.0;
		mDefLongitude = 60.0;
		
		Intent mockIntent = new Intent();
		Bundle args = new Bundle();
		args.putCharSequence(FindGameActivity.FINDGAME_LOCATION, mDefLocationText);
		args.putDouble(FindGameActivity.FINDGAME_LATITUDE, mDefLatitude);
		args.putDouble(FindGameActivity.FINDGAME_LONGITUDE, mDefLongitude);
		mockIntent.putExtras(args);
		
		setActivityIntent(mockIntent);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
		
		mLocationText    = (EditText) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.fg_location_text);
		mStartTimeButton = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.start_time_button);
		mEndTimeButton   = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.end_time_button);
		mStartDateButton = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.start_date_button);
		mEndDateButton   = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.end_date_button);
		mRadiusSpinner   = (Spinner) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.radius_spinner);
		mSportsButton    = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.search_pref_button);
		mResetButton     = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.search_reset_button);
		mSubmitButton    = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.find_game_submit_button);
	}
	
	/**
	 * Tests that the activity components are properly initialized
	 * and all default values are correct
	 */
	public void testActivityInitializationAndDefaults() {
		assertNotNull(mLocationText);
		assertNotNull(mStartTimeButton);
		assertNotNull(mEndTimeButton);
		assertNotNull(mStartDateButton);
		assertNotNull(mEndDateButton);
		assertNotNull(mRadiusSpinner);
		assertNotNull(mSportsButton);
		assertNotNull(mResetButton);
		assertNotNull(mSubmitButton);
		
		assertEquals(mDefLocationText, mLocationText.getText().toString());
		assertEquals(mActivity.getResources()
				.getString(com.uwcse403.pocketpickup.R.string.select_time),
				     mStartTimeButton.getText());
		assertEquals(mActivity.getResources()
				.getString(com.uwcse403.pocketpickup.R.string.select_time),
	                 mEndTimeButton.getText());
		assertEquals(mActivity.getResources()
				.getString(com.uwcse403.pocketpickup.R.string.select_date),
					 mStartDateButton.getText());
		assertEquals(mActivity.getResources()
				.getString(com.uwcse403.pocketpickup.R.string.select_date),
					 mEndDateButton.getText());
		
		assertEquals(0, mRadiusSpinner.getSelectedItemPosition());
	}
	
	/**
	 * Tests that the Start Time button opens the time dialog
	 */
	public void testStartTimeButtonShowsDialog() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
		mInstrumentation.waitForIdleSync();
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mStartTimeButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    assertTrue(solo.waitForDialogToOpen());
	    
	    solo.goBack();
	}
	
	/**
	 * Tests that the End Time button opens the time dialog
	 */
	public void testEndTimeButtonShowsDialog() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mEndTimeButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	   
	    assertTrue(solo.waitForDialogToOpen());

	    solo.goBack();
	}
	
	/**
	 * Tests that the Start Date button opens the date dialog
	 */
	public void testStartDateButtonShowsDialog() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mStartDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    assertTrue(solo.waitForDialogToOpen());

	    solo.goBack();
	}
	
	/**
	 * Tests that the End Date button opens the date dialog
	 */
	public void testEndDateButtonShowsDialog() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mEndDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();

	    assertTrue(solo.waitForDialogToOpen());
	    solo.goBack();
	}
	
	/**
	 * Tests that the radius spinner has all the proper
	 * choices
	 */
	public void testRadiusSpinnerIsPopulated() {
		ArrayAdapter<CharSequence> expected = ArrayAdapter.createFromResource(
				mActivity, 
				com.uwcse403.pocketpickup.R.array.radius_choices, 
				android.R.layout.simple_spinner_item);
		
		SpinnerAdapter actual = mRadiusSpinner.getAdapter();
		
		assertEquals(expected.getCount(), actual.getCount());
		for (int i = 0; i < expected.getCount(); ++i) {
			assertEquals(expected.getItem(i), actual.getItem(i));
		}
	}
	
	/**
	 * Tests that the reset button clears all form components
	 * to the default values
	 */
	public void testResetButtonClearsForm() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		setTimeDateButtons(solo);
	    		
	    mActivity.runOnUiThread(new Runnable() {
	    	public void run() {
	    		mRadiusSpinner.setSelection(1);
	    	}
	    });
	    mInstrumentation.waitForIdleSync();
		
		// Issue reset
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mResetButton.performClick();
	        }
	      });
	    mInstrumentation.waitForIdleSync();
	    
	    assertEquals(0, mRadiusSpinner.getSelectedItemPosition());
		assertTrue(mStartTimeButton.getText().equals(
				mActivity.getResources().getString(com.uwcse403.pocketpickup.R.string.select_time)));
		assertTrue(mEndTimeButton.getText().equals(
				mActivity.getResources().getString(com.uwcse403.pocketpickup.R.string.select_time)));
		assertTrue(mStartDateButton.getText().equals(
				mActivity.getResources().getString(com.uwcse403.pocketpickup.R.string.select_date)));
		assertTrue(mEndDateButton.getText().equals(
				mActivity.getResources().getString(com.uwcse403.pocketpickup.R.string.select_date)));
	}
	
	private void setTimeDateButtons(Solo solo) {
		Calendar c = Calendar.getInstance();
		
		// Set start time
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mStartTimeButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    solo.waitForDialogToOpen();
	    solo.setTimePicker(0, 1, 0);
	    solo.clickOnText("Set");
	    
	    // Set end time
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mEndTimeButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    solo.waitForDialogToOpen();
	    solo.setTimePicker(0, 3, 0);
	    solo.clickOnText("Set");
		
		
		// Set start date
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mStartDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    solo.waitForDialogToOpen();
	    solo.setDatePicker(0, c.get(Calendar.YEAR) + 1, 1, 1);
	    solo.clickOnText("Set");
	    
	    // Set end date
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mEndDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    solo.waitForDialogToOpen();
	    solo.setDatePicker(0, c.get(Calendar.YEAR) + 2, 1, 1);
	    solo.clickOnText("Set");
	}
}
