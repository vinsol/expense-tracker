package com.vinsol.expensetracker;

import java.util.HashMap;

import com.vinsol.expensetracker.location.LocationLast;
import com.vinsol.expensetracker.utils.DateHelper;
import com.vinsol.expensetracker.utils.FileDelete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TextEntry extends Activity implements OnClickListener{

	private DatabaseAdapter mDatabaseAdapter;
	private Long _id;
	private Bundle intentExtras;
	private EditText text_voice_camera_amount;
	private EditText text_voice_camera_tag;
	private TextView text_voice_camera_date_bar_dateview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

        ///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.text_voice_camera);
        
        mDatabaseAdapter = new DatabaseAdapter(this);
        text_voice_camera_amount = (EditText) findViewById(R.id.text_voice_camera_amount);
        text_voice_camera_tag = (EditText) findViewById(R.id.text_voice_camera_tag);
        text_voice_camera_date_bar_dateview = (TextView) findViewById(R.id.text_voice_camera_date_bar_dateview);
        
        ////////*********     Get id from intent extras     ********   ////////////
        intentExtras = getIntent().getBundleExtra("textEntryBundle");
        _id = intentExtras.getLong("_id");
        ///////  ******* Sets Header Margin  ******* ////////
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 30, 0, 20);
        
        EditText text_voice_camera_amount = (EditText) findViewById(R.id.text_voice_camera_amount);
        text_voice_camera_amount.setLayoutParams(params);
        
        
        ////////   ********    Handle Date Bar   *********   ////////
        new DateHandler(this);
        
        ////////   *********     Get Last most accurate location info   *********   /////////
        LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
		
		setClickListeners();
		
		
	}
	
	@Override
	protected void onResume() {
		new LocationLast(this);
		super.onResume();
	}
	
	private void setClickListeners() {
		////////    *******    Adding Click Listeners to UI Items ******** //////////
		
		Button text_voice_camera_save_entry = (Button) findViewById(R.id.text_voice_camera_save_entry);
		text_voice_camera_save_entry.setOnClickListener(this);
		
		Button text_voice_camera_delete = (Button) findViewById(R.id.text_voice_camera_delete);
		text_voice_camera_delete.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		////////********  Adding Action to save entry     *********    ///////////
		
		if(v.getId() == R.id.text_voice_camera_save_entry){
			///////    *******  Creating HashMap to update info   *******  ////////
			HashMap<String, String> _list = new HashMap<String, String>();
			_list.put(DatabaseAdapter.KEY_ID, Long.toString(_id));
			_list.put(DatabaseAdapter.KEY_AMOUNT, text_voice_camera_amount.getText().toString());
			if(text_voice_camera_tag.getText().toString() != ""){
				_list.put(DatabaseAdapter.KEY_TAG, text_voice_camera_tag.getText().toString());
			}
			try{
				DateHelper mDateHelper = new DateHelper(text_voice_camera_date_bar_dateview.getText().toString());
				_list.put(DatabaseAdapter.KEY_DATE_TIME, mDateHelper.getTimeMillis()+"");
			} catch (Exception e){
				e.printStackTrace();
			}
			//////    *******   Update database if user added additional info   *******  ///////
			mDatabaseAdapter.open();
			mDatabaseAdapter.editDatabase(_list);
			mDatabaseAdapter.close();
			
			finish();
			Intent intentExpenseListing = new Intent(this, ExpenseListing.class);
			startActivity(intentExpenseListing);
		}
	
	
		/////////     *********   Adding action if delete button **********  /////////
	
		if(v.getId() == R.id.text_voice_camera_delete){
			new FileDelete(_id);
			
			//////   *******   Delete entry from database ********   /////////
			mDatabaseAdapter.open();
			mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
			mDatabaseAdapter.close();
			finish();
		}
	}
}
