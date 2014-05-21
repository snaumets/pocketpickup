package com.uwcse403.pocketpickup.game;

import com.uwcse403.pocketpickup.R;

/**
 * This class stores the various sport names as constant strings.
 * It will be capable of returing the resource id of a sport-specific
 * marker which should be used.
 *
 */
public class Sports {
	// sports
	public static final String BASKETBALL = "Basketball";
	public static final String BASEBALL = "Baseball";
	public static final String SOCCER = "Soccer";
	public static final String FOOTBALL = "Football";
	public static final String ULTIMATE = "Ultimate";
	public static final String VOLLEYBALL = "Volleyball";
	public static final String SOFTBALL = "Softball";
	
	// image resource ids for each sport
	public static final int BASKETBALL_ID = R.drawable.ic_basketball_red_marker;
	public static final int BASEBALL_ID = R.drawable.ic_baseball_red_marker;
	public static final int SOCCER_ID = R.drawable.ic_soccer_red_marker;
	public static final int FOOTBALL_ID = R.drawable.ic_football_red_marker;
	public static final int ULTIMATE_ID = R.drawable.ic_ultimate_red_marker;
	public static final int VOLLEYBALL_ID = R.drawable.ic_volleyball_red_marker;
	
	public static int getResourceIdForSport(String sport) {
		if (sport.equals(BASKETBALL)) {
			return BASKETBALL_ID;
		} else if (sport.equals(BASEBALL)) {
			return BASEBALL_ID;
		} else if (sport.equals(SOCCER)) {
			return SOCCER_ID;
		} else if (sport.equals(FOOTBALL)) {
			return FOOTBALL_ID;
		} else if (sport.equals(ULTIMATE)) {
			return ULTIMATE_ID;
		} else if (sport.equals(VOLLEYBALL)) {
			return VOLLEYBALL_ID;
		} else if (sport.equals(SOFTBALL)) {
			return BASEBALL_ID;
		} else { // default to basketball since it is the logo
			return BASKETBALL_ID;
		}
	}
}
