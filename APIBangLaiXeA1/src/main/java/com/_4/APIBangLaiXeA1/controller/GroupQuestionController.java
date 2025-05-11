package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.dto.GroupQuestionDTO;
import com._4.APIBangLaiXeA1.service.GroupQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/group-questions")
public class GroupQuestionController {

    @Autowired
    private GroupQuestionService groupQuestionService;

    @GetMapping
    public ResponseEntity<List<GroupQuestionDTO>> getAllGroupQuestions() {
        List<GroupQuestionDTO> groupQuestions = groupQuestionService.getAllGroupQuestions();
        return ResponseEntity.ok(groupQuestions);
    }
}