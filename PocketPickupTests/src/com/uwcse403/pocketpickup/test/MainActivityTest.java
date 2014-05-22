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

/**
 * Test main activity functionality.  It is assumed that the client running
 * these tests is logged into a Facebook account.  If the testing client is
 * not logged in, testActivityInitialized will fail, and the behavior of other
 * tests is undefined.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

	// UI Components under test
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

	/**
	 * Asserts that 
	 */
	public void testActivityInitialized() {
		assertTrue(mActivity != null);
		assertTrue(mCreateGameButton != null);
		assertTrue(mFindGameButton != null);
		
		assertNotNull(ParseUser.getCurrentUser());
	}
	
	/** 
	 * Tests that Google map is zoomed to user locations on start
	 */
	public void testMapZoomsOnStart() {
		// TODO this
	}
	
	/**
	 * Tests that Google map is not zoomed on screen orientation change,
	 * on return from any other activity, or on screen sleep and wake-up.
	 */
	public void testMapOnlyZoomsOnStart() {
		// TODO this
	}
	
	/**
	 * Tests that the address field updates when the map moves,
	 * and that during load the address field displays an updating message.
	 */
	public void testAddressFieldUpdates() {
		// TODO UI automator
	}
	
	/**
	 * Tests that the find game button starts the FindGameActivity.
	 * 
	 * Note this also asserts than the Find Game button is only enabled when
	 * a location is filled in, as otherwise the FindGameActivity will not load.
	 */
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
	
	/**
	 * Tests that a single result is drawn from Find Game Activity.
	 */
	public void testFindGameButtonOneResult() {
		
	}
	
	/**
	 * Tests that all results are drawn when Find Game returns multiple games
	 */
	public void testFindGameButtonManyResults() {
		
	}
	
	/**
	 * Tests that Create Game button launches CreateGameActivity
	 * 
	 * Note this also asserts than the Find Game button is only enabled when
	 * a location is filled in, as otherwise the FindGameActivity will not load.
	 */
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
	
	/**
	 * Tests that Create Game paints a pin on the map.
	 */
	public void testCreateGameButtonResult() {
		
	}
	
	/**
	 * Tests that the application closes when back is pressed
	 * from Main Activity
	 */
	public void testBackButtonSuspendsApp() {

	}
	
	/**
	 * Tests that the sliding menu 'Settings' option launches 
	 * the settings activity
	 */
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
	
	/**
	 * Tests that the sliding menu 'Help' option launches 
	 * the help activity
	 */
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
	
	
	/**
	 * Tests that the sliding menu 'Logout' option logs out from
	 * Facebook and launches the login activity
	 */
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
