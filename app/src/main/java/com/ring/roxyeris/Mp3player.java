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

package com.ring.roxyeris;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.widget.Toast;

public class Mp3player {
	private Context context;
	private static MediaPlayer mp3Player;
	
	public Mp3player(Context context) {
		this.context = context;
	}
	
	public void PlayMp3(String filename, final String mediapath) { // Play 선택 시 재생
		Toast.makeText(context, mediapath, Toast.LENGTH_SHORT).show();
		final AlertDialog.Builder mp3dialog = new AlertDialog.Builder(context);

		mp3dialog.setMessage("Now playing:\n" + filename)// + "\nLength:" + timeConverter(mp3Player.getDuration()))
			.setCancelable(false)
			.setPositiveButton("▶", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// Test Play Ringtone
					mp3Play(mediapath);
					mp3dialog.show();
				}
			})
				.setNegativeButton("■(Close)", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Add to Database
						mp3Stop();
						dialog.cancel();
					}
				});
		mp3dialog.show();
	}

	public String timeConverter(int duration) {
		int sec = duration % 60;
		int min = duration / 60;
		return min+":"+sec;
	}
	
	public void mp3Play(String mediapath) {
		mp3Stop();
		mp3Player = new MediaPlayer();
	    try{
			mp3Player.setDataSource(mediapath); // mp3파일 경로
	    	mp3Player.prepare(); // 준비
	    	mp3Player.setLooping(false); // 반복재생 false
	    	mp3Player.start(); // 시작
	    } catch(IOException e){
	        Toast.makeText(context, "Error!!", Toast.LENGTH_SHORT).show();
	    }
	}
	
	public boolean isPlaying() {
		if(mp3Player == null) {
			return false;
		}
		else if(mp3Player.isPlaying()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPause() {
		if(mp3Player != null && mp3Player.getCurrentPosition() > 0) return true;
		else return false;
	}
	
	public void mp3Resume() {
		mp3Player.start();
	}
	
	public void mp3Pause() {
		mp3Player.pause();
	}
	
	public void mp3Stop() {
		 if (mp3Player != null) {
			 mp3Player.stop(); // 중지
			 mp3Player.release(); // 자원 반환
			 mp3Player = null;
	     }
	}
}
