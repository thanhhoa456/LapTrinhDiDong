package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.dto.TestQuestionWithDetails;
import com._4.APIBangLaiXeA1.service.TestQuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-questions")
public class TestQuestionsController {

    @Autowired
    private TestQuestionsService testQuestionsService;

    @GetMapping("/by-topic/{topicsId}")
    public List<TestQuestionWithDetails> getQuestionsByTopic(@PathVariable int topicsId) {
        return testQuestionsService.getTestQuestionsByTopicsId(topicsId);
    }
}
