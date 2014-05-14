package com.uwcse403.pocketpickup.game;

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
	public final Date gameStartDate;
	/**Date and time for the end of the game**/
	public final Date gameEndDate;
	/**Type of game**/
	public final String gameType;
	
	public final int idealGameSize;

	/**
	 * Default constructor
	 * 
	 * @requires no arguments are null
	 */
	public Game(String creatorName, LatLng gameLocation, Date gameStartDate, Date gameEndDate, 
			String gameType, int idealGameSize) {
		this.creatorName = creatorName;
		this.gameLocation = gameLocation;
		this.gameStartDate = gameStartDate;
		this.gameEndDate = gameEndDate;
		this.gameType = gameType;
		this.idealGameSize = idealGameSize;
	}

	/**
	 * for testing only
	 * @param idealSize
	 */
	public Game(int idealSize) {
		this.idealGameSize = idealSize;
		creatorName = null;
		gameLocation = null;
		gameStartDate = null;
		gameEndDate = null;
		gameType = null;
	}

}