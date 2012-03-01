/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.vinsol.expensetracker.R;

public class ConfirmSaveEntryDialog extends Dialog implements android.view.View.OnClickListener {

	private boolean isOK;
	
	public ConfirmSaveEntryDialog(Context context) {
		super(context);
		setContentView(R.layout.delete_dialog);
		setTitle(context.getString(R.string.confirm_discard));
	}
	
	public void setMessage(String message) {
		TextView dialogMessage = (TextView) findViewById(R.id.dialog_message);
		dialogMessage.setText(message);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox) findViewById(R.id.disable_delete_dialog_checkbox)).setVisibility(View.GONE);
		TextView dialogMessage = (TextView) findViewById(R.id.dialog_message);
		dialogMessage.setTypeface(Typeface.DEFAULT);
		dialogMessage.setPadding(0, 0, 0, 15);
		((Button) findViewById(R.id.delete_dialog_ok)).setOnClickListener(this);
		((Button) findViewById(R.id.delete_dialog_cancel)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.delete_dialog_ok:
			isOK = true;
			Toast.makeText(getContext(), getContext().getString(R.string.entry_discarded), Toast.LENGTH_SHORT).show();
			break;

		case R.id.delete_dialog_cancel:
			isOK = false;
			break;
			
		default:
			break;
		}
		dismiss();
	}

	public boolean isOK() {
		return isOK;
	}

}
