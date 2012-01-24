package com.vinsol.expensetracker.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.vinsol.expensetracker.R;

public class DeleteDialog extends Dialog {
	
	public DeleteDialog(Context context) { 
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_dialog);
	}
	
}
