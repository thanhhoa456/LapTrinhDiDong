package com.example.laixea1.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.dto.FeedbackDTO;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<FeedbackDTO> feedbackList;

    public FeedbackAdapter(List<FeedbackDTO> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackDTO feedback = feedbackList.get(position);
        Log.d("FeedbackAdapter", "Hiển thị: id=" + feedback.getId() + ", soSao=" + feedback.getSoSao());
        holder.textViewUserId.setText("User ID: " + feedback.getUserId());
        holder.ratingBarFeedback.setRating(feedback.getSoSao());
        holder.textViewNoiDung.setText(feedback.getNoiDung());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public void updateFeedbacks(List<FeedbackDTO> newFeedbacks) {
        this.feedbackList = newFeedbacks;
        notifyDataSetChanged();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserId;
        RatingBar ratingBarFeedback;
        TextView textViewNoiDung;

        FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserId = itemView.findViewById(R.id.textViewUserId);
            ratingBarFeedback = itemView.findViewById(R.id.ratingBarFeedback);
            textViewNoiDung = itemView.findViewById(R.id.textViewNoiDung);
        }
    }
}