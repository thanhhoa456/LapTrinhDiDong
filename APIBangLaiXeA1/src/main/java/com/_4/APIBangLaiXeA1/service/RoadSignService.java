package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.entity.RoadSign;
import com._4.APIBangLaiXeA1.repo.RoadSignRepo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoadSignService {

    private final RoadSignRepo roadSignRepo;

    public RoadSignService(RoadSignRepo roadSignRepo) {
        this.roadSignRepo = roadSignRepo;
    }

    public List<RoadSign> getAllRoadSignsWithGroup() {
        return roadSignRepo.findAllWithGroup();
    }

    public List<RoadSign> getRoadSignsByGroupId(int groupId) {
        return roadSignRepo.findByRoadSignGroupId(groupId);
    }

    // Thêm phương thức phân trang
    public List<RoadSign> getRoadSignsByGroupId(int groupId, Pageable pageable) {
        return roadSignRepo.findByRoadSignGroupId(groupId, pageable);
    }
}