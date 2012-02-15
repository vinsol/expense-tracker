/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.vinsol.expensetracker.ExpenseTrackerApplication;
import com.vinsol.expensetracker.utils.Log;

public class LocationHelper {
	
	private final int SIGNIFICANT_TIME_DELTA = 2 * 60 * 1000; // 2 MINS
	// PROD
	private final int STALE_GPS_REQUEST_TIME_DELTA = 10 * 60 * 1000; // 10 MINS
	
	private static Location currentLocation;
	public static String currentAddress = null;
	
	// This enables location listener, ir-respective if none of the providers are available
	// this helps if a provider gets enabled in wait period
	public void requestLocationUpdate() {
		final LocationManager locationManager = (LocationManager) ExpenseTrackerApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        for( String p : locationManager.getAllProviders() ) {
            Log.d("Starting Location Updates with " + p + " provider");
            locationManager.requestLocationUpdates(p, 10000, 5, locationListener);
        }
        
        // Sticks around and listen to the GPS for awhile (to get best accuracy for later activities) before shutting down.
        Thread gpsThread = new Thread(new Runnable() {
        	@Override
        	public void run() {
    			try {
    				Thread.sleep( 30 * 1000 );
    			} catch( InterruptedException e ) {
    				Thread.interrupted();
    			}
	
        		Log.d("Shutting down location updates");
        		locationManager.removeUpdates(locationListener);
        	}
        }, "GPS Initialization");
        gpsThread.setDaemon(true);
        gpsThread.start();
	}
	
	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			if(isBetterLocation(location)) {
				Log.d("Location updated to " + location.toString() );
		        currentLocation = location;
		        getCurrentAddress();
			} else {
				Log.d("Current Location is better then new Location");
			}
		}

		public void onProviderDisabled(String provider) { Log.d("**** Provider disabled: " + provider); }
		public void onProviderEnabled(String provider) { Log.d("**** Provider enabled: " + provider); } 
		public void onStatusChanged(String provider, int status, Bundle extras) { Log.d("**** Provider changed: " + provider + " " + "status: " + status);}
	};
	
	public Location getBestAvailableLocation() {
		final LocationManager locationManager = (LocationManager) ExpenseTrackerApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
		for (String provider: locationManager.getAllProviders()) {
			Location location = null;
			try {
				location = locationManager.getLastKnownLocation(provider);
				Log.d("Location From " + provider + " = " + location);
			} catch(SecurityException se) {
				Log.d("Location Helper -> inside getBestAvailableCurrentLocation -> securityException" + se);
			} catch(IllegalArgumentException iae) {
				Log.d("Location Helper -> inside getBestAvailableCurrentLocation -> IllegalArgumentException" + iae);
			} 
			if (location != null) {
				if(isBetterLocation(location)) {
					Log.d("Location is better than current location");
					currentLocation = location;
				}
			}
		}
				
		if (currentLocation == null) {
			currentAddress = null;
			return null;
		}
		
		long timeAccuracy = System.currentTimeMillis() - currentLocation.getTime();
		if (timeAccuracy > STALE_GPS_REQUEST_TIME_DELTA) {
			Log.d("*** STALE FIX, DISCARDING..., timeAccuracy was: " + timeAccuracy);
			currentAddress = null;
			return null;
		}
		
		getCurrentAddress();
		return currentLocation;
	}
	
	private boolean isBetterLocation(Location newLocation) {
	    if (currentLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = newLocation.getTime() - currentLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > SIGNIFICANT_TIME_DELTA;
	    boolean isSignificantlyOlder = timeDelta < -SIGNIFICANT_TIME_DELTA;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (newLocation.getAccuracy() - currentLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	//Checks whether two providers are the same
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	    	return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	// ///// ******** Asynctask to get info using geo-coder by passing latitude and longitude values ******** ///////
	private void getCurrentAddress() {
		currentAddress = null;
		
		AsyncTask<Void, Void, Void> getAddress = new AsyncTask<Void, Void, Void>() {
			List<Address> list;
			@Override
			protected Void doInBackground(Void... params) {
				try {
					list = new Geocoder(ExpenseTrackerApplication.getContext()).getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
				}catch (Exception e) {
				}
				return null;
			}
	
			@Override
			protected void onPostExecute(Void result) {
				currentAddress = "";
				if (list != null) {
					if(list.size() > 0) {
						Address address = (Address)list.get(0);
						if (address.getFeatureName() != null && !address.getFeatureName().equals("")) {
							currentAddress += address.getFeatureName() + ", ";
						} 
						if(address.getLocality() != null && !address.getLocality().equals("")) {
							currentAddress += address.getLocality() + ", ";
						}
						if(address.getAdminArea() != null && !address.getAdminArea().equals("")) {
							currentAddress += address.getAdminArea();
						}
					}
				}
			}
		};
		
		getAddress.execute();
	}
}