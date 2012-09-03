package com.vinsol.confconnect.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.vinsol.expensetracker.utils.Strings;

/*
 * ExclusionStrategy -> A strategy (or policy) definition that is used to decide 
 * whether or not a field or top-level class should be serialized or deserialized 
 * as part of the JSON output/input
 * */

public class ExpenseTrackerExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> c) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
//		if(Strings.equal(f.getName(), "fileToDownload")) {return true;}
		if(Strings.equal(f.getName(), "syncBit")) {return true;}
		if(Strings.equal(f.getName(), "id")) {return true;}
		return false;
	}

}
