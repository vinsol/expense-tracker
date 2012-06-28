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
	public static final String ET_FOLDER = "/ExpenseTracker/";
	public static String DIRECTORY = ExpenseTrackerApplication.FILES_DIR + ET_FOLDER;
	public static final String DIRECTORY_AUDIO = "Audio/";
	public static final String DIRECTORY_FAVORITE = "Favorite/";
	
	//Intent Extras
	public static final String KEY_HIGHLIGHT = "toHighLight";
	public static final String KEY_ID = "_id";
	public static final String KEY_SET_LOCATION = "setLocation";
	public static final String KEY_ENTRY_LIST_EXTRA = "mDisplayList";
	public static final String KEY_POSITION = "position";
	public static final String KEY_IS_COMING_FROM_SHOW_PAGE = "isFromShowPage";
	public static final String KEY_IS_COMING_FROM_FAVORITE = "isFromFavorite";
	public static final String KEY_IS_FAVORITE = "isFavorite";
	public static final String KEY_DATA_CHANGED = "isChanged";
	public static final String KEY_TYPE = "type";
	public static final String KEY_TIME_IN_MILLIS_TO_SET_TAB = "timeInMillisToSetTab";
	public static final String KEY_FULL_SIZE_IMAGE_PATH = "FullSizeImagePath";
	public static final String KEY_TIME_IN_MILLIS = "timeInMillis";
	public static final String KEY_MANAGE_FAVORITE = "manageFavorite";
	public static final String KEY_IS_SIGNUP = "isSignUp";
	
	//ExpenseTracker Market UI
	public static final String EXPENSE_TRACKER_MARKET_URI = "market://details?id=com.vinsol.expensetracker";
	
}
