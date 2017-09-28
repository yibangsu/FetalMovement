package com.bang.fetalmovement.untils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FetalMovementDatabaseHelper {
	
	private final String TAG = "fetal-movement";

	private final String databaseName = "FetalMovement";
	private final String tableName = "history";
	private final String dateKey = "date";
	private final String availKey = "avail";
	private final String maxKey = "max";
	private final String totalKey = "total";
	
	private static FetalMovementDatabaseHelper instance;
	private SQLiteDatabase db;
	
	public static FetalMovementDatabaseHelper getInstance(Context mContext) {
		if (instance == null) {
			instance = new FetalMovementDatabaseHelper(mContext);
		}
		return instance;
	}
	
	public FetalMovementDatabaseHelper(Context mContext) {
		if (db == null) {
			db = mContext.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);
			try {
				String sqlStatements = "create table " + tableName
						+ " (pid INTEGER primary key autoincrement,"
						+ dateKey + " text,"
						+ availKey + " INTEGER,"
						+ totalKey + " INTEGER,"
						+ maxKey + " INTEGER)";
				Log.d(TAG, sqlStatements);
				db.setVersion(0);
				db.execSQL(sqlStatements);
			} catch (Exception e) {
				
			}
		}
	}
	
	public SQLiteDatabase getDatabase() {
		return db;
	}
	
	public void insert(String date, int avail, int total, int max) {
		//db.beginTransaction();
		ContentValues values = new ContentValues();
		values.put(dateKey, date);
		values.put(availKey, avail);
		values.put(totalKey, total);
		values.put(maxKey, max);
		db.insert(tableName, null, values);
		//db.endTransaction();
	}
	
	public ArrayList<HistoryItem> getAll() {
		ArrayList<HistoryItem> mList = new ArrayList<HistoryItem>();
		//db.beginTransaction();
		String sqlStatements = "select * from " + tableName;
		Log.d(TAG, sqlStatements);
		Cursor cursor = db.rawQuery(sqlStatements, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				HistoryItem item = new HistoryItem();
				item.date = cursor.getString(1);
				item.avail = cursor.getInt(2);
				item.total = cursor.getInt(3);
				item.max = cursor.getInt(4);
				mList.add(item);
			}
			cursor.close();
		}
		//db.endTransaction();
		return mList;
	}
	
}
