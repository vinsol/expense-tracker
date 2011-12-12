package com.vinsol.expensetracker;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class ShowLocationHandler {

	private TextView show_text_voice_camera_location;
	private Activity activity;

	public ShowLocationHandler(Context mContext, String location) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_location = (TextView) activity.findViewById(R.id.show_text_voice_camera_location);
		show_text_voice_camera_location.setText(location);
	}

}
