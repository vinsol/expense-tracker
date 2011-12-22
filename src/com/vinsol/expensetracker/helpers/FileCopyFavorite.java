package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyFavorite {
	public FileCopyFavorite(File source,File destinition) {
		copy(source,destinition);
	}

	public FileCopyFavorite(long _id,long targetId) {
		File createDirectories = new File("/sdcard/ExpenseTracker/Favorite/Audio");
		createDirectories.mkdirs();
		copyAll(_id,targetId);
	}
	
	public FileCopyFavorite(long _id,long targetId,String from) {
		File createDirectories = new File("/sdcard/ExpenseTracker/Favorite/Audio");
		createDirectories.mkdirs();
		copyAllFrom(_id,targetId);
	}

	private void copyAllFrom(long _id,long targetId) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File("/sdcard/ExpenseTracker/Favorite/" + _id + ".jpg");
			File mfile = new File("/sdcard/ExpenseTracker/" + targetId + ".jpg");
			copy(file,mfile);
			file = new File("/sdcard/ExpenseTracker/Favorite/" + _id + "_small.jpg");
			mfile = new File("/sdcard/ExpenseTracker/" + targetId + "_small.jpg");
			copy(file,mfile);
			file = new File("/sdcard/ExpenseTracker/Favorite/" + _id + "_thumbnail.jpg");
			mfile = new File("/sdcard/ExpenseTracker/" + targetId + "_thumbnail.jpg");
			copy(file,mfile);
			file = new File("/sdcard/ExpenseTracker/Favorite/Audio/" + _id + ".amr");
			mfile = new File("/sdcard/ExpenseTracker/Audio/" + targetId + ".amr");
			copy(file,mfile);
		}
	}
	
	private void copyAll(long _id,long targetId) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File("/sdcard/ExpenseTracker/" + _id + ".jpg");
			File mfile = new File("/sdcard/ExpenseTracker/Favorite/" + targetId + ".jpg");
			copy(file,mfile);
			file = new File("/sdcard/ExpenseTracker/" + _id + "_small.jpg");
			mfile = new File("/sdcard/ExpenseTracker/Favorite/" + targetId + "_small.jpg");
			copy(file,mfile);
			file = new File("/sdcard/ExpenseTracker/" + _id + "_thumbnail.jpg");
			mfile = new File("/sdcard/ExpenseTracker/Favorite/" + targetId + "_thumbnail.jpg");
			copy(file,mfile);
			file = new File("/sdcard/ExpenseTracker/Audio/" + _id + ".amr");
			mfile = new File("/sdcard/ExpenseTracker/Favorite/Audio/" + targetId + ".amr");
			copy(file,mfile);
		}
	}

	private void copy(File source,File target) {
		try {
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(target);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
		}
	}
}
