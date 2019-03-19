/**
 * Created by Kang Tae Jun on 2016-01-05.
 *
 * The MIT License
 *
 * Copyright (c) 2016 Kang Tae Jun (Nira)
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

package com.ring.roxyeris.filelist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ring.roxyeris.db.filedb.FileListDBAdapter;
import com.ring.roxyeris.db.notifile.NotifileListDBAdapter;
import com.ring.roxyeris.Mp3player;
import com.ring.roxyeris.R;
import com.ring.roxyeris.base.FileAdapter;
import com.ring.roxyeris.base.FileList;
import com.ring.roxyeris.base.RMBase;

import java.util.ArrayList;

/**
 * BaseMusicList - 음악 목록 표시를 위한 인터페이스
 */
public class BaseMusicList extends RMBase {
    // UI element
    protected ListView musicList;
    protected TextView emptyView;
    protected FileAdapter fa;

    // Class element
    protected Mp3player previewPlayer;
    protected ArrayList<FileList> allFiles;
    protected Cursor cursor;

    // DB manager (for deletion)
    protected FileListDBAdapter FileDb;
    protected NotifileListDBAdapter NotiDb;

    // Data element
    protected ArrayList<String> musicMenu;
    protected String[] itemList;

    /**
     * 앱을 완전히 종료하지 않고 다시 작업 목록에서 불러오는 경우에는 onResume로 시작하며,
     * onCreate를 통해 앱이 시작되어도 onResume를 반드시 거치므로, 미리듣기 기능의 처리를 위하여
     * onResume에서 intialize를 처리함
     *
     * 단, 이전버전과 다르게 메뉴를 불러올 수 있으므로 여기서는 파일 관리만 수행하도록 기능을 변경
     * RMBase를 extend하여 ListActivity가 사용 불가능하므로, List를 하나 생성하여 적용
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_musiclist, container);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(previewPlayer != null && previewPlayer.isPlaying()) {
            previewPlayer.mp3Pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
        loadList();
        showList();

        if(previewPlayer != null && previewPlayer.isPause()) {
            previewPlayer.mp3Resume();
        }
    }

    @Override
    protected void initialize() {
        super.initialize();

        musicMenu = new ArrayList<String>();
        musicMenu.add(getText(R.string.preview).toString());
        musicMenu.add(getText(R.string.delete).toString());

        itemList = new String[musicMenu.size()];
        for(int i = 0; i < musicMenu.size(); i++) {
            itemList[i] = musicMenu.get(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater2 = getMenuInflater();
        inflater2.inflate(R.menu.actionbar_filelist, menu);
        return true;
    }
    // onOptionsItemSelected는 각 클래스에서 정의

    /**
     * DB에서 음악 리스트를 불러옴
     */
    public Cursor getListFromDB() {
        // implement on each class
        return null;
    }

    /**
     * 음악 리스트에서 파일별 확장자를 분석하여 아이콘을 포함한 리스트를 만듦
     */
    public void loadList() {
        cursor = getListFromDB();
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++) {
            String name = cursor.getString(1);
            String path = cursor.getString(2);
            int icon;

            // Check extension
            if (path.toLowerCase().endsWith(".mp3"))
                icon = R.drawable.icon_mp3;
            else if (path.toLowerCase().endsWith(".ogg"))
                icon = R.drawable.icon_ogg;
            else if (path.toLowerCase().endsWith(".wav"))
                icon = R.drawable.icon_wav;
            else if (path.toLowerCase().endsWith(".flac"))
                icon = R.drawable.icon_flac;
            else
                icon = -1;

            // Add file
            if (icon != -1) {
                FileList fladd = new FileList(icon, name, path);
                allFiles.add(fladd);
            }
            cursor.moveToNext();
        }
    }

    /**
     * 리스트 보여주기
     */
    public void showList() {
        fa = new FileAdapter(this, 0, allFiles);
        musicList.setAdapter(fa);
        musicList.setOnItemClickListener(listener);
    }

    /**
     * 리스트 터치 인터렉션
     */
    // Music list listener
    protected AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final FileList selected = allFiles.get(position);
            final int pos = position;

            // Open dialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(BaseMusicList.this);
            dialog.setTitle(getText(R.string.m_msg));
            dialog.setItems(itemList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0) {
                        // Preview
                        previewPlayer.PlayMp3(selected.getFileName(), selected.getPath());
                    }
                    else if(which == 1) {
                        // Delete
                        openDialogDelete(pos);
                    }
                }
            });
            dialog.setNegativeButton(getText(R.string.m_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    };

    // Delete Dialog
    public void openDialogDelete(final int position) {
        // Implement on each class
    }
}

