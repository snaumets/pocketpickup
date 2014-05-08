package com.uwcse403.pocketpickup;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

/**
 * Stores information about an individual pickupgame.
 * 
 * Game objects are immutable.
 * 
 * @author Jacob Gile
 */
public final class Game {
	/**Name of the user who created the game**/
	public final String creatorName;
	/**Location of the game**/
	public final LatLng gameLocation;
	/**Date and time for the beginning of the game**/
	public final Date gameDate;
	
	/**
	 * Default constructor
	 * 
	 * @requires no argument is null
	 */
	public Game(String creatorName, LatLng gameLocation, Date gameDate) {
		this.creatorName = creatorName;
		this.gameLocation = gameLocation;
		this.gameDate = gameDate;
	}
	
}
