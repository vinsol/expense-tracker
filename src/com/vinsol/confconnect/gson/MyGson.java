package com.vinsol.confconnect.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {

	public Gson get() {
		return new GsonBuilder().setFieldNamingStrategy(new ExpenseTrackerFieldNamingPolicy()).create(); 
	}
	
}
