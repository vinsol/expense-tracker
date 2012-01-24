package com.vinsol.expensetracker.helpers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vinsol.expensetracker.R;

public class DeleteDialog extends Dialog implements android.view.View.OnClickListener {
	
	private boolean isDelete;
	
	public DeleteDialog(Context context) { 
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_dialog);
		Button yesButton = (Button) findViewById(R.id.delete_dialog_yes);
		Button noButton = (Button) findViewById(R.id.delete_dialog_no);
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
	
}
