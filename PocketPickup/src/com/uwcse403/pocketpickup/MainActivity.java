package com.uwcse403.pocketpickup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;
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
	
	// Unique return codes to be used by activities started by this activity for result.
	private static final int CREATE_GAME_CODE = 1111;
	private static final int FIND_GAME_CODE = 2222;
	
	public static final String LOG_TAG = "MainActivity";

	// State Fields
	private boolean         mFirstLaunch;    // true only on first launch
	private LatLng          mLatLngLocation;
	private ArrayList<Game> mDisplayedGames;
	
	// Layout Fields
	private GoogleMap             mGoogleMap;
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
		mFirstLaunch = (savedInstanceState == null);
		if (!mFirstLaunch) {
			restoreMap();
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
		// Settings
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], mNavMenuIcons
				.getResourceId(0, -1)));
		// Help
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], mNavMenuIcons
				.getResourceId(1, -1)));
		// Logout
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[2], mNavMenuIcons
				.getResourceId(2, -1)));

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
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
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
		/*
		MenuItem settings = menu.findItem(R.menu.settings);
		settings.setVisible(false);
		super.onPrepareOptionsMenu(menu);
		*/
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
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Starting activity for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by starting new activities
		switch (position) {
		case 0:
			settings(null); // View supposed to be passed in, but it is not
							// used, therefore null is fine
			break;
		case 1:
			help(null); // View supposed to be passed in, but it is not used,
						// therefore null is fine
			break;
		case 2:
			logout(null);
			break;
		default:
			break;
		}

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		setTitle(mNavMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// This method will try to set the map to the given location with the given
	// zoom level (ex: 16)
	private void zoomMapToLocation(Location location, int zoomLevel) {
		if (location != null && mGoogleMap != null) {
			LatLng latLngLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					latLngLocation, zoomLevel));
		}
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
			TouchableMapFragment tmf = (TouchableMapFragment) getFragmentManager().findFragmentById(R.id.map);
			mGoogleMap = tmf.getMap();
			
			// This listener is simply used to 
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
				}

				@Override
				public void onMapSettled() {
					// Change the location text field to the actual location that the map settled on
					updateLocationTextField();
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
				// This will update the location text field when the camera
				// changes
				/*googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
							@Override
							public void onCameraChange(
									CameraPosition cameraPosition) {
								updateLocationTextField();
							}
						});*/
				
				// Setting a custom info window adapter for the google map (for markers)
			    mGoogleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

			        // Use default InfoWindow frame
			        @Override
			        public View getInfoWindow(Marker arg0) {
			            return null;
			        }
			        
			        // Defines the contents of the InfoWindow
			        @Override
			        public View getInfoContents(Marker marker) {
			            // Getting view from the layout file info_window_layout
			            View v = getLayoutInflater().inflate(R.layout.windowlayout, null);

			            // Getting the title from the marker
			            String title = marker.getTitle();
			            
			            // Getting the snippet from the marker
			            String snippet = marker.getSnippet();

			            // Getting reference to the TextView to set title
			            TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);

			            // Getting reference to the TextView to set snippet
			            TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

			            // Setting the title
			            tvTitle.setText(title);

			            // Setting the snippet
			            tvSnippet.setText(snippet);

			            // Returning the view containing InfoWindow contents
			            return v;

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
			/*
			 * Marker mark = googleMap.addMarker(new
			 * MarkerOptions().position(latLngLocation));
			 */

			try {
				List<Address> matches = geoCoder.getFromLocation(
						mLatLngLocation.latitude, mLatLngLocation.longitude, 1);
				Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
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
		//Log.v("MainActivity", "in restoreMap()");
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
		// Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
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
		// Below Toast is useful for testing
		// Toast.makeText(this, "Connection Failed to Location Client",
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDisconnected() {
		// Below Toast is useful for testing
		// Toast.makeText(this, "Disconnected from Location Client",
		// Toast.LENGTH_LONG).show();
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

	// Simply starts a sign up activity
	public void signUp(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
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

		// Go to the login view
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	// This method will erase all markers and circle/radius from map
	public void clearSearchResults(View view) {
		Button clearButton = (Button) findViewById(R.id.buttonClearResults);
		clearButton.setVisibility(View.GONE);
		// Clear map markers
		for (Marker marker : mMapMarkers) {
			marker.remove();
		}
		mMapMarkers.clear();
		// Clear map circle
		for (Circle circle: mMapCircles) {
			circle.remove();
		}
		mMapCircles.clear();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CREATE_GAME_CODE:
			if (data != null) {
				mDisplayedGames = data.getParcelableArrayListExtra(CreateGameActivity.CREATEGAME_GAMELIST);
				
				// Defaults will never get used
				final double lat = data.getDoubleExtra(CreateGameActivity.CREATEGAME_LATITUDE, 0.0);
				final double lon = data.getDoubleExtra(CreateGameActivity.CREATEGAME_LONGITUDE, 0.0);
				
				onGameDisplayUpdate(0, null);
				Toast.makeText(this, "Your game was created!", Toast.LENGTH_LONG).show();
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
				Button clearButton = (Button) findViewById(R.id.buttonClearResults);
				clearButton.setVisibility(View.VISIBLE);
				Toast.makeText(this, displayMessage, Toast.LENGTH_LONG).show();
			}
			break;
		default:
			// Do nothing
			break;
		}
	}
	
	/**
	 * 
	 * @param radius The radius of the search in miles
	 * @param loc The location of the center of the search
	 */
	private void onGameDisplayUpdate(int radius, LatLng loc) {
		if (mGoogleMap != null) {
			if (radius != 0 && loc != null) {
				int meters = radius * 1609; // convert miles to meters (1609 meters in 1 mile)
				Circle circle = mGoogleMap.addCircle(new CircleOptions().center(loc)
						.radius(meters).strokeColor(Color.GREEN));
				mMapCircles.add(circle);
			}
			
			if (mDisplayedGames != null) {
				for (Game game : mDisplayedGames) {
					// You can customize the marker image using images bundled with
					// your app, or dynamically generated bitmaps.
					String gameType = game.mGameType;
					Log.v(LOG_TAG, "gameType: " + gameType);
					
					String details = ((game.mDetails == null || game.mDetails.equals("")) ? "None" : game.mDetails);
					
					Date startDate = new Date(game.mGameStartDate);
					SimpleDateFormat formatter = new SimpleDateFormat(
		                    "hh:mm a EE, MMM d, yyyy", Locale.getDefault());
					String dateString = formatter.format(startDate);
					
					// Convert millisecond difference to hours, ms / (1000 ms/s) / (60 s/min) / (60 min/hr)
					int durationInHours = (int) (game.mGameEndDate - game.mGameStartDate) / 1000 / 60 / 60;
					String durationUnit = durationInHours > 1 ? " Hours" : " Hour"; 
					
					
					String gameData = "Event starts: " + dateString + "\n" +
					"Duration: " + durationInHours + durationUnit + "\n" + 
					"Details: " + details;
					
					int markerResource = Sports.getResourceIdForSport(gameType);
					
					Marker marker = mGoogleMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.fromResource(markerResource))
					.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
					.position(game.mGameLocation)
					.title(game.mGameType)
					.snippet(gameData));
					mMapMarkers.add(marker);
				}
			}
		}
	}

	// Simply starts a log in activity
	public void setLocation(View view) {
		// Intent intent = new Intent(this, SetLocationActivity.class);
		// startActivity(intent);
		updateLocationTextField();
		Toast.makeText(this, "TODO: Location Activity", Toast.LENGTH_LONG)
				.show();
	}
	
	@Override
	public void onDestroy() {
		Log.v(LOG_TAG, "onDestroy()");
		super.onDestroy();
		//Log.v("MainActivity", "in onDestroy()");
		//Toast.makeText(this, "destroying", Toast.LENGTH_LONG).show();
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
}
