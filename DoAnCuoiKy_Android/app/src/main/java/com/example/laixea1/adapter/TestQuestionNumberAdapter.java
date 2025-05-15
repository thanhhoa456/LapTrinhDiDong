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

public class TestQuestionNumberAdapter extends RecyclerView.Adapter<TestQuestionNumberAdapter.QuestionNumberViewHolder> {
    private final List<Integer> questionNumbers;
    private int currentQuestionIndex;
    private final List<QuestionDTO> questions;
    private final String currentUser;
    private final int quizId;
    private final DatabaseHelper dbHelper;
    private final OnQuestionNumberClickListener listener;
    private boolean isReviewMode;

    public interface OnQuestionNumberClickListener {
        void onQuestionNumberClick(int position);
    }

    public TestQuestionNumberAdapter(List<Integer> questionNumbers, int currentQuestionIndex, List<QuestionDTO> questions,
                                     String currentUser, int quizId, DatabaseHelper dbHelper,
                                     OnQuestionNumberClickListener listener, boolean isReviewMode) {
        this.questionNumbers = questionNumbers;
        this.currentQuestionIndex = currentQuestionIndex;
        this.questions = questions;
        this.currentUser = currentUser;
        this.quizId = quizId;
        this.dbHelper = dbHelper;
        this.listener = listener;
        this.isReviewMode = isReviewMode;
        Log.d("TestQuestionNumberAdapter", "Initialized with reviewMode=" + isReviewMode);
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
        notifyDataSetChanged();
    }

    public void setReviewMode(boolean reviewMode) {
        this.isReviewMode = reviewMode;
        notifyDataSetChanged();
        Log.d("TestQuestionNumberAdapter", "Set reviewMode=" + reviewMode);
    }

    @NonNull
    @Override
    public QuestionNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_number, parent, false);
        return new QuestionNumberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionNumberViewHolder holder, int position) {
        int number = questionNumbers.get(position);
        holder.numberText.setText(String.valueOf(number));
        holder.numberText.setSelected(position == currentQuestionIndex);

        if (position < questions.size()) {
            int questionId = questions.get(position).getId();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            if (isReviewMode) {
                String query = "SELECT uta.selectedAnswer, tq.answer " +
                        "FROM UserTestAnswers uta " +
                        "INNER JOIN TestQuestions tq ON uta.questionId = tq.questionId " +
                        "WHERE uta.userId = ? AND uta.testId = ? AND uta.questionId = ? AND tq.testId = ?";
                Cursor cursor = db.rawQuery(query, new String[]{
                        currentUser, String.valueOf(quizId), String.valueOf(questionId), String.valueOf(quizId)
                });

                if (cursor.moveToFirst()) {
                    int selectedAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
                    int correctAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("answer"));
                    holder.statusImage.setVisibility(View.VISIBLE);
                    if (selectedAnswer == correctAnswer) {
                        holder.statusImage.setImageResource(R.drawable.correct);
                        Log.d("TestQuestionNumberAdapter", "Question " + questionId + " answered correctly");
                    } else {
                        holder.statusImage.setImageResource(R.drawable.incorrect);
                        Log.d("TestQuestionNumberAdapter", "Question " + questionId + " answered incorrectly");
                    }
                } else {
                    holder.statusImage.setVisibility(View.GONE);
                    Log.d("TestQuestionNumberAdapter", "Question " + questionId + " has not been answered");
                }
                cursor.close();
            } else {
                boolean isAnswered = isQuestionAnswered(questionId);
                if (isAnswered) {
                    holder.statusImage.setImageResource(R.drawable.red_dot);
                    holder.statusImage.setVisibility(View.VISIBLE);
                    Log.d("TestQuestionNumberAdapter", "Question " + questionId + " has been answered");
                } else {
                    holder.statusImage.setVisibility(View.GONE);
                    Log.d("TestQuestionNumberAdapter", "Question " + questionId + " has not been answered");
                }
            }

            db.close();
        } else {
            Log.w("TestQuestionNumberAdapter", "Position " + position + " exceeds questions size: " + questions.size());
            holder.statusImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionNumberClick(position);
            }
        });
    }

    private boolean isQuestionAnswered(int questionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM UserTestAnswers WHERE userId = ? AND testId = ? AND questionId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(quizId), String.valueOf(questionId)});
        boolean isAnswered = false;
        if (cursor.moveToFirst()) {
            isAnswered = cursor.getInt(0) > 0;
        }
        cursor.close();
        db.close();
        return isAnswered;
    }

    @Override
    public int getItemCount() {
        return questionNumbers.size();
    }

    static class QuestionNumberViewHolder extends RecyclerView.ViewHolder {
        TextView numberText;
        ImageView statusImage;

        QuestionNumberViewHolder(@NonNull View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.numberText);
            statusImage = itemView.findViewById(R.id.statusImage);
        }
    }
}