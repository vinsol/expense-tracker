package com.vinsol.expensetracker.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Utils {
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() == null) {return false;}
		return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
}
