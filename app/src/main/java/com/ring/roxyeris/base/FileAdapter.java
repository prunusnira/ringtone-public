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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ring.roxyeris.R;

/********************************    fileAdapter    ***************************/
/*fileList 클래스를 이용하여 만든 어댑터, 이를 이용하여 ListView에 List 목록을 올릴 수 있다.*/
/********************************    fileAdapter    ***************************/
public class FileAdapter extends ArrayAdapter<FileList> {
	private ArrayList<FileList> listitem;
	private Context context;
	
	public FileAdapter(Context context, int textViewResourceId, ArrayList<FileList> items) {
		super(context, textViewResourceId, items);
		listitem = items;
		this.context = context;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Activity를 extend해야 사용 가능한 getSystemService를 Context를 이용하여 사용 가능 하도록 설정
			v = vi.inflate(R.layout.listview, null);
		}
		FileList f_l = listitem.get(position);
		if (f_l != null) {
			ImageView icon = (ImageView) v.findViewById(R.id.icon);
			TextView filename = (TextView) v.findViewById(R.id.name);
			TextView filepath = (TextView) v.findViewById(R.id.path);
		
			if(icon != null) {
				icon.setImageResource(f_l.getIcon());
			}
			if (filename != null){
				filename.setText(f_l.getFileName());                           
			}
			if(filepath != null){
				filepath.setText(f_l.getPath());
			}
		}
		return v;
	}
}