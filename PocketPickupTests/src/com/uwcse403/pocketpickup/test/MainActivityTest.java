package com.uwcse403.pocketpickup.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;

import com.parse.ParseUser;
import com.uwcse403.pocketpickup.CreateGameActivity;
import com.uwcse403.pocketpickup.FindGameActivity;
import com.uwcse403.pocketpickup.HelpActivity;
import com.uwcse403.pocketpickup.LoginActivity;
import com.uwcse403.pocketpickup.MainActivity;
import com.uwcse403.pocketpickup.SettingsActivity;
import com.uwcse403.pocketpickup.info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private MainActivity mActivity;
	private Button mFindGameButton;
	private Button mCreateGameButton;

	private Instrumentation mInstrumentation;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
		
		mCreateGameButton = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.create_button);
		mFindGameButton  = (Button) mActivity.findViewById(
				com.uwcse403.pocketpickup.R.id.findgame_button);
	}

	public void testActivityInitialized() {
		assertTrue(mActivity != null);
		assertTrue(mCreateGameButton != null);
		assertTrue(mFindGameButton != null);
		
		assertNotNull(ParseUser.getCurrentUser());
	}
	
	
	public void testMapZoomsOnStart() {
		// TODO this
	}
	
	public void testMapOnlyZoomsOnStart() {
		// TODO this
	}
	
	public void testAddressFieldUpdates() {
		// TODO UI automator
	}
	
	public void testFindGameButton() {	
		// spin wait for map to load
		while(!mFindGameButton.isEnabled());
	
	    Instrumentation.ActivityMonitor activityMonitor = 
	    		mInstrumentation.addMonitor(FindGameActivity.class.getName(), 
	    				null , false);
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mFindGameButton.performClick();
	        }
	      });
	    
	    mInstrumentation.waitForIdleSync();
	    FindGameActivity fgActivity = (FindGameActivity) 
	    		mInstrumentation.waitForMonitorWithTimeout(activityMonitor, 5);
	    
	    assertNotNull(fgActivity);
	    fgActivity.finish();
	    
	    mInstrumentation.waitForIdleSync();
	}
	
	public void testFindGameButtonOneResult() {
		
	}
	
	public void testFindGameButtonManyResults() {
		
	}
	
	public void testCreateGameButton() {
		// spin wait for map to load
		while(!mCreateGameButton.isEnabled());
	
	    Instrumentation.ActivityMonitor activityMonitor = 
	    		mInstrumentation.addMonitor(CreateGameActivity.class.getName(), 
	    				null , false);
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	          mCreateGameButton.performClick();
	        }
	      });
	    
	    mInstrumentation.waitForIdleSync();
	    CreateGameActivity cgActivity = (CreateGameActivity) 
	    		mInstrumentation.waitForMonitorWithTimeout(activityMonitor, 5);
	    
	    assertNotNull(cgActivity);
	    cgActivity.finish();
	    
	    mInstrumentation.waitForIdleSync();
	}
	
	public void testCreateGameButtonResult() {
		
	}
	
	public void testBackButtonSuspendsApp() {

	}
	
	public void testOrientationChangeSavesState() {
		
	}
	
	public void testSettingSlideMenuOption() {
	    Instrumentation.ActivityMonitor activityMonitor = 
	    		mInstrumentation.addMonitor(SettingsActivity.class.getName(), 
	    				null , false);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	    		ListView drawerList = (ListView) mActivity.findViewById(
	    				com.uwcse403.pocketpickup.R.id.list_slidermenu);
	    		NavDrawerListAdapter adapter = (NavDrawerListAdapter) drawerList.getAdapter();
	    		int pos = 0;
	    		drawerList.performItemClick(
	    				adapter.getView(pos, null, null), pos, adapter.getItemId(pos));
	        }
	      });
	    mInstrumentation.waitForIdleSync();
	    
	    SettingsActivity settingActivity = (SettingsActivity) 
	    		mInstrumentation.waitForMonitorWithTimeout(activityMonitor, 5);
	    
	    assertNotNull(settingActivity);
	    settingActivity.finish();
	    
	    mInstrumentation.waitForIdleSync();
	}
	
	public void testHelpSlideMenuOption() {
	    Instrumentation.ActivityMonitor activityMonitor = 
	    		mInstrumentation.addMonitor(HelpActivity.class.getName(), 
	    				null , false);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	    		ListView drawerList = (ListView) mActivity.findViewById(
	    				com.uwcse403.pocketpickup.R.id.list_slidermenu);
	    		NavDrawerListAdapter adapter = (NavDrawerListAdapter) drawerList.getAdapter();
	    		int pos = 1;
	    		drawerList.performItemClick(
	    				adapter.getView(pos, null, null), pos, adapter.getItemId(pos));
	        }
	      });
	    mInstrumentation.waitForIdleSync();
	    
	    HelpActivity helpActivity = (HelpActivity) 
	    		mInstrumentation.waitForMonitorWithTimeout(activityMonitor, 5);
	    
	    assertNotNull(helpActivity);
	    helpActivity.finish();
	    
	    mInstrumentation.waitForIdleSync();
	}
	
	public void testLogout() {
	    Instrumentation.ActivityMonitor activityMonitor = 
	    		mInstrumentation.addMonitor(LoginActivity.class.getName(), 
	    				null , false);
		
	    mActivity.runOnUiThread(new Runnable() {
	        public void run() {
	    		ListView drawerList = (ListView) mActivity.findViewById(
	    				com.uwcse403.pocketpickup.R.id.list_slidermenu);
	    		NavDrawerListAdapter adapter = (NavDrawerListAdapter) drawerList.getAdapter();
	    		int pos = 2;
	    		drawerList.performItemClick(
	    				adapter.getView(pos, null, null), pos, adapter.getItemId(pos));
	        }
	      });
	    mInstrumentation.waitForIdleSync();
	    
	    LoginActivity loginActivity = (LoginActivity) 
	    		mInstrumentation.waitForMonitorWithTimeout(activityMonitor, 5);
	    
	    assertNull(ParseUser.getCurrentUser());
	    loginActivity.finish();
	    
	    mInstrumentation.waitForIdleSync();
	}
}
