/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker;

public class Constants {
	
	public static final String IMAGE_SMALL_SUFFIX = "_small.jpg";
	public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";
	public static final String IMAGE_LARGE_SUFFIX = ".jpg";
	public static final String AUDIO_FILE_SUFFIX = ".amr";
	public static final String DIRECTORY = ExpenseTrackerApplication.FILES_DIR + "/ExpenseTracker/";
	public static final String DIRECTORY_AUDIO = "Audio/";
	public static final String DIRECTORY_FAVORITE = "Favorite/";
	
	//Intent Extras
	public static final String HIGHLIGHT = "toHighLight";
	public static final String ENTRY_LIST_EXTRA = "mDisplayList";
	public static final String POSITION = "position";
	public static final String IS_COMING_FROM_SHOW_PAGE = "isFromShowPage";
	public static final String DATA_CHANGED = "isChanged";
	public static final String TYPE = "type";
	public static final String TIME_IN_MILLIS_TO_SET_TAB = "timeInMillisToSetTab";
	public static final String FULL_SIZE_IMAGE_PATH = "FullSizeImagePath";
	public static final String TIME_IN_MILLIS = "timeInMillis";
	
	//ExpenseTracker Market UI
	public static final String EXPENSE_TRACKER_MARKET_URI = "market://details?id=com.vinsol.expensetracker";
	
}
