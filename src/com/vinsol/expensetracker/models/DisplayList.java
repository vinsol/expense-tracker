package com.vinsol.expensetracker.models;

import android.os.Parcel;

public class DisplayList extends Favorite{
	
	public String timeLocation;
	public String location;
	public String favorite;
	public Long timeInMillis;
	public String displayTime;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(timeLocation);
		dest.writeString(location);
		dest.writeString(favorite);
		dest.writeLong(timeInMillis);
		dest.writeString(displayTime);
	}
	
}
