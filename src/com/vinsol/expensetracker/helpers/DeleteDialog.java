/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.vinsol.expensetracker.R;

public class DeleteDialog extends AlertDialog implements DialogInterface.OnClickListener {
	
	private boolean isDelete;
	private CheckBox checkBox;
	
	public DeleteDialog(Context context) { 
		super(context);
		setTitle(context.getString(R.string.confirm_delete));
		setMessage(context.getString(R.string.delete_dialog_text));
		View checkBoxView = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		setView(checkBoxView);
		setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), this);
		setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), this);
		checkBox = (CheckBox) checkBoxView.findViewById(R.id.disable_delete_dialog_checkbox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setDeletePrefs();
			}
		});
	}

	public boolean isDelete() {
		return isDelete;
	}
	
	private void setDeletePrefs() {
		new SharedPreferencesHelper(getContext()).setDeletePrefs(checkBox.isChecked());
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			isDelete = true;
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			isDelete = false;
			break;
		default:
			break;
		}
		dismiss();		
	}
	
}
