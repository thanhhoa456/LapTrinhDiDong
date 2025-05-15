package com.example.laixea1.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.laixea1.R;
import com.example.laixea1.activity.QuizTestActivity;
import com.example.laixea1.entity.CategoryTest;

import java.util.List;

public class CategoryTestAdapter extends ArrayAdapter<CategoryTest> {
    private Context context;
    private List<CategoryTest> categoryTests;

    public CategoryTestAdapter(Context context, List<CategoryTest> categoryTests) {
        super(context, 0, categoryTests);
        this.context = context;
        this.categoryTests = categoryTests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_test, parent, false);
        }

        CategoryTest categoryTest = categoryTests.get(position);

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView quizTitle = convertView.findViewById(R.id.quizTitle);
        TextView quizDescription = convertView.findViewById(R.id.quizDescription);
        TextView status = convertView.findViewById(R.id.status);
        Button continueButton = convertView.findViewById(R.id.continueButton);

        icon.setImageResource(categoryTest.getIconResId());
        quizTitle.setText(categoryTest.getTitle());
        quizDescription.setText(categoryTest.getDescription());
        status.setText(categoryTest.getStatus());

        switch (categoryTest.getStatus()) {
            case "ĐẠT":
                status.setTextColor(Color.parseColor("#02A902"));
                break;
            case "KHÔNG ĐẠT":
                status.setTextColor(Color.parseColor("#FF0000"));
                break;
            case "TẠM DỪNG":
                status.setTextColor(Color.parseColor("#FFA500"));
                break;
            default:
                status.setTextColor(Color.parseColor("#808080"));
                break;
        }

        status.setVisibility(View.VISIBLE);

        // Cập nhật nút Tiếp tục/Xem lại
        String buttonText = categoryTest.getButtonText();
        continueButton.setText(buttonText);
        continueButton.setVisibility(buttonText.isEmpty() ? View.GONE : View.VISIBLE);
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizTestActivity.class);
            intent.putExtra("quizId", categoryTest.getId());
            intent.putExtra("quiz_name", categoryTest.getTitle());
            intent.putExtra("reviewMode", buttonText.equals("Xem lại"));
            context.startActivity(intent);
        });

        return convertView;
    }
}