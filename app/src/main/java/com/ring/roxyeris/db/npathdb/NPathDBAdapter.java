/**
 * Created by Kang Tae Jun on 2011.
 *
 * The MIT License
 *
 * Copyright (c) 2011 Kang Tae Jun (Nira)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ring.roxyeris.db.npathdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NPathDBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PATH = "path";
	private static final String FDB_TABLE = "cpath";	// DB TABLE NAME
    private Context mCtx;
    private NPathDBHelper mDbHelper;
    private SQLiteDatabase mDb;		// DB를 저장하는 용도

    public NPathDBAdapter(Context context) {
    	mCtx = context;
    }
    
    // DB 자체에 대한 open/close 함수
    public NPathDBAdapter open() throws SQLException {
    	mDbHelper = new NPathDBHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
    }
    
    public void close() {
    	if(mDbHelper != null) mDbHelper.close();
    }
    
    // DB 각 항목에 대한 create / delete / update
    public long createDB(String path) {
		ContentValues initialValues = createContentValues(path);

		//Toast.makeText(mCtx, "Path Create : " + path, 1000).show();
		return mDb.insert(FDB_TABLE, null, initialValues);
	}
    
    public boolean updateDB(String oldpath, String path) {
		ContentValues updateValues = createContentValues(path);

		return mDb.update(FDB_TABLE, updateValues, KEY_PATH + "=" + oldpath, null) > 0;
	}
    
    public void deletePath() {
    	//Toast.makeText(mCtx, "Delete Current Path", 1000).show();
		mDb.delete(FDB_TABLE, null, null);
		mDb.execSQL("VACUUM");
	}
    
    // Cursor
    // 1. 전체 데이터베이스에 대해서 리턴 - No Need
    /**public Cursor fetchAll() {
		return mDb.query(FDB_TABLE, new String[] { KEY_ROWID,
				KEY_PATH }, null, null, null, null, null);
	}**/
    
    // 2. 하나의 데이터를 리턴
    public String fetch(long rowId) throws SQLException {
    	String rtn = null;
    	
		Cursor mCursor = mDb.query(true, FDB_TABLE, new String[] {
				KEY_ROWID, KEY_PATH },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor.moveToFirst()) {
			rtn = mCursor.getString(1);
		}
		mCursor.close();
		return rtn;
	}
    
    // ContentValues에 대한 함수
    private ContentValues createContentValues(String path) {
		ContentValues values = new ContentValues();
		values.put(KEY_PATH, path);
		return values;
	}
}