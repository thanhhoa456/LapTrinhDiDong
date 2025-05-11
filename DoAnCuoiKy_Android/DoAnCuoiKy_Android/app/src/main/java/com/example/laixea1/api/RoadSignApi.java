package com.example.laixea1.api;

import com.example.laixea1.entity.RoadSign;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface RoadSignApi {
    @GET("roadsigns/group/{groupId}")
    Call<List<RoadSign>> getRoadSignsByGroupId(
            @Path("groupId") int groupId,
            @Query("page") int page,
            @Query("size") int size
    );
}