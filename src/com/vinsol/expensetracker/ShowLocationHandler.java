package com.vinsol.expensetracker;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class ShowLocationHandler {

	private TextView showLocation;
	private Activity activity;

	public ShowLocationHandler(Context mContext, String location) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		showLocation = (TextView) activity.findViewById(R.id.show_location);
		showLocation.setText(location);
	}

}
