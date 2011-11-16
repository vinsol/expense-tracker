package com.vinsol.expensetracker.utils;

import java.io.File;
import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class AudioPlay {
	
	///////	   ********    Declaring variables and classes  ********    ///////
	File mPath;
	MediaPlayer mPlayer;
	
	////////   *********     Constructor   *********   //////////
	public AudioPlay(String mFileName) {
		File mDirectory = new File("/mnt/sdcard/ExpenseTracker/Audio");
		mDirectory.mkdirs();
		mPath = new File(mDirectory, mFileName+".amr");
		mPlayer = new MediaPlayer();
		mPlayer.setScreenOnWhilePlaying(true);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		try {
			mPlayer.setDataSource(mPath.toString());
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	////////   *********    Function to start audio playback    ********   //////////////
	public void startPlayBack(){
		try {
			mPlayer.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	////////    *********    Function to stop audio playback   *********   ///////////
	public void stopPlayBack(){
		if(mPlayer.isPlaying())
			mPlayer.stop();
	}
	
	
	/////////     **********    Function to get playback time  *********  ///////////
	public int getPlayBackTime(){
		return mPlayer.getDuration();
	}
	
	
	/////////    **********    Check whether audio is playing or not    ***********    ////////////
	public boolean isAudioPlaying(){
		return mPlayer.isPlaying();
	}
}
