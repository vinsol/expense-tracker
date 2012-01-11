/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.show;

import com.vinsol.expensetracker.R;

import android.app.Activity;
import android.widget.TextView;

public class ShowLocationHandler {

	private TextView showLocation;

	public ShowLocationHandler(Activity activity, String location) {
		showLocation = (TextView) activity.findViewById(R.id.show_location);
		showLocation.setText(location);
	}

}
