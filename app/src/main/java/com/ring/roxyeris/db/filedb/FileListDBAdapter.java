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

package com.ring.roxyeris.db.filedb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FileListDBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_PATH = "path";
	private static final String DATABASE_TABLE = "rings";	// DB TABLE NAME
    private Context mCtx;
    private FileDBHelper mDbHelper;
    private SQLiteDatabase mDb;		// DB를 저장하는 용도

    public FileListDBAdapter(Context context) {
    	mCtx = context;
    }
    
    // open/close DB
    public FileListDBAdapter open() throws SQLException {
    	mDbHelper = new FileDBHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
    }
    
    public void close() {
    	if(mDbHelper != null) mDbHelper.close();
    	if(mDb != null) mDb.close();
    }
    
    public long createDB(String name, String path) {
		ContentValues initialValues = createContentValues(name, path);

		//Toast.makeText(mCtx, "Create : " + name + ' ' + path, 1000).show();
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
    
    public boolean updateDB(long rowId, String name, String path) {
		ContentValues updateValues = createContentValues(name, path);

		//Toast.makeText(mCtx, "Update : " + String.valueOf(rowId) + ' ' + name + ' ' + path, 1000).show();
		return mDb.update(DATABASE_TABLE, updateValues, KEY_ROWID + "=" + rowId, null) > 0;
	}
    
    public void deleteDB(long rowid) {
    	//Toast.makeText(mCtx, "Delete : " + String.valueOf(rowid), 1000).show();
		mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowid, null);
		/**mDb.execSQL("VACUUM");**/
		
		mDb.execSQL("insert into 'temp' (name, path) select name, path from rings");
		deleteAllDB();

		mDb.execSQL("insert into 'rings' (name, path) select name, path from temp");
		mDb.delete("temp", null, null);
	}
    
    public void deleteAllDB() {
		mDb.delete(DATABASE_TABLE, null, null);
	}
    
    // Cursor
    // 1. for all db
    public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_PATH }, null, null, null, null, null);
	}
    
    // 2. for one db data
    public Cursor fetchOne(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_PATH },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
    
    // ContentValues
    private ContentValues createContentValues(String name, String path) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_PATH, path);
		return values;
	}
}