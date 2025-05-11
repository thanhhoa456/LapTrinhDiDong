package com.example.laixea1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;
import com.example.laixea1.fragment.QuestionFragment;

import java.util.List;
import java.util.Map;

public class QuestionPagerAdapter extends FragmentStateAdapter {
    private final List<QuestionDTO> questionList;
    private final Map<Integer, List<Answer>> answerCache;
    private final QuestionFragment.OnAnswerSelectedListener answerSelectedListener;

    public QuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<QuestionDTO> questionList,
                                Map<Integer, List<Answer>> answerCache, QuestionFragment.OnAnswerSelectedListener listener) {
        super(fragmentActivity);
        this.questionList = questionList;
        this.answerCache = answerCache;
        this.answerSelectedListener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        QuestionDTO question = questionList.get(position);
        QuestionFragment fragment = QuestionFragment.newInstance(question, question.getId(), answerCache);
        fragment.setAnswerSelectedListener(answerSelectedListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}