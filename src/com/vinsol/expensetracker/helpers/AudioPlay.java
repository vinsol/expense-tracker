package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

public class AudioPlay {

	// ///// ******** Declaring variables and classes ******** ///////
	File mPath;
	MediaPlayer mPlayer;
	Context mContext;

	// ////// ********* Constructor ********* //////////
	public AudioPlay(String mFileName, Context _context) {
		mContext = _context;
		mPlayer = new MediaPlayer();
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File mDirectory = new File("/mnt/sdcard/ExpenseTracker/Audio");
			mDirectory.mkdirs();
			mPath = new File(mDirectory, mFileName + ".amr");
			mPlayer.setScreenOnWhilePlaying(true);
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mPlayer.setDataSource(mPath.toString());
				mPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
	}
	
	public AudioPlay(String mFileName, Context _context,String fav) {
		mContext = _context;
		mPlayer = new MediaPlayer();
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File mDirectory = new File("/mnt/sdcard/ExpenseTracker/Favorite/Audio");
			mDirectory.mkdirs();
			mPath = new File(mDirectory, mFileName + ".amr");
			mPlayer.setScreenOnWhilePlaying(true);
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mPlayer.setDataSource(mPath.toString());
				mPlayer.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
	}

	// ////// ********* Function to start audio playback ******** //////////////
	public void startPlayBack() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			try {
				mPlayer.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
	}

	// ////// ********* Function to stop audio playback ********* ///////////
	public void stopPlayBack() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			if (mPlayer.isPlaying())
				mPlayer.stop();
		} else {
			Toast.makeText(mContext, "sdcard not available", Toast.LENGTH_LONG)
					.show();
		}
	}

	// /////// ********** Function to get playback time ********* ///////////
	public int getPlayBackTime() {
		return mPlayer.getDuration();
	}

	// /////// ********** Check whether audio is playing or not ***********
	// ////////////
	public boolean isAudioPlaying() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return mPlayer.isPlaying();
		} else {
			return false;
		}
	}
}
