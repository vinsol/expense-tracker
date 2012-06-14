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

import com.vinsol.expensetracker.R;
import com.vinsol.expensetracker.models.Entry;
import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.utils.Log;
import com.vinsol.expensetracker.utils.Utils;

public class DatabaseAdapter {

	// database and table name
	private static int DB_VERSION = 3;
	private final String DATABASE_NAME = "ExpenseTrackerDB";
	private final String ENTRY_TABLE = "EntryTable";
	private final String FAVORITE_TABLE = "FavoriteTable";
	
	private final String PREVIOUS_VERSION_ENTRY_TABLE = "ExpenseTrackerTable";
	private Context context;
	
	// column index
	public static final String KEY_ID = "_id";
	public static final String KEY_TAG = "TAG";
	public static final String KEY_AMOUNT = "AMOUNT";
	public static final String KEY_DATE_TIME = "DATE_TIME";
	public static final String KEY_LOCATION = "LOCATION";
	public static final String KEY_FAVORITE = "FAVORITE";
	public static final String KEY_TYPE = "TYPE";
	public static final String KEY_ID_FROM_SERVER = "ID_FROM_SERVER";
	public static final String KEY_UPDATED_AT = "UPDATED_AT";
	public static final String KEY_SYNC_BIT = "SYNC_BIT";
	public static final String KEY_MY_HASH = "MY_HASH";
	public static final String KEY_DELETE_BIT = "DELETED";
	public static final String KEY_FILE_UPLOADED = "FILE_UPLOADED";
	public static final String KEY_FILE_UPDATED_AT = "FILE_UPLOADED_AT";
	
	// sql open or create database
	private final String ENTRY_TABLE_CREATE = "create table if not exists "
			+ ENTRY_TABLE + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
			+ KEY_TAG + " TEXT,"
			+ KEY_AMOUNT + " TEXT, " 
			+ KEY_DATE_TIME + " TEXT NOT NULL,"
			+ KEY_LOCATION + " TEXT, " 
			+ KEY_FAVORITE + " INTEGER, "
			+ KEY_TYPE + " VARCHAR(1) NOT NULL, "
			+ KEY_ID_FROM_SERVER + " INTEGER UNIQUE, "
			+ KEY_UPDATED_AT + " STRING, "
			+ KEY_MY_HASH + " TEXT, "
			+ KEY_DELETE_BIT +" BOOLEAN, "
			+ KEY_SYNC_BIT +" INTEGER, "
			+ KEY_FILE_UPLOADED +" BOOLEAN, "
			+ KEY_FILE_UPDATED_AT +" STRING "
			+ ")";

	private final String FAVORITE_TABLE_CREATE = "create table if not exists "
			+ FAVORITE_TABLE + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," 
			+ KEY_TAG + " TEXT,"
			+ KEY_AMOUNT + " TEXT, " 
			+ KEY_TYPE + " VARCHAR(1) NOT NULL, " 
			+ KEY_LOCATION + " TEXT, "
			+ KEY_ID_FROM_SERVER + " INTEGER UNIQUE, "
			+ KEY_UPDATED_AT + " STRING, "
			+ KEY_MY_HASH + " TEXT, "
			+ KEY_DELETE_BIT + " BOOLEAN, "
			+ KEY_SYNC_BIT +" INTEGER, "
			+ KEY_FILE_UPLOADED +" BOOLEAN, "
			+ KEY_FILE_UPDATED_AT +" STRING "
			+ ")";
	
	
	private SQLiteDatabase db;
	private MyCreateOpenHelper createOpenHelper;

	public DatabaseAdapter(Context context) {
		this.context = context;
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
		contentValues.put(KEY_MY_HASH, Utils.getMD5());
		contentValues.put(KEY_DELETE_BIT, false);
		contentValues.put(KEY_ID_FROM_SERVER, list.idFromServer);
		contentValues.put(KEY_SYNC_BIT, list.syncBit);
		contentValues.put(KEY_UPDATED_AT, list.updatedAt);
		contentValues.put(KEY_FILE_UPLOADED, false);
		contentValues.put(KEY_FILE_UPDATED_AT, list.fileUpdatedAt);
		Log.d("TRYING");
		long id = db.insert(FAVORITE_TABLE, null, contentValues);
		Log.d("ADDED");
		return id;
	}
	
