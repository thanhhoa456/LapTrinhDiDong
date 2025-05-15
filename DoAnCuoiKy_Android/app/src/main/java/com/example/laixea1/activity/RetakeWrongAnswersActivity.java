package com.example.laixea1.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RetakeWrongAnswersActivity extends QuizActivity {

    private static final String TAG = "RetakeWrongAnswersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleText.setText("Ôn tập câu trả lời sai");
        loadQuestions(-1, false); // Call loadQuestions to initialize wrong questions
    }

    @Override
    protected void loadQuestions(int groupId, boolean isFailingScore) {
        Log.d(TAG, "Loading wrong answers for user: " + currentUser);
        reloadWrongQuestions();
    }

    private void reloadWrongQuestions() {
        questionList = loadWrongQuestionsFromSQLite();
        if (!questionList.isEmpty()) {
            Log.d(TAG, "Loaded " + questionList.size() + " wrong questions for user: " + currentUser);
            initializeQuiz(); // Initialize quiz UI with loaded questions
        } else {
            Log.w(TAG, "No wrong questions found for user: " + currentUser);
            titleText.setText("Không có câu trả lời sai để ôn tập");
            questionCounter.setText("0/0");
            questionPager.setAdapter(null);
            questionNumbers.clear();
            if (questionNumberAdapter != null) {
                questionNumberAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<QuestionDTO> loadWrongQuestionsFromSQLite() {
        List<QuestionDTO> questions = new ArrayList<>();
        Set<Integer> wrongQuestionIds = new HashSet<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            Log.d(TAG, "Database opened for user: " + currentUser);

            // Query to get the latest wrong answers from UserProgress
            String query = "SELECT questionId, isCorrect, timestamp " +
                    "FROM UserProgress " +
                    "WHERE userId = ? " +
                    "AND isCorrect = 0 " +
                    "AND timestamp = (SELECT MAX(timestamp) FROM UserProgress up2 " +
                    "WHERE up2.userId = UserProgress.userId AND up2.questionId = UserProgress.questionId)";
            cursor = db.rawQuery(query, new String[]{currentUser});
            Log.d(TAG, "UserProgress query executed, found " + cursor.getCount() + " wrong answers");

            while (cursor.moveToNext()) {
                int questionId = cursor.getInt(cursor.getColumnIndexOrThrow("questionId"));
                wrongQuestionIds.add(questionId);
                Log.d(TAG, "Found wrong questionId: " + questionId);
            }
            cursor.close();
            cursor = null;

            // Load question details for each wrong questionId
            for (Integer questionId : wrongQuestionIds) {
                String questionQuery = "SELECT id, text, groupId, failingScore, explainQuestion, answer " +
                        "FROM Questions WHERE id = ?";
                cursor = db.rawQuery(questionQuery, new String[]{String.valueOf(questionId)});
                if (cursor.moveToFirst()) {
                    QuestionDTO question = new QuestionDTO();
                    try {
                        question.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                        question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow("text")));
                        question.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("groupId")));
                        question.setFailingScore(cursor.getInt(cursor.getColumnIndexOrThrow("failingScore")) == 1);
                        question.setExplainQuestion(cursor.getString(cursor.getColumnIndexOrThrow("explainQuestion")));
                        question.setAnswer(cursor.getInt(cursor.getColumnIndexOrThrow("answer")));
                        Log.d(TAG, "Loaded questionId: " + question.getId() + ", text: " + question.getQuestion());

                        // Load answer options
                        List<String> options = new ArrayList<>();
                        String answerQuery = "SELECT text FROM Answers WHERE questionId = ? ORDER BY id";
                        Cursor answerCursor = db.rawQuery(answerQuery, new String[]{String.valueOf(questionId)});
                        while (answerCursor.moveToNext()) {
                            String text = answerCursor.getString(answerCursor.getColumnIndexOrThrow("text"));
                            if (text != null) {
                                options.add(text);
                            }
                        }
                        answerCursor.close();
                        Log.d(TAG, "Loaded " + options.size() + " options for questionId: " + questionId);
                        if (options.size() >= 1) question.setOption1(options.get(0));
                        if (options.size() >= 2) question.setOption2(options.get(1));
                        if (options.size() >= 3) question.setOption3(options.get(2));
                        if (options.size() >= 4) question.setOption4(options.get(3));

                        // Load image (if any)
                        String imageQuery = "SELECT imagePath FROM Images WHERE questionId = ?";
                        Cursor imageCursor = db.rawQuery(imageQuery, new String[]{String.valueOf(questionId)});
                        if (imageCursor.moveToFirst()) {
                            String imagePath = imageCursor.getString(imageCursor.getColumnIndexOrThrow("imagePath"));
                            question.setImage(imagePath);
                            Log.d(TAG, "Loaded image path: " + imagePath + " for questionId: " + questionId);
                        }
                        imageCursor.close();

                        questions.add(question);
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing questionId: " + questionId + ", error: " + e.getMessage(), e);
                    }
                }
                cursor.close();
                cursor = null;
            }
            Log.d(TAG, "Total questions loaded: " + questions.size());
        } catch (Exception e) {
            Log.e(TAG, "Error loading wrong questions: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
            Log.d(TAG, "Database closed");
        }

        return questions;
    }

    @Override
    public void onAnswerSelected(int questionId, int position) {
        super.onAnswerSelected(questionId, position);
        Log.d(TAG, "Answer selected for questionId: " + questionId + ", position: " + position);
        // Không gọi reloadWrongQuestions() để tránh làm mới danh sách trong phiên hiện tại
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("categoryName", "Ôn tập câu trả lời sai");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        titleText.setText("Ôn tập câu trả lời sai");
    }
}