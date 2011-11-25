package com.vinsol.expensetracker;

import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.location.LocationData;
import com.vinsol.expensetracker.location.LocationLast;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	public static String mCurrentLocation;
	public static Location mLocation;
	private DatabaseAdapter mDatabaseAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ///////   ****** No Title Bar   ********* /////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        
        
        //temp view of graph
        //******start view******//
//        float[] values = new float[] { 200.0f,100.5f, 22.5f, 140.0f , 3.0f,89.0f,72.0f };
//		String[] horlabels = new String[] { "mon", "tue", "wed", "thu","fri","sat","sun" };
//		GraphView graphView = new GraphView(this, values, "GraphTest",horlabels);
//		
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.FILL_PARENT,
//						LinearLayout.LayoutParams.WRAP_CONTENT
//						);
//		
//		params.setMargins(20, 40, 40, 50);
//		
//		LinearLayout layout  = (LinearLayout) findViewById(R.id.main_graph);
//		layout.addView(graphView, params);
		//******stop view******//
		
		
		
//		DatabaseAdapter adapter =new DatabaseAdapter(this);
//		adapter.open();
//		adapter.drop_table();
//		adapter.close();
//		
		
        /////////     *********    DatabaseAdaptor initialize     *********    ////////
        mDatabaseAdapter = new DatabaseAdapter(this);
        
        
        
        
		//////     *********    Adding Click Listeners to MainActivity **********   /////////
		
		
		
		//opens text entry Activity
		Button main_text = (Button) findViewById(R.id.main_text);
		main_text.setOnClickListener(this);
		
		//opens voice Activity
		Button main_voice = (Button) findViewById(R.id.main_voice);
		main_voice.setOnClickListener(this);
		
		
		//opens Camera Activity
		Button main_camera = (Button) findViewById(R.id.main_camera);
		main_camera.setOnClickListener(this);
		
		
		
		//opens Favorite Activity
		Button main_favorite = (Button) findViewById(R.id.main_favorite);
		main_favorite.setOnClickListener(this);
		
		
		//opens Save Reminder Activity
		Button main_save_reminder = (Button) findViewById(R.id.main_save_reminder);
		main_save_reminder.setOnClickListener(this);
		
		
		//opens ListView
		ImageView main_listview = (ImageView) findViewById(R.id.main_listview);
		main_listview.setOnClickListener(this);
		
		
		/////////    ********    Finished Adding Click Listeners *********    ///////////
		
    }

    @Override
    protected void onResume() {
    	
    	/////////    ********    Starts GPS and Check for Location each time Activity Resumes *******   ////////
    	new LocationData(this);
    	LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
		
		
		
    	super.onResume();
    }
    
    
    
	@Override
	public void onClick(View v) {
		
		//////   *******    opens TextEntry Activity    ********    ///////////
		
		if(v.getId() == R.id.main_text){
			Intent intentTextEntry = new Intent(this, TextEntry.class);
			long _id = insertToDatabase(R.string.text);
			Bundle bundle = new Bundle();
			bundle.putLong("_id", _id);
			intentTextEntry.putExtra("textEntryBundle", bundle);
			startActivity(intentTextEntry);
		}

		
		//////   *******    opens Voice Activity    ********    ///////////
		else if (v.getId() == R.id.main_voice){
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				Intent intentVoice = new Intent(this, Voice.class);
				long _id = insertToDatabase(R.string.voice);
				Bundle bundle = new Bundle();
				bundle.putLong("_id", _id);
				intentVoice.putExtra("voiceBundle", bundle);
				startActivity(intentVoice);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
		}
		
		
		//////   *******    opens Camera Activity    ********    ///////////
		else if(v.getId() == R.id.main_camera){
			if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				Intent intentCamera = new Intent(this, CameraActivity.class);
				long _id = insertToDatabase(R.string.camera);
				Bundle bundle = new Bundle();
				bundle.putLong("_id", _id);
				intentCamera.putExtra("cameraBundle", bundle);
				startActivity(intentCamera);
			} else {
				Toast.makeText(this, "sdcard not available", Toast.LENGTH_SHORT).show();
			}
		}

		
		//////   *******    opens Favorite Activity    ********    ///////////
		else if(v.getId() == R.id.main_favorite){
			Intent intentFavorite = new Intent(this, FavoriteActivity.class);
			long _id = insertToDatabase(R.string.favorite_entry);
			Bundle bundle = new Bundle();
			bundle.putLong("_id", _id);
			intentFavorite.putExtra("favoriteBundle", bundle);
			startActivity(intentFavorite);
		}
		
		
		//////   *******    opens List Activity  and adds unknown entry to database  ********    ///////////
		else if(v.getId() == R.id.main_save_reminder){
			insertToDatabase(R.string.unknown);
			Intent intentListView = new Intent(this, ExpenseListing.class);
			startActivity(intentListView);
		}

		
		//////   *******    opens ListView Activity    ********    ///////////
		else if(v.getId() == R.id.main_listview){
			Intent intentListView = new Intent(this, ExpenseListing.class);
			startActivity(intentListView);
		}
	
	}

	
	/////////    ********   function to mark entry into the database and returns the id of the new entry *****  //////
	private long insertToDatabase(int type){
		HashMap<String, String> _list = new HashMap<String, String>();
		Calendar mCalendar = Calendar.getInstance();
		_list.put(DatabaseAdapter.KEY_DATE_TIME, Long.toString(mCalendar.getTimeInMillis()));
		
		if(MainActivity.mCurrentLocation != null){
			_list.put(DatabaseAdapter.KEY_LOCATION, MainActivity.mCurrentLocation);
		}
		_list.put(DatabaseAdapter.KEY_TYPE, getString(type));
		_list.put(DatabaseAdapter.KEY_FAVORITE, getString(R.string.favorite_not));
		mDatabaseAdapter.open();
		long _id = mDatabaseAdapter.insert_to_database(_list);
		mDatabaseAdapter.close();
		return _id;
	}
	
	@Override
	protected void onStop() {
		mLocation = null;
		mCurrentLocation = null;
		super.onStop();
	}
	
}