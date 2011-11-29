package com.vinsol.expensetracker.utils;

import java.io.File;

public class FileDeleteFavorite {

	public FileDeleteFavorite(File file) {
		delete(file);
	}

	public FileDeleteFavorite(long _id) {
		deleteAll(_id);
	}
	

	private void deleteAll(long _id) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File("/sdcard/ExpenseTracker/Favorite/" + _id + ".jpg");
			delete(file);
			file = new File("/sdcard/ExpenseTracker/Favorite/" + _id + "_small.jpg");
			delete(file);
			file = new File("/sdcard/ExpenseTracker/Favorite/" + _id + "_thumbnail.jpg");
			delete(file);
			file = new File("/sdcard/ExpenseTracker/Favorite/Audio/" + _id + ".amr");
			delete(file);
		}
	}

	private void delete(File file) {
		try {
			file.delete();
		} catch (Exception e) {
		}
	}
	
}
