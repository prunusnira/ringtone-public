/**
 * Created by Kang Tae Jun on 2015-07-01.
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

package com.ring.roxyeris;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ring.roxyeris.db.filedb.FileListDBAdapter;
import com.ring.roxyeris.db.notifile.NotifileListDBAdapter;
import com.ring.roxyeris.background.BackgroundServiceForNotification;
import com.ring.roxyeris.background.BackgroundServiceForRingtone;
import com.ring.roxyeris.base.RMBase;

public class MainRunner extends RMBase {
    private Switch swRunRing;
    private Switch swRunNoti;

    // Service Intent
    private Intent ringService;
    private Intent notiService;

    // DB manager (for deletion)
    protected FileListDBAdapter FileDb;
    protected NotifileListDBAdapter NotiDb;

    private CompoundButton.OnCheckedChangeListener ringSw = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked == true) {
                // 서비스 시작 전 파일 존재 확인
                FileDb.open();
                if(FileDb.fetchAll().getCount() != 0) {
                    // Turn on
                    swRunRing.setText(R.string.sw_ring_on);
                    startService(ringService);
                }
                else {
                    // 사용자 알림
                    noticeNoFile();
                    if(isMyServiceRunning(BackgroundServiceForRingtone.class)) {
                        stopService(ringService);
                    }
                    swRunRing.setChecked(false);
                }
                FileDb.close();
            }
            else {
                // Turn off
                swRunRing.setText(R.string.sw_ring_off);
                stopService(ringService);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener notiSw = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked == true) {
                // 서비스 시작 전 파일 존재 확인
                NotiDb.open();
                if(NotiDb.fetchAll().getCount() != 0) {
                    // Turn on
                    swRunNoti.setText(R.string.sw_noti_on);
                    startService(notiService);
                }
                else {
                    // 사용자 알림
                    noticeNoFile();
                    if(isMyServiceRunning(BackgroundServiceForNotification.class)) {
                        stopService(notiService);
                    }
                    swRunNoti.setChecked(false);
                }
                NotiDb.close();
            }
            else {
                // Turn off
                swRunNoti.setText(R.string.sw_noti_off);
                stopService(notiService);
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_mainrunner, container);

        initialize();
    }

    protected void initialize() {
        super.initialize();
        FileDb = new FileListDBAdapter(this);
        NotiDb = new NotifileListDBAdapter(this);

        ringService = new Intent(this, BackgroundServiceForRingtone.class);
        notiService = new Intent(this, BackgroundServiceForNotification.class);

        swRunRing = (Switch) findViewById(R.id.sw_ring);
        swRunNoti = (Switch) findViewById(R.id.sw_noti);

        swRunRing.setOnCheckedChangeListener(ringSw);
        swRunNoti.setOnCheckedChangeListener(notiSw);

        // 서비스 실행 중 여부 검사
        if(isMyServiceRunning(BackgroundServiceForRingtone.class)) {
            swRunRing.setChecked(true);
        }

        if(isMyServiceRunning(BackgroundServiceForNotification.class)) {
            swRunNoti.setChecked(true);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void noticeNoFile() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getText(R.string.nolistfile_title));
        dialog.setMessage(getText(R.string.nolistfile_msg));
        dialog.setNeutralButton(R.string.m_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
