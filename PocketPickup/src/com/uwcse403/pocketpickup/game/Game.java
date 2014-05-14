package com.uwcse403.pocketpickup.game;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

/**
 * Stores information about an individual pickupgame.
 * 
 * Game objects are immutable.
 * 
 * @author Jacob Gile
 */
public final class Game {
	/**Name of the user who created the game**/
	public final ParseObject creator;
	/**Location of the game**/
	public final LatLng gameLocation;
	/**Date and time for the beginning of the game**/
	public final Long gameStartDate;
	/**Date and time for the end of the game**/
	public final Long gameEndDate;
	/**Type of game**/
	public final ParseObject gameType;
	
	public final int idealGameSize;

	/**
	 * Default constructor
	 * 
	 * @requires no arguments are null
	 */
	public Game(ParseObject creator, LatLng gameLocation, Long gameStartDate, Long gameEndDate, 
			ParseObject gameType, int idealGameSize) {
		this.creator = creator;
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
		creator = null;
		gameLocation = null;
		gameStartDate = null;
		gameEndDate = null;
		gameType = null;
	}

}