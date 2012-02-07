/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker;

public class Constants {
	
	public static String IMAGE_SMALL_SUFFIX = "_small.jpg";
	public static String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail.jpg";
	public static String IMAGE_LARGE_SUFFIX = ".jpg";
	public static String AUDIO_FILE_SUFFIX = ".amr";
	public static String DIRECTORY = ExpenseTrackerApplication.FILES_DIR + "/ExpenseTracker/";
	public static String DIRECTORY_AUDIO = "Audio/";
	public static String DIRECTORY_FAVORITE = "Favorite/";
	
	//Intent Extras
	public static String HIGHLIGHT = "toHighLight";
	public static String ENTRY_LIST_EXTRA = "mDisplayList";
	public static String POSITION = "position";
	public static String IS_COMING_FROM_SHOW_PAGE = "isFromShowPage";
	public static String DATA_CHANGED = "isChanged";
	public static String IS_COMING_FROM_EXPENSE_LISTING_ALL = "isFromExpenseListingAll";
	public static String IS_COMING_FROM_EXPENSE_LISTING_THIS_WEEK = "isFromExpenseListingThisWeek";
	public static String IS_COMING_FROM_EXPENSE_LISTING_THIS_MONTH = "isFromExpenseListingThisMonth";
	public static String IS_COMING_FROM_EXPENSE_LISTING_THIS_YEAR = "isFromExpenseListingThisYear";
	public static String IS_COMING_FROM_EXPENSE_SUB_LISTING = "isFromExpenseSubListing";
	public static String TYPE = "type";
	public static String TIME_IN_MILLIS_TO_SET_TAB = "timeInMillisToSetTab";
	
	//ExpenseTracker Market UI
	public static final String EXPENSE_TRACKER_MARKET_URI = "market://details?id=com.vinsol.expensetracker";
	
}
