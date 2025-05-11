package com.example.laixea1.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laixea1.R;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private String[] titles;
    private int[] icons;
    private float fontSize; // Thêm biến lưu cỡ chữ

    public MenuAdapter(Context context, String[] titles, int[] icons) {
        this.context = context;
        this.titles = titles;
        this.icons = icons;
        // Lấy cỡ chữ từ SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("A1StudyPrefs", Context.MODE_PRIVATE);
        this.fontSize = prefs.getInt("fontSize", 16); // Mặc định 16sp nếu chưa lưu
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_menu, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.menuIcon);
        TextView title = convertView.findViewById(R.id.menuTitle);

        icon.setImageResource(icons[position]);
        title.setText(titles[position]);
        title.setTextSize(fontSize); // Áp dụng cỡ chữ từ SharedPreferences

        return convertView;
    }
}