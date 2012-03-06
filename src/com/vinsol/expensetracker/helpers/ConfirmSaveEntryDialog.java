/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.vinsol.expensetracker.R;

public class ConfirmSaveEntryDialog extends AlertDialog implements DialogInterface.OnClickListener {

	private boolean isOK;
	
	public ConfirmSaveEntryDialog(Context context) {
		super(context);
		setTitle(context.getString(R.string.confirm_discard));
		setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), this);
		setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel), this);
	}

	public boolean isOK() {
		return isOK;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		
		case BUTTON_POSITIVE:
			isOK = true;
			Toast.makeText(getContext(), getContext().getString(R.string.entry_discarded), Toast.LENGTH_SHORT).show();
			break;

		case BUTTON_NEGATIVE:
			isOK = false;
			break;
			
		default:
			break;
		}
		dismiss();
	}

}
