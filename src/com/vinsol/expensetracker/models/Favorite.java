package com.vinsol.expensetracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite extends Entry implements Parcelable{
	
	public String type;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(type);
		dest.writeString(userId);
		dest.writeString(amount);
		dest.writeString(description);
	}
}
