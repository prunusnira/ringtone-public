/**
 * Created by Kang Tae Jun on 2011.
 *
 * The MIT License
 *
 * Copyright (c) 2011 Kang Tae Jun (Nira)
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

/************************************Class fileList*****************************/
/*각 데이터의 정보가 저장된다, 여기서는 파일이름과 파일의 경로를 포함한다.*/
/*String filename : 파일의 이름만 저장												*/
/*String path : 파일의 이름을 포함하는 파일의 경로를 저장						*/
/************************************Class fileList*****************************/
public class FileList {
	int icon;
	String filename;
	String path;
	
	public FileList(int ricon, String name,	String filepath) {
		icon = ricon;
		filename = name;
		path = filepath;
	}
	
	public int getIcon() { return icon; }
	public String getFileName() { return filename; }
	public String getPath() { return path; }
}