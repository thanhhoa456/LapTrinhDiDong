package com.example.laixea1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;
import com.example.laixea1.adapter.CategoryAdapter;
import com.example.laixea1.api.ApiService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.dto.GroupQuestionDTO;
import com.example.laixea1.dto.QuestionStatsDTO;
import com.example.laixea1.entity.Category;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TheoryActivity extends AppCompatActivity {
    private ListView categoryList;
    private CategoryAdapter adapter;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);

        // Initialize ListView
        categoryList = findViewById(R.id.categoryList);
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(this, categories);
        categoryList.setAdapter(adapter);

        // Thêm sự kiện nhấn vào ListView
        categoryList.setOnItemClickListener((parent, view, position, id) -> {
            Category category = categories.get(position);
            Intent intent = new Intent(TheoryActivity.this, QuizActivity.class);
            if (category.getId() == -1) { // -1 cho "Tổng hợp câu điểm liệt"
                intent.putExtra("isCritical", true);
            } else {
                intent.putExtra("groupId", category.getId());
            }
            intent.putExtra("category_name", category.getTitle());
            startActivity(intent);
        });

        // Fetch group questions from API
        fetchGroupQuestions();
    }

    private void fetchGroupQuestions() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<GroupQuestionDTO>> call = apiService.getAllGroupQuestions();

        call.enqueue(new Callback<List<GroupQuestionDTO>>() {
            @Override
            public void onResponse(Call<List<GroupQuestionDTO>> call, Response<List<GroupQuestionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GroupQuestionDTO> groupQuestions = response.body();
                    fetchQuestionStats(groupQuestions);
                } else {
                    Toast.makeText(TheoryActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<GroupQuestionDTO>> call, Throwable t) {
                Log.e("TheoryActivity", "API call failed: " + t.getMessage());
                Toast.makeText(TheoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchQuestionStats(List<GroupQuestionDTO> groupQuestions) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<QuestionStatsDTO>> call = apiService.getQuestionStatsByGroup();

        call.enqueue(new Callback<List<QuestionStatsDTO>>() {
            @Override
            public void onResponse(Call<List<QuestionStatsDTO>> call, Response<List<QuestionStatsDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<QuestionStatsDTO> stats = response.body();
                    mapGroupQuestionsToCategories(groupQuestions, stats);
                } else {
                    Toast.makeText(TheoryActivity.this, "Failed to load question stats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuestionStatsDTO>> call, Throwable t) {
                Log.e("TheoryActivity", "API call failed: " + t.getMessage());
                Toast.makeText(TheoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapGroupQuestionsToCategories(List<GroupQuestionDTO> groupQuestions, List<QuestionStatsDTO> stats) {
        categories.clear();

        // Tạo map để tra cứu số liệu theo groupId
        Map<Integer, QuestionStatsDTO> statsMap = new HashMap<>();
        for (QuestionStatsDTO stat : stats) {
            statsMap.put(stat.getGroupId(), stat);
        }

        // Map group questions to categories
        for (GroupQuestionDTO group : groupQuestions) {
            int groupId = group.getId();
            String name = group.getName();
            String description;
            int completed = 0; // Để 0 theo yêu cầu
            long total = 0;
            long criticalCount = 0;
            int iconResId;

            QuestionStatsDTO stat = statsMap.get(groupId);
            if (stat != null) {
                total = stat.getTotalQuestions();
                criticalCount = stat.getCriticalInGroup();
            }

            switch (name.toUpperCase()) {
                case "KHÁI NIỆM VÀ QUY TẮC":
                    description = String.format("Gồm %d câu hỏi (%d Câu điểm liệt)", total, criticalCount);
                    iconResId = R.drawable.rule;
                    break;
                case "VĂN HÓA VÀ ĐẠO ĐỨC LÁI XE":
                    description = String.format("Gồm %d câu hỏi", total);
                    iconResId = R.drawable.culture;
                    break;
                case "KỸ THUẬT LÁI XE":
                    description = String.format("Gồm %d câu hỏi (%d Câu điểm liệt)", total, criticalCount);
                    iconResId = R.drawable.driving;
                    break;
                case "BIỂN BÁO ĐƯỜNG BỘ":
                    description = String.format("Gồm %d câu hỏi", total);
                    iconResId = R.drawable.streetsign;
                    break;
                case "SA HÌNH":
                    description = String.format("Gồm %d câu hỏi", total);
                    iconResId = R.drawable.traffic;
                    break;
                default:
                    description = String.format("Gồm %d câu hỏi", total);
                    iconResId = R.drawable.important;
                    break;
            }

            categories.add(new Category(groupId, name, description, completed, (int) total, iconResId));
        }

        // Thêm mục "Tổng hợp câu điểm liệt"
        addCriticalQuestionsCategory(stats);
        adapter.notifyDataSetChanged();
    }

    private void addCriticalQuestionsCategory(List<QuestionStatsDTO> stats) {
        long totalCritical = 0;
        if (!stats.isEmpty()) {
            totalCritical = stats.get(0).getTotalCritical(); // Lấy từ phần tử đầu tiên vì totalCritical là giống nhau
        }

        int id = -1; // ID đặc biệt để biểu thị "Tổng hợp câu điểm liệt"
        String name = "TỔNG HỢP CÂU ĐIỂM LIỆT";
        String description = String.format("Gồm %d câu hỏi (Tất cả là câu điểm liệt)", totalCritical);
        int completed = 0;
        int total = (int) totalCritical;
        int iconResId = R.drawable.important;

        categories.add(new Category(id, name, description, completed, total, iconResId));
    }
}