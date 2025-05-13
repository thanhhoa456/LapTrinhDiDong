package com.example.laixea1.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.laixea1.R;
import com.example.laixea1.adapter.FeedbackAdapter;
import com.example.laixea1.api.FeedbackApiService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.dto.FeedbackDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText editTextNoiDung;
    private Button buttonSubmit;
    private RecyclerView recyclerViewFeedbacks;
    private FeedbackAdapter feedbackAdapter;
    private FeedbackApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Khởi tạo các view
        ratingBar = findViewById(R.id.ratingBar);
        editTextNoiDung = findViewById(R.id.editTextNoiDung);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        recyclerViewFeedbacks = findViewById(R.id.recyclerViewFeedbacks);

        // Khởi tạo Retrofit service
        apiService = RetrofitClient.createService(FeedbackApiService.class);

        // Thiết lập RecyclerView
        recyclerViewFeedbacks.setLayoutManager(new LinearLayoutManager(this));
        feedbackAdapter = new FeedbackAdapter(new ArrayList<>());
        recyclerViewFeedbacks.setAdapter(feedbackAdapter);

        // Lắng nghe thay đổi số sao
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    Log.d("RatingBar", "Chọn: " + rating + " sao");
                    Toast.makeText(FeedbackActivity.this, "Chọn: " + rating + " sao", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Tải danh sách feedback
        loadFeedbacks();

        // Xử lý sự kiện nút Gửi
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                String noiDung = editTextNoiDung.getText().toString().trim();

                Log.d("Feedback", "Trước khi gửi: rating=" + rating);

                if (rating == 0) {
                    Toast.makeText(FeedbackActivity.this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rating < 1 || rating > 5) {
                    Toast.makeText(FeedbackActivity.this, "Số sao phải từ 1 đến 5!", Toast.LENGTH_SHORT).show();
                    ratingBar.setRating(0);
                    return;
                }
                if (noiDung.isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
                    return;
                }

                FeedbackDTO feedback = new FeedbackDTO(0, 1, (int) rating, noiDung);
                Log.d("Feedback", "Gửi: userId=" + feedback.getUserId() + ", soSao=" + feedback.getSoSao());

                apiService.createFeedback(feedback).enqueue(new Callback<FeedbackDTO>() {
                    @Override
                    public void onResponse(Call<FeedbackDTO> call, Response<FeedbackDTO> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("Feedback", "Nhận phản hồi: soSao=" + response.body().getSoSao());
                            Toast.makeText(FeedbackActivity.this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                            ratingBar.setRating(0);
                            editTextNoiDung.setText("");
                            loadFeedbacks();
                        } else {
                            Toast.makeText(FeedbackActivity.this, "Lỗi khi gửi đánh giá!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FeedbackDTO> call, Throwable t) {
                        Toast.makeText(FeedbackActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadFeedbacks() {
        apiService.getAllFeedbacks().enqueue(new Callback<List<FeedbackDTO>>() {
            @Override
            public void onResponse(Call<List<FeedbackDTO>> call, Response<List<FeedbackDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (FeedbackDTO feedback : response.body()) {
                        Log.d("Feedback", "Nhận: id=" + feedback.getId() + ", soSao=" + feedback.getSoSao());
                    }
                    feedbackAdapter.updateFeedbacks(response.body());
                } else {
                    Toast.makeText(FeedbackActivity.this, "Lỗi khi tải danh sách đánh giá!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FeedbackDTO>> call, Throwable t) {
                Toast.makeText(FeedbackActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}