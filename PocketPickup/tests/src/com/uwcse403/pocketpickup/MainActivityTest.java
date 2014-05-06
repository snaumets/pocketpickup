package com.uwcse403.pocketpickup;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	private Activity mActivity;

	public MainActivityTest() {
		super("com.uwcse403.pocketpickup", MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
	    super.setUp();

	    setActivityInitialTouchMode(false);

	    mActivity = getActivity();
	} // end of setUp() method definition

	public void testFail() {
		assertTrue(false);
	}
}
