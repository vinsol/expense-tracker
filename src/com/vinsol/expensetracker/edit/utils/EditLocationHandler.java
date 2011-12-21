package com.vinsol.expensetracker.edit.utils;


import com.vinsol.expensetracker.R;

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

	private TextView editLocation;
	private Activity activity;
	private ImageView editNextArrow;
	private ImageView editPreviousArrow;

	public EditLocationHandler(Context mContext, String location) {
		activity = (mContext instanceof Activity) ? (Activity) mContext : null;
		editNextArrow = (ImageView) activity.findViewById(R.id.edit_date_bar_next_arrow);
		editPreviousArrow = (ImageView) activity.findViewById(R.id.edit_date_bar_previous_arrow);
		editNextArrow.setVisibility(View.GONE);
		editPreviousArrow.setVisibility(View.GONE);
		editLocation = (TextView) activity.findViewById(R.id.edit_date_bar_dateview);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		editLocation.setLayoutParams(params);
		editLocation.setTypeface(Typeface.DEFAULT);
		editLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		editLocation.setTextColor(Color.BLACK);
		editLocation.setMaxLines(1);
		editLocation.setText(location);
	}

}
