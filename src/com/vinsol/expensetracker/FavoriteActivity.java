package com.vinsol.expensetracker;

import com.vinsol.expensetracker.location.LocationLast;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class FavoriteActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ///// ****** No Title Bar ********* /////////

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.text_voice_camera);

		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		handleUI();

		// ///// ******* Handles Date Bar ******* ////////
		new DateHandler(this);

		// ////// ********* Get Last most accurate location info *********
		// /////////
		LocationLast mLocationLast = new LocationLast(this);
		mLocationLast.getLastLocation();
	}

	private void handleUI() {
		// ///// ******* Hide Main Body of layout and make favorite body visible
		// ******* ///////
		ScrollView mScrollView = (ScrollView) findViewById(R.id.text_voice_camera_body);
		mScrollView.setVisibility(View.GONE);
		RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.text_voice_camera_body_favorite);
		mRelativeLayout.setVisibility(View.VISIBLE);
	}
}
