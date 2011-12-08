package com.vinsol.expensetracker;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class EditLocationHandler {

	private TextView text_voice_camera_location;
	private Activity activity;
	private ImageView text_voice_camera_date_bar_next_arrow;
	private ImageView text_voice_camera_date_bar_previous_arrow;

	public EditLocationHandler(Context mContext, String location) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		text_voice_camera_date_bar_next_arrow = (ImageView) activity.findViewById(R.id.text_voice_camera_date_bar_next_arrow);
		text_voice_camera_date_bar_previous_arrow = (ImageView) activity.findViewById(R.id.text_voice_camera_date_bar_previous_arrow);
		text_voice_camera_date_bar_next_arrow.setVisibility(View.GONE);
		text_voice_camera_date_bar_previous_arrow.setVisibility(View.GONE);
		text_voice_camera_location = (TextView) activity.findViewById(R.id.text_voice_camera_date_bar_dateview);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		text_voice_camera_location.setLayoutParams(params);
		text_voice_camera_location.setTypeface(Typeface.DEFAULT);
		text_voice_camera_location.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		text_voice_camera_location.setTextColor(Color.BLACK);
		text_voice_camera_location.setMaxLines(1);
		text_voice_camera_location.setText(location);
	}

}
