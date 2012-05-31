/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     

package com.vinsol.expensetracker.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.utils.Log;

public class DatabaseAdapter {

	// database and table name
	private static int DB_VERSION = 3;
	private final String DATABASE_NAME = "ExpenseTrackerDB";
	private final String ENTRY_TABLE = "EntryTable";
	private final String FAVORITE_TABLE = "FavoriteTable";
	
	private final String PREVIOUS_VERSION_ENTRY_TABLE = "ExpenseTrackerTable";
	
	// column index
	public static final String KEY_ID = "_id";
	public static final String KEY_TAG = "TAG";
	public static final String KEY_AMOUNT = "AMOUNT";
	public static final String KEY_DATE_TIME = "DATE_TIME";
	public static final String KEY_LOCATION = "LOCATION";
	public static final String KEY_FAVORITE = "FAVORITE";
	public static final String KEY_TYPE = "TYPE";
	public static final String KEY_ID_FROM_SERVER = "id";
	public static final String KEY_SYNC_BIT = "SYNCBIT";
	
	// sql open or create database
	private final String ENTRY_TABLE_CREATE = "create table if not exists "
			+ ENTRY_TABLE + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," 
			+ KEY_TAG + " TEXT,"
			+ KEY_AMOUNT + " TEXT, " 
			+ KEY_DATE_TIME + " TEXT NOT NULL,"
			+ KEY_LOCATION + " TEXT, " 
			+ KEY_FAVORITE + " INTEGER, "
			+ KEY_TYPE + " VARCHAR(1) NOT NULL " 
			+ ")";

	private final String FAVORITE_TABLE_CREATE = "create table if not exists "
			+ FAVORITE_TABLE + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," 
			+ KEY_TAG + " TEXT,"
			+ KEY_AMOUNT + " TEXT, " 
			+ KEY_TYPE + " VARCHAR(1) NOT NULL, " 
			+ KEY_LOCATION + " TEXT " 
			+ ")";
	
	
	private SQLiteDatabase db;
	private MyCreateOpenHelper createOpenHelper;

	public DatabaseAdapter(Context context) {
		createOpenHelper = new MyCreateOpenHelper(context);
	}

