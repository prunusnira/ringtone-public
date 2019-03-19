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

package com.ring.roxyeris.filelist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.ring.roxyeris.db.filedb.FileListDBAdapter;
import com.ring.roxyeris.Mp3player;
import com.ring.roxyeris.R;
import com.ring.roxyeris.base.FileList;

import java.util.ArrayList;

/**
 * RingmusicListBase
 * 현재 등록되어있는 음악의 리스트를 보여줌
 * 등록된 음악의 리스트는 SQLite DB에 저장되어 있음
 * @refer com.ring.roxyeris.db.filedb
 */
public class RingmusicListBase extends BaseMusicList {
    @Override
    protected void initialize() {
        super.initialize();

        // UI
        musicList = (ListView) findViewById(R.id.music_list);
        emptyView = (TextView) findViewById(R.id.emptyview);
        musicList.setEmptyView(emptyView);

        // Local variables
        previewPlayer = new Mp3player(this);
        allFiles = new ArrayList<FileList>();

        // DB Open
        FileDb = new FileListDBAdapter(this);
        FileDb.open();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.fl_menu_delall) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getText(R.string.deleteall));
            dialog.setMessage(getText(R.string.sure));
                    dialog.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // for each class
                            FileDb.deleteAllDB();
                            allFiles.clear();
                            showList();
                        }
                    });
            dialog.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
        return true;
    }

    /**
     * DB에서 음악 리스트를 불러옴
     */
    public Cursor getListFromDB() {
        // implement on each class
        Cursor c = FileDb.fetchAll();
        return c;
    }

    // Delete Dialog
    public void openDialogDelete(final int position) {
        AlertDialog.Builder delete = new AlertDialog.Builder(RingmusicListBase.this);
        delete.setTitle(getText(R.string.delete));
        delete.setMessage(getText(R.string.delsure));
        delete.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                FileDb.deleteDB(position + 1);
                allFiles.remove(position);
                showList();
            }
        });
        delete.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        delete.show();
    }
}
