package com._4.APIBangLaiXeA1.controller;

import com._4.APIBangLaiXeA1.entity.Answer;
import com._4.APIBangLaiXeA1.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    // GET /answers/123?groupId=456
    @GetMapping("/{userId}")
    public List<Answer> getAnswersByUser(@PathVariable int userId, @RequestParam(value = "groupId", required = false) Integer groupId) {
        if (groupId != null) {
            return answerService.getAnswersByUserIdAndGroupId(userId, groupId);
        }
        return answerService.getAnswersByUserId(userId);
    }

    // POST /answers
    @PostMapping
    public Answer saveAnswer(@RequestBody Answer answer) {
        return answerService.saveOrUpdateAnswer(answer);
    }

    // POST /answers/batch
    @PostMapping("/batch")
    public List<Answer> saveAnswers(@RequestBody List<Answer> answers) {
        return answerService.saveOrUpdateAnswers(answers);
    }
}