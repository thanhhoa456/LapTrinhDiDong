package com.example.laixea1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;
import com.example.laixea1.adapter.CategoryAdapter;
import com.example.laixea1.api.ApiService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.database.DatabaseHelper;
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
    private ImageButton resetButton;
    private CategoryAdapter adapter;
    private List<Category> categories;
    private DatabaseHelper dbHelper;
    private String currentUser;
    private List<GroupQuestionDTO> groupQuestionsCache;
    private List<QuestionStatsDTO> statsCache;
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Lấy current_user từ App_Settings
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        currentUser = appPrefs.getString("current_user", "Guest");

        // Initialize ListView and Button
        categoryList = findViewById(R.id.categoryList);
        resetButton = findViewById(R.id.imageButton);
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(this, categories);
        categoryList.setAdapter(adapter);

        // Thêm sự kiện nhấn vào ListView
        categoryList.setOnItemClickListener((parent, view, position, id) -> {
            Category category = categories.get(position);
            Intent intent = new Intent(TheoryActivity.this, QuizActivity.class);
            if (category.getId() == -1) { // -1 cho "Tổng hợp câu điểm liệt"
                intent.putExtra("isFailingScore", true);
                intent.putExtra("category_name", category.getTitle());
                Log.d("TheoryActivity", "Starting QuizActivity for critical questions");
            } else {
                intent.putExtra("groupId", category.getId());
                intent.putExtra("category_name", category.getTitle());
                Log.d("TheoryActivity", "Starting QuizActivity for groupId: " + category.getId());
            }
            startActivity(intent);
        });

        // Thêm sự kiện nhấn nút Reset với xác nhận
        resetButton.setOnClickListener(v -> {
            new AlertDialog.Builder(TheoryActivity.this)
                    .setTitle("Xác nhận reset")
                    .setMessage("Bạn có chắc chắn muốn reset tiến độ bài học không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        resetProgress();
                        Toast.makeText(TheoryActivity.this, "Đã reset tiến độ bài học!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        dialog.dismiss(); // Đóng dialog nếu chọn "Không"
                    })
                    .setCancelable(true) // Cho phép hủy bằng nút Back
                    .show();
        });

        // Khởi tạo cache
        groupQuestionsCache = new ArrayList<>();
        statsCache = new ArrayList<>();

        // Fetch group questions from API
        fetchGroupQuestions();
    }

    private void resetProgress() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // Xóa tất cả tiến độ của người dùng hiện tại
            int rowsDeleted = db.delete("UserProgress", "userId = ?", new String[]{currentUser});
            Log.d("TheoryActivity", "Deleted " + rowsDeleted + " rows from UserProgress for user: " + currentUser);
            // Làm mới giao diện
            updateCategoriesWithProgress();
        } catch (Exception e) {
            Log.e("TheoryActivity", "Error resetting progress", e);
            Toast.makeText(this, "Lỗi khi reset tiến độ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Làm mới dữ liệu khi quay lại activity
        if (isDataLoaded && !groupQuestionsCache.isEmpty() && !statsCache.isEmpty()) {
            updateCategoriesWithProgress();
        } else {
            fetchGroupQuestions();
        }
    }

    private void fetchGroupQuestions() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<GroupQuestionDTO>> call = apiService.getAllGroupQuestions();

        call.enqueue(new Callback<List<GroupQuestionDTO>>() {
            @Override
            public void onResponse(Call<List<GroupQuestionDTO>> call, Response<List<GroupQuestionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupQuestionsCache.clear();
                    groupQuestionsCache.addAll(response.body());
                    fetchQuestionStats(groupQuestionsCache);
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
                    statsCache.clear();
                    statsCache.addAll(response.body());
                    updateCategoriesWithProgress();
                    isDataLoaded = true;
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

    private void updateCategoriesWithProgress() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Tính số câu đã hoàn thành cho mỗi groupId
        Map<Integer, Integer> completedCounts = new HashMap<>();
        for (GroupQuestionDTO group : groupQuestionsCache) {
            int groupId = group.getId();
            String query = "SELECT COUNT(DISTINCT up.questionId) " +
                    "FROM UserProgress up " +
                    "JOIN Questions q ON up.questionId = q.id " +
                    "WHERE up.userId = ? AND q.groupId = ?";
            Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(groupId)});
            int completed = 0;
            if (cursor.moveToFirst()) {
                completed = cursor.getInt(0);
            }
            cursor.close();
            completedCounts.put(groupId, completed);
            Log.d("TheoryActivity", "Group ID " + groupId + " has " + completed + " completed questions");
        }

        // Map group questions to categories
        categories.clear();
        Map<Integer, QuestionStatsDTO> statsMap = new HashMap<>();
        for (QuestionStatsDTO stat : statsCache) {
            statsMap.put(stat.getGroupId(), stat);
        }

        for (GroupQuestionDTO group : groupQuestionsCache) {
            int groupId = group.getId();
            String name = group.getName();
            String description;
            int completed = completedCounts.getOrDefault(groupId, 0);
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
        addCriticalQuestionsCategory(statsCache, db);
        db.close();

        adapter.notifyDataSetChanged();
        Log.d("TheoryActivity", "Categories updated with new progress");
    }

    private void addCriticalQuestionsCategory(List<QuestionStatsDTO> stats, SQLiteDatabase db) {
        long totalCritical = 0;
        if (!stats.isEmpty()) {
            totalCritical = stats.get(0).getTotalCritical();
        }

        // Tính số câu điểm liệt đã hoàn thành
        String query = "SELECT COUNT(DISTINCT up.questionId) " +
                "FROM UserProgress up " +
                "JOIN Questions q ON up.questionId = q.id " +
                "WHERE up.userId = ? AND q.failingScore = 1";
        Cursor cursor = db.rawQuery(query, new String[]{currentUser});
        int completedCritical = 0;
        if (cursor.moveToFirst()) {
            completedCritical = cursor.getInt(0);
        }
        cursor.close();
        Log.d("TheoryActivity", "Total critical questions completed: " + completedCritical);

        int id = -1;
        String name = "TỔNG HỢP CÂU ĐIỂM LIỆT";
        String description = String.format("Gồm %d câu hỏi (Tất cả là câu điểm liệt)", totalCritical);
        int completed = completedCritical;
        int total = (int) totalCritical;
        int iconResId = R.drawable.important;

        categories.add(new Category(id, name, description, completed, total, iconResId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}