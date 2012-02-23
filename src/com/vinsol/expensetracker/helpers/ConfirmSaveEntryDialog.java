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
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
	
	public void setButtonText(String yes,String no) {
		ImageButton saveEntryButton = (ImageButton) findViewById(R.id.delete_dialog_yes);
		ImageButton discardButton = (ImageButton) findViewById(R.id.delete_dialog_no);
		saveEntryButton.setBackgroundResource(R.drawable.save_entry_button_dialog_states);
		discardButton.setBackgroundResource(R.drawable.discard_button_states);
		saveEntryButton.setOnClickListener(this);
		discardButton.setOnClickListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox) findViewById(R.id.disable_delete_dialog_checkbox)).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.dialog_message)).setTypeface(Typeface.DEFAULT);
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
