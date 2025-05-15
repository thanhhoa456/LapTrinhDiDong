package com.example.laixea1.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.entity.Answer;
import com.example.laixea1.fragment.TestQuestionFragment;

import java.util.List;

public class TestAnswerAdapter extends RecyclerView.Adapter<TestAnswerAdapter.ViewHolder> {
    private Context context;
    private List<Answer> answerList;
    private int questionId;
    private String currentUser;
    private int testId;
    private DatabaseHelper dbHelper;
    private TestQuestionFragment.OnAnswerSelectedListener answerSelectedListener;
    private boolean isReviewMode;
    private TestQuestionNumberAdapter questionNumberAdapter; // Add reference to TestQuestionNumberAdapter

    private static final String PREF_NAME = "Settings_";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final int DEFAULT_FONT_SIZE = 16;

    public TestAnswerAdapter(Context context, List<Answer> answerList, int questionId, String currentUser,
                             int testId, TestQuestionFragment.OnAnswerSelectedListener listener,
                             DatabaseHelper dbHelper, TestQuestionNumberAdapter questionNumberAdapter) {
        this.context = context;
        this.answerList = answerList;
        this.questionId = questionId;
        this.currentUser = currentUser;
        this.testId = testId;
        this.answerSelectedListener = listener;
        this.dbHelper = dbHelper;
        this.questionNumberAdapter = questionNumberAdapter; // Initialize reference
        try {
            loadAnswerStateFromSQLite();
        } catch (Exception e) {
            Log.e("TestAnswerAdapter", "Failed to load answer state: " + e.getMessage(), e);
        }
    }

    public void setReviewMode(boolean reviewMode) {
        this.isReviewMode = reviewMode;
    }

    public void setAnswerSelectedListener(TestQuestionFragment.OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
    }

    private void loadAnswerStateFromSQLite() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT selectedAnswer FROM UserTestAnswers WHERE userId = ? AND testId = ? AND questionId = ?";
            cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(testId), String.valueOf(questionId)});
            if (cursor.moveToFirst()) {
                int selectedAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
                if (selectedAnswer >= 0 && selectedAnswer < answerList.size()) {
                    answerList.get(selectedAnswer).setSelected(true);
                    Log.d("TestAnswerAdapter", "Loaded answer state: questionId=" + questionId + ", selectedAnswer=" + selectedAnswer);
                }
            }
        } catch (Exception e) {
            Log.e("TestAnswerAdapter", "Error loading answer state: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
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

            // Apply font size
            float fontSize = context.getSharedPreferences(PREF_NAME + currentUser, Context.MODE_PRIVATE)
                    .getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
            viewHolder.answerText.setTextSize(fontSize);
            Log.d("TestAnswerAdapter", "Applied fontSize " + fontSize + " to answerText at position " + position);

            viewHolder.itemView.setEnabled(!isReviewMode);
            viewHolder.radioAnswer.setEnabled(!isReviewMode);

            viewHolder.itemView.setOnClickListener(isReviewMode ? null : v -> {
                boolean wasSelected = answer.isSelected();
                for (Answer ans : answerList) {
                    ans.setSelected(false);
                }
                if (!wasSelected) {
                    answer.setSelected(true);
                    saveAnswerToSQLite(position);
                    if (answerSelectedListener != null) {
                        answerSelectedListener.onAnswerSelected(questionId, position);
                    }
                } else {
                    // Deselect the answer
                    answer.setSelected(false);
                    deleteAnswerFromSQLite();
                    if (answerSelectedListener != null) {
                        answerSelectedListener.onAnswerSelected(questionId, -1); // Indicate no answer selected
                    }
                }
                notifyDataSetChanged();
                if (questionNumberAdapter != null) {
                    questionNumberAdapter.notifyDataSetChanged(); // Notify question number adapter to update status
                }
            });
        } else {
            viewHolder.itemView.setVisibility(View.GONE);
            Log.w("TestAnswerAdapter", "Position " + position + " exceeds answerList size: " + answerList.size());
        }
    }

    private void saveAnswerToSQLite(int selectedPosition) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userId", currentUser);
            values.put("testId", testId);
            values.put("questionId", questionId);
            values.put("selectedAnswer", selectedPosition);
            values.put("isCorrect", answerList.get(selectedPosition).isCorrect() ? 1 : 0);
            values.put("timestamp", System.currentTimeMillis());
            db.delete("UserTestAnswers", "userId = ? AND testId = ? AND questionId = ?",
                    new String[]{currentUser, String.valueOf(testId), String.valueOf(questionId)});
            db.insert("UserTestAnswers", null, values);
            Log.d("TestAnswerAdapter", "Saved answer to SQLite: questionId=" + questionId + ", selectedAnswer=" + selectedPosition);
        } catch (Exception e) {
            Log.e("TestAnswerAdapter", "Error saving answer to SQLite: " + e.getMessage(), e);
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void deleteAnswerFromSQLite() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete("UserTestAnswers", "userId = ? AND testId = ? AND questionId = ?",
                    new String[]{currentUser, String.valueOf(testId), String.valueOf(questionId)});
            Log.d("TestAnswerAdapter", "Deleted answer from SQLite: questionId=" + questionId);
        } catch (Exception e) {
            Log.e("TestAnswerAdapter", "Error deleting answer from SQLite: " + e.getMessage(), e);
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    @Override
    public int getItemCount() {
        return Math.max(answerList.size(), 4);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioAnswer;
        TextView answerText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioAnswer = itemView.findViewById(R.id.radioAnswer);
            answerText = itemView.findViewById(R.id.answerText);
        }
    }
}