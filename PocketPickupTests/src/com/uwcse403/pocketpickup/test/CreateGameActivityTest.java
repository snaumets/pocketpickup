package com.uwcse403.pocketpickup.test;

import java.util.ArrayList;
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
import com.uwcse403.pocketpickup.CreateGameActivity;
import com.uwcse403.pocketpickup.PocketPickupApplication;

public class CreateGameActivityTest 
extends ActivityInstrumentationTestCase2<CreateGameActivity> {
	private Activity        mActivity;
	private Instrumentation mInstrumentation;
	
	// UI Components
	private EditText mLocationText;
	private Spinner  mSportsSpinner;
	private Button   mDateButton;
	private Button   mTimeButton;
	private Spinner  mDurationSpinner;
	private EditText mDetailsText;
	private Button   mResetButton;
	private Button   mSubmitButton;
	
	// Mock Default Inputs
	private CharSequence mDefLocationText = "LOCATION";
	private double       mDefLatitude  = 10.0;
	private double       mDefLongitude = 15.0;
	
	public CreateGameActivityTest() {
		super(CreateGameActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Intent mockIntent = new Intent();
		Bundle args = new Bundle();
		args.putCharSequence(CreateGameActivity.CREATEGAME_LOCATION, mDefLocationText);
		args.putDouble(CreateGameActivity.CREATEGAME_LATITUDE, mDefLatitude);
		args.putDouble(CreateGameActivity.CREATEGAME_LONGITUDE, mDefLongitude);
		mockIntent.putExtras(args);
		
		setActivityIntent(mockIntent);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
		
		mLocationText    = (EditText) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.cg_location_text);
		mSportsSpinner   = (Spinner) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.cg_sports_spinner);
		mDateButton      = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.create_date_button);
		mTimeButton      = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.create_time_button);
		mDurationSpinner = (Spinner) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.cg_duration_spinner);
		mDetailsText     = (EditText) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.cg_details);
		mResetButton     = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.cg_reset);
		mSubmitButton    = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.cg_submit);
	}
	
	/**
	 * Tests that all components are initialized and that
	 * the values passed into the activity are correct
	 */
	public void testActivityInitializationAndDefaults() {
		// Initialization
		assertNotNull(mLocationText);
		assertNotNull(mSportsSpinner);
		assertNotNull(mDateButton);
		assertNotNull(mTimeButton);
		assertNotNull(mDurationSpinner);
		assertNotNull(mDetailsText);
		assertNotNull(mResetButton);
		assertNotNull(mSubmitButton);

		// Default values
		assertEquals(mDefLocationText, mLocationText.getText().toString());
	}
	
	/**
	 * Tests that the Sports selection spinner choices are
	 * equal to the choices in the Application's sports BiMap
	 */
	public void testSportsSpinnerInitialization() {
		ArrayList<String> sports = new ArrayList<String>(
				PocketPickupApplication.sportsAndObjs.keySet());
		
		assertEquals(sports.size(), mSportsSpinner.getCount());
		for (int i = 0; i < sports.size(); ++i) {
			assertEquals(sports.get(i), (String)mSportsSpinner.getItemAtPosition(i));
		}
	}
	
	/**
	 * Tests that pressing the Date button opens the date picker
	 * dialog
	 */
	public void testDateButtonOpensDialog() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
	    mInstrumentation.waitForIdleSync();
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    assertTrue(solo.waitForDialogToOpen());	    
	    solo.goBack();
	}
	
	/**
	 * Tests that invalid times are rejected
	 */
	public void testTimeButtonOpensDialog() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();

	    assertTrue(solo.waitForDialogToOpen());	    
	    solo.goBack();
	}
	
	/**
	 * Tests that the spinner choices are correctly
	 * populated
	 */
	public void testDurationSpinnerPopulated() {
		ArrayAdapter<CharSequence> expected = ArrayAdapter.createFromResource(
				mActivity, 
				com.uwcse403.pocketpickup.R.array.duration_choices, 
				android.R.layout.simple_spinner_item);
		
		SpinnerAdapter actual = mDurationSpinner.getAdapter();
			
		assertEquals(expected.getCount(), actual.getCount());
		for (int i = 0; i < expected.getCount(); ++i) {
			assertEquals(expected.getItem(i), actual.getItem(i));
		}
	}
	
	/**
	 * Tests that the reset button clears all selections
	 */
	public void testResetButtonClearsAll() {
		Solo solo = new Solo(mInstrumentation, mActivity);
		
	    // Set time
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mTimeButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    solo.waitForDialogToOpen();
	    solo.setTimePicker(0, Calendar.getInstance().get(Calendar.HOUR), 0);
	    solo.clickOnText("Set");
		
		// Set date
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mDateButton.performClick();
	        }
	      });	    
	    mInstrumentation.waitForIdleSync();
	    
	    solo.waitForDialogToOpen();
	    solo.setDatePicker(0, Calendar.getInstance().get(Calendar.YEAR) + 1, 1, 1);
	    solo.clickOnText("Set");
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	    	  mSportsSpinner.setSelection(2);
	    	  mDurationSpinner.setSelection(2);
	    	  mDetailsText.setText("RESET TEST");
	          mResetButton.performClick();
	        }
	      });
	    mInstrumentation.waitForIdleSync();
	    
	    assertFalse(solo.searchText("" + Calendar.getInstance().get(Calendar.YEAR)+ 1));
	    assertFalse(solo.searchText(Calendar.getInstance().get(Calendar.HOUR) + ":00 AM"));
	    assertEquals(0, mDurationSpinner.getSelectedItemPosition());
	    assertEquals("", mDetailsText.getText().toString());
	}
}
