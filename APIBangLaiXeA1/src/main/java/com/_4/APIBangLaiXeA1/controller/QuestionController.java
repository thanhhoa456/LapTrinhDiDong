package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.dto.QuestionDTO;
import com._4.APIBangLaiXeA1.dto.QuestionStatsDTO;
import com._4.APIBangLaiXeA1.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        List<QuestionDTO> questions = questionService.getAllQuestionsWithDetails();
        return ResponseEntity.ok(questions);
    }
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Integer id) {
        Optional<QuestionDTO> question = questionService.getQuestionById(id);
        return question.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByGroupId(@PathVariable Integer groupId) {
        List<QuestionDTO> questions = questionService.getQuestionsByGroupId(groupId);
        return ResponseEntity.ok(questions);
    }
    @GetMapping("/critical")
    public ResponseEntity<List<QuestionDTO>> getCriticalQuestions() {
        List<QuestionDTO> questions = questionService.getCriticalQuestions();
        return ResponseEntity.ok(questions);
    }
    @GetMapping("/stats-by-group")
    public ResponseEntity<List<QuestionStatsDTO>> getQuestionStatsByGroup() {
        try {
            List<QuestionStatsDTO> stats = questionService.getQuestionStatsByGroup();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

}