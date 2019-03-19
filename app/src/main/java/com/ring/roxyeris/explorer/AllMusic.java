/**
 * Created by Kang Tae Jun on 2015-10-02.
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
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

/**
 * Created by arinpc on 2015-10-02.
 */
public class AllMusic extends RMBase implements IExplorer {
    // Local Variable
    private static final File path = Environment.getExternalStorageDirectory(); //new File("/sdcard/");
    private ArrayList<FileList>	fileal = new ArrayList<FileList>();
    private Mp3player mp3p = new Mp3player(this);

    // DB Manager
    private FileListDBAdapter FileDb;
    private NotifileListDBAdapter NotiDb;

    // Data variable
    private FileAdapter fladapter;
    private Thread load_thread;
    private String curpath;

    // UI variable
    private ProgressDialog loadDialog;
    private ListView filelist;
    private Button select;
    private TextView emptyView;
    private SearchView searchView;

    // Listener
    private View.OnClickListener btnSelectListener = new View.OnClickListener() {
        public void onClick(View v) {
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(AllMusic.this);

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
                Toast.makeText(AllMusic.this, getText(R.string.nopermission).toString(), Toast.LENGTH_SHORT).show();
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
        getLayoutInflater().inflate(R.layout.layout_filelist, container);

        initialize();
        createThreadAndDialog(); // create thread and control with handler
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileDb.close();
        NotiDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_search, menu);

        // searchview를 사용한 검색 설정
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getText(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                if(query.length() == 0) {
                    // Show all
                    filelist.setAdapter(fladapter);
                }
                else {
                    ArrayList<FileList> tmplist = new ArrayList<FileList>();
                    FileAdapter tmpflist = new FileAdapter(AllMusic.this, R.layout.listview, tmplist);
                    int listlen = fileal.size();
                    for(int i = 0; i < listlen; i++) {
                        if(fileal.get(i).getFileName().toLowerCase().contains(query.toLowerCase())) {
                            if(fileal.get(i).getFileName().toLowerCase().endsWith(".mp3"))
                                tmplist.add(new FileList(R.drawable.icon_mp3, fileal.get(i).getFileName(), fileal.get(i).getPath()));
                            if(fileal.get(i).getFileName().toLowerCase().endsWith(".ogg"))
                                tmplist.add(new FileList(R.drawable.icon_ogg, fileal.get(i).getFileName(), fileal.get(i).getPath()));
                            if(fileal.get(i).getFileName().toLowerCase().endsWith(".wav"))
                                tmplist.add(new FileList(R.drawable.icon_wav, fileal.get(i).getFileName(), fileal.get(i).getPath()));
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && fileal.get(i).getFileName().toLowerCase().endsWith(".flac"))
                                tmplist.add(new FileList(R.drawable.icon_flac, fileal.get(i).getFileName(), fileal.get(i).getPath()));
                        }
                    }
                    fileal = tmplist;
                    filelist.setAdapter(tmpflist);
                }
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    public void initialize() {
        super.initialize();

        FileDb = new FileListDBAdapter(this);
        NotiDb = new NotifileListDBAdapter(this);
        FileDb.open();
        NotiDb.open();

        filelist = (ListView)findViewById(R.id.list_files);
        fladapter = new FileAdapter(this, R.layout.listview, fileal);
        select = (Button)findViewById(R.id.select);
        select.setOnClickListener(btnSelectListener);

        // listener for list
        filelist.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1));
        filelist.setTextFilterEnabled(true);
        filelist.setOnItemClickListener(listListener);

        emptyView = (TextView) findViewById(R.id.emptyview);
        filelist.setEmptyView(emptyView);

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

    public void Process(File file) {
        String filepath;
        FileList adder;

        if(file.isFile() && file.getName().toLowerCase().endsWith(".mp3")) {
            filepath = file.getPath();
            adder = new FileList(R.drawable.icon_mp3, file.getName(), filepath);
            fileal.add(adder);
        }
        if(file.isFile() && file.getName().toLowerCase().endsWith(".wav")) {
            filepath = file.getPath();
            adder = new FileList(R.drawable.icon_wav, file.getName(), filepath);
            fileal.add(adder);
        }
        if(file.isFile() && file.getName().toLowerCase().endsWith(".ogg")) {
            filepath = file.getPath();
            adder = new FileList(R.drawable.icon_ogg, file.getName(), filepath);
            fileal.add(adder);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && file.isFile() && file.getName().toLowerCase().endsWith(".flac")) {
            filepath = file.getPath();
            adder = new FileList(R.drawable.icon_flac, file.getName(), filepath);
            fileal.add(adder);
        }
        else if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            if(listOfFiles!=null) {
                for (int i = 0; i < listOfFiles.length; i++)
                    Process(listOfFiles[i]);
            } else {
                //Toast.makeText(this, spcs + " [ACCESS DENIED]", 1000).show();
            }
        }
    }

    public void AddAll(int mode) {
        AlertDialog.Builder addList = new AlertDialog.Builder(this);
        addList.setMessage(getText(R.string.addlistreal));
        addList.setCancelable(true);
        addList.setPositiveButton(getText(R.string.m_ring), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                int len = fileal.size();
                for (int i = 0; i < len; i++) {
                    FileDb.createDB(fileal.get(i).getFileName(), fileal.get(i).getPath());
                }
            }
        });
        addList.setNegativeButton(getText(R.string.m_noti), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int len = fileal.size();
                for (int i = 0; i < len; i++) {
                    NotiDb.createDB(fileal.get(i).getFileName(), fileal.get(i).getPath());
                }
            }
        });
        addList.setNeutralButton(getText(R.string.m_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
       addList.show();
    }
}
