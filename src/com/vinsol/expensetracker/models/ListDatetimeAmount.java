package com.vinsol.expensetracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ListDatetimeAmount implements Parcelable{
	public String dateTime;
	public String amount;
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(dateTime);
		dest.writeString(amount);
	}
}
