package com.example.laixea1.adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;

import java.util.List;

public class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder> {
    private List<Integer> questionNumbers;
    private int currentQuestionIndex;
    private List<QuestionDTO> questionList;
    private String currentUser;
    private DatabaseHelper dbHelper;
    private OnQuestionNumberClickListener listener;

    public interface OnQuestionNumberClickListener {
        void onQuestionNumberClick(int position);
    }

    public QuestionNumberAdapter(List<Integer> questionNumbers, int currentQuestionIndex, List<QuestionDTO> questionList,
                                 String currentUser, DatabaseHelper dbHelper, OnQuestionNumberClickListener listener) {
        this.questionNumbers = questionNumbers;
        this.currentQuestionIndex = currentQuestionIndex;
        this.questionList = questionList;
        this.currentUser = currentUser;
        this.dbHelper = dbHelper;
        this.listener = listener;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_number, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int number = questionNumbers.get(position);
        holder.numberText.setText(String.valueOf(number));
        holder.numberText.setSelected(position == currentQuestionIndex);

        // Kiểm tra trạng thái câu hỏi từ SQLite
        if (position < questionList.size()) {
            int questionId = questionList.get(position).getId();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT selectedAnswer, isCorrect FROM UserProgress WHERE userId = ? AND questionId = ?";
            Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(questionId)});
            if (cursor.moveToFirst()) {
                int selectedAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
                boolean isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow("isCorrect")) == 1;
                holder.statusImage.setImageResource(isCorrect ? R.drawable.check : R.drawable.delete);
                holder.statusImage.setVisibility(View.VISIBLE);
                Log.d("QuestionNumberAdapter", "Question " + questionId + " has selected answer: " + selectedAnswer + ", isCorrect: " + isCorrect);
            } else {
                holder.statusImage.setVisibility(View.GONE);
                Log.d("QuestionNumberAdapter", "No answer found for question " + questionId);
            }
            cursor.close();
            db.close();
        } else {
            Log.d("QuestionNumberAdapter", "Position " + position + " exceeds questionList size: " + questionList.size());
            holder.statusImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionNumberClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionNumbers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numberText;
        ImageView statusImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.numberText);
            statusImage = itemView.findViewById(R.id.statusImage);
        }
    }
}