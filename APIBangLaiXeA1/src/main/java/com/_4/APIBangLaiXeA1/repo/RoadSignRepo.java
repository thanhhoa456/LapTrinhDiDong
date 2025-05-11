package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.RoadSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoadSignRepo extends JpaRepository<RoadSign, Integer> {

    @Query("SELECT r FROM RoadSign r JOIN FETCH r.roadSignGroup")
    List<RoadSign> findAllWithGroup();
}
