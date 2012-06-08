package com.vinsol.confconnect.gson;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;
import com.vinsol.expensetracker.utils.Strings;

public class ExpenseTrackerFieldNamingPolicy implements FieldNamingStrategy{
	
	@Override
	public String translateName(Field f) {
		String name = f.getName();
		return getConvention(name);
	}
	
	private String getConvention(String name) {
		if(Strings.equal(name, "id")) { return "_id";}
		if(Strings.equal(name, "favId")) { return "fav_id";}
		if(Strings.equal(name, "myHash")) { return "my_hash";}
		if(Strings.equal(name, "timeInMillis")) { return "time_milis_utc";}
		if(Strings.equal(name, "idFromServer")) { return "id";}
		if(Strings.equal(name, "type")) { return "expense_type";}
		return name;
	}

}
