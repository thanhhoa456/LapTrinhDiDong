package com.example.laixea1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
    private Context context;
    private String[] titles;
    private int[] icons;

    public MenuAdapter(Context context, String[] titles, int[] icons) {
        this.context = context;
        this.titles = titles;
        this.icons = icons;
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

        return convertView;
    }
}