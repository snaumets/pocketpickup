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
		// Add your initialization code here
		AssetManager am = this.getAssets();
		InputStream is = null;
		try {
			is = am.open("credentials.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("PocketPickupApplication", "Parse.com credentials not found");
			e.printStackTrace();
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
