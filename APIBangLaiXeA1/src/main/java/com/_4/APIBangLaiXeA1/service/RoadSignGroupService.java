package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.entity.RoadSignGroup;
import com._4.APIBangLaiXeA1.repo.RoadSignGroupRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoadSignGroupService {

    private final RoadSignGroupRepo roadSignGroupRepo;

    public RoadSignGroupService(RoadSignGroupRepo roadSignGroupRepo) {
        this.roadSignGroupRepo = roadSignGroupRepo;
    }

    public List<RoadSignGroup> getAllGroups() {
        return roadSignGroupRepo.findAll();
    }
}
