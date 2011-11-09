package com.vinsol.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener{
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
	public void onClick(View v) {
		
		//////   *******    opens TextEntry Activity    ********    ///////////
		
		if(v.getId() == R.id.main_text){
			Intent intentTextEntry = new Intent(this, TextEntry.class);
			startActivity(intentTextEntry);
		}

		
		//////   *******    opens Voice Activity    ********    ///////////
		else if (v.getId() == R.id.main_voice){
			Intent intentVoice = new Intent(this, Voice.class);
			startActivity(intentVoice);
		}
		
		
		//////   *******    opens Camera Activity    ********    ///////////
		else if(v.getId() == R.id.main_camera){
			Intent intentCamera = new Intent(this, CameraActivity.class);
			startActivity(intentCamera);
		}

		
		//////   *******    opens Favorite Activity    ********    ///////////
		else if(v.getId() == R.id.main_favorite){
			Log.v("Favorite", "Favorite");
		}
		
		
		//////   *******    opens SaveReminder Activity    ********    ///////////
		else if(v.getId() == R.id.main_save_reminder){
			Log.v("Save Reminder", "Save Reminder");
		}

		
		//////   *******    opens ListView Activity    ********    ///////////
		else if(v.getId() == R.id.main_listview){
			Log.v("ListView", "ListView");
		}

		
	}
}