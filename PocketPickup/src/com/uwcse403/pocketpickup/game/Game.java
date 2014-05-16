package com.uwcse403.pocketpickup.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;

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
	
	public final int mIdealGameSize;

	/**
	 * Default constructor
	 * 
	 * @requires no arguments are null
	 */
	public Game(String creator, LatLng gameLocation, Long gameStartDate, Long gameEndDate, 
			String gameType, int idealGameSize) {
		this.mCreator = creator;
		this.mGameLocation = gameLocation;
		this.mGameStartDate = gameStartDate;
		this.mGameEndDate = gameEndDate;
		this.mGameType = gameType;
		this.mIdealGameSize = idealGameSize;
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
	}
	
	private Game(Parcel in) {
		mIdealGameSize = in.readInt();
		mCreator = null;
		
		final double lat = in.readDouble();
		final double lon = in.readDouble();
		mGameLocation = new LatLng(lat, lon);
		
		mGameStartDate = in.readLong();
		mGameEndDate = in.readLong();
		mGameType = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeInt(mIdealGameSize);
		out.writeDouble(mGameLocation.latitude);
		out.writeDouble(mGameLocation.longitude);
		out.writeLong(mGameStartDate);
		out.writeLong(mGameEndDate);
		out.writeString(mGameType);
	}

}