package com.uwcse403.pocketpickup.game;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Stores information about an individual pickupgame.
 * 
 * Game objects are immutable.
 * 
 * @author Jacob Gile
 */
public final class Game2 {
	/** user who created the game**/
	private ParseUser creator = null;
	/**Location of the game**/
	private LatLng location = null;
	/**Date and time for the beginning of the game**/
	private Date startDate = null;
	/**Date and time for the end of the game**/
	private Date endDate = null;
	/**Type of game**/
	private ParseObject type = null;
	// ideal game size
	private int idealSize;
	
	/**
	 * Default constructor
	 * 
	 * @requires no arguments are null
	 */
	public Game2(String creatorName, LatLng gameLocation, Date gameStartDate, Date gameEndDate, 
			String gameType) {
		this.location = gameLocation;
		this.startDate = gameStartDate;
		this.endDate = gameEndDate;
	}
	/**
	 * This constructor should only be used for testing purposes. Creates a game with
	 * only one piece of data, the ideal game size. Obviously this is not enough information
	 * to be useful to any app user, so this serves as a way to easily test if objects are 
	 * begin saved to Parse 
	 * @param n: ideal game size.
	 * 
	 */
	public Game2(int n) {
		idealSize = n;
	}
	
	public ParseUser getCreator() {
		return creator;
	}
	public LatLng getLocation() {
		return location;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public ParseObject getGameType() {
		return type;
	}
	public int getIdealSize() {
		return idealSize;
	}
	
	
}
