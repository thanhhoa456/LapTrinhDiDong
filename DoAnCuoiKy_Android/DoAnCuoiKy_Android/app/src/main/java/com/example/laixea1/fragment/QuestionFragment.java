package com.example.laixea1.fragment;

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
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionFragment extends Fragment {

    private static final String ARG_QUESTION = "question";
    private static final String ARG_QUESTION_ID = "question_id";
    private static final String ARG_ANSWER_CACHE = "answer_cache";

    private QuestionDTO question;
    private int currentQuestionId;
    private Map<Integer, List<Answer>> answerCache;
    private List<Answer> answerList;
    private AnswerAdapter answerAdapter;
    private TextView explanationText;
    private OnAnswerSelectedListener answerSelectedListener;

    public interface OnAnswerSelectedListener {
        void onAnswerSelected();
    }

    public static QuestionFragment newInstance(QuestionDTO question, int questionId, Map<Integer, List<Answer>> answerCache) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTION, question);
        args.putInt(ARG_QUESTION_ID, questionId);
        args.putSerializable(ARG_ANSWER_CACHE, (HashMap<Integer, List<Answer>>) answerCache);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (QuestionDTO) getArguments().getSerializable(ARG_QUESTION);
            currentQuestionId = getArguments().getInt(ARG_QUESTION_ID);
            answerCache = (Map<Integer, List<Answer>>) getArguments().getSerializable(ARG_ANSWER_CACHE);
        }
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
            questionText.setText("No question text available");
            Log.w("QuestionFragment", "Question text is null for question " + currentQuestionId);
        }

        // Load image if available
        if (question != null && question.getImage() != null && !question.getImage().isEmpty()) {
            Picasso.get()
                    .load(question.getImage())
                    .into(questionImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("QuestionFragment", "Image loaded successfully for question " + currentQuestionId);
                            questionImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("QuestionFragment", "Error loading image for question " + currentQuestionId + ": " + e.getMessage());
                            questionImage.setVisibility(View.GONE);
                        }
                    });
        } else {
            Log.d("QuestionFragment", "No image available for question " + currentQuestionId);
            questionImage.setVisibility(View.GONE);
        }

        // Initialize answer list
        answerList = new ArrayList<>();
        if (question != null) {
            // Tạo danh sách các đáp án từ option1, option2, option3, option4
            List<String> options = new ArrayList<>();
            if (question.getOption1() != null) options.add(question.getOption1());
            if (question.getOption2() != null) options.add(question.getOption2());
            if (question.getOption3() != null) options.add(question.getOption3());
            if (question.getOption4() != null) options.add(question.getOption4());

            // Đáp án đúng dựa trên answer (giá trị int từ 1 đến 4)
            int correctAnswerIndex = question.getAnswer() - 1; // Chuyển từ 1-based index sang 0-based index
            for (int i = 0; i < options.size(); i++) {
                boolean isCorrect = (i == correctAnswerIndex);
                answerList.add(new Answer(options.get(i), isCorrect));
            }
        } else {
            Log.w("QuestionFragment", "Question is null for question " + currentQuestionId);
        }

        // Restore selected answers from cache if available
        List<Answer> cachedAnswers = answerCache.get(currentQuestionId);
        if (cachedAnswers != null) {
            Log.d("QuestionFragment", "Restoring answers from cache for question " + currentQuestionId);
            answerList = new ArrayList<>(cachedAnswers); // Tạo bản sao để tránh thay đổi trực tiếp cache
            for (Answer answer : answerList) {
                Log.d("QuestionFragment", "Restored answer: " + answer.getText() + ", isSelected: " + answer.isSelected());
            }
        } else {
            Log.d("QuestionFragment", "No cached answers for question " + currentQuestionId);
            for (Answer answer : answerList) {
                Log.d("QuestionFragment", "Initial answer: " + answer.getText() + ", isSelected: " + answer.isSelected());
            }
        }

        // Debug answerCache state
        Log.d("QuestionFragment", "Current state of answerCache before display: ");
        for (Map.Entry<Integer, List<Answer>> entry : answerCache.entrySet()) {
            Log.d("QuestionFragment", "Question ID: " + entry.getKey());
            for (Answer answer : entry.getValue()) {
                Log.d("QuestionFragment", "Answer: " + answer.getText() + ", isSelected: " + answer.isSelected());
            }
        }

        // Initialize RecyclerView for answers
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        answerAdapter = new AnswerAdapter(getContext(), R.layout.item_answer, answerList,
                question != null ? question.getExplainQuestion() : null,
                explanationText, currentQuestionId, answerCache, answerSelectedListener);
        answerRecyclerView.setAdapter(answerAdapter);

        return view;
    }

    public void setAnswerSelectedListener(OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
        if (answerAdapter != null) {
            answerAdapter.setAnswerSelectedListener(listener);
        }
    }
}