package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.RoadSign;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoadSignRepo extends JpaRepository<RoadSign, Integer> {
    // Sửa findAllWithGroup bằng @Query
    @Query("SELECT rs FROM RoadSign rs")
    List<RoadSign> findAllWithGroup();

    List<RoadSign> findByRoadSignGroupId(int groupId);

    // Thêm phương thức phân trang
    List<RoadSign> findByRoadSignGroupId(int groupId, Pageable pageable);
}