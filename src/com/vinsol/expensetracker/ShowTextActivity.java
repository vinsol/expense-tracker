package com.vinsol.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowTextActivity extends Activity implements OnClickListener{

	private ArrayList<String> mShowList;
	private Bundle intentExtras;
	private Long _id = null;
	private DatabaseAdapter mDatabaseAdapter;
	private TextView show_text_voice_camera_amount;
	private TextView show_text_voice_camera_tag_textview;
	private Button show_text_voice_camera_delete;
	private Button show_text_voice_camera_edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_text_voice_camera);

		// ///// ****** Assigning memory ******* /////////
		show_text_voice_camera_amount = (TextView) findViewById(R.id.show_text_voice_camera_amount);
		show_text_voice_camera_tag_textview = (TextView) findViewById(R.id.show_text_voice_camera_tag_textview);
		show_text_voice_camera_delete = (Button) findViewById(R.id.show_text_voice_camera_delete);
		show_text_voice_camera_edit = (Button) findViewById(R.id.show_text_voice_camera_edit);
		mDatabaseAdapter = new DatabaseAdapter(this);
		
		// //////********* Get id from intent extras ******** ////////////

		intentExtras = getIntent().getBundleExtra("textShowBundle");
		mShowList = new ArrayList<String>();

		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mShowList.get(0));
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);
			show_text_voice_camera_tag_textview.setText(tag);
			show_text_voice_camera_amount.setText(amount);
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
			if(mShowList.get(7) != null)
				new ShowLocationHandler(this, mShowList.get(7));
			new FavoriteHelper(this, mShowList);
		}
		show_text_voice_camera_delete.setOnClickListener(this);
		show_text_voice_camera_edit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.show_text_voice_camera_delete) {
			if (_id != null) {
				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}
		
		if(v.getId() == R.id.show_text_voice_camera_edit){
			Intent editIntent = new Intent(this, TextEntry.class);
			editIntent.putExtra("textEntryBundle", intentExtras);
			startActivity(editIntent);
			finish();
		}
	}
}
