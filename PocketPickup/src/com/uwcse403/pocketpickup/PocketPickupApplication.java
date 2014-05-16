package com.uwcse403.pocketpickup;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.uwcse403.pocketpickup.ParseInteraction.DbColumns;

public class PocketPickupApplication extends Application {
	public static final String LOG_TAG = "PocketPickupApplication";
	public List<ParseObject> allowedSports;
	public static BiMap<String,ParseObject> sportsAndObjs;
	public final String SPORTS_CACHE_LABEL = "sports";
	public static String userObjectId;

	@Override
	public void onCreate() {
		super.onCreate();
		
		// read the credentials.txt file to establish connection with Parse			
		AssetManager am = this.getAssets();
		InputStream is = null;
		try {
			is = am.open("credentials.txt");
		} catch (IOException e) {
			Log.e(LOG_TAG, "Parse.com credentials not found");
			e.printStackTrace();
			// quit the application because we will not be able to send or
			// receive data
			System.exit(1);
		}
		Scanner s = null;
		s = new Scanner(is);
		String applicationID = null;
		String clientKey = null;
		applicationID = s.next();
		clientKey = s.next();
		s.close();
		Parse.initialize(this, applicationID, clientKey);
		ParseFacebookUtils.initialize(getString(R.string.app_id)); // for facebook login
		Log.v(LOG_TAG, "parse credentials success");
		getSports();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			userObjectId = currentUser.getObjectId();
		} else {
			userObjectId = null;
		}
	}

	private void getSports() {
		sportsAndObjs = HashBiMap.create();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sport");
		// looks in the cache first
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> sports, ParseException e) {
				if (e == null) {
					// found
					allowedSports = sports;
					Log.v(LOG_TAG, "successfully retreived sports from cache or network");
					for (int i = 0; i < sports.size(); i++) {
						sportsAndObjs.put(sports.get(i).getString(DbColumns.SPORT_NAME), sports.get(i));
						Log.v(LOG_TAG, sports.get(i).getString("name"));
					}
				} else {
					// error
					Log.e(LOG_TAG, "unable to retreive sports: " + e.getCode() + ": " + e.getMessage());
				}
			}
		});
		try {
			ParseObject.pinAll(SPORTS_CACHE_LABEL, allowedSports);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void forceSportsLoading() {
		sportsAndObjs = HashBiMap.create();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sport");
		// Can't look in the cache first
		//query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		List<ParseObject> sports = null;
		try {
			sports = query.find();
		} catch (ParseException e) {
			// error
			Log.e(LOG_TAG, "unable to retreive sports when forced");
			return;
		}
		allowedSports = sports;
		Log.v(LOG_TAG, "successfully retreived sports from cache or network");
		for (int i = 0; i < sports.size(); i++) {
			sportsAndObjs.put(sports.get(i).getString(DbColumns.SPORT_NAME), sports.get(i));
			Log.v(LOG_TAG, sports.get(i).getString("name"));
		}
	}
}
