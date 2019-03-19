/**
 * Created by Kang Tae Jun on 2015-04-05.
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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ring.roxyeris.dataclass.IntStringStructure;
import com.ring.roxyeris.R;

import java.util.ArrayList;

/*
 * NavigationDrawerLayoutBase.java
 * 프레임, 드로어, 액션바를 가지는 앱 전체의 기본 UI 베이스
 */
public class NavigationDrawerLayoutBase extends AppCompatActivity {
    // 기본 액티비티 항목들
    protected ArrayList<IntStringStructure> menuItem;
    protected ListView naviMenu;
    protected FrameLayout container;

    // 액션바 토글 - 사용자에게 네비게이션 드로어의 상태를 직관적으로 알려줌
    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle dToggle;

    // 네비게이션에서 선택한 것
    protected int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_ui);

        // 모든 어플리케이션 화면에 공통적으로 들어가게 되는 메뉴
        menuItem = new ArrayList<IntStringStructure>();
        menuItem.add(new IntStringStructure(R.drawable.icon_shuffle, getText(R.string.activity_runner).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_ringtone, getText(R.string.activity_ringlist).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_notification, getText(R.string.activity_notilist).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_explorer, getText(R.string.activity_explorer).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_allfile, getText(R.string.activity_musiclist).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_setting, getText(R.string.activity_setting).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_about, getText(R.string.activity_about).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_copyright, getText(R.string.activity_copyright).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_patch, getText(R.string.activity_patch).toString()));
        menuItem.add(new IntStringStructure(R.drawable.icon_blog, getText(R.string.activity_blog).toString()));

        naviMenu = (ListView)findViewById(R.id.listviewNavi);
        container = (FrameLayout)findViewById(R.id.frameMain);

        // DrawerLayout의 인스턴스 획득
        drawer = (DrawerLayout)findViewById(R.id.drawer);

        // 액션바 토글 인스턴스 생성
        dToggle = new ActionBarDrawerToggle(this, drawer,
                //R.drawable.ic_drawer,     // V7에서 안씀
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            // 내부 메소드
        };

        // 네비게이션 메뉴 어댑터 등록
        naviMenu.setAdapter(
                new DrawerAdapter(this, menuItem)
        );
        //naviMenu.setOnItemClickListener(new DrawerItemListener());
        naviMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openActivity(position);
            }
        });

        drawer.setDrawerListener(dToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 액션바 아이콘을 업 네비게이션으로 표시 (뒤로 가기 추가 가능)

        // 첫 화면은 Home Timeline
        //DrawerItemListener dit = new DrawerItemListener();
        //dit.viewSelector(0);
    }

    // 액션바 토글 상태를 지속적으로 동기화하기 위한 메소드
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        dToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dToggle.onConfigurationChanged(newConfig);
    }

    // 액션바 버튼 추가를 위한 메소드
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflater를 사용하여 액션바에서 사용할 메뉴를 가져옴
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // 홈 버튼을 눌렀을 때 액션바 토글에서 모든 이벤트를 처리하고 다른 곳으로 전달되지 않도록 방지
    // 액션바 버튼의 동작도 여기서 정의함
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dToggle.onOptionsItemSelected(item)){
            return true;
        }
        /*return super.onOptionsItemSelected(item);*/
        switch(item.getItemId()) {
            case R.id.action_search:
                //startActivity(new Intent(this, Search.class));
                return true;
            //case R.id.action_newtweet:
                //startActivity(new Intent(this, WriteNewTweet.class));
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // 액티비티를 FrameLayout에 띄우는 방법
    /*
     * 단, User Info를 열기 위해서는 자기 정보를 받아야 하며 이는
     * 각 사용자의 함수에서 획득해야 함
     */
    protected void openActivity(int position) {
    }
}

