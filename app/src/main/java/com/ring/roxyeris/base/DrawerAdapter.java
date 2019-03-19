/**
 * Created by Kang Tae Jun on 2015-05-13.
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ring.roxyeris.dataclass.IntStringStructure;
import com.ring.roxyeris.R;

import java.util.ArrayList;

public class DrawerAdapter extends BaseAdapter {
    private ArrayList<IntStringStructure> item;
    private Context context;
    private ImageView icon;
    private TextView content;

    public DrawerAdapter(Context context, ArrayList<IntStringStructure> item) {
        this.context = context;
        this.item = item;
    }

    public int getCount() {
        return item.size();
    }

    public Object getItem(int position) {
        return item.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Activity를 extend해야 사용 가능한 getSystemService를 Context를 이용하여 사용 가능 하도록 설정
            v = vi.inflate(R.layout.drawer_style, null);
        }
        icon = (ImageView) v.findViewById(R.id.drawer_icon);
        content = (TextView) v.findViewById(R.id.drawer_text);

        IntStringStructure data = item.get(position);

        icon.setImageResource(data.getIntValue());
        content.setText(data.getStrValue());

        return v;
    }
}
