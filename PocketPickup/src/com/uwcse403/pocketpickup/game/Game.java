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
	 * Parcel grunt
	 */
	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
		public Game createFromParcel(Parcel in) {
			return new Game(in);
	    }

	    public Game[] newArray(int size) {
	    	return new Game[size];
	    }
	};
	
	/**ParseObject objectId of the user who created the game**/
	public final String mCreator;
	/**Location of the game**/
	public final LatLng mGameLocation;
	/**Date and time for the beginning of the game**/
	public final Long mGameStartDate;
	/**Date and time for the end of the game**/
	public final Long mGameEndDate;
	/**Type of game**/
	public final String mGameType;
	/**Ideal game size**/
	public final int mIdealGameSize;
	/**Game Details**/
	public final String mDetails;
	/** objectId of the Parse object that this game represents. The field is non-null when
	 * a Game is returned from the database from a query.
	 */
	public final String id;

	/**
	 * Default constructor for creating a new game, i.e., one that does not exist in the database.
	 * 
	 * @requires no arguments are null
	 */
	public Game(String creator, LatLng gameLocation, Long gameStartDate, Long gameEndDate, 
			String gameType, int idealGameSize, String details) {
		this.mCreator = creator;
		this.mGameLocation = gameLocation;
		this.mGameStartDate = gameStartDate;
		this.mGameEndDate = gameEndDate;
		this.mGameType = gameType;
		this.mIdealGameSize = idealGameSize;
		this.mDetails = details;
		id = null;
	}
	
	/**
	 * Default constructor for returning a game that represents a game that is already in the
	 * database. Should be used when returning game objects from a query. 
	 * 
	 * @requires no arguments are null
	 */
	public Game(String creator, LatLng gameLocation, Long gameStartDate, Long gameEndDate, 
			String gameType, int idealGameSize, String details, String objectId) {
		this.mCreator = creator;
		this.mGameLocation = gameLocation;
		this.mGameStartDate = gameStartDate;
		this.mGameEndDate = gameEndDate;
		this.mGameType = gameType;
		this.mIdealGameSize = idealGameSize;
		this.mDetails = details;
		id = objectId;
	}
	
	
	/**
	 * for testing only
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
	 * Creates a game from a parcel
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
	}
}