	public Long insertToEntryTable(Entry list) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TAG, list.description);
		contentValues.put(KEY_AMOUNT, list.amount);
		contentValues.put(KEY_DATE_TIME, list.timeInMillis);
		contentValues.put(KEY_LOCATION, list.location);
		contentValues.put(KEY_FAVORITE, list.favId);
		contentValues.put(KEY_TYPE, list.type);
		contentValues.put(KEY_MY_HASH, Utils.getMD5());
		contentValues.put(KEY_DELETE_BIT, false);
		contentValues.put(KEY_ID_FROM_SERVER, list.idFromServer);
		contentValues.put(KEY_SYNC_BIT, list.syncBit);
		contentValues.put(KEY_UPDATED_AT, list.updatedAt);
		contentValues.put(KEY_FILE_UPLOADED, false);
		contentValues.put(KEY_FILE_UPDATED_AT, list.fileUpdatedAt);
		Log.d("TRYING");
		long id = db.insert(ENTRY_TABLE, null, contentValues);
		Log.d("ADDED");
		return id;
	}
	
	public boolean deleteFavoriteTableEntryID(String favID) {
		String where = KEY_ID + "=" + favID;
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DELETE_BIT, true);
		try {
			Log.d("Deleting");
			db.update(FAVORITE_TABLE, contentValues, where, null);
			Log.d("Deleted");
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean deleteEntryTableEntryID(String id) {
		String where = KEY_ID + "=" + id;
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DELETE_BIT, true);
		try {
			Log.d("Deleting");
			db.update(ENTRY_TABLE, contentValues, where, null);
			Log.d("Deleted");
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean permanentDeleteFavoriteTableEntryID(String favID) {
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
	
	public boolean findEntryById(String id) {
		String where = KEY_ID + "=" + id;
		Cursor cursor = db.query(ENTRY_TABLE, null, where, null, null, null, null);
		boolean isPresent = false;
		if(cursor.moveToFirst()) {
			isPresent = true;
			cursor.close();
		}
		return isPresent;
	}
	
	public boolean permanentDeleteEntryTableEntryID(String id) {
		String where = KEY_ID + "=" + id;
		try {
			Log.d("Deleting");
			db.delete(ENTRY_TABLE, where, null);
			Log.d("Deleted");
		} catch (SQLiteException e) {
			e.printStackTrace();
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
		if (list.location != null)
			contentValues.put(KEY_LOCATION, list.location);
		if (list.idFromServer != null)
			contentValues.put(KEY_ID_FROM_SERVER, list.idFromServer);
		if (list.syncBit != null)
			contentValues.put(KEY_SYNC_BIT, list.syncBit);
		if (list.updatedAt != null)
			contentValues.put(KEY_UPDATED_AT, list.updatedAt);
		if (list.fileUploaded != null)
			contentValues.put(KEY_FILE_UPLOADED, list.fileUploaded);
		if (list.deleted != null)
			contentValues.put(KEY_DELETE_BIT, list.deleted);
		if (list.fileUpdatedAt != null)
			contentValues.put(KEY_FILE_UPDATED_AT, list.fileUpdatedAt);
		
		String where = KEY_ID + "=" + list.favId;
		try {
			Log.d("EDITING");
			db.update(FAVORITE_TABLE, contentValues, where, null);
			Log.d("EDITED");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		if (list.idFromServer != null)
			contentValues.put(KEY_ID_FROM_SERVER, list.idFromServer);
		if (list.syncBit != null)
			contentValues.put(KEY_SYNC_BIT, list.syncBit);
		if (list.updatedAt != null)
			contentValues.put(KEY_UPDATED_AT, list.updatedAt);
		if (list.fileUploaded != null)
			contentValues.put(KEY_FILE_UPLOADED, list.fileUploaded);
		if (list.deleted != null)
			contentValues.put(KEY_DELETE_BIT, list.deleted);
		if (list.fileUpdatedAt != null)
			contentValues.put(KEY_FILE_UPDATED_AT, list.fileUpdatedAt);
		
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
	
	public Cursor getEntryDataFileNotUploaded() {
		String where = KEY_FILE_UPLOADED+" IS NULL OR NOT "+KEY_FILE_UPLOADED;
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getFavoriteDataFileNotUploaded() {
		String where = KEY_FILE_UPLOADED+" IS NULL OR NOT "+KEY_FILE_UPLOADED;
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataNotSyncedAndCreated() {
		String where = KEY_UPDATED_AT+" IS NULL AND "+getNotDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataNotSyncedAndUpdated() {
		String where = KEY_UPDATED_AT+" IS NOT NULL AND "+KEY_SYNC_BIT+" = "+context.getResources().getInteger(R.integer.syncbit_not_synced)+" AND "+getNotDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataNotSyncedAndDeleted() {
		String where = KEY_SYNC_BIT+" = "+context.getResources().getInteger(R.integer.syncbit_not_synced)+" AND "+getDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}

	public Cursor getEntryTableDateDatabaseDescending() {
		return db.query(ENTRY_TABLE, null, getNotDeletedString(), null, null, null, KEY_DATE_TIME+" desc");
	}
	
	public Cursor getEntryTableDateDatabaseDescending(String id) {
		if(id != null && id.length() > 1){
			id = id.substring(0, id.length()-1);
		}
		String where = KEY_ID+" in ("+id +") AND "+getNotDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, KEY_DATE_TIME+" desc");
	}
	
	public Cursor getEntryTableDateDatabaseAscending() {
		return db.query(ENTRY_TABLE, null, getNotDeletedString(), null, null, null, KEY_DATE_TIME+" asc");
	}
	
	public Cursor getEntryTableDateDatabaseAscending(String id) {
		if(id != null && id.length() > 1){
			id = id.substring(0, id.length()-1);
		}
		String where = KEY_ID+" in ("+id +") AND "+getNotDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, KEY_DATE_TIME+" asc");
	}
	
	public Long getFavoriteIdEntryTable(String id) {
		String where = KEY_ID+" = "+id + " AND "+getNotDeletedString();
		Cursor cr = db.query(ENTRY_TABLE,  new String[] {
				KEY_FAVORITE}, where, null, null, null, null);
		cr.moveToFirst();
		Long favId = -1L ;
		if(!cr.isAfterLast()) {favId = cr.getLong(cr.getColumnIndex(KEY_FAVORITE));}
		cr.close();
		return favId;
	}

	public void editFavoriteIdEntryTable(String favId) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_FAVORITE, "");
		String where = KEY_FAVORITE+" = "+favId;
		db.update(ENTRY_TABLE, contentValues, where, null);
	}
 
	public Cursor getFavoriteTableComplete() {
		return db.query(FAVORITE_TABLE, null, getNotDeletedString(), null, null, null, null);
	}
	
	private String getNotDeletedString() {
		return "(NOT "+KEY_DELETE_BIT+" OR "+KEY_DELETE_BIT+" IS NULL)";
	}
	
	private String getDeletedString() {
		return KEY_DELETE_BIT;
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
				db.execSQL("ALTER TABLE " + ENTRY_TABLE +" ADD ("+KEY_ID_FROM_SERVER+" INTEGER UNIQUE," +
						  KEY_UPDATED_AT+" STRING," +
						  KEY_MY_HASH+" TEXT," +
						  KEY_DELETE_BIT+" BOOLEAN," +
						  KEY_SYNC_BIT+" INTEGER," +
						  KEY_FILE_UPLOADED +" BOOLEAN," +
						  KEY_UPDATED_AT +" STRING);");
				db.execSQL("ALTER TABLE " + FAVORITE_TABLE +" ADD ("+KEY_ID_FROM_SERVER+" INTEGER UNIQUE," +
						  KEY_UPDATED_AT+" STRING," +
						  KEY_MY_HASH+" TEXT," +
						  KEY_DELETE_BIT+" BOOLEAN," +
						  KEY_SYNC_BIT+" INTEGER," +
						  KEY_FILE_UPLOADED +" BOOLEAN," +
						  KEY_UPDATED_AT +" STRING);");
			}
		}
		
	}
}
