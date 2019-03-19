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

public class RestartRingtone extends BroadcastReceiver {
	public static final String ACTION_RESTART_PERSISTENTSERVICE="ACTION.RestartRingtone.BackgroundServiceForRingtone";
	
	// 리스너가 실행 중 죽어도 자동으로 재시작을 수행 START_STICKY 보다 강제로 수행함
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)) {
			Intent i = new Intent(context, BackgroundServiceForRingtone.class);
			context.startService(i);
		}
	}
}
