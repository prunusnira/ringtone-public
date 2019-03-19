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

package com.ring.roxyeris.explorer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ring.roxyeris.db.filedb.FileListDBAdapter;
import com.ring.roxyeris.db.notifile.NotifileListDBAdapter;
import com.ring.roxyeris.Mp3player;
import com.ring.roxyeris.R;
import com.ring.roxyeris.base.FileAdapter;
import com.ring.roxyeris.base.FileList;
import com.ring.roxyeris.base.RMBase;

import java.io.File;
import java.util.ArrayList;

public class FileExplorer extends RMBase implements IExplorer {
    // Local Variable
    private static final File path = Environment.getExternalStorageDirectory(); //new File("/sdcard/");
    private ArrayList<FileList>	fileal = new ArrayList<FileList>();
    private Mp3player mp3p = new Mp3player(this);
    private String mode;

    // DB Manager
    private FileListDBAdapter FileDb;
    private NotifileListDBAdapter NotiDb;

    // Data variable
    private FileAdapter fladapter;
    private Thread load_thread;
    private String curpath;

    // UI variable
    private ProgressDialog loadDialog; // 로딩 표시를 위한 progress dialog
    private ListView filelist; // 파일 목록을 보여주는 리스트뷰
    private TextView curfld; // 현재 경로 표시
    private Button btnAddAll; // 현재 폴더의 모든 파일 추가
    private TextView emptyView;

