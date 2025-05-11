package com.example.laixea1.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;

import java.util.List;
import java.util.Map;

public class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberAdapter.ViewHolder> {
    private List<Integer> questionNumbers;
    private int currentQuestionIndex;
    private List<QuestionDTO> questionList;
    private Map<Integer, List<Answer>> answerCache;
    private OnQuestionNumberClickListener listener;

    public interface OnQuestionNumberClickListener {
        void onQuestionNumberClick(int position);
    }

    public QuestionNumberAdapter(List<Integer> questionNumbers, int currentQuestionIndex, List<QuestionDTO> questionList,
                                 Map<Integer, List<Answer>> answerCache, OnQuestionNumberClickListener listener) {
        this.questionNumbers = questionNumbers;
        this.currentQuestionIndex = currentQuestionIndex;
        this.questionList = questionList;
        this.answerCache = answerCache;
        this.listener = listener;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_number, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int number = questionNumbers.get(position);
        holder.numberText.setText(String.valueOf(number));
        holder.numberText.setSelected(position == currentQuestionIndex);

        // Kiểm tra trạng thái câu hỏi từ answerCache
        if (position < questionList.size()) {
            int questionId = questionList.get(position).getId();
            Log.d("QuestionNumberAdapter", "Position: " + position + ", Question ID: " + questionId);

            List<Answer> answers = answerCache.get(questionId);
            if (answers != null) {
                Log.d("QuestionNumberAdapter", "Answers found for Question ID: " + questionId + ", Size: " + answers.size());
                boolean hasSelected = false;
                boolean isCorrect = false;
                for (Answer answer : answers) {
                    Log.d("QuestionNumberAdapter", "Answer: " + answer.getText() + ", isSelected: " + answer.isSelected() + ", isCorrect: " + answer.isCorrect());
                    if (answer.isSelected()) {
                        hasSelected = true;
                        isCorrect = answer.isCorrect();
                        break;
                    }
                }
                if (hasSelected) {
                    Log.d("QuestionNumberAdapter", "Question " + questionId + " has selected answer, isCorrect: " + isCorrect);
                    holder.statusImage.setImageResource(isCorrect ? R.drawable.check : R.drawable.delete);
                    holder.statusImage.setVisibility(View.VISIBLE);

                } else {
                    Log.d("QuestionNumberAdapter", "Question " + questionId + " has no selected answer");
                    holder.statusImage.setVisibility(View.GONE);
                }
            } else {
                Log.d("QuestionNumberAdapter", "No answers found in answerCache for Question ID: " + questionId);
                holder.statusImage.setVisibility(View.GONE);
            }
        } else {
            Log.d("QuestionNumberAdapter", "Position " + position + " exceeds questionList size: " + questionList.size());
            holder.statusImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionNumberClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questionNumbers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numberText;
        ImageView statusImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.numberText);
            statusImage = itemView.findViewById(R.id.statusImage);
        }
    }
}