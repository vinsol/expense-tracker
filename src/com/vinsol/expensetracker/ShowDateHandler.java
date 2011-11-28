package com.vinsol.expensetracker;

import java.util.Calendar;

import com.vinsol.expensetracker.utils.DisplayDate;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class ShowDateHandler {

	private TextView show_text_voice_camera_date_bar_dateview;
	private Activity activity;

	public ShowDateHandler(Context mContext, Calendar mCalendar) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		show_text_voice_camera_date_bar_dateview = (TextView) activity
				.findViewById(R.id.show_text_voice_camera_date_bar_dateview);
		DisplayDate mDisplayDate = new DisplayDate(mCalendar);
		show_text_voice_camera_date_bar_dateview.setText(mDisplayDate
				.getDisplayDate());
	}

}
