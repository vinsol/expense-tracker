package com.vinsol.expensetracker.location;

import java.io.IOException;
import java.util.List;

import com.vinsol.expensetracker.MainActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class LocationLast {
	
	Geocoder geocoder;
	List<Address> list = null;
	private Context mContext;
	private Location mLocation;
	private Location mTempLocation;
	
	public LocationLast(Context _context){
		mContext = _context;
		geocoder = new Geocoder(mContext);
		getLastLocation();
	}
	
	public void getLastLocation(){
		LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		List<String> mProviderList = mLocationManager.getAllProviders();
		for(int i = 0 ; i<mProviderList.size();i++){
			mTempLocation = mLocationManager.getLastKnownLocation(mProviderList.get(i));
			Log.v("asd", mProviderList.get(i));
			if(mTempLocation != null){
				if(mLocation == null && MainActivity.mLocation == null){
					mLocation = mTempLocation;
				} else if(mLocation == null){
					mLocation = MainActivity.mLocation;
				} 
			
				int accuracy = (int) Math.ceil(mLocation.getAccuracy() - mTempLocation.getAccuracy());
				if(mLocation.getTime() == mTempLocation.getTime()){
					if(accuracy > 0 ){
						mLocation = mTempLocation;
					}
				}
				if(mLocation.getTime() < mTempLocation.getTime()){
					mLocation = mTempLocation;
				}
			}
		}
		
		if(MainActivity.mLocation != mLocation){
			new GetLocation().execute();
		}
	}
	
	///////   ******** Asynctask to get info using geoocoder by passing latitude and longitude values   ********  ///////
	private class GetLocation extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				list = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
    	
		
		@Override
		protected void onPostExecute(Void result) {
			MainActivity.mCurrentLocation = list.get(0).getFeatureName()+", "+list.get(0).getLocality()+", "+list.get(0).getAdminArea();
			MainActivity.mLocation = mLocation;
			Log.v("loc last", MainActivity.mCurrentLocation);
			super.onPostExecute(result);
		}
    }
}
