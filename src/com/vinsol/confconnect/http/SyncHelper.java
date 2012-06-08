package com.vinsol.confconnect.http;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.vinsol.confconnect.gson.MyGson;
import com.vinsol.expensetracker.helpers.ConvertCursorToListString;
import com.vinsol.expensetracker.utils.Log;

public class SyncHelper extends AsyncTask<Void, Void, Void>{
	
	private Context context;
	
	public SyncHelper(Context context) {
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("************************** Starting Sync **********************************");
		Log.d("*********************** Getting SyncData **********************************");
		try {
			String fetchedSyncResponse = new HTTP(context).getSyncData(); 
			Log.d(" *************  "+ fetchedSyncResponse);
//			new MyGson().get().fromJson(fetchedSyncResponse, ConfConnect.class)
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("************************** Finishing Sync **********************************");
		super.onPostExecute(result);
	}
	
}