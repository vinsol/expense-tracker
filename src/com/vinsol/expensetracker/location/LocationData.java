package com.vinsol.expensetracker.location;

import com.vinsol.expensetracker.MainActivity;
import com.vinsol.expensetracker.location.LocationHelper.LocationResult;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class LocationData {

	// ////******** Declaring Variables To Use ********* //////////
	Context mContext;

	// //// ******** Constructor to pass Context ******** /////////
	public LocationData(Context _context) {
		mContext = _context;
		getLocationInfo();
	}

	// ///// ********* Function to invoke LocationHelper class and get
	// locationdata ******** //////
	public String getLocationInfo() {
		LocationHelper mLocationHelper = new LocationHelper();
		mLocationHelper.getLocation(mContext, new LocationResult() {

			@Override
			public void gotLocation(Location _location) {
				MainActivity.mLocation = _location;
				Log.v("Got", "Got");
			}
		});
		return null;
	}

}
