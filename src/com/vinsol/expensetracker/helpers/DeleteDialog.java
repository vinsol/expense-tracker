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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.vinsol.expensetracker.R;

public class DeleteDialog extends Dialog implements android.view.View.OnClickListener {
	
	private boolean isDelete;
	private CheckBox checkBox;
	
	public DeleteDialog(Context context) { 
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_dialog);
		Button yesButton = (Button) findViewById(R.id.delete_dialog_yes);
		Button noButton = (Button) findViewById(R.id.delete_dialog_no);
		checkBox = (CheckBox) findViewById(R.id.disable_delete_dialog_checkbox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setDeletePrefs();
			}
		});
		yesButton.setOnClickListener(this);
		noButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_dialog_yes:
			isDelete = true;
			break;

		case R.id.delete_dialog_no:
			isDelete = false;
			break;
		default:
			break;
		}
		dismiss();
	}

	public boolean isDelete() {
		return isDelete;
	}
	
	private void setDeletePrefs() {
		if(checkBox.isChecked()) {
			checkBox.setText(getContext().getString(R.string.delete_dialog_title_enable));
		} else {
			checkBox.setText(getContext().getString(R.string.delete_dialog_title_disable));
		}
		new SharedPreferencesHelper(getContext()).setDeletePrefs(checkBox.isChecked());
	}
	
}
