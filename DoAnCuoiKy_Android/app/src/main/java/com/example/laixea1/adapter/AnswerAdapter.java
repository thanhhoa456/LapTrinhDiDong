package com.example.laixea1.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.entity.Answer;
import com.example.laixea1.fragment.QuestionFragment;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private Context context;
    private List<Answer> answerList;
    private String explanation;
    private TextView explanationText;
    private int currentQuestionId;
    private String currentUser;
    private DatabaseHelper dbHelper;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private QuestionFragment.OnAnswerSelectedListener answerSelectedListener;

    public AnswerAdapter(Context context, int layout, List<Answer> answerList, String explanation,
                         TextView explanationText, int currentQuestionId, String currentUser, DatabaseHelper dbHelper,
                         QuestionFragment.OnAnswerSelectedListener listener) {
        this.context = context;
        this.answerList = answerList;
        this.explanation = explanation;
        this.explanationText = explanationText;
        this.currentQuestionId = currentQuestionId;
        this.currentUser = currentUser;
        this.dbHelper = dbHelper;
        this.answerSelectedListener = listener;
        correctSound = MediaPlayer.create(context, R.raw.correct);
        wrongSound = MediaPlayer.create(context, R.raw.wrong);
        Log.d("AnswerAdapter", "Initialized with " + answerList.size() + " answers for question " + currentQuestionId);
    }

    public void setAnswerSelectedListener(QuestionFragment.OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (position < answerList.size()) { // Chỉ bind nếu position hợp lệ
            Answer answer = answerList.get(position);
            viewHolder.answerText.setText(answer.getText());
            viewHolder.radioAnswer.setChecked(answer.isSelected());

            // Hiển thị trạng thái đúng/sai và giải thích
            if (answer.isSelected()) {
                if (answer.isCorrect()) {
                    viewHolder.statusImage.setImageResource(R.drawable.correct);
                    viewHolder.statusImage.setVisibility(View.VISIBLE);
                    if (correctSound != null) {
                        correctSound.start();
                    }
                    if (explanation != null && !explanation.trim().isEmpty()) {
                        explanationText.setText(explanation);
                        explanationText.setVisibility(View.VISIBLE);
                    } else {
                        explanationText.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.statusImage.setImageResource(R.drawable.incorrect);
                    viewHolder.statusImage.setVisibility(View.VISIBLE);
                    if (wrongSound != null) {
                        wrongSound.start();
                    }
                    explanationText.setVisibility(View.GONE);
                }
            } else {
                viewHolder.statusImage.setVisibility(View.GONE);
                boolean anySelectedCorrect = false;
                for (Answer ans : answerList) {
                    if (ans.isSelected() && ans.isCorrect()) {
                        anySelectedCorrect = true;
                        break;
                    }
                }
                if (!anySelectedCorrect) {
                    explanationText.setVisibility(View.GONE);
                }
            }

            // Xử lý sự kiện click trên item
            viewHolder.itemView.setOnClickListener(v -> {
                Log.d("AnswerAdapter", "Item clicked: " + position + " for question " + currentQuestionId);
                boolean wasSelected = answer.isSelected();
                for (Answer ans : answerList) {
                    ans.setSelected(false);
                }
                if (!wasSelected) {
                    answer.setSelected(true);
                }
                Log.d("AnswerAdapter", wasSelected ? "Deselected: " + position : "Selected: " + position);

                // Lưu đáp án vào SQLite
                saveAnswerToSQLite(position, wasSelected);

                // Thông báo cho QuizActivity
                if (answerSelectedListener != null) {
                    answerSelectedListener.onAnswerSelected();
                }

                notifyDataSetChanged();
            });
        } else {
            // Nếu position vượt quá số lượng đáp án, ẩn item
            viewHolder.itemView.setVisibility(View.GONE);
            Log.w("AnswerAdapter", "Position " + position + " exceeds answerList size: " + answerList.size());
        }
    }

    private void saveAnswerToSQLite(int selectedPosition, boolean wasSelected) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (!wasSelected) {
                ContentValues values = new ContentValues();
                values.put("userId", currentUser);
                values.put("questionId", currentQuestionId);
                values.put("selectedAnswer", selectedPosition);
                values.put("isCorrect", answerList.get(selectedPosition).isCorrect() ? 1 : 0);
                values.put("timestamp", System.currentTimeMillis());
                db.insertWithOnConflict("UserProgress", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.d("AnswerAdapter", "Saved answer to SQLite: questionId=" + currentQuestionId + ", selectedAnswer=" + selectedPosition);
            } else {
                db.delete("UserProgress", "userId = ? AND questionId = ?",
                        new String[]{currentUser, String.valueOf(currentQuestionId)});
                Log.d("AnswerAdapter", "Deleted answer from SQLite: questionId=" + currentQuestionId);
            }
        } catch (Exception e) {
            Log.e("AnswerAdapter", "Error saving answer to SQLite", e);
        } finally {
            db.close();
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(answerList.size(), 4); // Đảm bảo số lượng item tối thiểu là 4 để tránh giao diện bị lệch
    }

    public void releaseMediaPlayers() {
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioAnswer;
        TextView answerText;
        ImageView statusImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioAnswer = itemView.findViewById(R.id.radioAnswer);
            answerText = itemView.findViewById(R.id.answerText);
            statusImage = itemView.findViewById(R.id.statusImage);
        }
    }
}