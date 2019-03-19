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
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

/**
 * BackgroundServiceForRingtone.java
 *
 * 전화 여부를 실시간으로 감지하는 서비스 클래스
 */

public class BackgroundServiceForRingtone extends Service {
	private TelephonyManager telMgr;
	private CallStateListener newCSL;
	private int serviceId = 74836;

	@Override
	public void onCreate() {
		super.onCreate();
		newCSL = new CallStateListener(this);
		telMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		unregisterRestartAlarm();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		telMgr.listen(newCSL, PhoneStateListener.LISTEN_CALL_STATE);

		// 알림 설정
		NotificationCompat.Builder nf = new NotificationCompat.Builder(this);
		nf.setContentTitle("Eris Ringtone Manager");
		nf.setContentText("Ringtone random running");
		nf.setPriority(Notification.PRIORITY_MIN);
		startForeground(serviceId, nf.build());
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		registerRestartAlarm();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void registerRestartAlarm() {
		Intent intent = new Intent(this, RestartRingtone.class);
		intent.setAction(RestartRingtone.ACTION_RESTART_PERSISTENTSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		long firstTime = SystemClock.elapsedRealtime();
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime+500, 10*1000, sender);
	}
	
	private void unregisterRestartAlarm() {
		Intent intent = new Intent(this, RestartRingtone.class);
		intent.setAction(RestartRingtone.ACTION_RESTART_PERSISTENTSERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		am.cancel(sender);
	}
}