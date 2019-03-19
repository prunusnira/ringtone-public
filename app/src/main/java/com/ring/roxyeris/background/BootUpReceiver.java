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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ring.roxyeris.R;

/**
 * BootUpReceiver.java
 *
 * 디바이스 부팅시 미리 설정된 옵션에 따라 동작을 수행
 * 각 랜덤 동작을 서비스로 실행
 */
public class BootUpReceiver extends BroadcastReceiver {
	private Context mctx;
	private String autoStartType;
	private SharedPreferences pref;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mctx = context;
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		autoStartType = pref.getString("startup_onoff1", "2");
		int type = Integer.valueOf(autoStartType);

		Intent nci = new Intent(mctx, BackgroundServiceForNotification.class);
		Intent bci = new Intent(mctx, BackgroundServiceForRingtone.class);
        
        switch(type) {
			case 0:
				// 벨소리 off 알림 off
				break;
			case 1:
				// 벨소리 on 알림 on
				mctx.startService(nci);
				mctx.startService(bci);
				Toast.makeText(context, mctx.getText(R.string.auto1), Toast.LENGTH_SHORT).show();
				break;
			case 2:
				// 벨소리 on 알림 off
				mctx.startService(bci);
				Toast.makeText(context, mctx.getText(R.string.auto2), Toast.LENGTH_SHORT).show();
				break;
			case 3:
				// 벨소리 off 알림 on
				mctx.startService(nci);
				Toast.makeText(context, mctx.getText(R.string.auto3), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
        }
	}
}