package com.uwcse403.pocketpickup;


import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import com.parse.Parse;

public class PocketPickupApplication extends Application {


	@Override
	public void onCreate() {
		super.onCreate();
		// read the credentials.txt file to establish connection with Parse
		AssetManager am = this.getAssets();
		InputStream is = null;
		try {
			is = am.open("credentials.txt");
		} catch (IOException e) {
			Log.e("PocketPickupApplication", "Parse.com credentials not found");
			e.printStackTrace();
			// quit the application because we will not be able to send or receive data
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
	}
	

}
