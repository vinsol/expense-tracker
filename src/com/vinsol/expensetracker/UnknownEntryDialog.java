package com.vinsol.expensetracker;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class UnknownEntryDialog extends Dialog implements android.view.View.OnClickListener {

	private Button deleteButton;
	
	public UnknownEntryDialog(Context mContext) {
		super(mContext);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unknown_entry_dialog);
		show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.description_dialog_cancel_button) {
			dismiss();
		}
	}
}
