package com.example.laixea1.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;

import java.util.ArrayList;

public class AdvAdapter extends RecyclerView.Adapter<AdvAdapter.AdViewHolder> {
    private ArrayList<Integer> adImages;

    public AdvAdapter(ArrayList<Integer> adImages) {
        this.adImages = adImages;
    }

    @Override
    public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ad, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdViewHolder holder, int position) {
        holder.adImage.setImageResource(adImages.get(position));
    }

    @Override
    public int getItemCount() {
        return adImages.size();
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView adImage;

        AdViewHolder(View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.adImage);
        }
    }
}
