package com.vinsol.confconnect.gson;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;
import com.vinsol.expensetracker.utils.Strings;

public class ExpenseTrackerFieldNamingPolicy implements FieldNamingStrategy{

	private boolean isFav;
	
	public ExpenseTrackerFieldNamingPolicy(boolean isFav) {
		this.isFav = isFav;
	}
	
	@Override
	public String translateName(Field f) {
		String name = f.getName();
		return getConvention(name);
	}
	
	private String getConvention(String name) {
		if(isFav) {
			if(Strings.equal(name, "favId")) { return "_id";}
		} else {
			if(Strings.equal(name, "id")) { return "_id";}
		}
		if(Strings.equal(name, "timeInMillis")) { return "time_milis_utc";}
		if(Strings.equal(name, "idFromServer")) { return "id";}
		return name;
	}

}
