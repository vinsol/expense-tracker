package com.vinsol.expensetracker.helpers;

import java.io.File;

public class FileDelete {

	public FileDelete(File file) {
		delete(file);
	}

	public FileDelete(String _id) {
		deleteAll(_id);
	}
	

	private void deleteAll(String _id) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File("/sdcard/ExpenseTracker/" + _id + ".jpg");
			delete(file);
			file = new File("/sdcard/ExpenseTracker/" + _id + "_small.jpg");
			delete(file);
			file = new File("/sdcard/ExpenseTracker/" + _id + "_thumbnail.jpg");
			delete(file);
			file = new File("/sdcard/ExpenseTracker/Audio/" + _id + ".amr");
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
