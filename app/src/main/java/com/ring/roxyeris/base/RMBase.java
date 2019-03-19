/**
 * Created by Kang Tae Jun on 2015-05-03.
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

package com.ring.roxyeris.base;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.ring.roxyeris.R;
import com.ring.roxyeris.filelist.NotimusicListBase;
import com.ring.roxyeris.setting.SettingActivity;
import com.ring.roxyeris.explorer.AllMusic;
import com.ring.roxyeris.explorer.FileExplorer;
import com.ring.roxyeris.MainRunner;
import com.ring.roxyeris.filelist.RingmusicListBase;

public class RMBase extends NavigationDrawerLayoutBase {
    protected SharedPreferences pref;
    protected AdView adview;
    protected boolean isAdRunning = false;

    protected void initialize() {
        // 공통 속성
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        // adview 추가
        if(!isAdRunning) {
            adview = new AdView(this, AdSize.BANNER, "GOOGLE_ADMOB_ID_HERE");
            adview.loadAd(new AdRequest());

            LinearLayout layout = (LinearLayout) findViewById(R.id.adview);

            layout.addView(adview);
            isAdRunning = true;
        }
    }

    /////////////////////////////////////////////////////////////
    /* 액티비티를 FrameLayout에 띄우는 방법
     * 본래 NavigationDrawerLayoutBase에 있어야 하지만
     * 사용자의 정보가 필요하여 이곳에서 작성 */
    /////////////////////////////////////////////////////////////
    protected void openActivity(int position) {
        // position은 drawer에서의 위치
        drawer.closeDrawer(naviMenu);
        this.position = position;

        switch(position) {
            case 0:
                // home
                startActivity(new Intent(this, MainRunner.class));
                break;
            case 1:
                // ring list
                startActivity(new Intent(this, RingmusicListBase.class));
                break;
            case 2:
                // noti list
                startActivity(new Intent(this, NotimusicListBase.class));
                break;
            case 3:
                // explorer
                startActivity(new Intent(this, FileExplorer.class));
                break;
            case 4:
                // all file
                startActivity(new Intent(this, AllMusic.class));
                break;
            case 5:
                // setting
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case 6:
                // about
                AlertDialog.Builder about = new AlertDialog.Builder(this);
                about.setTitle("About");
                about.setMessage(R.string.about);
                about.setCancelable(true);
                about.setIcon(R.drawable.icon_about);
                about.show();
                break;
            case 7:
                // copyright
                AlertDialog.Builder cpr = new AlertDialog.Builder(this);
                cpr.setTitle("Resource Copyright");
                cpr.setMessage(R.string.copyright);
                cpr.setCancelable(true);
                cpr.setIcon(R.drawable.icon_copyright);
                cpr.show();
                break;
            case 8:
                // patch note
                AlertDialog.Builder ver = new AlertDialog.Builder(this);
                ver.setTitle("Version/Update History");
                ver.setMessage(R.string.ver);
                ver.setCancelable(true);
                ver.setIcon(R.drawable.icon_patch);
                ver.show();
                break;
            case 9:
                // blog
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getText(R.string.move_url).toString())));
                break;
                /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_logout)
                    .setMessage(R.string.que_logout)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences.Editor edit = pref.edit();
                            edit.remove("accToken");
                            edit.remove("accTokSec");
                            finish();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                AlertDialog dialog = builder.create();
                dialog.show();*/
            default:
                break;
        }
    }
}
