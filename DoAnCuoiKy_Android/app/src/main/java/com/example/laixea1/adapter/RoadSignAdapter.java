package com.example.laixea1.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.entity.RoadSign;

import java.util.List;

public class RoadSignAdapter extends RecyclerView.Adapter<RoadSignAdapter.RoadSignViewHolder> {
    private List<RoadSign> roadSignList;

    public RoadSignAdapter(List<RoadSign> roadSignList) {
        this.roadSignList = roadSignList;
    }

    @NonNull
    @Override
    public RoadSignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.road_sign_item, parent, false);
        return new RoadSignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoadSignViewHolder holder, int position) {
        RoadSign roadSign = roadSignList.get(position);
        holder.signName.setText(roadSign.getName());
        holder.signDescription.setText(roadSign.getDescription());

        // Giải mã Base64 và hiển thị ảnh
        String base64Image = roadSign.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                byte[] decodedBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    holder.signImage.setImageBitmap(bitmap);
                } else {
                    holder.signImage.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                holder.signImage.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            holder.signImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return roadSignList.size();
    }

    public static class RoadSignViewHolder extends RecyclerView.ViewHolder {
        ImageView signImage;
        TextView signName;
        TextView signDescription;

        public RoadSignViewHolder(@NonNull View itemView) {
            super(itemView);
            signImage = itemView.findViewById(R.id.sign_image);
            signName = itemView.findViewById(R.id.sign_name);
            signDescription = itemView.findViewById(R.id.sign_description);
        }
    }
}
