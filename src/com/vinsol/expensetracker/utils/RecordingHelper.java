package com.vinsol.expensetracker.utils;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

public class RecordingHelper {
	
	///////	   ********    Declaring variables and classes  ********    ///////
	MediaRecorder mRecorder;
	private boolean isRecording = false;

	
	////////   *********     Constructor   *********   //////////
	public RecordingHelper(String mFileName) {
		mRecorder = new MediaRecorder();
		File mDirectory = new File("/mnt/sdcard/ExpenseTracker/Audio");
		mDirectory.mkdirs();
		File mPath = new File(mDirectory, mFileName+".amr");
		mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(mPath.toString());
		mRecorder.setMaxDuration(60000*60);
	}
	
	
	///////   *********    Function to start recording   *********   /////////
	public void startRecording(){
		try {
			mRecorder.prepare();
			mRecorder.start();
			isRecording = true;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	////////     ***********    Function to stop recording ********** //////////
	public void stopRecording(){
		if(isRecording){
			mRecorder.stop();
			mRecorder.release();
			isRecording = false;
		}
	}
	
	
	////////     **********   Function to check whether recorder is running or not    ********   /////////
	public boolean isRecording(){
		return isRecording;
	}
}
