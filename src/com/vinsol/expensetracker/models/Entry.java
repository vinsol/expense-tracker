/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Entry extends Favorite {
	
	public String id;
	public Long timeInMillis;
	
	public static final Parcelable.Creator<Entry> CREATOR = new Parcelable.Creator<Entry>() {
    	public Entry createFromParcel(Parcel in) {
    		return new Entry(in);
    	}
 
        public Entry[] newArray(int size) {
        	return new Entry[size];
        }
    };
    
    public Entry() {}
    
    public Entry(Parcel in) {
    	id = in.readString();
    	amount = in.readString();
    	description = in.readString();
    	type = in.readString();
    	location = in.readString();
    	favId = in.readString();
    	timeInMillis = in.readLong();
    	myHash = in.readString();
    };
    
    @Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(amount);
		dest.writeString(description);
		dest.writeString(type);
		dest.writeString(location);
		dest.writeString(favId);
		dest.writeLong(timeInMillis);
		dest.writeString(myHash);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
