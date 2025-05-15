package com.example.laixea1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.fragment.TestQuestionFragment;

import java.util.List;

public class TestQuestionPagerAdapter extends FragmentStateAdapter {
    private final List<QuestionDTO> questions;
    private final String currentUser;
    private final int quizId;
    private final TestQuestionFragment.OnAnswerSelectedListener answerSelectedListener;

    public TestQuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<QuestionDTO> questions,
                                    String currentUser, int quizId, TestQuestionFragment.OnAnswerSelectedListener listener) {
        super(fragmentActivity);
        this.questions = questions;
        this.currentUser = currentUser;
        this.quizId = quizId;
        this.answerSelectedListener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        QuestionDTO question = questions.get(position);
        TestQuestionFragment fragment = TestQuestionFragment.newInstance(question, question.getId(), currentUser, quizId);
        fragment.setAnswerSelectedListener(answerSelectedListener); // Gán listener sau khi tạo fragment
        return fragment;
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}