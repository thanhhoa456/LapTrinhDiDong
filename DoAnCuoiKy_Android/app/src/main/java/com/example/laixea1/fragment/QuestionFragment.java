package com.example.laixea1.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.adapter.AnswerAdapter;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment {

    private static final String ARG_QUESTION = "question";
    private static final String ARG_QUESTION_ID = "question_id";
    private static final String ARG_CURRENT_USER = "current_user";

    private QuestionDTO question;
    private int currentQuestionId;
    private String currentUser;
    private DatabaseHelper dbHelper;
    private List<Answer> answerList;
    private AnswerAdapter answerAdapter;
    private TextView explanationText;
    private OnAnswerSelectedListener answerSelectedListener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected();
    }

    public static QuestionFragment newInstance(QuestionDTO question, int questionId, String currentUser) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_QUESTION_ID, questionId);
        args.putString(ARG_CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (QuestionDTO) getArguments().getSerializable(ARG_QUESTION);
            currentQuestionId = getArguments().getInt(ARG_QUESTION_ID);
            currentUser = getArguments().getString(ARG_CURRENT_USER);
        }
        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        TextView questionText = view.findViewById(R.id.questionText);
        ImageView questionImage = view.findViewById(R.id.questionImage);
        RecyclerView answerRecyclerView = view.findViewById(R.id.answerRecyclerView);
        explanationText = view.findViewById(R.id.explanationText);

        // Debug questionId
        Log.d("QuestionFragment", "Current Question ID: " + currentQuestionId);

        // Set question text
        if (question != null && question.getQuestion() != null) {
            questionText.setText(question.getQuestion());
        } else {
            questionText.setText("Không có nội dung câu hỏi");
            Log.w("QuestionFragment", "Question text is null for question " + currentQuestionId);
        }

        // Load image if available
        if (question != null && question.getImage() != null && !question.getImage().isEmpty()) {
            Log.d("QuestionFragment", "Attempting to load image: " + question.getImage() + " for question " + currentQuestionId);
            File imageFile = new File(question.getImage());
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    questionImage.setImageBitmap(bitmap);
                    questionImage.setVisibility(View.VISIBLE);
                    Log.d("QuestionFragment", "Image loaded successfully: " + question.getImage());
                } else {
                    Log.w("QuestionFragment", "Failed to decode bitmap from file: " + question.getImage());
                    questionImage.setVisibility(View.GONE);
                }
            } else {
                Log.w("QuestionFragment", "Image file not found: " + question.getImage());
                questionImage.setVisibility(View.GONE);
            }
        } else {
            Log.d("QuestionFragment", "No image available for question " + currentQuestionId);
            questionImage.setVisibility(View.GONE);
        }

        // Initialize answer list with actual number of options
        answerList = new ArrayList<>();
        if (question != null) {
            List<String> options = new ArrayList<>();
            if (question.getOption1() != null && !question.getOption1().isEmpty()) options.add(question.getOption1());
            if (question.getOption2() != null && !question.getOption2().isEmpty()) options.add(question.getOption2());
            if (question.getOption3() != null && !question.getOption3().isEmpty()) options.add(question.getOption3());
            if (question.getOption4() != null && !question.getOption4().isEmpty()) options.add(question.getOption4());

            Log.d("QuestionFragment", "Number of options for question " + currentQuestionId + ": " + options.size());
            int correctAnswerIndex = question.getAnswer() - 1;
            for (int i = 0; i < options.size(); i++) {
                boolean isCorrect = (i == correctAnswerIndex && correctAnswerIndex < options.size());
                answerList.add(new Answer(options.get(i), isCorrect));
            }
        } else {
            Log.w("QuestionFragment", "Question is null for question " + currentQuestionId);
        }

        // Restore selected answers from SQLite
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT selectedAnswer FROM UserProgress WHERE userId = ? AND questionId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(currentQuestionId)});
        if (cursor.moveToFirst()) {
            int selectedAnswerIndex = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
            if (selectedAnswerIndex >= 0 && selectedAnswerIndex < answerList.size()) {
                answerList.get(selectedAnswerIndex).setSelected(true);
                Log.d("QuestionFragment", "Restored selected answer: " + selectedAnswerIndex);
            } else {
                Log.w("QuestionFragment", "Invalid selectedAnswerIndex: " + selectedAnswerIndex + " for question " + currentQuestionId);
            }
        }
        cursor.close();
        db.close();

        // Initialize RecyclerView for answers
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        answerAdapter = new AnswerAdapter(getContext(), R.layout.item_answer, answerList,
                question != null ? question.getExplainQuestion() : null,
                explanationText, currentQuestionId, currentUser, dbHelper, answerSelectedListener);
        answerRecyclerView.setAdapter(answerAdapter);

        return view;
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
        if (answerAdapter != null) {
            answerAdapter.setAnswerSelectedListener(listener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}