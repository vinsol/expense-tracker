package com.vinsol.expensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //temp view of graph
        //******start view******//
        float[] values = new float[] { 200.0f,100.5f, 22.5f, 140.0f , 3.0f,89.0f,72.0f };
		String[] horlabels = new String[] { "mon", "tue", "wed", "thu","fri","sat","sun" };
		GraphView graphView = new GraphView(this, values, "GraphTest",horlabels);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT
						);
		
		params.setMargins(0, 40, 0, 5);
		
		LinearLayout layout  = (LinearLayout) findViewById(R.id.main_graph);
		layout.addView(graphView, params);
		//******stop view******//
		
		
		
		
		
    }
}