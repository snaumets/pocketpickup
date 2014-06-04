package com.uwcse403.pocketpickup.user;

import java.util.HashSet;
import java.util.Set;

import com.google.android.gms.maps.model.LatLng;
import com.uwcse403.pocketpickup.game.Game;

/**
 * This class will hold all state and data for the user.
 * It will hold details about the user that we can learn from 
 * their Facebook account, and things like a list of their sport
 * preferences, a list of the games they are currently attending,
 * and a list of games they have created.
 * 
 * This object is going to be created and populated from facebook
 * and the database whenever the application launches.
 * 
 * @author Serge
 *
 */
public class User {
	public final String mFirstName;
	public final String mLastName;
	public final String mEmail;
	public final int mAge;
	public final String mGender;
	
	/**
	 * This set will hold a string for each sport that the user
	 * prefers to play most often.
	 */
	public Set<String> mPreferredSports;
	
	/**
	 * This set will hold all of the games that the user is currently
	 * planning to attend, including games they have created and games
	 * they have joined (that they didn't create).
	 * Only games that havent yet expired will be in this list.
	 */
	public Set<Game> mAttendingGames;
	
	/**
	 * This set will hold all of the games that the user has created.
	 * Only games that havent yet expired will be in this list.
	 */
	public Set<Game> mCreatedGames;
	
	/**
	 * This is the default constructor for a user.
	 * If certain information cannot be pulled from Facebook because of permission
	 * issues, the variable will be set to a default value (empty string for Strings,
	 * or -1 for age).
	 */
	public User(String firstName, String lastName, String email, int age, String gender) {
		this.mFirstName = firstName;
		this.mLastName = lastName;
		this.mEmail = email;
		this.mAge = age;
		this.mGender = gender;
		// The following sets are null, the set has not yet been initialized from the database
		this.mPreferredSports = null;
		this.mAttendingGames = null;
		this.mCreatedGames = null;
	}
	
	/**
	 * This method will initialize the preferred sports set
	 */
	public void initPreferredSports() {
		this.mPreferredSports = new HashSet<String>();
	}
	
	/**
	 * This method will initialize the preferred sports set
	 */
	public void initAttendingGames() {
		this.mAttendingGames = new HashSet<Game>();
	}
	
	/**
	 * This method will initialize the preferred sports set
	 */
	public void initCreatedGames() {
		this.mCreatedGames = new HashSet<Game>();
	}
	
	/**
	 * Returns true if the preferred sports set is initialized.
	 * @return	true if the set is not null, false otherwise
	 */
	public boolean isPreferredSportsInitialized() {
		return mPreferredSports != null;
	}
	
	/**
	 * Returns true if the attending games set is initialized.
	 * @return	true if the set is not null, false otherwise
	 */
	public boolean isAttendingGamesInitialized() {
		return mAttendingGames != null;
	}
	
	/**
	 * Returns true if the created games set is initialized.
	 * @return	true if the set is not null, false otherwise
	 */
	public boolean isCreateGamesInitialized() {
		return mCreatedGames != null;
	}
}
