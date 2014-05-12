package com.uwcse403.pocketpickup.game;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;


public final class FindGameCriteria {
	/** Default radius, in miles **/
	private static final double DEFAULT_RADIUS = 5.0;
	
	/** Radius of search results to highlight **/
	public double mRadius;
	
	public double getRadius() {
		return mRadius;
	}

	public void setRadius(final double mRadius) {
		this.mRadius = mRadius;
	}

	public LatLng getSearchLocation() {
		return mSearchLocation;
	}

	public void setSearchLocation(final LatLng mSearchLocation) {
		this.mSearchLocation = mSearchLocation;
	}

	public Date getStartDate() {
		return mStartDate;
	}

	public void setStartDate(final Date mStartDate) {
		this.mStartDate = mStartDate;
	}

	public Date getEndDate() {
		return mEndDate;
	}

	public void setEndDate(final Date mEndDate) {
		this.mEndDate = mEndDate;
	}

	public String getGameType() {
		return mGameType;
	}

	public void setGameType(final String mGameType) {
		this.mGameType = mGameType;
	}

	/** Location of the center of the search radius **/
	public LatLng mSearchLocation;
	
	/** Date and time for the beginning of the game **/
	public Date mStartDate;
	
	/** Date and time for the end of the game **/
	public Date mEndDate;
	
	/**Type of game**/
	public String mGameType;
	
	/**
	 * Object holding criteria input by the user from the Find Game screen
	 * 
	 * @param radius	Search radius, must be positive
	 * @param loc       Location of the center of the search radius
	 * @param start		Date of start time - null indicates no limit
	 * @param end       Date of end time - null indicates no limit
	 * @param gameType  Name of sport to search TODO: change to sport preference object
	 */
	public FindGameCriteria(final double radius, final LatLng loc, 
			final Date start, final Date end, final String gameType) {
		mRadius = radius;
		mSearchLocation = loc;
		mStartDate = start;
		mEndDate = end;
		mGameType = gameType;
	}
	
	public FindGameCriteria(final LatLng loc) {
		mSearchLocation = loc;		
		mRadius = DEFAULT_RADIUS;
		mStartDate = null;
		mEndDate = null;
		mGameType = "";	
	}	
	
}
