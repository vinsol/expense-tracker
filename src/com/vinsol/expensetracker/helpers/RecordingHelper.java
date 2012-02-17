/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.widget.Toast;

public class RecordingHelper {

	/////// ******** Declaring variables and classes ******** ///////
	MediaRecorder mRecorder;
	private boolean isRecording = false;
	private Context mContext;

	//////// ********* Constructor ********* //////////
	public RecordingHelper(File mPath, Context _context) {
		mRecorder = new MediaRecorder();
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(mPath.toString());
			mRecorder.setMaxDuration(60000 * 60);
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	/////// ********* Function to start recording ********* /////////
	public void startRecording() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			try {
				mRecorder.prepare();
				mRecorder.start();
				isRecording = true;
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}

	//////// *********** Function to stop recording ********** //////////
	public void stopRecording() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			if (isRecording) {
				mRecorder.stop();
				mRecorder.release();
				isRecording = false;
			}
		}
	}

	//////// ********** Function to check whether recorder is running or not ******** /////////
	public boolean isRecording() {
		return isRecording;
	}
}
