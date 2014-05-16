package com.uwcse403.pocketpickup.game;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;


public final class FindGameCriteria {
	/** Default radius, in miles **/
	private static final double DEFAULT_RADIUS = 1.0;
	/** Radius of search results to highlight **/
	public final double mRadius;
	/** Location of the center of the search radius **/
	public final LatLng mSearchLocation;
	/** Date for the beginning of the game **/
	public final long mStartDate;
	/** Date for the end of the game **/
	public final long mEndDate;
	/** Time for the beginning of the game **/
	public final long mStartTime;
	/** Time for the end of the game **/
	public final long mEndTime;
	
	/**Type of game**/
	public final String mGameType;
	
	/**
	 * Object holding criteria input by the user from the Find Game screen
	 * 
	 * @param radius	Search radius, must be positive
	 * @param loc       Location of the center of the search radius
	 * @param start		Date and time of start - null indicates no limit
	 * @param end       Date and time of end - null indicates no limit
	 * @param gameType  Name of sport to search TODO: change to sport preference object
	 */
	public FindGameCriteria(final double radius, final LatLng loc, final long startDate, 
			final long endDate, final long startTime, final long endTime, final String gameType) {
		mRadius = radius;
		mSearchLocation = loc;
		mStartDate = startDate;
		mEndDate = endDate;
		mStartTime = startTime;
		mEndTime = endTime;
		mGameType = gameType;
	}
	
	/**
	 * This is a convenience constructor that will set all fields except for the given LatLng location to default values.
	 * It only makes sense to call this if all fields except location are unset (set to default values) when the form was submitted. 
	 * @param loc 	The latitude/longitude of the location that the user specified.
	 */
	public FindGameCriteria(final LatLng loc) {
		mSearchLocation = loc;		
		mRadius = DEFAULT_RADIUS;
		mStartDate = 0;
		mEndDate = 0;
		mStartTime = 0;
		mEndTime = 0;
		mGameType = "";	
	}	
}
