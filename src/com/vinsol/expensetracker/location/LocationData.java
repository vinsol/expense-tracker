package com.vinsol.expensetracker.location;

import java.io.IOException;
import java.util.List;

import com.vinsol.expensetracker.MainActivity;
import com.vinsol.expensetracker.location.LocationHelper.LocationResult;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

public class LocationData {

	//////********    Declaring Variables To Use   *********  //////////
	Geocoder geocoder;
	List<Address> list = null;
	Location location;
	LocationManager locationManager;
	Context mContext;
	
	//////   ********   Constructor to pass Context ********  /////////
	public LocationData(Context _context) {
		mContext = _context;
		getLocationInfo();
	}
	
	
	///////     *********    Function to invoke LocationHelper class and get locationdata  ********  ////// 
	public String getLocationInfo(){
		geocoder = new Geocoder(mContext);
		LocationHelper mLocationHelper = new LocationHelper();
		mLocationHelper.getLocation(mContext, new LocationResult() {
		
			@Override
			public void gotLocation(Location _location) {
				location = _location;
				MainActivity.mLocation = _location;
				if(location != null){
					exGetLocation();
				}
			}
		});
		return null;
	}
	
	
	///////   ******** Asynctask to get info using geoocoder by passing latitude and longitude values   ********  ///////
	private class GetLocation extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
    	
		
		@Override
		protected void onPostExecute(Void result) {
			MainActivity.mCurrentLocation = list.get(0).getFeatureName()+"   "+list.get(0).getLocality()+"   "+list.get(0).getAdminArea()+"   "+list.get(0).getCountryName();
			MainActivity.mLocation = location;
//			Toast.makeText(mContext, MainActivity.mCurrentLocation, Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
    }
	
	private void exGetLocation(){
		new GetLocation().execute();
	}
	
}
