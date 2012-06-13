package com.vinsol.confconnect.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ExpenseTrackerExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> c) {
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
//		if(Strings.equal(f.getName(), "syncBit")) {return true;}
		return false;
	}

}