	public DatabaseAdapter open() throws SQLException {
		db = createOpenHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	protected void dropEntryTable() {
		db.execSQL("drop table " + ENTRY_TABLE);
	}
	
	protected void dropFavoriteTable() {
		db.execSQL("drop table " + FAVORITE_TABLE);
	}

	public long insertToFavoriteTable(Favorite list) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TAG, list.description);
		contentValues.put(KEY_AMOUNT, list.amount);
		contentValues.put(KEY_TYPE, list.type);
		contentValues.put(KEY_LOCATION, list.location);
		Log.d("TRYING");
		long id = db.insert(FAVORITE_TABLE, null, contentValues);
		Log.d("ADDED");
		return id;
	}
	
	public boolean deleteFavoriteTableEntryID(String favID) {
		String where = KEY_ID + "=" + favID;
		try {
			Log.d("Deleting");
			db.delete(FAVORITE_TABLE, where, null);
			Log.d("Deleted");
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean editFavoriteTable(Favorite list) {
		ContentValues contentValues = new ContentValues();
		if (list.description != null)
			contentValues.put(KEY_TAG, list.description);
		if (list.amount != null)
			contentValues.put(KEY_AMOUNT, list.amount);
		if (list.type != null)
			contentValues.put(KEY_TYPE, list.type);
		String where = KEY_ID + "=" + list.favId;
		try {
			Log.d("EDITING");
			db.update(FAVORITE_TABLE, contentValues, where, null);
			Log.d("EDITED");
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public Long insertToEntryTable(Entry list) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TAG, list.description);
		contentValues.put(KEY_AMOUNT, list.amount);
		contentValues.put(KEY_DATE_TIME, list.timeInMillis);
		contentValues.put(KEY_LOCATION, list.location);
		contentValues.put(KEY_FAVORITE, list.favId);
		contentValues.put(KEY_TYPE, list.type);
		Log.d("TRYING");
		long id = db.insert(ENTRY_TABLE, null, contentValues);
		Log.d("ADDED");
		return id;
	}

	public boolean deleteEntryTableEntryID(String id) {
		String where = KEY_ID + "=" + id;
		try {
			Log.d("Deleting");
			db.delete(ENTRY_TABLE, where, null);
			Log.d("Deleted");
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}

	public boolean editEntryTable(Entry list) {
		ContentValues contentValues = new ContentValues();
		if (list.description != null)
			contentValues.put(KEY_TAG, list.description);
		if (list.amount != null)
			contentValues.put(KEY_AMOUNT, list.amount);
		if (list.timeInMillis != null)
			contentValues.put(KEY_DATE_TIME, list.timeInMillis);
		if (list.location != null)
			contentValues.put(KEY_LOCATION, list.location);
		if (list.favId != null)
			contentValues.put(KEY_FAVORITE, list.favId);
		if (list.type != null)
			contentValues.put(KEY_TYPE, list.type);
		String where = KEY_ID + "=" + list.id;
		try {
			Log.d("EDITING");
			db.update(ENTRY_TABLE, contentValues, where, null);
			Log.d("EDITED");
			return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Cursor getEntryTableDateDatabaseDescending() {
		return db.query(ENTRY_TABLE, null, null, null, null, null, KEY_DATE_TIME+" desc");
	}
	
	public Cursor getEntryTableDateDatabaseDescending(String id) {
		if(id != null && id.length() > 1){
			id = id.substring(0, id.length()-1);
		}
		String where = KEY_ID+" in ("+id +")";
		return db.query(ENTRY_TABLE, null, where, null, null, null, KEY_DATE_TIME+" desc");
	}
	
	public Cursor getEntryTableDateDatabaseAscending() {
		return db.query(ENTRY_TABLE, null, null, null, null, null, KEY_DATE_TIME+" asc");
	}
	
	public Cursor getEntryTableDateDatabaseAscending(String id) {
		if(id != null && id.length() > 1){
			id = id.substring(0, id.length()-1);
		}
		String where = KEY_ID+" in ("+id +")";
		return db.query(ENTRY_TABLE, null, where, null, null, null, KEY_DATE_TIME+" asc");
	}
	
	public Long getFavoriteIdEntryTable(String id) {
		String where = KEY_ID+" = "+id;
		Cursor cr = db.query(ENTRY_TABLE,  new String[] {
				KEY_FAVORITE}, where, null, null, null, null);
		cr.moveToFirst();
		Long favId = -1L ;
		if(!cr.isAfterLast()) {favId = cr.getLong(cr.getColumnIndex(KEY_FAVORITE));}
		cr.close();
		return favId;
	}

	public void editFavoriteIdEntryTable(String favID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_FAVORITE, "");
		String where = KEY_FAVORITE+" = "+favID;
		db.update(ENTRY_TABLE, contentValues, where, null);
	}
 
	public Cursor getFavoriteTableComplete() {
		return db.query(FAVORITE_TABLE, null, null,null, null, null, null);
	}
	
	private class MyCreateOpenHelper extends SQLiteOpenHelper {

		public MyCreateOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(ENTRY_TABLE_CREATE);
			database.execSQL(FAVORITE_TABLE_CREATE);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int prevVersion, int newVersion) {
			if(prevVersion == 1) {
				db.execSQL("ALTER TABLE " + PREVIOUS_VERSION_ENTRY_TABLE +" RENAME TO "+ENTRY_TABLE);
			}
			if(prevVersion == 2) {
				db.execSQL("ALTER TABLE " + FAVORITE_TABLE +" ADD "+KEY_LOCATION+" TEXT");
			}
			if(prevVersion == 3) {
				db.execSQL("ALTER TABLE " + ENTRY_TABLE +" ADD ("+KEY_ID_FROM_SERVER+" INTEGER," +
						  KEY_SYNC_BIT+" INTEGER);");
				db.execSQL("ALTER TABLE " + FAVORITE_TABLE +" ADD ("+KEY_ID_FROM_SERVER+" INTEGER," +
						  KEY_SYNC_BIT+" INTEGER);");
			}
		}
		
	}
}
