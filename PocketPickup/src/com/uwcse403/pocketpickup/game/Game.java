package com.uwcse403.pocketpickup.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Stores information about an individual pickupgame.
 * 
 * Game objects are immutable.
 * 
 * @author Jacob Gile
 */
public final class Game implements Parcelable {
	
	/**
	 * Parcel grunt.
	 */
	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
		public Game createFromParcel(Parcel in) {
			return new Game(in);
	    }

	    public Game[] newArray(int size) {
	    	return new Game[size];
	    }
	};
	
	/** ParseObject objectId of the user who created the game. **/
	public final String mCreator;
	/** Location of the game. **/
	public final LatLng mGameLocation;
	/** Date and time for the beginning of the game. **/
	public final Long mGameStartDate;
	/** Date and time for the end of the game. **/
	public final Long mGameEndDate;
	/** Type of game. **/
	
	// unix time in miliseconds mod the number of miliseconds in a day
	public Long startTime = 0L;
	// unix time in miliseconds mod the number of miliseconds in a day
	public Long endTime = 0L;
	
	public final String mGameType;
	/** Ideal game size. **/
	public final int mIdealGameSize;
	/** Game Details. **/
	public final String mDetails;
	/** objectId of the Parse object that this game represents. The field is non-null when
	 * a Game is returned from the database from a query.
	 */
	public final String id;

	public Game(String creator, LatLng gameLocation, Long gameStartDate, Long gameEndDate, 
			Long startTime, Long endTime, String gameType, int idealGameSize, String details) {
		this.mCreator = creator;
		this.mGameLocation = gameLocation;
		this.mGameStartDate = gameStartDate;
		this.mGameEndDate = gameEndDate;
		this.mGameType = gameType;
		this.mIdealGameSize = idealGameSize;
		this.mDetails = details;
		id = null;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * Default constructor for returning a game that represents a game that is already in the
	 * database. Should be used when returning game objects from a query. 
	 * 
	 * @requires no arguments are null
	 */
	public Game(String creator, LatLng gameLocation, Long gameStartDate,
			Long gameEndDate, Long startTime, Long endTime, String gameType,
			int idealGameSize, String details, String objectId) {
		this.mCreator = creator;
		this.mGameLocation = gameLocation;
		this.mGameStartDate = gameStartDate;
		this.mGameEndDate = gameEndDate;
		this.mGameType = gameType;
		this.mIdealGameSize = idealGameSize;
		this.mDetails = details;
		id = objectId;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * Creates a game with only an idealSie field.
	 * For testing only; will not pass validation in regular use cases
	 * @param idealSize
	 */
	public Game(int idealSize) {
		mIdealGameSize = idealSize;
		mCreator = null;
		mGameLocation = null;
		mGameStartDate = null;
		mGameEndDate = null;
		mGameType = null;
		mDetails = null;
		id = null;
	}
	
	/**
	 * Create a game with only an ObjectId field
	 * For testing only will; will not pass validation in regular use cases
	 * @param id - ObjectID known to be in the database
	 */
	public Game(String id) {
		mIdealGameSize = 2;
		mCreator = null;
		mGameLocation = null;
		mGameStartDate = null;
		mGameEndDate = null;
		mGameType = null;
		mDetails = null;
		this.id = id;
	}
	
	/**
	 * Creates a game from a parcel.
	 * @param in - parcel to be converted to Game
	 */
	private Game(Parcel in) {
		mIdealGameSize = in.readInt();
		mCreator = null;
		
		final double lat = in.readDouble();
		final double lon = in.readDouble();
		mGameLocation = new LatLng(lat, lon);
		
		mGameStartDate = in.readLong();
		mGameEndDate = in.readLong();
		mGameType = in.readString();
		mDetails = in.readString();
		id = in.readString();
		
		startTime = in.readLong();
		endTime = in.readLong();
	}

	@Override
	/**{@inheritDoc}**/
	public int describeContents() {
		return 0;
	}

	@Override
	/**{@inheritDoc}**/
	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(mIdealGameSize);
		out.writeDouble(mGameLocation.latitude);
		out.writeDouble(mGameLocation.longitude);
		out.writeLong(mGameStartDate);
		out.writeLong(mGameEndDate);
		out.writeString(mGameType);
		out.writeString(mDetails);
		out.writeString(id);
		out.writeLong(startTime);
		out.writeLong(endTime);
	}

	@Override
	/**{@inheritDoc}**/
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	
	@Override
	/**{@inheritDoc}**/
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game other = (Game) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}