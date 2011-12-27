package com.vinsol.expensetracker;

import java.util.HashMap;

import com.vinsol.expensetracker.utils.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter {

	// database and table name
	private final String DATABASE_NAME = "ExpenseTrackerDB";
	private final String TABLE_NAME = "ExpenseTrackerTable";
	private final String TABLE_NAME_FAVORITE = "FavoriteTable";

	// column index
	public static final String KEY_ID = "_id";
	public static final String KEY_TAG = "TAG";
	public static final String KEY_AMOUNT = "AMOUNT";
	public static final String KEY_DATE_TIME = "DATE_TIME";
	public static final String KEY_LOCATION = "LOCATION";
	public static final String KEY_FAVORITE = "FAVORITE";
	public static final String KEY_TYPE = "TYPE";
	

	// sql open or create database
	private final String DATABASE_CREATE = "create table if not exists "
			+ TABLE_NAME + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," 
			+ KEY_TAG + " TEXT,"
			+ KEY_AMOUNT + " TEXT, " 
			+ KEY_DATE_TIME + " TEXT NOT NULL,"
			+ KEY_LOCATION + " TEXT, " 
			+ KEY_FAVORITE + " INTEGER, "
			+ KEY_TYPE + " VARCHAR(1) NOT NULL " 
			+ ")";

	private final String DATABASE_CREATE_FAVORITE = "create table if not exists "
			+ TABLE_NAME_FAVORITE + "(" 
			+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," 
			+ KEY_TAG + " TEXT,"
			+ KEY_AMOUNT + " TEXT, " 
			+ KEY_TYPE + " VARCHAR(1) NOT NULL " 
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

	protected void dropTable() {
		db.execSQL("drop table " + TABLE_NAME);
	}

	public Long insertToDatabase(HashMap<String, String> list) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_TAG, list.get(KEY_TAG));
		contentValues.put(KEY_AMOUNT, list.get(KEY_AMOUNT));
		contentValues.put(KEY_DATE_TIME, list.get(KEY_DATE_TIME));
		contentValues.put(KEY_LOCATION, list.get(KEY_LOCATION));
		contentValues.put(KEY_FAVORITE, list.get(KEY_FAVORITE));
		contentValues.put(KEY_TYPE, list.get(KEY_TYPE));
		Log.d("TRYING");
		long id = db.insert(TABLE_NAME, null, contentValues);
		Log.d("ADDED");
		return id;
	}

	public boolean deleteDatabaseEntryID(String id) {
		String where = KEY_ID + "=" + id;
		
		try {
			Log.d("Deleting");
			db.delete(TABLE_NAME, where, null);
			Log.d("Deleted");
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}

	public boolean editDatabase(HashMap<String, String> list) {
		ContentValues contentValues = new ContentValues();
		if (list.get(KEY_TAG) != null)
			contentValues.put(KEY_TAG, list.get(KEY_TAG));
		if (list.get(KEY_AMOUNT) != null)
			contentValues.put(KEY_AMOUNT, list.get(KEY_AMOUNT));
		if (list.get(KEY_DATE_TIME) != null)
			contentValues.put(KEY_DATE_TIME, list.get(KEY_DATE_TIME));
		if (list.get(KEY_LOCATION) != null)
			contentValues.put(KEY_LOCATION, list.get(KEY_LOCATION));
		if (list.get(KEY_FAVORITE) != null)
			contentValues.put(KEY_FAVORITE, list.get(KEY_FAVORITE));
		if (list.get(KEY_TYPE) != null)
			contentValues.put(KEY_TYPE, list.get(KEY_TYPE));
		String where = KEY_ID + "=" + list.get(KEY_ID);
		try {
			Log.d("EDITING");
			db.update(TABLE_NAME, contentValues, where, null);
			Log.d("EDITED");
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	protected Cursor getCompleteDatabase() {

		return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_TAG, KEY_AMOUNT,
				KEY_DATE_TIME, KEY_LOCATION, KEY_FAVORITE, KEY_TYPE }, null,
				null, null, null, null);

	}

	public Cursor getDateDatabase() {

		return db.query(TABLE_NAME, new String[] { KEY_ID, 
				KEY_TAG, 
				KEY_AMOUNT,
				KEY_DATE_TIME, 
				KEY_LOCATION, 
				KEY_FAVORITE, 
				KEY_TYPE }, null, null, null, null, KEY_DATE_TIME+" desc");
		
	}
	
	public Cursor getDateDatabase(String id) {
		if(id != null){
			if(id.length() > 1)
				id = id.substring(0, id.length()-1);
		}
		String where = KEY_ID+" in ("+id +")";
		return db.query(TABLE_NAME, new String[] { KEY_ID, 
				KEY_TAG, 
				KEY_AMOUNT,
				KEY_DATE_TIME, 
				KEY_LOCATION, 
				KEY_FAVORITE, 
				KEY_TYPE }, where, null, null, null, KEY_DATE_TIME+" desc");
		
	}
	
	public Long getFavoriteId(String id) {
		String where = KEY_ID+" = "+id;
		
		Cursor cr = db.query(TABLE_NAME,  new String[] {
				KEY_FAVORITE}, where, null, null, null, null);
		cr.moveToFirst();
		Long favId = cr.getLong(cr.getColumnIndex(KEY_FAVORITE));
		cr.close();
		return favId;
	}

	public void editDatabaseFavorite(Long favID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_FAVORITE, "");
		String where = KEY_FAVORITE+" = "+favID;
		db.update(TABLE_NAME, contentValues, where, null);
	}

	private class MyCreateOpenHelper extends SQLiteOpenHelper {

		public MyCreateOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
			database.execSQL(DATABASE_CREATE_FAVORITE);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		}
	}
}
