/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite implements Parcelable {

	public String favId = null;
	public String amount;
	public String description;
	public String type;
	public String location;
	public String myHash;

	public static final Parcelable.Creator<Favorite> CREATOR = new Parcelable.Creator<Favorite>() {
    	public Favorite createFromParcel(Parcel in) {
    		return new Favorite(in);
    	}
 
        public Favorite[] newArray(int size) {
        	return new Favorite[size];
        }
    };
    
    public Favorite() {}
    
    public Favorite(Parcel in) {
    	amount = in.readString();
    	description = in.readString();
    	type = in.readString();
    	favId = in.readString();
    	location = in.readString();
    	myHash = in.readString();
    };
    
    @Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(amount);
		dest.writeString(description);
		dest.writeString(type);
		dest.writeString(favId);
		dest.writeString(location);
		dest.writeString(myHash);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
}
