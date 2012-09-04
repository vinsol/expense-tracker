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
import com.vinsol.expensetracker.utils.Strings;
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
	public static final String KEY_FILE_TO_DOWNLOAD = "FILE_TO_DOWNLOAD";
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
			+ KEY_DELETE_BIT +" BOOLEAN DEFAULT 'FALSE', "
			+ KEY_SYNC_BIT +" INTEGER, "
			+ KEY_FILE_UPLOADED +" BOOLEAN DEFAULT 'FALSE', "
			+ KEY_FILE_TO_DOWNLOAD +" BOOLEAN DEFAULT 'FALSE', "
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
			+ KEY_DELETE_BIT + " BOOLEAN DEFAULT 'FALSE', "
			+ KEY_SYNC_BIT +" INTEGER, "
			+ KEY_FILE_UPLOADED +" BOOLEAN DEFAULT 'FALSE', "
			+ KEY_FILE_TO_DOWNLOAD +" BOOLEAN DEFAULT 'FALSE', "
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
	
	public long insertToFavoriteTable(Favorite favorite) {
		ContentValues contentValues = getInsertContentValues(favorite);
		long id = db.insert(FAVORITE_TABLE, null, contentValues);
		return id;
	}
	
	public Long insertToEntryTable(Entry entry) {
		ContentValues contentValues = getInsertContentValues(entry);
		if(entry.timeInMillis != null)
			contentValues.put(KEY_DATE_TIME, entry.timeInMillis);
		if(Strings.notEmpty(entry.favorite))
			contentValues.put(KEY_FAVORITE, entry.favorite);
		long id = db.insert(ENTRY_TABLE, null, contentValues);
		return id;
	}
	
	private ContentValues getInsertContentValues(Favorite object) {
		ContentValues contentValues = new ContentValues();
		if(Strings.notEmpty(object.description))
			contentValues.put(KEY_TAG, object.description);
		if(Strings.notEmpty(object.amount))
			contentValues.put(KEY_AMOUNT, object.amount);
		if(Strings.notEmpty(object.location))
			contentValues.put(KEY_LOCATION, object.location);
		
		if(Strings.notEmpty(object.type))
			contentValues.put(KEY_TYPE, object.type);
		
		if(Strings.isEmpty(object.myHash)) {
			contentValues.put(KEY_MY_HASH, Utils.getMD5());
		} else {
			contentValues.put(KEY_MY_HASH, object.myHash);
		}
		
		contentValues.put(KEY_DELETE_BIT, object.deleted); // a boolean
		
		if(Strings.notEmpty(object.idFromServer))
			contentValues.put(KEY_ID_FROM_SERVER, object.idFromServer);
		if(Strings.notEmpty(object.syncBit))
			contentValues.put(KEY_SYNC_BIT, object.syncBit);
		if(Strings.notEmpty(object.updatedAt))
			contentValues.put(KEY_UPDATED_AT, object.updatedAt);
			
		contentValues.put(KEY_FILE_UPLOADED, object.fileUploaded); // a boolean	
		contentValues.put(KEY_FILE_TO_DOWNLOAD, object.fileToDownload); // a boolean
		if(Strings.notEmpty(object.fileUpdatedAt))
			contentValues.put(KEY_FILE_UPDATED_AT, object.fileUpdatedAt);
		return contentValues;
	}
	
	public boolean deleteFavoriteEntryByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		return deleteFavoriteEntry(where);
	}
	
	public boolean deleteFavoriteEntryByID(String favID) {
		String where = KEY_ID + "=" + favID;
		return deleteFavoriteEntry(where);
	}
	
	private boolean deleteFavoriteEntry(String where) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DELETE_BIT, true);
		try {
			db.update(FAVORITE_TABLE, contentValues, where, null);
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean updateFileUploadedEntryTable(String id) {
		return updateFileUploaded(id, ENTRY_TABLE);
	}
	
	public boolean updateFileUploadedFavoriteTable(String favID) {
		return updateFileUploaded(favID, FAVORITE_TABLE);
	}
	
	private boolean updateFileUploaded(String id, String TABLE) {
		String where = KEY_ID + "=" + id;
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_FILE_UPLOADED, false);
		contentValues.put(KEY_SYNC_BIT, context.getString(R.string.syncbit_not_synced));
		try {
			db.update(TABLE, contentValues, where, null);
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean deleteExpenseEntryByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DELETE_BIT, true);
		try {
			db.update(ENTRY_TABLE, contentValues, where, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getEntryIdByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		try {
			Cursor cursor = db.query(ENTRY_TABLE, null, where, null, null, null, null);
			if(cursor.moveToFirst()) {
				String id = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				cursor.close();
				return id;
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getFavIdByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		try {
			Cursor cursor = db.query(FAVORITE_TABLE, null, where, null, null, null, null);
			if(cursor.moveToFirst()) {
				String id = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
				cursor.close();
				return id;
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getFavHashById(String id) {
		String where = KEY_ID + "=" + id;
		try {
			Cursor cursor = db.query(FAVORITE_TABLE, null, where, null, null, null, null);
			if(cursor.moveToFirst()) {
				String hash = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_MY_HASH));
				cursor.close();
				return hash;
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getEntryHashById(String id) {
		String where = KEY_ID + "=" + id;
		try {
			Cursor cursor = db.query(ENTRY_TABLE, null, where, null, null, null, null);
			if(cursor.moveToFirst()) {
				String hash = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_MY_HASH));
				cursor.close();
				return hash;
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public boolean permanentDeleteExpenseEntryByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		try {
			db.delete(ENTRY_TABLE, where, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean permanentDeleteFavoriteEntryByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		try {
			db.delete(FAVORITE_TABLE, where, null);
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean deleteExpenseEntryByID(String id) {
		String where = KEY_ID + "=" + id;
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DELETE_BIT, true);
		try {
			db.update(ENTRY_TABLE, contentValues, where, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean permanentDeleteFavoriteEntryByMyHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		try {
			db.delete(FAVORITE_TABLE, where, null);
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	public boolean deleteExpenseEntryByMyHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_DELETE_BIT, true);
		try {
			db.update(ENTRY_TABLE, contentValues, where, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean permanentDeleteFavoriteEntryByID(String favID) {
		String where = KEY_ID + "=" + favID;
		try {
			db.delete(FAVORITE_TABLE, where, null);
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
	
	public boolean findEntryByMyHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		Cursor cursor = db.query(ENTRY_TABLE, null, where, null, null, null, null);
		boolean isPresent = false;
		if(cursor.moveToFirst()) {
			isPresent = true;
			cursor.close();
		}
		return isPresent;
	}
	
	public boolean findFavoriteByMyHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		Cursor cursor = db.query(FAVORITE_TABLE, null, where, null, null, null, null);
		boolean isPresent = false;
		if(cursor.moveToFirst()) {
			isPresent = true;
			cursor.close();
		}
		return isPresent;
	}
	
	public Cursor getFavoriteByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryByHash(String hash) {
		String where = KEY_MY_HASH + "=\"" + hash+"\"";
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public boolean permanentDeleteExpenseEntryID(String id) {
		String where = KEY_ID + "=" + id;
		try {
			db.delete(ENTRY_TABLE, where, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean editFavoriteEntryById(Favorite favorite) {
		ContentValues contentValues = getEditContentValues(favorite);
		String where = KEY_ID + "=" + favorite.id;
		try {
			db.update(FAVORITE_TABLE, contentValues, where, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean editFavoriteEntryByHash(Favorite favorite) {
		ContentValues contentValues = getEditContentValues(favorite);
		String where = KEY_MY_HASH + "=\"" + favorite.myHash+"\"";
		try {
			db.update(FAVORITE_TABLE, contentValues, where, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean editExpenseEntryByHash(Entry entry) {
		ContentValues contentValues = getEditContentValues(entry);
		
		if(entry.timeInMillis != null)
			contentValues.put(KEY_DATE_TIME, entry.timeInMillis);
		
		if (Strings.notEmpty(entry.favorite))
			contentValues.put(KEY_FAVORITE, entry.favorite);
		String where = KEY_MY_HASH + "=\"" + entry.myHash+"\"";
		try {
			db.update(ENTRY_TABLE, contentValues, where, null);
			return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean editExpenseEntryById(Entry entry) {
		String where = KEY_ID + "=" + entry.id;
		ContentValues contentValues = getEditContentValues(entry);
		if(entry.timeInMillis != null)
			contentValues.put(KEY_DATE_TIME, entry.timeInMillis);
		
		if (Strings.notEmpty(entry.favorite))
			contentValues.put(KEY_FAVORITE, entry.favorite);
		try {
			db.update(ENTRY_TABLE, contentValues, where, null);
			return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private ContentValues getEditContentValues(Favorite object) {
		ContentValues contentValues = new ContentValues();
		if (Strings.notEmpty(object.description))
			contentValues.put(KEY_TAG, object.description);
		if (Strings.notEmpty(object.amount))
			contentValues.put(KEY_AMOUNT, object.amount);
		if (Strings.notEmpty(object.type))
			contentValues.put(KEY_TYPE, object.type);
		if (Strings.notEmpty(object.location))
			contentValues.put(KEY_LOCATION, object.location);
		if (Strings.notEmpty(object.idFromServer))
			contentValues.put(KEY_ID_FROM_SERVER, object.idFromServer);
		if (Strings.notEmpty(object.syncBit))
			contentValues.put(KEY_SYNC_BIT, object.syncBit);
		if (Strings.notEmpty(object.updatedAt))
			contentValues.put(KEY_UPDATED_AT, object.updatedAt);
		
		contentValues.put(KEY_FILE_UPLOADED, object.fileUploaded); //a boolean
		contentValues.put(KEY_FILE_TO_DOWNLOAD, object.fileToDownload); // a boolean
		contentValues.put(KEY_DELETE_BIT, object.deleted);// a boolean
		
		if (Strings.notEmpty(object.fileUpdatedAt))
			contentValues.put(KEY_FILE_UPDATED_AT, object.fileUpdatedAt);
		return contentValues;
	}
	
	public Cursor getEntryDataFileNotUploaded() {
		String where = KEY_FILE_UPLOADED+" IS NULL OR "+KEY_FILE_UPLOADED+"= \"\" OR NOT "+KEY_FILE_UPLOADED;
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getFavoriteDataFileNotUploaded() {
		String where = KEY_FILE_UPLOADED+" IS NULL OR "+KEY_FILE_UPLOADED+"= \"\" OR NOT "+KEY_FILE_UPLOADED;
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataNotSyncedAndCreated() {
		String where = KEY_UPDATED_AT+" IS NULL OR "+KEY_UPDATED_AT+"= \"\"";
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getFavoriteDataNotSyncedAndCreated() {
		String where = KEY_UPDATED_AT+" IS NULL OR "+KEY_UPDATED_AT+"= \"\"";
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataFileToDownload() {
		String where = KEY_FILE_TO_DOWNLOAD;
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getFavoriteDataFileToDownload() {
		String where = KEY_FILE_TO_DOWNLOAD;
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataNotSyncedAndUpdated() {
		String where = KEY_UPDATED_AT+" IS NOT NULL AND "+KEY_UPDATED_AT+" != \"\" AND "+KEY_SYNC_BIT+" = "+context.getString(R.string.syncbit_not_synced)+" AND "+getNotDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getFavoriteDataNotSyncedAndUpdated() {
		String where = KEY_UPDATED_AT+" IS NOT NULL AND "+KEY_UPDATED_AT+" != \"\" AND "+KEY_SYNC_BIT+" = "+context.getString(R.string.syncbit_not_synced)+" AND "+getNotDeletedString();
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getEntryDataNotSyncedAndDeleted() {
		String where = getDeletedString();
		return db.query(ENTRY_TABLE, null, where, null, null, null, null);
	}
	
	public Cursor getFavoriteDataNotSyncedAndDeleted() {
		String where = getDeletedString();
		return db.query(FAVORITE_TABLE, null, where, null, null, null, null);
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
	
	public String getFavoriteHashEntryTable(String id) {
		String where = KEY_ID+" = "+id + " AND "+getNotDeletedString();
		Cursor cr = db.query(ENTRY_TABLE,  new String[] {
				KEY_FAVORITE}, where, null, null, null, null);
		cr.moveToFirst();
		String favHash = "" ;
		if(!cr.isAfterLast()) {favHash = cr.getString(cr.getColumnIndex(KEY_FAVORITE));}
		cr.close();
		return favHash;
	}

	public void editFavoriteHashEntryTable(String hash) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_FAVORITE, "");
		contentValues.put(KEY_SYNC_BIT, context.getString(R.string.syncbit_not_synced));
		String where = KEY_FAVORITE+" = \""+hash+"\"";
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
						  KEY_DELETE_BIT+" BOOLEAN DEFAULT 'FALSE'," +
						  KEY_SYNC_BIT+" INTEGER," +
						  KEY_FILE_UPLOADED +" BOOLEAN DEFAULT 'FALSE'," +
						  KEY_FILE_TO_DOWNLOAD +" BOOLEAN DEFAULT 'FALSE', " +
						  KEY_FILE_UPDATED_AT +" STRING);");
				db.execSQL("ALTER TABLE " + FAVORITE_TABLE +" ADD ("+KEY_ID_FROM_SERVER+" INTEGER UNIQUE," +
						  KEY_UPDATED_AT+" STRING," +
						  KEY_MY_HASH+" TEXT," +
						  KEY_DELETE_BIT+" BOOLEAN DEFAULT 'FALSE'," +
						  KEY_SYNC_BIT+" INTEGER," +
						  KEY_FILE_UPLOADED +" BOOLEAN DEFAULT 'FALSE'," +
						  KEY_FILE_TO_DOWNLOAD +" BOOLEAN DEFULT 'FALSE', " +
						  KEY_FILE_UPDATED_AT +" STRING);");
			}
		}
		
	}
}
