/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vinsol.expensetracker.R;

public class ConfirmSaveEntryDialog extends Dialog implements android.view.View.OnClickListener {

	private boolean toSave;
	
	public ConfirmSaveEntryDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delete_dialog);
	}
	
	public void setMessage(String message) {
		TextView dialogMessage = (TextView) findViewById(R.id.dialog_message);
		dialogMessage.setText(message);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button saveEntryButton = (Button) findViewById(R.id.delete_dialog_yes);
		Button discardButton = (Button) findViewById(R.id.delete_dialog_no);
		((CheckBox) findViewById(R.id.disable_delete_dialog_checkbox)).setVisibility(View.GONE);
		saveEntryButton.setText(getContext().getString(R.string.save_entry));
		discardButton.setText(getContext().getString(R.string.discard));
		saveEntryButton.setOnClickListener(this);
		discardButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.delete_dialog_yes:
			toSave = true;
			break;

		case R.id.delete_dialog_no:
			toSave = false;
			break;
			
		default:
			break;
		}
		dismiss();
	}

	public boolean isToSave() {
		return toSave;
	}

}
