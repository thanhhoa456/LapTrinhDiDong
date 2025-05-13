package com.example.laixea1.api;

import com.example.laixea1.dto.FeedbackDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import java.util.List;

public interface FeedbackApiService {
    @POST("feedback")
    Call<FeedbackDTO> createFeedback(@Body FeedbackDTO feedbackDTO);

    @GET("feedback")
    Call<List<FeedbackDTO>> getAllFeedbacks();
}