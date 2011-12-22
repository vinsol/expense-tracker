package com.vinsol.expensetracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DisplayList extends Entry implements Parcelable{
	public String timeLocation;
	public String location;
	public String favorite;
	public String type;
	public Long timeInMillis;
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(timeLocation);
		dest.writeString(location);
		dest.writeString(favorite);
		dest.writeString(type);
		dest.writeLong(timeInMillis);
		dest.writeLong(userId);
		dest.writeString(amount);
		dest.writeString(description);
	}
	
}
