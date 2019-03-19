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

package com.ring.roxyeris.background;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.ring.roxyeris.db.filedb.FileListDBAdapter;
import com.ring.roxyeris.db.pathdb.PathDBAdapter;
import com.ring.roxyeris.R;

import java.io.File;
import java.util.Random;

/**
 * RingtoneSetter.java
 *
 * 실제 벨소리가 변경되는 기능 수행
 */

public class RingtoneSetter {
	private Context context;
	private FileListDBAdapter FileDb;
	private PathDBAdapter PathDb;
	private Cursor FAllCursor;
	private File setFile;

	public RingtoneSetter(Context context) {
		this.context = context;
		
		// DB 열기
		FileDb = new FileListDBAdapter(context);
		PathDb = new PathDBAdapter(context);
		
		FileDb.open();
		PathDb.open();
		
		// 모든 DB의 데이터 페치
		FAllCursor = FileDb.fetchAll();
	}
	
	// 실제 벨소리 설정 동작
	public String SetRing() {
		if(FAllCursor.getCount() == 0) {
			Toast.makeText(context, context.getText(R.string.noring), Toast.LENGTH_SHORT).show();
			return null;
		}
		else {
			Uri uri;
			String FCurrentPath;
			FCurrentPath = PathDb.fetch(1);
			int rings;
			int s_ring = -1;
			Cursor Ringing;
			
			// 기존 파일을 다시 미디어 스캐너를 통해 되돌리는 작업
			if(FCurrentPath != null) {
				uri = MediaStore.Audio.Media.getContentUriForPath(FCurrentPath);
				context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + FCurrentPath + "\"", null);
				context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + FCurrentPath)));
				PathDb.deletePath();
			}
			
			// get random number of ring
			/*String[] FROM = new String[] {FileListDBAdapter.KEY_NAME, FileListDBAdapter.KEY_PATH};
			int[] TO = new int[]{R.id.name, R.id.path};
			
			// 요 파트 바꿔볼까... DB에서 직접 확인하는 방식으로
			mca = new SimpleCursorAdapter(context, R.layout.listview, FAllCursor, FROM, TO);
			rings = mca.getCount();*/
			rings = FAllCursor.getCount();
			Log.d("RINGSCOUNT", Integer.toString(rings));
			
			// 랜덤 결정
			Random rnd = new Random();
			s_ring = rnd.nextInt(rings);
			Ringing = FileDb.fetchOne(s_ring+1);
			
			// register ringtone
			setFile = new File(Ringing.getString(2));
						
			// SettingFragment Rinetone
			ContentValues setter = new ContentValues();
			setter.put(MediaStore.MediaColumns.DATA, setFile.getAbsolutePath());
			setter.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
			setter.put(MediaStore.Audio.Media.TITLE, setFile.getName());
			setter.put(MediaStore.Audio.Media.IS_RINGTONE, true);
			setter.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
			setter.put(MediaStore.Audio.Media.IS_ALARM, false);
			setter.put(MediaStore.Audio.Media.IS_MUSIC, true);
			
			// Insert to DB
			uri = MediaStore.Audio.Media.getContentUriForPath(setFile.getAbsolutePath());
			context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + setFile.getAbsolutePath() + "\"", null);
			Uri newUri = context.getContentResolver().insert(uri, setter);
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
			
			PathDb.createDB(Ringing.getString(2));
			PathDb.close();
			FileDb.close();
			Ringing.close();
			setter.clear();
			FAllCursor.close();
			return setFile.getName();
		}
	}
}
