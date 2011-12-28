package com.vinsol.expensetracker.helpers;

import java.io.File;

import android.content.Context;
import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.DisplayList;

public class CheckEntryComplete {
	
	public boolean isEntryComplete(DisplayList displayList,Context mContext) {
		if(isAmountValid(displayList.amount)) {
			if (displayList.type.equals(mContext.getString(R.string.camera))) {
				return isCameraFileReadable(displayList.id);
			} else if (displayList.type.equals(mContext.getString(R.string.voice))) {
				return isAudioFileReadable(displayList.id);
			} else if (displayList.type.equals(mContext.getString(R.string.text))) {
				return isTagValid(displayList.description,mContext);
			}
		}
		return false;
	}
	
	private boolean isAmountValid(String amount){
		if( amount!= null){
			if (amount.contains("?")) {
				return false;
			} else {
				return true;
			} 
				
		} else {
			return false;
		}
	}
	private boolean isTagValid(String tag,Context context) {
		if(tag != null){
			if (tag.equals("") || tag.equals(context.getString(R.string.unfinished_textentry)) || tag.equals(context.getString(R.string.finished_textentry))) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean isAudioFileReadable(String id) {
		File mFile = new File("/sdcard/ExpenseTracker/Audio/" + id + ".amr");
		if (mFile.canRead()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isCameraFileReadable(String id) {
		File mFileSmall = new File("/sdcard/ExpenseTracker/" + id + "_small.jpg");
		File mFile = new File("/sdcard/ExpenseTracker/" + id + ".jpg");
		File mFileThumbnail = new File("/sdcard/ExpenseTracker/" + id + "_thumbnail.jpg");
		if (mFile.canRead() && mFileSmall.canRead() && mFileThumbnail.canRead()) {
			return true;
		}
		return false;
	}
}
