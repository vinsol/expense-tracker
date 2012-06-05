package com.vinsol.expensetracker.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;

public class Utils {
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm.getActiveNetworkInfo() == null) {return false;}
		return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	public static String getMD5() {
		String toEnc = Calendar.getInstance().getTimeInMillis()+""+100000 + (int)(Math.random() * ((999999 - 100000) + 1));
		try {
			MessageDigest mdEnc = MessageDigest.getInstance("MD5"); 
			mdEnc.update(toEnc.getBytes(), 0, toEnc.length());
			return String.format("%1$032X", new BigInteger(1, mdEnc.digest())).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
