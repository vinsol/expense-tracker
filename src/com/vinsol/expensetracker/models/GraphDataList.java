package com.vinsol.expensetracker.models;

import android.os.Parcel;

public class GraphDataList extends ListDatetimeAmount{
	
	public String idList;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(idList);
	}
}
