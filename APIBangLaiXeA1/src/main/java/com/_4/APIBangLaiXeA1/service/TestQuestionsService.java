package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.dto.TestQuestionWithDetails;
import com._4.APIBangLaiXeA1.repo.TestQuestionsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestQuestionsService {

    @Autowired
    private TestQuestionsRepo testQuestionsRepo;

    public List<TestQuestionWithDetails> getTestQuestionsByTopicsId(int topicsId) {
        return testQuestionsRepo.findTestQuestionsWithQuestionByTopicsId(topicsId);
    }
}
