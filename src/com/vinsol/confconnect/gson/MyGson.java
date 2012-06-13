package com.vinsol.confconnect.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MyGson {

	public Gson get() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingStrategy(new ExpenseTrackerFieldNamingPolicy());
		gsonBuilder.setExclusionStrategies(new ExpenseTrackerExclusionStrategy());
		return gsonBuilder.create(); 
	}
	
}
