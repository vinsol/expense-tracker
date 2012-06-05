package com.vinsol.expensetracker.helpers;

import com.vinsol.confconnect.gson.MyGson;
import com.vinsol.expensetracker.utils.Log;

import android.content.Context;
import android.os.AsyncTask;

public class SyncAdapter extends AsyncTask<Void, Void, Void>{
	
	private Context context;
	
	public SyncAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d("************************** Starting Sync **********************************");
		Log.d(" *************** Entry JSON \n "+new MyGson().get(false).toJson(new ConvertCursorToListString(context).getEntryList(true, null)));
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("************************** Finishing Sync **********************************");
		super.onPostExecute(result);
	}
	
}
