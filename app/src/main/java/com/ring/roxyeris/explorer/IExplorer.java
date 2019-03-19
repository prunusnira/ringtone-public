/**
 * Created by Kang Tae Jun on 2015-10-12.
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
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.ring.roxyeris.base.FileList;

import java.io.File;

public interface IExplorer {
    int RINGMODE = 0;
    int NOTIMODE = 1;

    /**
     * 쓰레드를 사용하여 progress dialog와 process 메소드의 동작을 진행
     */
    void createThreadAndDialog();

    /**
     * 음악 파일 선택시 나타나는 다이얼로그
     * @param filename
     * @param filepath
     */
    void SelectDialog(final String filename, final String filepath);

    /**
     * 다이얼로그에서 파일 추가를 결정했을 때 벨소리/알림음 여부를 선택하는 다른 다이얼로그
     * @param filename
     * @param filepath
     */
    void SelectDlgType(final String filename, final String filepath);

    /**
     * 현재 경로에서 파일 목록을 불러오는 메소드
     * @param file
     */
    void Process(File file);

    /**
     * 현재 리스트에 보이는 모든 음악파일 추가
     */
    void AddAll(int mode);
}
