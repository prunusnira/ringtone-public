/**
 * Created by Kang Tae Jun on 2015.
 *
 * The MIT License
 *
 * Copyright (c) 2015 Kang Tae Jun (Nira)
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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * BackgroundServiceForNoti.java
 *
 * AudioManager를 사용하여 기본 시스템 알림음을 변경
 */

public class BackgroundServiceForNotification extends Service implements OnAudioFocusChangeListener {
	// Ringtone setter setting
	private NotificationSetter notificationSetter;
	private String currentNotification;
	private AudioManager mAudioManager;
	private Context ctx;
	private int serviceId = 74837;

	@Override
	public void onCreate() {
		ctx = this;
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		unregisterRestartAlarm();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAudioManager.requestAudioFocus(this, AudioManager.STREAM_NOTIFICATION, AudioManager.AUDIOFOCUS_GAIN);

		// 알림 설정
		NotificationCompat.Builder nf = new NotificationCompat.Builder(this);
		nf.setContentTitle("Eris Ringtone Manager");
		nf.setContentText("Notification random running");
		nf.setPriority(Notification.PRIORITY_MIN);
		startForeground(serviceId, nf.build());
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		registerRestartAlarm();
	}

	public void onAudioFocusChange(int focusChange) {
		//if(focusChange == AudioManager.AUDIOFOCUS_LOSS) {
			notificationSetter = new NotificationSetter(ctx);
			currentNotification = notificationSetter.SetNoti();
			Log.d("CurrentNoti", currentNotification);
		//}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void registerRestartAlarm() {
		Intent intent = new Intent(this, RestartNotification.class);
		intent.setAction(RestartNotification.ACTION_RESTART_PERSISTENTSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		long firstTime = SystemClock.elapsedRealtime();
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime + 500, 10 * 1000, sender);
	}

	private void unregisterRestartAlarm() {
		Intent intent = new Intent(this, RestartNotification.class);
		intent.setAction(RestartNotification.ACTION_RESTART_PERSISTENTSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}
}
