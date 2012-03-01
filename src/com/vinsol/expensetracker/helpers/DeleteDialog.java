/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
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
		setTitle(context.getString(R.string.confirm_delete));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_dialog);
		Button yesButton = (Button) findViewById(R.id.delete_dialog_ok);
		Button noButton = (Button) findViewById(R.id.delete_dialog_cancel);
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
		case R.id.delete_dialog_ok:
			isDelete = true;
			break;

		case R.id.delete_dialog_cancel:
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
		new SharedPreferencesHelper(getContext()).setDeletePrefs(checkBox.isChecked());
	}
	
}
