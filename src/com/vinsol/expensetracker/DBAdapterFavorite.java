package com.vinsol.expensetracker;

import com.vinsol.expensetracker.models.Favorite;
import com.vinsol.expensetracker.utils.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapterFavorite {
	// database and table name
		private static int 	  DB_VERSION = 1;
		private final String DATABASE_NAME = "ExpenseTrackerDB";
		private final String TABLE_NAME = "FavoriteTable";

		// column index
		public static final String KEY_ID = "_id";
		public static final String KEY_TAG = "TAG";
		public static final String KEY_AMOUNT = "AMOUNT";
		public static final String KEY_TYPE = "TYPE";
		

		// sql open or create database
		private final String DATABASE_CREATE = "create table if not exists "
				+ TABLE_NAME + "(" 
				+ KEY_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT ," 
				+ KEY_TAG + " TEXT,"
				+ KEY_AMOUNT + " TEXT, " 
				+ KEY_TYPE + " VARCHAR(1) NOT NULL " + ")";

		private SQLiteDatabase db;
		private Context context;
		private MyCreateOpenHelper createOpenHelper;

		public DBAdapterFavorite(Context _context) {
			context = _context;
			createOpenHelper = new MyCreateOpenHelper(context);
		}

		public DBAdapterFavorite open() throws SQLException {
			db = createOpenHelper.getWritableDatabase();
			return this;
		}

		public void close() {
			db.close();
		}

		protected void dropTable() {
			db.execSQL("drop table " + TABLE_NAME);
		}

		public long insertToDatabase(Favorite list) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(KEY_TAG, list.description);
			contentValues.put(KEY_AMOUNT, list.amount);
			contentValues.put(KEY_TYPE, list.type);
			Log.d("TRYING");
			long id = db.insert(TABLE_NAME, null, contentValues);
			Log.d("ADDED");
			return id;
		}

		public boolean deleteDatabaseEntryID(Long favID) {
			String where = KEY_ID + "=" + favID;
			try {
				Log.d("Deleting");
				db.delete(TABLE_NAME, where, null);
				Log.d("Deleted");
			} catch (SQLiteException e) {
				return false;
			}
			return true;
		}

		protected boolean editDatabase(Favorite list) {
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
				db.update(TABLE_NAME, contentValues, where, null);
				Log.d("EDITED");
				return true;
			} catch (Exception e) {
			}
			return false;
		}

		public Cursor getCompleteDatabase() {

			return db.query(TABLE_NAME, new String[] { KEY_ID, KEY_TAG, KEY_AMOUNT, KEY_TYPE }, null,
					null, null, null, null);

		}

		private class MyCreateOpenHelper extends SQLiteOpenHelper {

			public MyCreateOpenHelper(Context context) {
				super(context, DATABASE_NAME, null, DB_VERSION);
			}

			@Override
			public void onCreate(SQLiteDatabase database) {
				database.execSQL(DATABASE_CREATE);
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
