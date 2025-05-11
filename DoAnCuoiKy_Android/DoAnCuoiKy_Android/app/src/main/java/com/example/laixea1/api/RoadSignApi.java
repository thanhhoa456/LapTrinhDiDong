package com.example.laixea1.api;

import com.example.laixea1.entity.RoadSign;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RoadSignApi {
    @GET("roadsigns/group/{groupId}")
    Call<List<RoadSign>> getRoadSignsByGroupId(@Path("groupId") int groupId);
}