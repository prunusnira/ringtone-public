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

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ring.roxyeris.Mp3player;

/**
 * CallStateListener.java
 *
 * 전화가 오는지의 여부를 파악하는 클래스
 * 전화중인 상태에서 idle로 전환 시 벨소리를 변경
 */
public class CallStateListener extends PhoneStateListener {
	private RingtoneSetter ringtoneSetter;
	private String currentRing;
	private Context ctx;
	private Mp3player mp3p; // 전화가 올 때 미리듣기가 진행중인 경우 미리 듣기를 멈추기 위함
	
	public CallStateListener(Context context) {
		ctx = context;
		mp3p = new Mp3player(ctx);
	}
	
	// 전화 상태 변경 시
	@Override
	public void onCallStateChanged(int state, String incomingNum) {
		super.onCallStateChanged(state, incomingNum);
		switch(state) {
		case TelephonyManager.CALL_STATE_RINGING:
			if(mp3p.isPlaying()) mp3p.mp3Pause();
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			if(mp3p.isPause()) mp3p.mp3Resume();

			ringtoneSetter = new RingtoneSetter(ctx);
			currentRing = ringtoneSetter.SetRing();

			Log.d("CurrentRing-IDLE", currentRing);
			break;
		}
	}
}