    // Listener
    private View.OnClickListener btnSelectListener = new View.OnClickListener() {
        public void onClick(View v) {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(FileExplorer.this);

            alt_bld.setMessage("Will you add all the files?").setCancelable(true).
                    setPositiveButton(getText(R.string.m_ring), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Test Play Ringtone
                            AddAll(RINGMODE);
                        }
                    }).setNegativeButton(getText(R.string.m_noti),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddAll(NOTIMODE);
                        }
                    }).setNeutralButton(getText(R.string.m_cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alt_bld.create();
            // Title for AlertDialog
            alert.setTitle("Add all files");
            alert.show();
        }
    };

    private AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String fpath = fileal.get(position).getPath();
            if(fileal.get(position).getFileName().equals(".android_secure")) {
                Toast.makeText(FileExplorer.this, getText(R.string.nopermission).toString(), Toast.LENGTH_SHORT).show();
            }
            else if(new File(fpath).isFile()) {
                String name = fileal.get(position).getFileName();
                String path = fileal.get(position).getPath();
                SelectDialog(name, path);
            }
            else if(new File(fpath).isDirectory()){
                fladapter.clear();
                fileal.clear();
                Process(new File(fpath));
                filelist.setAdapter(fladapter);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_explorer, container);

        initialize();
        createThreadAndDialog(); // create thread and control with handler
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileDb.close();
        NotiDb.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mp3p.isPlaying()) mp3p.mp3Pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mp3p.isPause()) mp3p.mp3Resume();
    }

    public void initialize() {
        super.initialize();

        FileDb = new FileListDBAdapter(this);
        NotiDb = new NotifileListDBAdapter(this);
        FileDb.open();
        NotiDb.open();

        filelist = (ListView)findViewById(R.id.list_files);
        fladapter = new FileAdapter(this, R.layout.listview, fileal);
        btnAddAll = (Button)findViewById(R.id.select);
        btnAddAll.setOnClickListener(btnSelectListener);

        // listener for list
        filelist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1));
        filelist.setTextFilterEnabled(true);
        filelist.setOnItemClickListener(listListener);

        emptyView = (TextView) findViewById(R.id.emptyview);
        filelist.setEmptyView(emptyView);

        curfld = (TextView)findViewById(R.id.current);

        loadDialog = new ProgressDialog(this);
        loadDialog = ProgressDialog.show(this, "", "File List Loading...", true);
    }

    public void createThreadAndDialog() {
	    /* ProgressDialog */
        load_thread = new Thread(new Runnable() {
            public void run() {
                // time consuming works
                Process(path);
                handler.sendEmptyMessage(0);
            }
        });
        load_thread.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                loadDialog.dismiss();
                filelist.setAdapter(fladapter); // View update
                load_thread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    /**
     * 음악 파일 선택시 나타나는 다이얼로그
     * @param filename
     * @param filepath
     */
    public void SelectDialog(final String filename, final String filepath) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setMessage(getText(R.string.m_msg)).setCancelable(true).
                setPositiveButton(getText(R.string.m_play), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Test Play Ringtone
                        mp3p.PlayMp3(filename, filepath);
                    }
                }).setNeutralButton(getText(R.string.m_add),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Add to Database
                        SelectDlgType(filename, filepath);
                    }
                }).setNegativeButton(getText(R.string.m_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle(filename);
        // Icon for AlertDialog
        if(filename.toLowerCase().endsWith(".mp3"))
            alert.setIcon(R.drawable.icon_mp3);
        if(filename.toLowerCase().endsWith(".ogg"))
            alert.setIcon(R.drawable.icon_ogg);
        if(filename.toLowerCase().endsWith(".wav"))
            alert.setIcon(R.drawable.icon_wav);
        if(filename.toLowerCase().endsWith(".flac"))
            alert.setIcon(R.drawable.icon_flac);
        alert.show();
    }

    /**
     * 다이얼로그에서 파일 추가를 결정했을 때 벨소리/알림음 여부를 선택하는 다른 다이얼로그
     * @param filename
     * @param filepath
     */
    public void SelectDlgType(final String filename, final String filepath) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setMessage(getText(R.string.m_which)).setCancelable(true).
                setPositiveButton(getText(R.string.m_ring),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Add to ring db
                        FileDb.createDB(filename, filepath);
                    }
                }).setNegativeButton(getText(R.string.m_noti),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Add to noti db
                        NotiDb.createDB(filename, filepath);
                    }
                }).setNeutralButton(getText(R.string.m_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle(filename);
        // Icon for AlertDialog
        if(filename.toLowerCase().endsWith(".mp3"))
            alert.setIcon(R.drawable.icon_mp3);
        if(filename.toLowerCase().endsWith(".ogg"))
            alert.setIcon(R.drawable.icon_ogg);
        if(filename.toLowerCase().endsWith(".wav"))
            alert.setIcon(R.drawable.icon_wav);
        if(filename.toLowerCase().endsWith(".flac"))
            alert.setIcon(R.drawable.icon_flac);
        alert.show();
    }

    /**
     * 현재 경로에서 파일 목록을 불러오는 메소드
     * @param file
     */
    public void Process(File file) {
        String filepath;
        FileList adder;
        curfld.setText(getText(R.string.current) + ": " + file.getName());
        curpath = file.getPath();
        File[] listOfFiles = file.listFiles();

        if(file.getParent() != null)
            fileal.add(new FileList(R.drawable.foldericon, getText(R.string.upper).toString(), file.getParent()));
        if(listOfFiles != null) {
            for (File current : listOfFiles) {
                if (current.isDirectory()) {
                    filepath = current.getPath();
                    adder = new FileList(R.drawable.foldericon, current.getName(), filepath);
                    fileal.add(adder);
                }
            }
            for (File current : listOfFiles) {
                if (current.isFile() && current.getName().endsWith(".mp3")) {
                    filepath = current.getPath();
                    adder = new FileList(R.drawable.icon_mp3, current.getName(), filepath);
                    fileal.add(adder);
                }
                if (current.isFile() && current.getName().endsWith(".wav")) {
                    filepath = current.getPath();
                    adder = new FileList(R.drawable.icon_wav, current.getName(), filepath);
                    fileal.add(adder);
                }
                if (current.isFile() && current.getName().endsWith(".ogg")) {
                    filepath = current.getPath();
                    adder = new FileList(R.drawable.icon_ogg, current.getName(), filepath);
                    fileal.add(adder);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && current.isFile() && current.getName().endsWith(".flac")) {
                    filepath = current.getPath();
                    adder = new FileList(R.drawable.icon_flac, current.getName(), filepath);
                    fileal.add(adder);
                }
            }
        }
    }

    public void AddAll(int mode) {
        File[] list = new File(curpath).listFiles();
        int count = 0;
        if(list != null) {
            for (File current : list) {
                if (current.isFile() && (current.getName().endsWith(".mp3") || current.getName().endsWith(".wav") || current.getName().endsWith(".ogg") || current.getName().endsWith(".flac"))) {
                    if (mode == RINGMODE) FileDb.createDB(current.getName(), current.getPath());
                    if (mode == NOTIMODE) NotiDb.createDB(current.getName(), current.getPath());
                    count++;
                }
            }
            Toast.makeText(this, getText(R.string.madd1).toString() + ' ' + count + ' ' + getText(R.string.madd2).toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
