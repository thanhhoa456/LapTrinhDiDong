package com.example.laixea1.api;

import com.example.laixea1.dto.GroupQuestionDTO;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.dto.QuestionStatsDTO;
import com.example.laixea1.dto.TestQuestionsDTO;
import com.example.laixea1.dto.TestsDTO;
import com.example.laixea1.entity.Question;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {
    @GET("questions")
    Call<List<Question>> getQuestions();

    @GET("questions/{id}")
    Call<Question> getQuestionById(@Path("id") int id);
    @GET("group-questions")
    Call<List<GroupQuestionDTO>> getAllGroupQuestions();
    @GET("questions/group/{groupId}")
    Call<List<QuestionDTO>> getQuestionsByGroupId(@Path("groupId") int groupId);

    // Thêm phương thức để lấy danh sách câu hỏi điểm liệt
    @GET("questions/critical")
    Call<List<QuestionDTO>> getCriticalQuestions();
    @GET("questions/stats-by-group")
    Call<List<QuestionStatsDTO>> getQuestionStatsByGroup();

    @GET("test-questions/by-topic/{topicsId}")
    Call<List<QuestionDTO>> getQuestionsByTopic(@Path("topicsId") int topicsId);
    @GET("tests")
    Call<List<TestsDTO>> getAllTests();
}