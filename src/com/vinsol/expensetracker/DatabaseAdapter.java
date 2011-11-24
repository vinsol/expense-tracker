package com.vinsol.expensetracker;

import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseAdapter {
	
	//database and table name
		private final String DATABASE_NAME = "ExpenseTrackerDB";
		private final String TABLE_NAME = "ExpenseTrackerTable";
		
		//column index
		public static final String KEY_ID="_id";
		public static final String KEY_TAG="TAG";
		public static final String KEY_AMOUNT="AMOUNT";
		public static final String KEY_DATE_TIME="DATE_TIME";
		public static final String KEY_LOCATION="LOCATION";
		public static final String KEY_FAVORITE="FAVORITE";
		public static final String KEY_TYPE = "TYPE";
		
		//sql open or create database
		private final String DATABASE_CREATE= "create table if not exists "+ TABLE_NAME +"("+
				KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
				KEY_TAG+" TEXT,"+
				KEY_AMOUNT+" TEXT, "+
				KEY_DATE_TIME+" TEXT NOT NULL,"+
				KEY_LOCATION+" TEXT, "+
				KEY_FAVORITE+" VARCHAR(1), "+
				KEY_TYPE+" VARCHAR(1) NOT NULL "+
				")";
		
		private SQLiteDatabase db;
		private Context context;
		private MyCreateOpenHelper createOpenHelper;
		
		protected DatabaseAdapter(Context _context) {
			context=_context;
			createOpenHelper=new MyCreateOpenHelper(context);
		}
		
		protected DatabaseAdapter open() throws SQLException{
			db=createOpenHelper.getWritableDatabase();
			return this;
		}
		
		protected void close(){
			db.close();
		}
		
		protected void drop_table(){
			db.execSQL("drop table "+TABLE_NAME);
		}
		
		protected long insert_to_database(HashMap<String, String> _list){
			ContentValues contentValues=new ContentValues();
			contentValues.put(KEY_TAG, _list.get(KEY_TAG));
			contentValues.put(KEY_AMOUNT, _list.get(KEY_AMOUNT));
			contentValues.put(KEY_DATE_TIME, _list.get(KEY_DATE_TIME));
			contentValues.put(KEY_LOCATION, _list.get(KEY_LOCATION));
			contentValues.put(KEY_FAVORITE, _list.get(KEY_FAVORITE));
			contentValues.put(KEY_TYPE, _list.get(KEY_TYPE));
			Log.v("com.vinsol.expensetracker", "TRYING");
			long _id = db.insert(TABLE_NAME, null, contentValues);
			Log.v("com.vinsol.expensetracker", "ADDED");
			return _id;
		}
		
		protected boolean deleteDatabaseEntryID(String id){
			String where = KEY_ID + "="+ id;
			try{
				Log.v("com.vinsol.expensetracker", "Deleting");
				db.delete(TABLE_NAME, where, null);
				Log.v("com.vinsol.expensetracker", "Deleted");
			}
			catch(SQLiteException e){
				return false;
			}
			return true;
		}
		
		protected boolean editDatabase(HashMap<String, String> _list){
			ContentValues contentValues=new ContentValues();
			if(_list.get(KEY_TAG) != null)
				contentValues.put(KEY_TAG, _list.get(KEY_TAG));
			if(_list.get(KEY_AMOUNT) != null)
				contentValues.put(KEY_AMOUNT, _list.get(KEY_AMOUNT));
			if(_list.get(KEY_DATE_TIME) != null)
				contentValues.put(KEY_DATE_TIME, _list.get(KEY_DATE_TIME));
			if(_list.get(KEY_LOCATION) != null)
				contentValues.put(KEY_LOCATION, _list.get(KEY_LOCATION));
			if(_list.get(KEY_FAVORITE) != null)
				contentValues.put(KEY_FAVORITE, _list.get(KEY_FAVORITE));
			if(_list.get(KEY_TYPE) != null)
				contentValues.put(KEY_TYPE, _list.get(KEY_TYPE));
			String where = KEY_ID + "="+ _list.get(KEY_ID);
			try{
				Log.v("com.vinsol.expensetracker", "EDITING");
				db.update(TABLE_NAME, contentValues, where, null);
				Log.v("com.vinsol.expensetracker", "EDITED");
				return true;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}
		
		protected Cursor getCompleteDatabase(){
			
			return db.query(TABLE_NAME, new String [] { 
					KEY_ID,
					KEY_TAG,
					KEY_AMOUNT,
					KEY_DATE_TIME,
					KEY_LOCATION,
					KEY_FAVORITE,
					KEY_TYPE
					}, null, null, null, null, null);
			
		}
		
		protected Cursor getDateDatabase(){
			
			return db.rawQuery("select * from "+TABLE_NAME+" order by "+KEY_DATE_TIME+" desc", null);
			
		}
		
		private class MyCreateOpenHelper extends SQLiteOpenHelper{

			public MyCreateOpenHelper(Context context) {
				super(context, DATABASE_NAME, null, 1);
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
