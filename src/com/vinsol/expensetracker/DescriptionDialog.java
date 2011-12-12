package com.vinsol.expensetracker;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class DescriptionDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button description_dialog_cancel_button;
	private TextView description_dialog_textview;

	public DescriptionDialog(Context mContext, String string) {
		super(mContext);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.description_dialog);
		description_dialog_textview = (TextView) findViewById(R.id.description_dialog_textview);
		description_dialog_cancel_button = (Button) findViewById(R.id.description_dialog_cancel_button);
		description_dialog_textview.setText(string);
		description_dialog_cancel_button.setOnClickListener(this);
		show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.description_dialog_cancel_button) {
			dismiss();
		}
	}
}
