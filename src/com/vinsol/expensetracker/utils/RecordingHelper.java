package com.vinsol.expensetracker.utils;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.widget.Toast;

public class RecordingHelper {
	
	///////	   ********    Declaring variables and classes  ********    ///////
	MediaRecorder mRecorder;
	private boolean isRecording = false;
	private Context mContext;
	
	////////   *********     Constructor   *********   //////////
	public RecordingHelper(String mFileName,Context _context) {
		mRecorder = new MediaRecorder();
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			File mDirectory = new File("/mnt/sdcard/ExpenseTracker/Audio");
			mDirectory.mkdirs();
			File mPath = new File(mDirectory, mFileName+".amr");
			mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(mPath.toString());
			mRecorder.setMaxDuration(60000*60);
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	
	///////   *********    Function to start recording   *********   /////////
	public void startRecording(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			try {
				mRecorder.prepare();
				mRecorder.start();
				isRecording = true;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	////////     ***********    Function to stop recording ********** //////////
	public void stopRecording(){
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			if(isRecording){
				mRecorder.stop();
				mRecorder.release();
				isRecording = false;
			}
		} else {
			
		}
	}
	
	
	////////     **********   Function to check whether recorder is running or not    ********   /////////
	public boolean isRecording(){
		return isRecording;
	}
}
