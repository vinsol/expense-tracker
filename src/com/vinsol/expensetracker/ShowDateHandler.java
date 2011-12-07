package com.vinsol.expensetracker;

import java.util.Calendar;

import com.vinsol.expensetracker.utils.DisplayDate;
import com.vinsol.expensetracker.utils.DisplayTime;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class ShowDateHandler {
	private TextView show_text_voice_camera_header_title;
	private Activity activity;

	public ShowDateHandler(Context mContext, String timeInMillis) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_header_title = (TextView) activity.findViewById(R.id.show_text_voice_camera_header_title);
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(Long.parseLong(timeInMillis));
		
		DisplayDate mDisplayDate = new DisplayDate(mCalendar);
		DisplayTime mDisplayTime = new DisplayTime();
		//TODO
		show_text_voice_camera_header_title.setText(mDisplayDate.getDisplayDate()+"at "+mDisplayTime.getDisplayTime(Long.parseLong(timeInMillis)));
	}

	public ShowDateHandler(Context mContext) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_header_title = (TextView) activity.findViewById(R.id.show_text_voice_camera_header_title);
		show_text_voice_camera_header_title.setText("Camera Entry");
	}
}
