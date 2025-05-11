package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.entity.RoadSign;
import com._4.APIBangLaiXeA1.service.RoadSignService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roadsigns")
public class RoadSignController {

    private final RoadSignService roadSignService;

    public RoadSignController(RoadSignService roadSignService) {
        this.roadSignService = roadSignService;
    }

    @GetMapping
    public List<RoadSign> getAllRoadSignsWithGroup() {
        return roadSignService.getAllRoadSignsWithGroup();
    }
    @GetMapping("/group/{groupId}")
    public List<RoadSign> getRoadSignsByGroupId(@PathVariable int groupId) {
        return roadSignService.getRoadSignsByGroupId(groupId);
    }
}
