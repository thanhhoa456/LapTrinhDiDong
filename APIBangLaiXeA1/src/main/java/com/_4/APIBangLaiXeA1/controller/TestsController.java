package com._4.APIBangLaiXeA1.controller;


import com._4.APIBangLaiXeA1.dto.TestsDTO;
import com._4.APIBangLaiXeA1.service.TestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestsController {

    @Autowired
    private TestsService testsService;

    @GetMapping
    public ResponseEntity<List<TestsDTO>> getAllTests() {
        List<TestsDTO> tests = testsService.getAllTests();
        return ResponseEntity.ok(tests);
    }
}