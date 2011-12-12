package com.vinsol.expensetracker;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class UnknownEntryDialog extends Dialog implements android.view.View.OnClickListener {

	private Button deleteButton;
	private Button textEntryButton;
	private Button voiceEntryButton;
	private Button cameraEntryButton;
	private Button favoriteEntryButton;
	
	public UnknownEntryDialog(Context mContext) {
		super(mContext);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.unknown_entry_dialog);
		textEntryButton = (Button) findViewById(R.id.main_text);
		deleteButton = (Button) findViewById(R.id.unknown_entry_dialog_delete);
		voiceEntryButton = (Button) findViewById(R.id.main_voice);
		cameraEntryButton = (Button) findViewById(R.id.main_camera);
		favoriteEntryButton = (Button) findViewById(R.id.main_favorite);
		textEntryButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		voiceEntryButton.setOnClickListener(this);
		cameraEntryButton.setOnClickListener(this);
		favoriteEntryButton.setOnClickListener(this);
		show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_text:
			
			break;
		case R.id.unknown_entry_dialog_delete:
			
			break;
		case R.id.main_voice:
				
			break;
		case R.id.main_camera:
			
			break;
		case R.id.main_favorite:
	
			break;
		default:
			break;
		}
	}
}
