package com.example.laixea1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.laixea1.R;
import com.example.laixea1.entity.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.categoryIcon);
            holder.title = convertView.findViewById(R.id.categoryTitle);
            holder.description = convertView.findViewById(R.id.categoryDescription);
            holder.progressBar = convertView.findViewById(R.id.categoryProgress);
            holder.progressText = convertView.findViewById(R.id.progressText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);
        holder.title.setText(category.getTitle());
        holder.description.setText(category.getDescription());
        holder.progressText.setText(category.getCompleted() + "/" + category.getTotal());
        holder.icon.setImageResource(category.getIconResId());

        // Calculate progress percentage
        int progress = (category.getTotal() > 0) ? (category.getCompleted() * 100 / category.getTotal()) : 0;
        holder.progressBar.setProgress(progress);

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        ProgressBar progressBar;
        TextView progressText;
    }
}