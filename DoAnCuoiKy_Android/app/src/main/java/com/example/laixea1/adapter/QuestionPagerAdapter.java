package com.example.laixea1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.fragment.QuestionFragment;

import java.util.List;

public class QuestionPagerAdapter extends FragmentStateAdapter {
    protected final List<QuestionDTO> questionList;
    protected final String currentUser;
    protected final QuestionFragment.OnAnswerSelectedListener answerSelectedListener;

    public QuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<QuestionDTO> questionList,
                                String currentUser, QuestionFragment.OnAnswerSelectedListener listener) {
        super(fragmentActivity);
        this.questionList = questionList;
        this.currentUser = currentUser;
        this.answerSelectedListener = listener;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        QuestionDTO question = questionList.get(position);
        QuestionFragment fragment = QuestionFragment.newInstance(question, question.getId(), currentUser); // Sửa để không truyền dbHelper
        fragment.setAnswerSelectedListener(answerSelectedListener);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}