package com.example.laixea1.activity;

import static com.example.laixea1.activity.QuizActivity.SETTINGS_REQUEST_CODE;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;
import com.example.laixea1.adapter.CategoryTestAdapter;
import com.example.laixea1.api.ApiService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.TestsDTO;
import com.example.laixea1.entity.CategoryTest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizSelectionActivity extends AppCompatActivity {
    private ListView categoryList;
    private ImageButton resetButton;
    private CategoryTestAdapter adapter;
    private List<CategoryTest> categoryTests;
    private DatabaseHelper dbHelper;
    private String currentUser;
    private List<TestsDTO> testsCache;
    private boolean isDataLoaded = false;
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);
        titleText = findViewById(R.id.title);
        titleText.setText("Thi Thử");

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Lấy current_user từ App_Settings
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        currentUser = appPrefs.getString("current_user", "Guest");

        // Khởi tạo ListView và Button
        categoryList = findViewById(R.id.categoryList);
        resetButton = findViewById(R.id.imageButton);
        categoryTests = new ArrayList<>();
        adapter = new CategoryTestAdapter(this, categoryTests);
        categoryList.setAdapter(adapter);

        // Thêm sự kiện nhấn vào ListView
        categoryList.setOnItemClickListener((parent, view, position, id) -> {
            CategoryTest categoryTest = categoryTests.get(position);
            Intent intent = new Intent(QuizSelectionActivity.this, QuizTestActivity.class);
            intent.putExtra("quizId", categoryTest.getId());
            intent.putExtra("quiz_name", categoryTest.getTitle());
            startActivity(intent);
            // Lưu trạng thái khi bắt đầu làm đề
            saveTestProgress(categoryTest.getId(), 20 * 60, false);
        });

        // Thêm sự kiện nhấn nút Reset với xác nhận
        resetButton.setOnClickListener(v -> {
            new AlertDialog.Builder(QuizSelectionActivity.this)
                    .setTitle("Xác nhận reset")
                    .setMessage("Bạn có chắc chắn muốn reset tiến độ làm đề không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        resetProgress();
                        Toast.makeText(QuizSelectionActivity.this, "Đã reset tiến độ làm đề!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .setCancelable(true)
                    .show();
        });

        // Khởi tạo cache
        testsCache = new ArrayList<>();

        // Gọi API để lấy danh sách đề thi
        fetchAllTests();
    }

    private void fetchAllTests() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<TestsDTO>> call = apiService.getAllTests();

        call.enqueue(new Callback<List<TestsDTO>>() {
            @Override
            public void onResponse(Call<List<TestsDTO>> call, Response<List<TestsDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    testsCache.clear();
                    testsCache.addAll(response.body());
                    updateCategoriesWithTests();
                    isDataLoaded = true;
                } else {
                    Toast.makeText(QuizSelectionActivity.this, "Không thể tải danh sách đề thi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TestsDTO>> call, Throwable t) {
                Log.e("QuizSelectionActivity", "API call failed: " + t.getMessage());
                Toast.makeText(QuizSelectionActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCategoriesWithTests() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        categoryTests.clear();

        try {
            db = dbHelper.getReadableDatabase();
            for (TestsDTO test : testsCache) {
                int testId = test.getId();
                String name = "Đề số " + testId;

                String query = "SELECT remainingTime, isPaused, isCompleted, isPassed FROM TestProgress WHERE userId = ? AND testId = ?";
                int remainingTime = 20 * 60;
                boolean isPaused = false;
                boolean isCompleted = false;
                boolean isPassed = false;
                boolean hasProgress = false;

                cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(testId)});
                if (cursor != null && cursor.moveToFirst()) {
                    hasProgress = true;
                    int remainingTimeIndex = cursor.getColumnIndex("remainingTime");
                    int isPausedIndex = cursor.getColumnIndex("isPaused");
                    int isCompletedIndex = cursor.getColumnIndex("isCompleted");
                    int isPassedIndex = cursor.getColumnIndex("isPassed");
                    if (remainingTimeIndex >= 0 && isPausedIndex >= 0 && isCompletedIndex >= 0 && isPassedIndex >= 0) {
                        remainingTime = cursor.getInt(remainingTimeIndex);
                        isPaused = cursor.getInt(isPausedIndex) == 1;
                        isCompleted = cursor.getInt(isCompletedIndex) == 1;
                        isPassed = cursor.getInt(isPassedIndex) == 1;
                    }
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }

                String status;
                String buttonText; // Thêm biến để lưu nội dung nút
                if (isCompleted) {
                    status = isPassed ? "ĐẠT" : "KHÔNG ĐẠT";
                    buttonText = "Xem lại";
                } else if (hasProgress) {
                    status = "TẠM DỪNG";
                    buttonText = "Tiếp tục";
                } else {
                    status = "CHƯA LÀM";
                    buttonText = "";
                }

                int minutes = remainingTime / 60;
                int seconds = remainingTime % 60;
                String description = String.format("Còn lại %d phút %d giây", minutes, seconds);

                int iconResId = R.drawable.clock;
                CategoryTest categoryTest = new CategoryTest(testId, name, description, status, iconResId);
                categoryTest.setButtonText(buttonText); // Lưu nội dung nút vào CategoryTest
                categoryTests.add(categoryTest);
            }
        } catch (Exception e) {
            Log.e("QuizSelectionActivity", "Error updating categories: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        adapter.notifyDataSetChanged();
        Log.d("QuizSelectionActivity", "CategoryTests updated with tests");
    }

    private void saveTestProgress(int testId, int remainingTime, boolean isPaused) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userId", currentUser);
            values.put("testId", testId);
            values.put("remainingTime", remainingTime);
            values.put("isPaused", isPaused ? 1 : 0);
            values.put("completedQuestions", 0);
            values.put("isCompleted", 0);
            values.put("isPassed", 0);

            String selection = "userId = ? AND testId = ?";
            String[] selectionArgs = {currentUser, String.valueOf(testId)};
            long count = DatabaseUtils.queryNumEntries(db, "TestProgress", selection, selectionArgs);

            if (count > 0) {
                db.update("TestProgress", values, selection, selectionArgs);
            } else {
                db.insert("TestProgress", null, values);
            }
        } catch (Exception e) {
            Log.e("QuizSelectionActivity", "Error saving test progress: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private void resetProgress() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.delete("TestProgress", "userId = ?", new String[]{currentUser});
            db.delete("UserTestAnswers", "userId = ?", new String[]{currentUser});
            updateCategoriesWithTests();
        } catch (Exception e) {
            Log.e("QuizSelectionActivity", "Error resetting progress: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi reset tiến độ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDataLoaded && !testsCache.isEmpty()) {
            updateCategoriesWithTests();
        } else {
            fetchAllTests();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}