/**
 * Created by Kang Tae Jun on 2012.
 *
 * The MIT License
 *
 * Copyright (c) 2012 Kang Tae Jun (Nira)
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

import java.io.File;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.ring.roxyeris.db.notifile.NotifileListDBAdapter;
import com.ring.roxyeris.db.npathdb.NPathDBAdapter;
import com.ring.roxyeris.R;

/**
 * NotificationSetter.java
 *
 * 실제 알림음이 변경되는 기능 수행
 */

public class NotificationSetter {
	private Context context;
	private NotifileListDBAdapter NotiDb;
	private NPathDBAdapter NPathDb;
	private Cursor NAllCursor;
	private File setFile;
	
	public NotificationSetter(Context context) {
		this.context = context;
		
		NotiDb = new NotifileListDBAdapter(context);
		NPathDb = new NPathDBAdapter(context);
		
		NotiDb.open();
		NPathDb.open();
		
		NAllCursor = NotiDb.fetchAll();
	}
	
	public String SetNoti() {
		if(NAllCursor.getCount() == 0) {
			Toast.makeText(context, context.getText(R.string.nonoti), Toast.LENGTH_SHORT).show();
			return null;
		}
		else {
			Uri uri;
			String NCurrentPath = null;
			NCurrentPath = NPathDb.fetch(1);
			int rings;
			int s_ring = -1;
			Cursor Ringing = null;
			
			if(NCurrentPath != null) {
				//Toast.makeText(context, "CURRENT : " + CurrentPath, 1000).show();
				uri = MediaStore.Audio.Media.getContentUriForPath(NCurrentPath);
				context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + NCurrentPath + "\"", null);
				context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + NCurrentPath)));
				NPathDb.deletePath();
			}
			//else Toast.makeText(context, "CURRENT NULL", 1000).show();
			
			// get random number of ring
			/*String[] FROM = new String[] {NotifileListDBAdapter.KEY_NAME, NotifileListDBAdapter.KEY_PATH};
			int[] TO = new int[]{R.id.name, R.id.path};
			
			mca = new SimpleCursorAdapter(context, R.layout.listview, NAllCursor, FROM, TO);
			rings = mca.getCount();*/
			rings = NAllCursor.getCount();

			Random rnd = new Random();
			s_ring = rnd.nextInt(rings);
			Ringing = NotiDb.fetchOne(s_ring+1);
			
			// register ringtone
			setFile = new File(Ringing.getString(2));
						
			// SettingFragment Rinetone
			ContentValues setter = new ContentValues();
			setter.put(MediaStore.MediaColumns.DATA, setFile.getAbsolutePath());
			setter.put(MediaStore.MediaColumns.MIME_TYPE, "audio/all");
			setter.put(MediaStore.Audio.Media.TITLE, setFile.getName());
			setter.put(MediaStore.Audio.Media.IS_RINGTONE, false);
			setter.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
			setter.put(MediaStore.Audio.Media.IS_ALARM, false);
			setter.put(MediaStore.Audio.Media.IS_MUSIC, true);
			
			/*File file=new File(setFile.getAbsolutePath());
	        AudioFile audioFile;
	        Tag tag;
	        
	        String mTitle = null;
	        String mArtist = null;
	        String mAlbum = null;
			try {
				audioFile = AudioFileIO.read(file);
				tag = audioFile.getTag();
				
				if(tag.hasField(FieldKey.TITLE)) {
					mTitle = tag.getFirst(FieldKey.TITLE);
					setter.put(MediaStore.Audio.Media.TITLE, mTitle);
				}
				if(tag.hasField(FieldKey.ARTIST)) {
					mArtist = tag.getFirst(FieldKey.ARTIST);
					setter.put(MediaStore.Audio.Media.ALBUM, mArtist);
				}
				if(tag.hasField(FieldKey.ALBUM)) {
					mAlbum = tag.getFirst(FieldKey.ALBUM);
					setter.put(MediaStore.Audio.Media.ALBUM, mAlbum);
				}
			} catch (CannotReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TagException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReadOnlyFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAudioFrameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		
			// Insert to DB
			uri = MediaStore.Audio.Media.getContentUriForPath(setFile.getAbsolutePath());
			context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + setFile.getAbsolutePath() + "\"", null);
			Uri newUri = context.getContentResolver().insert(uri, setter);
			RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);

			NPathDb.createDB(Ringing.getString(2));
			NPathDb.close();
			
			Ringing.close();
			setter.clear();
			NAllCursor.close();
			NotiDb.close();
			return setFile.getName();
		}
	}
}
