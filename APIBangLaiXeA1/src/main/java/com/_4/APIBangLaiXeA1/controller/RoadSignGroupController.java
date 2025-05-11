package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.entity.RoadSignGroup;
import com._4.APIBangLaiXeA1.service.RoadSignGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadsign-groups")
public class RoadSignGroupController {

    private final RoadSignGroupService roadSignGroupService;

    public RoadSignGroupController(RoadSignGroupService roadSignGroupService) {
        this.roadSignGroupService = roadSignGroupService;
    }

    @GetMapping
    public List<RoadSignGroup> getAllRoadSignGroups() {
        return roadSignGroupService.getAllGroups();
    }
}
