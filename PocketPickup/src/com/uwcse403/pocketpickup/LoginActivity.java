package com.uwcse403.pocketpickup;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

// This activity is shown once the user logs in. It contains a message
// a Facebook login button. Once the log in button is pressed, it connects
// to Facebook via the installed app, or opens up a dialog to perform Facebook
// login if the app is not installed. Upon successful login, the main activity
// is started.
public class LoginActivity extends Activity {
	public static final String LOG_TAG = "LoginActivity";

	private Button loginButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		Log.v("LoginActivity", "User is null at launch");
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the user info activity
			PocketPickupApplication.userObjectId = currentUser.getObjectId();
			getFaceBookGraphObject();
			showMainActivity();
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("user_friends"); //"public_profile",
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {

					Log.v("LoginActivity", "User is null");
					Toast.makeText(getApplicationContext(), "There was a problem during login", Toast.LENGTH_LONG).show();
					// Toast.makeText(getApplicationContext(), err.getLocalizedMessage(), Toast.LENGTH_LONG).show();
				} else {
					Log.v("LoginActivity", "User logged in: " + user.getUsername());
					PocketPickupApplication.userObjectId = user.getObjectId();
					getFaceBookGraphObject();
					// continue to main activity
					showMainActivity();
				}
			}
		});
	}
	
	/**
	 * This method will get a FaceBook graph object that will hold data such as the user's name.
	 */
	public void getFaceBookGraphObject() {
		   Session session =  ParseFacebookUtils.getSession();

		   Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

		    @Override
		    public void onCompleted(GraphUser user, Response response) {
			    if (user != null) {
			    	Log.d("LoginActivity", "facebookName: " + user.getName());
			    	Toast.makeText(getApplicationContext(), "Welcome, " + user.getFirstName() + "!", Toast.LENGTH_LONG).show();
			    }
		    }
		});
	}

	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}