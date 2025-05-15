package com.example.laixea1.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
    protected List<Answer> answerList;
    private String explanation;
    private TextView explanationText;
    private int currentQuestionId;
    private String currentUser;
    private DatabaseHelper dbHelper;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private QuestionFragment.OnAnswerSelectedListener answerSelectedListener;

    private static final String PREF_NAME = "Settings_";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final int DEFAULT_FONT_SIZE = 16;

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
        loadAnswerStateFromSQLite();
    }

    public void setAnswerSelectedListener(QuestionFragment.OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
    }

    protected void loadAnswerStateFromSQLite() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT selectedAnswer, isCorrect FROM UserProgress WHERE userId = ? AND questionId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(currentQuestionId)});
        if (cursor.moveToFirst()) {
            int selectedAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
            int isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow("isCorrect"));
            if (selectedAnswer >= 0 && selectedAnswer < answerList.size()) {
                for (int i = 0; i < answerList.size(); i++) {
                    Answer answer = answerList.get(i);
                    answer.setSelected(i == selectedAnswer);
                    if (i == selectedAnswer && answer.isCorrect() != (isCorrect == 1)) {
                        Log.w("AnswerAdapter", "Inconsistent isCorrect for questionId=" + currentQuestionId +
                                ": SQLite isCorrect=" + isCorrect + ", answerList isCorrect=" + answer.isCorrect());
                        updateSQLiteConsistency(selectedAnswer, answer.isCorrect());
                    }
                }
                Log.d("AnswerAdapter", "Loaded answer state from SQLite: questionId=" + currentQuestionId +
                        ", selectedAnswer=" + selectedAnswer + ", isCorrect=" + isCorrect);
            } else {
                Log.w("AnswerAdapter", "Invalid selectedAnswer from SQLite: " + selectedAnswer +
                        " for questionId=" + currentQuestionId);
                db.delete("UserProgress", "userId = ? AND questionId = ?",
                        new String[]{currentUser, String.valueOf(currentQuestionId)});
                resetAnswerListSelection();
            }
        } else {
            resetAnswerListSelection();
            Log.d("AnswerAdapter", "No answer state found in SQLite for questionId=" + currentQuestionId);
        }
        cursor.close();
        db.close();
    }

    protected void resetAnswerListSelection() {
        for (Answer answer : answerList) {
            answer.setSelected(false);
        }
    }

    private void updateSQLiteConsistency(int selectedAnswer, boolean isCorrect) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("userId", currentUser);
            values.put("questionId", currentQuestionId);
            values.put("selectedAnswer", selectedAnswer);
            values.put("isCorrect", isCorrect ? 1 : 0);
            values.put("timestamp", System.currentTimeMillis());
            db.insertWithOnConflict("UserProgress", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("AnswerAdapter", "Fixed inconsistent SQLite data: questionId=" + currentQuestionId +
                    ", selectedAnswer=" + selectedAnswer + ", isCorrect=" + isCorrect);
        } catch (Exception e) {
            Log.e("AnswerAdapter", "Error fixing SQLite data", e);
        } finally {
            db.close();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (position < answerList.size()) {
            Answer answer = answerList.get(position);
            viewHolder.answerText.setText(answer.getText());
            viewHolder.radioAnswer.setChecked(answer.isSelected());

            // Áp dụng font size cho answerText
            float fontSize = context.getSharedPreferences(PREF_NAME + currentUser, Context.MODE_PRIVATE)
                    .getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
            viewHolder.answerText.setTextSize(fontSize);
            Log.d("AnswerAdapter", "Applied fontSize " + fontSize + " to answerText at position " + position);

            // Hiển thị trạng thái đúng/sai và giải thích
            if (answer.isSelected()) {
                if (answer.isCorrect()) {
                    viewHolder.statusImage.setImageResource(R.drawable.correct);
                    viewHolder.statusImage.setVisibility(View.VISIBLE);
                    if (explanation != null && !explanation.trim().isEmpty()) {
                        explanationText.setText(explanation);
                        explanationText.setVisibility(View.VISIBLE);
                    } else {
                        explanationText.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.statusImage.setImageResource(R.drawable.incorrect);
                    viewHolder.statusImage.setVisibility(View.VISIBLE);
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

            viewHolder.itemView.setOnClickListener(v -> {
                Log.d("AnswerAdapter", "Item clicked: " + position + " for question " + currentQuestionId);
                boolean wasSelected = answer.isSelected();
                for (Answer ans : answerList) {
                    ans.setSelected(false);
                }
                if (!wasSelected) {
                    answer.setSelected(true);
                    if (answer.isCorrect()) {
                        if (correctSound != null) {
                            correctSound.start();
                        }
                    } else {
                        if (wrongSound != null) {
                            wrongSound.start();
                        }
                    }
                }
                Log.d("AnswerAdapter", wasSelected ? "Deselected: " + position : "Selected: " + position);

                saveAnswerToSQLite(position, wasSelected);

                if (answerSelectedListener != null) {
                    answerSelectedListener.onAnswerSelected(currentQuestionId, position);
                }

                notifyDataSetChanged();
            });
        } else {
            viewHolder.itemView.setVisibility(View.GONE);
            Log.w("AnswerAdapter", "Position " + position + " exceeds answerList size: " + answerList.size());
        }
    }

    protected void saveAnswerToSQLite(int selectedPosition, boolean wasSelected) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (!wasSelected) {
                ContentValues values = new ContentValues();
                values.put("userId", currentUser);
                values.put("questionId", currentQuestionId);
                values.put("selectedAnswer", selectedPosition);
                values.put("isCorrect", answerList.get(selectedPosition).isCorrect() ? 1 : 0);
                values.put("timestamp", System.currentTimeMillis());
                db.delete("UserProgress", "userId = ? AND questionId = ?",
                        new String[]{currentUser, String.valueOf(currentQuestionId)});
                db.insert("UserProgress", null, values);
                Log.d("AnswerAdapter", "Saved answer to SQLite: questionId=" + currentQuestionId +
                        ", selectedAnswer=" + selectedPosition + ", isCorrect=" + answerList.get(selectedPosition).isCorrect());
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
        return Math.max(answerList.size(), 2);
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