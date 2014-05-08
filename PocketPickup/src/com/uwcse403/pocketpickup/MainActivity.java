package com.uwcse403.pocketpickup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import com.uwcse403.pocketpickup.info.androidhive.slidingmenu.model.NavDrawerItem;

// This activity is started when Pocket Pickup is launched. It contains a map, sign up and log in buttons,
// and includes a sliding menu (the sliding menu was implemented by following a tutorial at 
// http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/ )
public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private GoogleMap googleMap;
    private int mapType;
    private LocationClient locationClient;
    private final Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.v("MainActivity", "in onCreate()");
		
		//////
		// Toast.makeText(this, "test", Toast.LENGTH_LONG).show(); // use this to print messages on phone screen
		setUpMapIfNeeded();
		
		// Make a new thread that will update the location text field periodically
		new Thread(new Runnable() {
	        @Override
	        public void run() {
	            while (true) {
	                try {
	                    Thread.sleep(1000);
	                    mHandler.post(new Runnable() {
	                        @Override
	                        public void run() {
	                        	updateLocationTextField();
	                        }
	                    });
	                } catch (Exception e) {
	                    // TODO: handle exception
	                }
	            }
	        }
	    }).start();
		
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Settings
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Help
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
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
	
	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * Starting activity for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by starting new activities
		switch (position) {
		case 0:
			settings(null); // View supposed to be passed in, but it is not used, therefore null is fine
			break;
		case 1:
			help(null); // View supposed to be passed in, but it is not used, therefore null is fine
			break;
		default:
			break;
		}
		
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
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
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	// This method will try to set the map to the given location with the given zoom level (ex: 16)
	private void zoomMapToLocation(Location location, int zoomLevel) {
		if (location != null) {
	        LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
	        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, zoomLevel));
	    }
	}
	
	// This method will confirm the availability of a GoogleMap
	private void setUpMapIfNeeded() {
		// Test is google play services are available
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (result != ConnectionResult.SUCCESS) {
			Toast.makeText(this, "Google Play service is not available", Toast.LENGTH_LONG).show();
			Log.v("MainActivity", "Google Play service is not available");
		}
		
		// Do a null check to confirm that we have not already instantiated the map.
	    if (googleMap == null) {
	        googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        
	        // Check if we were successful in obtaining the map.
	        if (googleMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	            googleMap.getUiSettings().setZoomControlsEnabled(true);
	            googleMap.setMyLocationEnabled(true); // enables the my-location layer, button will be visible
	            googleMap.getUiSettings().setTiltGesturesEnabled(false);

	            locationClient = new LocationClient(this, this, this);
	            locationClient.connect(); // upon success, onConnected() is called
	        }
	    }
	}
	
	// This method will update the location text field based on the longitude and latitude at the center of 
	// the map
	public void updateLocationTextField() {
		Geocoder geoCoder = new Geocoder(getApplicationContext());
		if (googleMap != null) {
			LinearLayout ll = (LinearLayout) findViewById(R.id.container);
			Point point = new Point(ll.getWidth()/2, ll.getHeight()/2);
			
			Projection proj = googleMap.getProjection();
			LatLng latLngLocation = proj.fromScreenLocation(point);
			/*Marker mark = googleMap.addMarker(new MarkerOptions().position(latLngLocation));*/
			
			try {
				List<Address> matches = geoCoder.getFromLocation(latLngLocation.latitude, latLngLocation.longitude, 1);
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
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        // save the map type so when we change orientation, the mape type can be restored
        LatLng cameraLatLng = googleMap.getCameraPosition().target;
        float cameraZoom = googleMap.getCameraPosition().zoom;
        outState.putInt("map_type", mapType);
        outState.putDouble("lat", cameraLatLng.latitude);
        outState.putDouble("lng", cameraLatLng.longitude);
        outState.putFloat("zoom", cameraZoom);
    }

	@Override
	public void onConnected(Bundle arg0) {
		//Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
		Location loc = locationClient.getLastLocation();
		LatLng latLngLocation = null;
		if (loc != null) {
			latLngLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
		}
		if (googleMap != null && latLngLocation != null) {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 16));
			zoomMapToLocation(loc, 17);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// Below Toast is useful for testing
		//Toast.makeText(this, "Connection Failed to Location Client", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDisconnected() {
		// Below Toast is useful for testing
		//Toast.makeText(this, "Disconnected from Location Client", Toast.LENGTH_LONG).show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (locationClient != null && !locationClient.isConnected()) { // sanity check
			locationClient.connect();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (locationClient != null && locationClient.isConnected()) { // sanity check
			locationClient.disconnect();
		}
	}
	
	//////////////////////// The methods below start new activities /////////////////////////////
	
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
	
	// Simply starts a sign up activity
	public void createGame(View view) {
		Intent intent = new Intent(this, CreateGameActivity.class);
		startActivity(intent);
	}
	
	// Simply starts a log in activity
	public void findGame(View view) {
		Intent intent = new Intent(this, FindGameActivity.class);
		Bundle args = new Bundle();
		args.putCharSequence(FindGameActivity.FINDGAME_LOCATION, ((EditText)findViewById(R.id.locationText)).getText());
		intent.putExtras(args);
		startActivity(intent);
	}
	
	// Simply starts a log in activity
	public void setLocation(View view) {
		//Intent intent = new Intent(this, SetLocationActivity.class);
		//startActivity(intent);
		updateLocationTextField();
		Toast.makeText(this, "TODO: Location Activity", Toast.LENGTH_LONG).show();
	}

}
