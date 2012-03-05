/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

import com.vinsol.expensetracker.Constants;
import com.vinsol.expensetracker.ExpenseTrackerApplication;

public class FileHelper {
	
	public FileHelper() {
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !ExpenseTrackerApplication.isInitialized) {
			ExpenseTrackerApplication.Initialize();
		}
	}
	
	public void copyAllFromFavorite(String _id,String targetId) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			copy(getCameraFileLargeFavorite(_id),getCameraFileLargeEntry(targetId));
			copy(getCameraFileSmallFavorite(_id),getCameraFileSmallEntry(targetId));
			copy(getCameraFileThumbnailFavorite(_id),getCameraFileThumbnailEntry(targetId));
			copy(getAudioFileFavorite(_id),getAudioFileEntry(targetId));
		}
	}
	
	public void copyAllToFavorite(String _id,String targetId) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			copy(getCameraFileLargeEntry(_id),getCameraFileLargeFavorite(targetId));
			copy(getCameraFileSmallEntry(_id),getCameraFileSmallFavorite(targetId));
			copy(getCameraFileThumbnailEntry(_id),getCameraFileThumbnailFavorite(targetId));
			copy(getAudioFileEntry(_id),getAudioFileFavorite(targetId));
		}
	}
	
	public void deleteAllEntryFiles(String _id) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			delete(getCameraFileLargeEntry(_id));
			delete(getCameraFileSmallEntry(_id));
			delete(getCameraFileThumbnailEntry(_id));
			delete(getAudioFileEntry(_id));
		}
	}
	
	public void deleteAllFavoriteFiles(String _id) {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			delete(getCameraFileLargeFavorite(_id));
			delete(getCameraFileSmallFavorite(_id));
			delete(getCameraFileThumbnailFavorite(_id));
			delete(getAudioFileFavorite(_id));
		}
	}
	
	public File getAudioFileFavorite(String _id) {
		return new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + Constants.DIRECTORY_AUDIO + _id + Constants.AUDIO_FILE_SUFFIX);
	}
	
	public File getAudioFileEntry(String _id) {
		return new File(Constants.DIRECTORY + Constants.DIRECTORY_AUDIO + _id + Constants.AUDIO_FILE_SUFFIX);
	}
	
	public File getCameraFileLargeFavorite(String _id) {
		return new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + _id + Constants.IMAGE_LARGE_SUFFIX);
	}

	public File getCameraFileLargeEntry(String _id) {
		return new File(Constants.DIRECTORY + _id + Constants.IMAGE_LARGE_SUFFIX);
	}
	
	public File getCameraFileSmallFavorite(String _id) {
		return new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + _id + Constants.IMAGE_SMALL_SUFFIX);
	}

	public File getCameraFileSmallEntry(String _id) {
		return new File(Constants.DIRECTORY + _id + Constants.IMAGE_SMALL_SUFFIX);
	}
	
	public File getCameraFileThumbnailFavorite(String _id) {
		return new File(Constants.DIRECTORY + Constants.DIRECTORY_FAVORITE + _id + Constants.IMAGE_THUMBNAIL_SUFFIX);
	}

	public File getCameraFileThumbnailEntry(String _id) {
		return new File(Constants.DIRECTORY + _id + Constants.IMAGE_THUMBNAIL_SUFFIX);
	}

	private void copy(File source,File target) {
		try {
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(target);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.flush();
			in.close();
			out.close();
		} catch (Exception e) {
			//Do Nothing
			e.printStackTrace();
		}
	}

	public void delete(File file) {
		try {
			file.delete();
		} catch (Exception e) {
			//Do Nothing
		}
	}
	
}
