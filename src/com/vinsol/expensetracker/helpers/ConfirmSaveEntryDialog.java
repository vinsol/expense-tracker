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

	private CheckBox checkBox;
	
	public ConfirmSaveEntryDialog(Context context,int stringResId) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		TextView dialogMessage = (TextView) findViewById(R.id.dialog_message);
		dialogMessage.setText(stringResId);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_dialog);
		Button saveEntryButton = (Button) findViewById(R.id.delete_dialog_yes);
		Button discardButton = (Button) findViewById(R.id.delete_dialog_no);
		checkBox = (CheckBox) findViewById(R.id.disable_delete_dialog_checkbox);
		checkBox.setVisibility(View.GONE);
		saveEntryButton.setText(getContext().getString(R.string.save_entry));
		discardButton.setText(R.string.discard);
		saveEntryButton.setOnClickListener(this);
		discardButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_dialog_yes:
			break;

		case R.id.delete_dialog_no:
			break;
		default:
			break;
		}
		dismiss();
	}

}
