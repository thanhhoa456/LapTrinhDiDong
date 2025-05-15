package com.example.laixea1.fragment;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.Context;
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
import com.example.laixea1.activity.QuizTestActivity;
import com.example.laixea1.adapter.TestAnswerAdapter;
import com.example.laixea1.adapter.TestQuestionNumberAdapter;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestQuestionFragment extends Fragment {

    private static final String ARG_QUESTION = "question";
    private static final String ARG_QUESTION_ID = "questionId";
    private static final String ARG_CURRENT_USER = "currentUser";
    private static final String ARG_TEST_ID = "testId";

    private QuestionDTO question;
    private int questionId;
    private String currentUser;
    private int testId;
    private DatabaseHelper dbHelper;
    private List<Answer> answerList;
    private TestAnswerAdapter answerAdapter;
    private TextView explanationText;
    private OnAnswerSelectedListener answerSelectedListener;

    private static final String PREF_NAME = "Settings_";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final int DEFAULT_FONT_SIZE = 16;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected(int questionId, int selectedAnswer);
    }

    public static TestQuestionFragment newInstance(QuestionDTO question, int questionId, String currentUser, int testId) {
        TestQuestionFragment fragment = new TestQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_QUESTION_ID, questionId);
        args.putString(ARG_CURRENT_USER, currentUser);
        args.putInt(ARG_TEST_ID, testId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (QuestionDTO) getArguments().getSerializable(ARG_QUESTION);
            questionId = getArguments().getInt(ARG_QUESTION_ID);
            currentUser = getArguments().getString(ARG_CURRENT_USER);
            testId = getArguments().getInt(ARG_TEST_ID);
        }
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

        // Apply font size
        float fontSize = getContext().getSharedPreferences(PREF_NAME + currentUser, Context.MODE_PRIVATE)
                .getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        questionText.setTextSize(fontSize);
        explanationText.setTextSize(fontSize);
        Log.d("TestQuestionFragment", "Applied fontSize " + fontSize + " to questionText and explanationText");

        boolean isReviewMode = getActivity() instanceof QuizTestActivity && ((QuizTestActivity) getActivity()).isReviewMode();

        if (isReviewMode && question != null && question.getExplainQuestion() != null && !question.getExplainQuestion().isEmpty()) {
            explanationText.setText(question.getExplainQuestion());
            explanationText.setVisibility(View.VISIBLE);
            Log.d("TestQuestionFragment", "Showing explanation for question " + questionId);
        } else {
            explanationText.setVisibility(View.GONE);
            Log.d("TestQuestionFragment", "Hiding explanation for question " + questionId);
        }

        Log.d("TestQuestionFragment", "Current Question ID: " + questionId + ", isReviewMode: " + isReviewMode);

        if (question != null && question.getQuestion() != null) {
            questionText.setText(question.getQuestion());
        } else {
            questionText.setText("Không có nội dung câu hỏi");
            Log.w("TestQuestionFragment", "Question text is null for question " + questionId);
        }

        if (question != null && question.getImage() != null && !question.getImage().isEmpty()) {
            Log.d("TestQuestionFragment", "Attempting to load image: " + question.getImage() + " for question " + questionId);
            File imageFile = new File(question.getImage());
            if (imageFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    questionImage.setImageBitmap(bitmap);
                    questionImage.setVisibility(View.VISIBLE);
                    Log.d("TestQuestionFragment", "Image loaded successfully: " + question.getImage());
                } else {
                    Log.w("TestQuestionFragment", "Failed to decode bitmap from file: " + question.getImage());
                    questionImage.setVisibility(View.GONE);
                }
            } else {
                Log.w("TestQuestionFragment", "Image file not found: " + question.getImage());
                questionImage.setVisibility(View.GONE);
            }
        } else {
            Log.d("TestQuestionFragment", "No image available for question " + questionId);
            questionImage.setVisibility(View.GONE);
        }

        answerList = new ArrayList<>();
        if (question != null) {
            List<String> options = new ArrayList<>();
            if (question.getOption1() != null && !question.getOption1().isEmpty()) options.add(question.getOption1());
            if (question.getOption2() != null && !question.getOption2().isEmpty()) options.add(question.getOption2());
            if (question.getOption3() != null && !question.getOption3().isEmpty()) options.add(question.getOption3());
            if (question.getOption4() != null && !question.getOption4().isEmpty()) options.add(question.getOption4());

            Log.d("TestQuestionFragment", "Number of options for question " + questionId + ": " + options.size());
            for (int i = 0; i < options.size(); i++) {
                answerList.add(new Answer(options.get(i), false));
            }
        } else {
            Log.w("TestQuestionFragment", "Question is null for question " + questionId);
        }

        answerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Retrieve TestQuestionNumberAdapter from QuizTestActivity
        TestQuestionNumberAdapter questionNumberAdapter = null;
        if (getActivity() instanceof QuizTestActivity) {
            questionNumberAdapter = ((QuizTestActivity) getActivity()).getTestQuestionNumberAdapter();
        }
        answerAdapter = new TestAnswerAdapter(getContext(), answerList, questionId, currentUser, testId,
                answerSelectedListener, dbHelper, questionNumberAdapter);
        answerAdapter.setReviewMode(isReviewMode);
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