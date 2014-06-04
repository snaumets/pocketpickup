package com.uwcse403.pocketpickup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.ParseInteraction.GameHandler;
import com.uwcse403.pocketpickup.ParseInteraction.SportPreferencesHandler;
import com.uwcse403.pocketpickup.ParseInteraction.Translator;
import com.uwcse403.pocketpickup.game.Game;
import com.uwcse403.pocketpickup.game.Sports;
import com.uwcse403.pocketpickup.info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import com.uwcse403.pocketpickup.info.androidhive.slidingmenu.model.NavDrawerItem;
import com.uwcse403.pocketpickup.mapwrapper.MapStateListener;
import com.uwcse403.pocketpickup.mapwrapper.TouchableMapFragment;

/**
 * This activity is shown once the user logs in, or if the user has previously logged in,
 * without logging out. It contains a Google map, sign up and log in buttons, and a 
 * sliding menu. 
 * 
 * (The sliding menu was implemented by following a tutorial at 
 * http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/ ) 
 */
public class MainActivity extends Activity implements ConnectionCallbacks,
													  OnConnectionFailedListener {
	private static final double EARTH_MEAN_RADIUS = 6371000.0; // in meters
	private static final int MARKER_PADDING_PIXELS = 200;
	private static final int CIRCLE_PADDING_PIXELS = 25;
	
	// Unique return codes to be used by activities started by this activity for result.
	private static final int CREATE_GAME_CODE = 1111;
	private static final int FIND_GAME_CODE = 2222;
	private static final int GAME_DETAILS_CODE = 3333;
	
	public static final String LOG_TAG = "MainActivity";
	
	private AlertDialog mSportsDialog;
	private ArrayList<Integer> mSelectedSports;

	// State Fields
	private boolean         mFirstLaunch;    // true only on first launch
	private LatLng          mLatLngLocation;
	private ArrayList<Game> mDisplayedGames;
	
	// Layout Fields
	private GoogleMap             mGoogleMap;
	
	@SuppressWarnings("unused") // this may be important later if we allow different map types
	private int                   mMapType;
	private LocationClient        mLocationClient;
	private CharSequence          mTitle;          // stores app title
	private Button                mFindButton;
	private Button                mCreateButton;

	// Slide Menu Fields
	private CharSequence             mDrawerTitle;   
	private DrawerLayout             mDrawerLayout;
	private ListView                 mDrawerList;
	private ActionBarDrawerToggle    mDrawerToggle;
	private String[]                 mNavMenuTitles;
	private TypedArray               mNavMenuIcons;
	private ArrayList<NavDrawerItem> mNavDrawerItems;
	private NavDrawerListAdapter     mAdapter;
	
	private Set<Marker> mMapMarkers;
	private Set<Circle> mMapCircles;
	private BiMap<Marker, Game> mMarkerToGame;

	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("MainActivity", "in onCreate()");
		
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();		
		initializeActionBar();
		initializeSlidingMenu();
		initializeButtons();
		
		// Restore map zoom level and location unless this is first launch
		mFirstLaunch = savedInstanceState == null;
		if (!mFirstLaunch) {
			restoreMap();
		} else {
			new InitSportsDialogTask().execute(""); // "" because we dont need any args
		}
	}
	
	private void initializeSlidingMenu() {
		mDrawerTitle = getTitle();
		
		// load slide menu items
		mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		mNavMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mNavDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Joined Games
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], mNavMenuIcons
				.getResourceId(0, -1)));
		// Created Games
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], mNavMenuIcons
				.getResourceId(1, -1)));
		// Preferred Sports
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[2], mNavMenuIcons
				.getResourceId(2, -1)));
		// Help
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[3], mNavMenuIcons
				.getResourceId(3, -1)));
		// Logout
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[4], mNavMenuIcons
				.getResourceId(4, -1)));

		// Recycle the typed array
		mNavMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// display view for selected nav drawer item
				displayView(position);
			}
		});

		// setting the nav drawer list adapter
		mAdapter = new NavDrawerListAdapter(getApplicationContext(),
				mNavDrawerItems);
		mDrawerList.setAdapter(mAdapter);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
				// Since map was unsettled, we need to make sure the location field is re-updated
				updateLocationTextField();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
				// Since map is unsettled, update the field to 'Updating...'
				updateLocationTextFieldToUpdating();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	private void initializeActionBar() {
		mTitle = getTitle();
		
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}
	
	private void initializeButtons() {
		mFindButton = (Button) findViewById(R.id.findgame_button);
		mCreateButton = (Button) findViewById(R.id.create_button);
		if (mLatLngLocation == null) {
			mFindButton.setEnabled(false);
			mCreateButton.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Starting activity for selected nav drawer list item.
	 * */
	private void displayView(int position) {
		// update the main content by starting new activities
		switch (position) {
		case 0:
			myJoinedGames();
			break;
		case 1:
			myCreatedGames();
			break;
		case 2:
			mySportsPreferences();
			break;
		case 3:
			help(null); // View supposed to be passed in, but it is not used,
						// therefore null is fine
			break;
		case 4:
			logout(null);
			break;
		default:
			break;
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		mDrawerLayout.closeDrawer(mDrawerList);
		mDrawerList.clearChoices();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#setTitle(java.lang.CharSequence)
	 */
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// This method will confirm the availability of a GoogleMap
	private void setUpMapIfNeeded() {
		// Test is google play services are available
		final int result = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (result != ConnectionResult.SUCCESS) {
			Toast.makeText(this, "Google Play service is not available",
					Toast.LENGTH_LONG).show();
			Log.v("MainActivity", "Google Play service is not available");
		}

		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mGoogleMap == null) {
			mMapMarkers = new HashSet<Marker>();
			mMapCircles = new HashSet<Circle>();
			mDisplayedGames = new ArrayList<Game>();
			mMarkerToGame = HashBiMap.create();
			TouchableMapFragment tmf = (TouchableMapFragment) getFragmentManager().findFragmentById(R.id.map);
			mGoogleMap = tmf.getMap();
			
			// This listener is simply used to detect touch events on the map that were
			// undetectable without the use of a wrapper around the fragment. This will
			// allow for much more efficient updating of the location text field (as
			// compared with the google map on change listener that fires an event on
			// every change and little movement), and disabling of the create and find
			// game buttons that will prevent potential bugs with location not being loaded yet.
			new MapStateListener(mGoogleMap, tmf, this) {
				@Override
				public void onMapTouched() {
					// do nothing when map is touched
				}

				@Override
				public void onMapReleased() {
					// do nothing when map is released
				}

				@Override
				public void onMapUnsettled() {
					// Change the location text field to say "Updating..."
					updateLocationTextFieldToUpdating();
					mFindButton.setEnabled(false);
					mCreateButton.setEnabled(false);
				}

				@Override
				public void onMapSettled() {
					// Change the location text field to the actual location that the map settled on
					updateLocationTextField();
					
					mFindButton.setEnabled(true);
					mCreateButton.setEnabled(true);
				}
			};

			// Check if we were successful in obtaining the map.
			if (mGoogleMap != null) {
				// The Map is verified. It is now safe to manipulate the map.
				mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				mMapType = GoogleMap.MAP_TYPE_NORMAL;
				mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
				mGoogleMap.setMyLocationEnabled(true); // enables the my-location
														// layer, button will be
														// visible
				mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
				
				mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);

				mLocationClient = new LocationClient(this, this, this);
				if (!mLocationClient.isConnected()  && !mLocationClient.isConnecting()) {
					mLocationClient.connect(); // upon success, onConnected() is
											// called
				}
				mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
					@Override
					public boolean onMarkerClick(Marker marker) {
						// Start a progress dialog that will be closed once the details screen opens
						if (mMarkerToGame.containsKey(marker)) {
							DialogTask task = new DialogTask(marker);
							task.execute("");
							/*game = mMarkerToGame.get(marker);
							Intent intent = new Intent(MainActivity.this, GameActivity.class);
							
							// Attach the details
							Bundle args = new Bundle();
							args.putParcelable(GameActivity.GAME, game);
							intent.putExtras(args);
							
							startActivityForResult(intent, GAME_DETAILS_CODE);*/
						} else {
							Toast.makeText(getApplicationContext(), "Sorry, no details for this game.", Toast.LENGTH_LONG).show();
							return false;
						}
						return true;
		            }
		        });
			}
		}
	}

	// This method will update the location text field based on the longitude
	// and latitude at the center of
	// the map
	public void updateLocationTextField() {
		Geocoder geoCoder = new Geocoder(getApplicationContext());
		if (mGoogleMap != null) {
			// Find the point of the center of the map fragment
			View map = findViewById(R.id.map);
			Point point = new Point(map.getWidth() / 2, map.getHeight() / 2);

			Projection proj = mGoogleMap.getProjection();
			mLatLngLocation = proj.fromScreenLocation(point);
			mFindButton.setEnabled(true);
			mCreateButton.setEnabled(true);

			try {
				List<Address> matches = geoCoder.getFromLocation(
						mLatLngLocation.latitude, mLatLngLocation.longitude, 1);
				Address bestMatch = matches.isEmpty() ? null : matches.get(0);
				EditText text = (EditText) findViewById(R.id.locationText);
				if (bestMatch != null) {
					text.setText(bestMatch.getAddressLine(0));
				} else {
					text.setText("");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	// This method will update the location text field to "Updating..."
	public void updateLocationTextFieldToUpdating() {
		if (mGoogleMap != null) {
			EditText text = (EditText) findViewById(R.id.locationText);
			text.setText("Updating...");
		}
	}

	/**
	 * This method simple saves the state of the map so that it can
	 * later be resumed.
	 * @param outState
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState != null) {
			super.onSaveInstanceState(outState);
			
		}
		if (mGoogleMap != null) {
			CameraPosition map = mGoogleMap.getCameraPosition();
			double latitude = map.target.latitude;
			double longitude = map.target.longitude;
			float zoom = map.zoom;
			
			SharedPreferences settings = getSharedPreferences("Map_State", 0);
			Editor editor = settings.edit();
			editor.putFloat("map_lng", (float) longitude);
			editor.putFloat("map_lat", (float) latitude);
			editor.putFloat("map_zoom", zoom);
			editor.commit();
		}
	}
	
	/**
	 * This method will be called to restore the state of the application once it is resumed.
	 * It will make sure to return the map to the location and zoom level that it was left at.
	 */
	private void restoreMap() {
		SharedPreferences settings = getSharedPreferences("Map_State", 0);
		float lng = settings.getFloat("map_lng", 0); // the second arg is returned if first is not found
		float lat = settings.getFloat("map_lat", 0);
		float zoom = settings.getFloat("map_zoom", 17); 
		
		Location location = new Location("loc");
		location.setLatitude(lat);
		location.setLongitude(lng);
		
		zoomMapToLocation(location, (int) zoom);
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (mFirstLaunch) {
			// When the app is launched, we want to zoom into the users location if possible
			Location loc = mLocationClient.getLastLocation();
			LatLng latLngLocation = null;
			if (loc != null) {
				latLngLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
			}
			if (mGoogleMap != null && latLngLocation != null) {
				zoomMapToLocation(loc, 17);
			}
			mFirstLaunch = false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// Do nothing
	}

	@Override
	public void onDisconnected() {
		// Do nothing
	}

	@Override
	protected void onResume() {
		Log.v(LOG_TAG, "onResume()");
		super.onResume();
		if (mLocationClient != null && !mLocationClient.isConnected() && !mLocationClient.isConnecting()) { // sanity check
			mLocationClient.connect();
		}
	}

	@Override
	protected void onPause() {
		Log.v(LOG_TAG, "onPause()");
		super.onPause();

		if (mLocationClient != null && (mLocationClient.isConnected() || mLocationClient.isConnecting())) { // sanity check
			mLocationClient.disconnect();
		}
	}

	// ////////////////////// The methods below start new activities /////////////////////////////

	// This method will display the user's joined games on the map
	private void myJoinedGames() {
		clearSearchResults(null); // clear previous results if any
		Set<Game> joinedGames = LoginActivity.user.mAttendingGames;
		
		if (joinedGames != null && joinedGames.size() > 0) {
			if (mDisplayedGames == null) {
				mDisplayedGames = new ArrayList<Game>();
			} else {
				mDisplayedGames.clear();
			}
			mDisplayedGames.addAll(joinedGames);
			onGameDisplayUpdate();
			zoomToShowAllMarkers(mMapMarkers);
		} else if (joinedGames == null) { // not finished initializing from database
			Toast.makeText(this, "Loading, Try Again Shortly", Toast.LENGTH_LONG).show();
		} else { // no games to show
			Toast.makeText(this, "No Joined Games", Toast.LENGTH_LONG).show();
		}
		
		if (mMapMarkers.size() > 0) {
			showClearButton();
		}
	}
	
	// This method will display the user's created games on the map
	private void myCreatedGames() {
		clearSearchResults(null); // clear previous results if any
		Set<Game> createdGames = LoginActivity.user.mCreatedGames;
		
		if (createdGames != null && createdGames.size() > 0) {
			if (mDisplayedGames == null) {
				mDisplayedGames = new ArrayList<Game>();
			} else {
				mDisplayedGames.clear();
			}
			mDisplayedGames.addAll(createdGames);
			onGameDisplayUpdate();
			zoomToShowAllMarkers(mMapMarkers);
		} else if (createdGames == null) { // not finished initializing from database
			Toast.makeText(this, "Loading, Try Again Shortly", Toast.LENGTH_LONG).show();
		} else { // no games to show
			Toast.makeText(this, "No Created Games", Toast.LENGTH_LONG).show();	
		}
		
		if (mMapMarkers.size() > 0) {
			showClearButton();
		}
	}
	
	// This method will display the user's created games on the map
	private void mySportsPreferences() {
		if (mSportsDialog != null) {
			mSportsDialog.show();
		} else { // Hasnt yet been initialized
			Toast.makeText(this, "Loading, Try Again Shortly", Toast.LENGTH_LONG).show();
			new InitSportsDialogTask().execute(""); // "" because we dont need any args
		}
	}
	
	// Simply starts a log in activity
	public void logIn(View view) {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	// Simply starts a help activity
	public void help(View view) {
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	// Simply starts a settings activity
	public void settings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	// Logs the user out of Parse, but not Facebook
	public void logout(View view) {
		ParseUser.logOut();
		// close Facebook session so that a Facebook logout will prompt the 
		// user for Facebook credentials upon next Pocket Pickup login
		com.facebook.Session fbs = com.facebook.Session.getActiveSession();
		if (fbs == null) {
			fbs = new com.facebook.Session(this);
		    com.facebook.Session.setActiveSession(fbs);
		}
		fbs.closeAndClearTokenInformation();
		// Go to the login view
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	// This method will erase all markers and circle/radius from map
	public void clearSearchResults(View view) {
		Button clearButton = (Button) findViewById(R.id.buttonClearResults);
		clearButton.setVisibility(View.GONE);
		// Clear map markers
		if (mMapMarkers != null) {
			for (Marker marker : mMapMarkers) {
				marker.remove();
			}
			mMapMarkers.clear();
		}
		
		if (mMapCircles != null) {
			// Clear map circle
			for (Circle circle: mMapCircles) {
				circle.remove();
			}
			mMapCircles.clear();
		}
		
		if (mDisplayedGames != null) {
			mDisplayedGames.clear();
		}
		
		if (mMarkerToGame != null) {
			mMarkerToGame.clear();
		}
	}
	
	// This method will remove the given marker from map and internal structures
	private void removeMarkerFromMap(Marker marker) {
		// Clear map marker
		if (marker != null) {
			if (mMapMarkers != null && mMapMarkers.contains(marker)) {
				mMapMarkers.remove(marker);
			}
			
			Game game = null;
			if (mMarkerToGame != null) {
				game = mMarkerToGame.get(marker);
			}
			
			if (mDisplayedGames != null && mDisplayedGames.contains(marker) && game != null) {
				mDisplayedGames.remove(game);
			}
			// remove the marker from the map
			marker.remove();
		}
	}
	
	// This will show the 
	private void showClearButton() {
		Button clearButton = (Button) findViewById(R.id.buttonClearResults);
		clearButton.setVisibility(View.VISIBLE);
	}

	// Simply starts a sign up activity
	public void createGame(View view) {
		Intent intent = new Intent(this, CreateGameActivity.class);
		Bundle args = new Bundle();
		args.putCharSequence(CreateGameActivity.CREATEGAME_LOCATION,
				((EditText) findViewById(R.id.locationText)).getText());
		args.putDouble(CreateGameActivity.CREATEGAME_LATITUDE,
				mLatLngLocation.latitude);
		args.putDouble(CreateGameActivity.CREATEGAME_LONGITUDE,
				mLatLngLocation.longitude);
		intent.putExtras(args);
		startActivityForResult(intent, CREATE_GAME_CODE);
	}

	// Simply starts a log in activity
	public void findGame(View view) {
		clearSearchResults(null); // clear previous results if any
		Intent intent = new Intent(this, FindGameActivity.class);
		Bundle args = new Bundle();
		args.putCharSequence(FindGameActivity.FINDGAME_LOCATION,
				((EditText) findViewById(R.id.locationText)).getText());
		args.putDouble(FindGameActivity.FINDGAME_LATITUDE,
				mLatLngLocation.latitude);
		args.putDouble(FindGameActivity.FINDGAME_LONGITUDE,
				mLatLngLocation.longitude);
		intent.putExtras(args);
		startActivityForResult(intent, FIND_GAME_CODE);
	}

	/**
	 * This method is called when an activity that was started from this
	 * activity waiting for a result finally returns with a result code
	 * and data if applicable.
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CREATE_GAME_CODE:
			if (data != null) {
				mDisplayedGames = data.getParcelableArrayListExtra(CreateGameActivity.CREATEGAME_GAMELIST);
				
				onGameDisplayUpdate(0, null);
				Toast.makeText(this, "Your game was created!", Toast.LENGTH_LONG).show();
				showClearButton();
				
				// Ensure that the static sets have been initialized
				if (LoginActivity.user.mCreatedGames == null) { // safety check
					LoginActivity.user.initCreatedGames();
				}
				if (LoginActivity.user.mAttendingGames == null) { // safety check
					LoginActivity.user.initAttendingGames();
				}
				
				LoginActivity.user.mCreatedGames.add(mDisplayedGames.get(0)); // there will be only one game
				LoginActivity.user.mAttendingGames.add(mDisplayedGames.get(0)); // there will be only one game
			}
			break;
		case FIND_GAME_CODE:
			if (data != null) {
				mDisplayedGames = data.getParcelableArrayListExtra(FindGameActivity.FINDGAME_RESULTS);
				final int radius = data.getIntExtra(FindGameActivity.FINDGAME_RADIUS, 1);
				
				// Defaults will never get used
				final double lat = data.getDoubleExtra(FindGameActivity.FINDGAME_LATITUDE, 0.0);
				final double lon = data.getDoubleExtra(FindGameActivity.FINDGAME_LONGITUDE, 0.0);
				
				onGameDisplayUpdate(radius, new LatLng(lat, lon));
				
				String displayMessage;
				if (mDisplayedGames.size() == 0) {
					displayMessage = "No games found";
				} else if (mDisplayedGames.size() == 1) {
					displayMessage = "Displaying one game!";
				} else {
					displayMessage = "Displaying " + mDisplayedGames.size() + " games!";
				}
				
				// Make the clear button visible
				showClearButton();
				Toast.makeText(this, displayMessage, Toast.LENGTH_LONG).show();
			}
			break;
		case GAME_DETAILS_CODE:
			if (data != null) {
				// This string will tell whether the user deleted, left, or joined a game
				int result = data.getIntExtra(GameActivity.GAME_RESULT, GameActivity.GAME_RESULT_JOIN_FAILED);
				Game game = data.getParcelableExtra(GameActivity.GAME);
				Marker marker = null;
				switch (result) {
				case GameActivity.GAME_RESULT_JOINED:
					Toast.makeText(getApplicationContext(), "Successfully Added To Game!",
							Toast.LENGTH_LONG).show();
					LoginActivity.user.mAttendingGames.add(game);
					break;
				case GameActivity.GAME_RESULT_ALREADY_JOINED:
					Toast.makeText(getApplicationContext(),
						"You Are Already An Attendee", Toast.LENGTH_LONG).show();
					break;
				case GameActivity.GAME_RESULT_JOIN_FAILED:
					Toast.makeText(getApplicationContext(), "Joining Game Failed",
						Toast.LENGTH_LONG).show();
					break;
				case GameActivity.GAME_RESULT_LEFT:
					Toast.makeText(this, "Successfully Left Game", Toast.LENGTH_LONG)
						.show();
					LoginActivity.user.mAttendingGames.remove(game);
					marker = mMarkerToGame.inverse().get(game);
					removeMarkerFromMap(marker);
					break;
				case GameActivity.GAME_RESULT_LEFT_FAILED:
					Toast.makeText(this, "Failed To Leave Game", Toast.LENGTH_LONG)
						.show();
					break;
				case GameActivity.GAME_RESULT_DELETED:
					Toast.makeText(this, "Successfully Deleted Game", Toast.LENGTH_LONG)
						.show();
					LoginActivity.user.mAttendingGames.remove(game);
					LoginActivity.user.mCreatedGames.remove(game);
					marker = mMarkerToGame.inverse().get(game);
					removeMarkerFromMap(marker);
					break;
				default:
					// Do nothing
					break;
				}
			}
		default:
			// Do nothing
			break;
		}
	}
	
	/**
	 * This method will try to set the map to the given location with the given zoom.
	 * @param location	The center location which will be zoomed to.
	 * @param zoomLevel	The zoom level
	 */
	private void zoomMapToLocation(Location location, int zoomLevel) {
		if (location != null && mGoogleMap != null) {
			LatLng latLngLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					latLngLocation, zoomLevel));
		}
	}
	
	/**
	 * NOTE: This is not currently used, but may be important later
	 * 
	 * This method will try to set the map zoom the map, not changing the location.
	 * @param zoomLevel	The zoom level.
	 */
	@SuppressWarnings("unused")
	private void zoomMapToLocation(int zoomLevel) {
		if (zoomLevel > 0 && mGoogleMap != null) {			
			mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
		}
	}
	
	/**
	 * This method will zoom the map to a level so that the entire circle
	 * is shown.
	 * 
	 * @param circle	The circle that must be shown
	 */
	private void zoomToShowCircle(Circle circle) { 
	    LatLngBounds.Builder builder = new LatLngBounds.Builder(); 
	    LatLng center = circle.getCenter();
	    double radius = circle.getRadius();
	    
	    // Convert lat and lng to radians 
	    double lat = center.latitude * Math.PI / 180.0; 
	    double lon = center.longitude * Math.PI / 180.0; 
	    
	    // Determine some points that are to be included as bounds
	    for (double t = 0; t <= Math.PI * 2; t += 1.0) { // we dont need very many points
		    double latPoint = lat + (radius / EARTH_MEAN_RADIUS) * Math.sin(t); // y
		    double lngPoint = lon + (radius / EARTH_MEAN_RADIUS) * Math.cos(t) / Math.cos(lat); // x
		
		    // saving the location on circle as a LatLng point
		    LatLng point = new LatLng(latPoint * 180.0 / Math.PI, lngPoint * 180.0 / Math.PI);
		    builder.include(point);
		}
	    LatLngBounds bounds = builder.build();
	    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, CIRCLE_PADDING_PIXELS));
    }
	
	/**
	 * This method will zoom the map out/in enough to show all of the markers
	 * that are in the given set.
	 * 
	 * @param mapMarkers	The set which contains all markers which need to be shown
	 */
	private void zoomToShowAllMarkers(Set<Marker> mapMarkers) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : mapMarkers) {
			LatLng point = marker.getPosition();
			builder.include(point);
		}
		
		LatLngBounds bounds = builder.build();
	    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MARKER_PADDING_PIXELS));
	}
	
	/**
	 * This will display the games in the global arraylist mDisplayedGames and 
	 * will draw a circle with the given radius and at the given location.
	 * 
	 * @param radius The radius of the search in miles
	 * @param loc The location of the center of the search
	 */
	private void onGameDisplayUpdate(int radius, LatLng loc) {
		if (mGoogleMap != null) {
			if (radius != 0 && loc != null) {
				int meters = radius * 1609; // convert miles to meters (1609 meters in 1 mile)
				Circle circle = mGoogleMap.addCircle(new CircleOptions().center(loc)
						.radius(meters).strokeColor(Color.GREEN).fillColor(0x2200ff00));
						// Green outline, transparent green fill color
						// 0xaabbbbbb => "bbbbbb" is the hex color, and "aa" is transparency
				mMapCircles.add(circle);
				
				// Set map to zoom out enough for to show entire circle
				zoomToShowCircle(circle);
			}
			
			if (mDisplayedGames != null) {
				Game gameWithId = null;
				for (Game game : mDisplayedGames) {
					// You can customize the marker image using images bundled with
					// your app, or dynamically generated bitmaps.
					String gameType = game.mGameType;					
					
					int markerResource = Sports.getResourceIdForSport(gameType);
					
					Marker marker = mGoogleMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.fromResource(markerResource))
					.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
					.position(game.mGameLocation));
					mMapMarkers.add(marker);
					
					if (radius == 0 && loc == null) {
						// game was created, must get game with id from database
						ParseObject poGame = GameHandler.getGameCreatedByCurrentUser(game);
						gameWithId = Translator.parseGameToAppGame(poGame);
						mMarkerToGame.put(marker, gameWithId);
					} else {
						// Games came from database since they are search results
						mMarkerToGame.put(marker, game);
					}
				}
				// If the game was created, we want to put the game object with the id from
				// the database set
				if (radius == 0 && loc == null) {
					mDisplayedGames.clear();
					mDisplayedGames.add(gameWithId);
				}
			}
		}
	}
	
	/**
	 * This function will simply draw the games that are currently in the 
	 * global arraylist mDisplayedGames that are to be draw. The precondition is that the
	 * arraylist mDisplayedGames was already filled with the games.
	 */
	private void onGameDisplayUpdate() {			
		if (mDisplayedGames != null) {
			for (Game game : mDisplayedGames) {
				// You can customize the marker image using images bundled with
				// your app, or dynamically generated bitmaps.
				String gameType = game.mGameType;
				int markerResource = Sports.getResourceIdForSport(gameType);
				
				Marker marker = mGoogleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromResource(markerResource))
				.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
				.position(game.mGameLocation));
				mMapMarkers.add(marker);
				mMarkerToGame.put(marker, game);
			}
		}
	}

	/**
	 * Updates the location text field based on the new 
	 * center pin location.
	 * 
	 * @param view		unused
	 */
	public void setLocation(View view) {
		updateLocationTextField();

	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGoogleMap != null) {
			CameraPosition map = mGoogleMap.getCameraPosition();
			double latitude = map.target.latitude;
			double longitude = map.target.longitude;
			float zoom = map.zoom;
			
			SharedPreferences settings = getSharedPreferences("Map_State", 0);
			Editor editor = settings.edit();
			editor.putFloat("map_lng", (float) longitude);
			editor.putFloat("map_lat", (float) latitude);
			editor.putFloat("map_zoom", zoom);
			editor.commit();
		}
	}
	
	// Build the sports dialog that a user will use to edit Preferred Sports
	private void buildSportsPreferencesDialog() {
		if (LoginActivity.user != null) { // user has finished being initialized
			// A list of the sports to display as options to check
			final ArrayList<String> availableSports = new ArrayList<String>(PocketPickupApplication.sportsAndObjs.keySet());
			
			// arraylist to keep the selected items' indexes
			mSelectedSports = new ArrayList<Integer>();
			
			boolean preferredSportsSet = LoginActivity.user.isPreferredSportsInitialized();
			
			// Make the equivalent CharSequence array of sports that the dialog uses to initialize
			CharSequence[] sports = new CharSequence[availableSports.size()];
			boolean[] preferred = new boolean[availableSports.size()];
			String sportStr = null;
			for (int i = 0; i < sports.length; i++) {
				sportStr = availableSports.get(i);
				sports[i] = (CharSequence) sportStr;
				
				
				// set initial state of checked options according to user's preferred sports
				if (preferredSportsSet && LoginActivity.user.mPreferredSports.contains(sportStr)) {
					preferred[i] = true;
					mSelectedSports.add(i);
				} else {
					preferred[i] = false;
				}
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
	        builder.setTitle("Select Preferred Sports");
	        builder.setMultiChoiceItems(sports, preferred,
	        		new DialogInterface.OnMultiChoiceClickListener() {
	        	@Override
	        	public void onClick(DialogInterface dialog, int indexSelected,
				         boolean isChecked) {
	        		
					if (isChecked) {
						// If the user checked the item, add it to the set of selected items
						if (!mSelectedSports.contains(indexSelected)) {
							mSelectedSports.add(indexSelected);
						}
					} else if (mSelectedSports.contains(indexSelected)) {
						// Else, if the item is already in the set, remove it
						mSelectedSports.remove(Integer.valueOf(indexSelected));
					}
	        	}
	        })
			// Set the action buttons
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// When user clicked on Ok, update the settings in the database
					int size = mSelectedSports.size();
					String[] newPreferredSports = new String[size];
					LoginActivity.user.mPreferredSports.clear();
					String sport = null;
					for (int i = 0; i < size; i++) {
						int selectedIndex = mSelectedSports.get(i);
						sport = availableSports.get(selectedIndex);
						LoginActivity.user.mPreferredSports.add(sport);
						newPreferredSports[i] = sport;
					}
					
					// save in database
					SportPreferencesHandler.setSportPreferences(newPreferredSports);
					Toast.makeText(getApplicationContext(), "Preferences Successfully Updated!", Toast.LENGTH_LONG).show();
				}
			});
	        
	        mSportsDialog = builder.create(); //AlertDialog dialog; create like this outside onClick
		}
	}
	
	/**
	 * This task will initialize the user's attending games set
	 */
	private class InitSportsDialogTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... arg0) {
			// Sleep until the LoginActivity.user is initialized
			while (LoginActivity.user == null) {
				try {
					Thread.sleep(250); // sleep for a quarter of a second and try again
				} catch (InterruptedException e) {
					// Do nothing, just exit loop
					break;
				}
			}	
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		   super.onProgressUpdate(values);
		}
 
		@Override
		protected void onPostExecute(String result) {
			// Once the LoginActivity.user has been initialized, create the dialog
			buildSportsPreferencesDialog();
			super.onPostExecute(result);
		}
	}
	
	/**
	 * This task will take show a progress dialog while the games are found.
	 * After it is complete, it will dismiss the dialog and go back to the 
	 * MainActivity.
	 */
	private class DialogTask extends AsyncTask<String, Integer, String> {
		private ProgressDialog mProgressDialog;
		private Marker marker;
		
		public DialogTask(Marker marker) {
			this.marker = marker;
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(MainActivity.this, "", "Loading...", true);
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			Game game = mMarkerToGame.get(marker);
			Intent intent = new Intent(MainActivity.this, GameActivity.class);
			
			// Attach the details
			Bundle args = new Bundle();
			args.putParcelable(GameActivity.GAME, game);
			intent.putExtras(args);
			
			startActivityForResult(intent, GAME_DETAILS_CODE);
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