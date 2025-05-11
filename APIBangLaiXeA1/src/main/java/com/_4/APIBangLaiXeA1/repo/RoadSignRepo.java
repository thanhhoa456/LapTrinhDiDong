package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.RoadSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoadSignRepo extends JpaRepository<RoadSign, Integer> {

    @Query("SELECT r FROM RoadSign r JOIN FETCH r.roadSignGroup")
    List<RoadSign> findAllWithGroup();
    @Query("SELECT r FROM RoadSign r JOIN FETCH r.roadSignGroup WHERE r.rsGroupId = :groupId")
    List<RoadSign> findByRoadSignGroupId(@Param("groupId") int groupId);
}
