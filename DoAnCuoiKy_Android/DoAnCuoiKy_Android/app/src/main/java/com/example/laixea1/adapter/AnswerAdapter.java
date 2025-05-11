package com.example.laixea1.adapter;

import android.content.Context;
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
import com.example.laixea1.entity.Answer;
import com.example.laixea1.fragment.QuestionFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private Context context;
    private List<Answer> answerList;
    private String explanation;
    private TextView explanationText;
    private int currentQuestionId;
    private Map<Integer, List<Answer>> answerCache;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private QuestionFragment.OnAnswerSelectedListener answerSelectedListener;

    public AnswerAdapter(Context context, int layout, List<Answer> answerList, String explanation,
                         TextView explanationText, int currentQuestionId, Map<Integer, List<Answer>> answerCache,
                         QuestionFragment.OnAnswerSelectedListener listener) {
        this.context = context;
        this.answerList = answerList;
        this.explanation = explanation;
        this.explanationText = explanationText;
        this.currentQuestionId = currentQuestionId;
        this.answerCache = answerCache;
        this.answerSelectedListener = listener;
        correctSound = MediaPlayer.create(context, R.raw.correct);
        wrongSound = MediaPlayer.create(context, R.raw.wrong);
    }

    public void setAnswerSelectedListener(QuestionFragment.OnAnswerSelectedListener listener) {
        this.answerSelectedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Answer answer = answerList.get(position);
        viewHolder.answerText.setText(answer.getText());
        viewHolder.radioAnswer.setChecked(answer.isSelected());

        // Hiển thị trạng thái đúng/sai và giải thích
        if (answer.isSelected()) {
            if (answer.isCorrect()) {
                viewHolder.statusImage.setImageResource(R.drawable.correct);
                viewHolder.statusImage.setVisibility(View.VISIBLE);
                if (correctSound != null) {
                    correctSound.start();
                }
                if (explanation != null && !explanation.trim().isEmpty()) {
                    explanationText.setText(explanation);
                    explanationText.setVisibility(View.VISIBLE);
                } else {
                    explanationText.setVisibility(View.GONE);
                }
            } else {
                viewHolder.statusImage.setImageResource(R.drawable.incorrect);
                viewHolder.statusImage.setVisibility(View.VISIBLE);
                if (wrongSound != null) {
                    wrongSound.start();
                }
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

        // Xử lý sự kiện click trên item
        viewHolder.itemView.setOnClickListener(v -> {
            Log.d("AnswerAdapter", "Item clicked: " + position);
            if (answer.isSelected()) {
                for (Answer ans : answerList) {
                    ans.setSelected(false);
                }
                Log.d("AnswerAdapter", "Deselected: " + position);
            } else {
                for (Answer ans : answerList) {
                    ans.setSelected(false);
                }
                answer.setSelected(true);
                Log.d("AnswerAdapter", "Selected: " + position);
            }

            // Lưu ngay vào answerCache khi người dùng chọn đáp án
            answerCache.put(currentQuestionId, new ArrayList<>(answerList));
            Log.d("AnswerAdapter", "Saved answers to cache for question " + currentQuestionId + ": " + answerList.size());
            for (Answer ans : answerList) {
                Log.d("AnswerAdapter", "Saved answer: " + ans.getText() + ", isSelected: " + ans.isSelected());
            }

            // Debug answerCache after saving
            Log.d("AnswerAdapter", "Current state of answerCache after saving: ");
            for (Map.Entry<Integer, List<Answer>> entry : answerCache.entrySet()) {
                Log.d("AnswerAdapter", "Question ID: " + entry.getKey());
                for (Answer ans : entry.getValue()) {
                    Log.d("AnswerAdapter", "Answer: " + ans.getText() + ", isSelected: " + ans.isSelected());
                }
            }

            // Thông báo cho QuizActivity
            if (answerSelectedListener != null) {
                answerSelectedListener.onAnswerSelected();
            }

            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return answerList.size();
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