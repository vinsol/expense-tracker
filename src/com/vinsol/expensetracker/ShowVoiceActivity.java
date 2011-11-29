package com.vinsol.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.vinsol.expensetracker.utils.AudioPlay;
import com.vinsol.expensetracker.utils.DisplayTime;
import com.vinsol.expensetracker.utils.FileDelete;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowVoiceActivity extends Activity implements OnClickListener {

	private TextView show_text_voice_camera_header_title;
	private ImageView show_text_voice_camera_voice_details_separator;
	private RelativeLayout show_text_voice_camera_voice_details;
	private TextView show_text_voice_camera_amount;
	private TextView show_text_voice_camera_tag_textview;
	private EditText show_text_voice_camera_tag;
	private Button show_text_voice_camera_delete;
	private View show_text_voice_camera_divider_amount_desc;
	private Button show_text_voice_camera_play_button;
	private Button show_text_voice_camera_stop_button;
	private Chronometer show_text_voice_camera_time_details_chronometer;
	private MyCount countDownTimer;
	private Button show_text_voice_camera_update_entry;
	private TextView show_text_voice_camera_description_show;

	private AudioPlay mAudioPlay;
	private Long _id = null;
	private Bundle intentExtras;
	private ArrayList<String> mShowList;
	private DatabaseAdapter mDatabaseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_text_voice_camera);

		show_text_voice_camera_header_title = (TextView) findViewById(R.id.show_text_voice_camera_header_title);
		show_text_voice_camera_voice_details_separator = (ImageView) findViewById(R.id.show_text_voice_camera_voice_details_separator);
		show_text_voice_camera_voice_details = (RelativeLayout) findViewById(R.id.show_text_voice_camera_voice_details);
		show_text_voice_camera_amount = (TextView) findViewById(R.id.show_text_voice_camera_amount);
		show_text_voice_camera_tag = (EditText) findViewById(R.id.show_text_voice_camera_tag);
		show_text_voice_camera_tag_textview = (TextView) findViewById(R.id.show_text_voice_camera_tag_textview);
		show_text_voice_camera_divider_amount_desc = findViewById(R.id.show_text_voice_camera_divider_amount_desc);
		show_text_voice_camera_delete = (Button) findViewById(R.id.show_text_voice_camera_delete);
		show_text_voice_camera_play_button = (Button) findViewById(R.id.show_text_voice_camera_play_button);
		show_text_voice_camera_stop_button = (Button) findViewById(R.id.show_text_voice_camera_stop_button);
		show_text_voice_camera_time_details_chronometer = (Chronometer) findViewById(R.id.show_text_voice_camera_time_details_chronometer);
		show_text_voice_camera_update_entry = (Button) findViewById(R.id.show_text_voice_camera_update_entry);
		show_text_voice_camera_description_show = (TextView) findViewById(R.id.show_text_voice_camera_description_show);

		mDatabaseAdapter = new DatabaseAdapter(this);

		intentExtras = getIntent().getBundleExtra("voiceShowBundle");

		if (intentExtras.containsKey("mDisplayList")) {
			mShowList = new ArrayList<String>();
			mShowList = intentExtras.getStringArrayList("mDisplayList");
			_id = Long.parseLong(mShowList.get(0));
			String amount = mShowList.get(2);
			String tag = mShowList.get(1);
			if (!(amount.equals("") || amount == null)) {
				if (!amount.contains("?"))
					show_text_voice_camera_amount.setText(amount);
			}
			if (!(tag.equals("") || tag == null || tag
					.equals(getString(R.string.unfinished_voiceentry)))) {
				show_text_voice_camera_tag_textview.setText(tag);
			} else {
				show_text_voice_camera_description_show
						.setVisibility(View.GONE);
				show_text_voice_camera_tag_textview.setVisibility(View.GONE);
				show_text_voice_camera_tag.setVisibility(View.VISIBLE);
				show_text_voice_camera_divider_amount_desc
						.setVisibility(View.GONE);
				show_text_voice_camera_update_entry.setVisibility(View.VISIBLE);
			}
			Calendar mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(Long.parseLong(mShowList.get(6)));
			new ShowDateHandler(this, mCalendar);
		}
		show_text_voice_camera_delete.setOnClickListener(this);
		show_text_voice_camera_play_button.setOnClickListener(this);
		show_text_voice_camera_stop_button.setOnClickListener(this);
		show_text_voice_camera_update_entry.setOnClickListener(this);

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			updateUI();
			if (intentExtras.containsKey("mDisplayList")) {
				File tempFile = new File("/sdcard/ExpenseTracker/Audio/" + _id
						+ ".amr");

				if (tempFile.canRead()) {
					mAudioPlay = new AudioPlay(Long.toString(_id), this);
					show_text_voice_camera_stop_button.setVisibility(View.GONE);
					show_text_voice_camera_play_button
							.setVisibility(View.VISIBLE);
					show_text_voice_camera_time_details_chronometer
							.setText(new DisplayTime()
									.getDisplayTime(mAudioPlay
											.getPlayBackTime()));
				} else {
					show_text_voice_camera_time_details_chronometer
							.setText("Audio File Missing");
					show_text_voice_camera_stop_button.setVisibility(View.GONE);
					show_text_voice_camera_play_button.setVisibility(View.GONE);
				}
				new FavoriteHelper(this, mShowList);
			}
		} else {
			Toast.makeText(this, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
		
	}

	private void updateUI() {
		// ///// ***** Sets Title Voice Entry *********///////
		show_text_voice_camera_header_title.setText("Voice Entry");

		// ///// ***** Sets Title Voice Entry *********///////
		show_text_voice_camera_voice_details_separator
				.setVisibility(View.VISIBLE);

		// //// ****** Shows Voice Details ********////////
		show_text_voice_camera_voice_details.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.show_text_voice_camera_delete) {

			if (_id != null) {

				// /// ******* If Audio PlayBack is there stop playing audio
				// *******//////
				try {
					if (mAudioPlay.isAudioPlaying()) {
						mAudioPlay.stopPlayBack();
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				show_text_voice_camera_time_details_chronometer.stop();

				new FileDelete(_id);

				mDatabaseAdapter.open();
				mDatabaseAdapter.deleteDatabaseEntryID(Long.toString(_id));
				mDatabaseAdapter.close();
				Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			}
		}

		if (v.getId() == R.id.show_text_voice_camera_play_button) {
			// //// ******** to handle playback of recorded file *********
			// ////////
			mAudioPlay = new AudioPlay(_id + "", this);

			// ///// ******* Chronometer Starts Countdown ****** ///////
			countDownTimer = new MyCount(mAudioPlay.getPlayBackTime(), 1000);

			// //// ****** Handles UI items on button click ****** ///////
			show_text_voice_camera_play_button.setVisibility(View.GONE);
			show_text_voice_camera_stop_button.setVisibility(View.VISIBLE);

			// /// ******** Start Audio Playback and counter to play audio
			// ****** ///////
			if (!mAudioPlay.isAudioPlaying()) {
				mAudioPlay.startPlayBack();
			} else {
				mAudioPlay.stopPlayBack();
				mAudioPlay.startPlayBack();
			}
			countDownTimer.start();
		}

		if (v.getId() == R.id.show_text_voice_camera_stop_button) {
			try {
				countDownTimer.cancel();
			} catch (NullPointerException e) {
			}
			;

			// //// ****** Handles UI items on button click ****** ///////
			show_text_voice_camera_stop_button.setVisibility(View.GONE);
			show_text_voice_camera_play_button.setVisibility(View.VISIBLE);

			// //// ******* Stop Recording Audio and stop chronometer ********
			// ////////
			show_text_voice_camera_time_details_chronometer.stop();
			try {
				if (mAudioPlay.isAudioPlaying())
					mAudioPlay.stopPlayBack();
			} catch (Exception e) {
			}
			show_text_voice_camera_time_details_chronometer
					.setText(new DisplayTime().getDisplayTime(mAudioPlay
							.getPlayBackTime()));
		}

		if (v.getId() == R.id.show_text_voice_camera_update_entry) {
			if (show_text_voice_camera_tag.isShown()) {
				if (show_text_voice_camera_tag.getText().toString() != "")
					saveEntry();
			}
		}
	}

	// /////// ********* CountdownTimer for Chronometer ********* //////////
	// countdowntimer is an abstract class, so extend it and fill in methods
	private class MyCount extends CountDownTimer {

		DisplayTime mDisplayTime;

		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mDisplayTime = new DisplayTime();
		}

		@Override
		public void onFinish() {
			show_text_voice_camera_time_details_chronometer
					.setText(mDisplayTime.getDisplayTime(mAudioPlay
							.getPlayBackTime()));
			show_text_voice_camera_stop_button.setVisibility(View.GONE);
			show_text_voice_camera_play_button.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			show_text_voice_camera_time_details_chronometer
					.setText(mDisplayTime.getDisplayTime(millisUntilFinished));
		}
	}

	// /// ****************** Handling back press of key ********** ///////////
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBackPressed();
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onBackPressed() {
		// This will be called either automatically for you on 2.0
		// or later, or by the code above on earlier versions of the platform.
		if (show_text_voice_camera_tag.isShown()) {
			if (show_text_voice_camera_tag.getText().toString() != "")
				saveEntry();
		}

		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {
		}
		finish();
	}

	@Override
	protected void onPause() {

		// //// ***** Check whether audio is recording or not ******* ///////
		// //// ****** If audio recording started then stop recording audio
		// ***** ///////
		try {
			if (mAudioPlay.isAudioPlaying())
				mAudioPlay.stopPlayBack();
		} catch (Exception e) {

		}
		super.onPause();
	}

	private void saveEntry() {
		// ///// ******* Creating HashMap to update info ******* ////////
		HashMap<String, String> _list = new HashMap<String, String>();
		_list.put(DatabaseAdapter.KEY_ID, Long.toString(_id));

		if (show_text_voice_camera_tag.getText().toString() != "") {
			_list.put(DatabaseAdapter.KEY_TAG, show_text_voice_camera_tag
					.getText().toString());
		}

		// //// ******* Update database if user added additional info *******
		// ///////
		mDatabaseAdapter.open();
		mDatabaseAdapter.editDatabase(_list);
		mDatabaseAdapter.close();
		finish();
	}
}
