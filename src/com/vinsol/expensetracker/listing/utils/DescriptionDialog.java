package com.vinsol.expensetracker.listing.utils;

import com.vinsol.expensetracker.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DescriptionDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button cancelButton;
	private TextView textview;

	public DescriptionDialog(Context mContext, String string) {
		super(mContext);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.description_dialog);
		textview = (TextView) findViewById(R.id.description_dialog_textview);
		cancelButton = (Button) findViewById(R.id.description_dialog_cancel_button);
		textview.setText(string);
		cancelButton.setOnClickListener(this);
		show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.description_dialog_cancel_button) {
			dismiss();
		}
	}
}
