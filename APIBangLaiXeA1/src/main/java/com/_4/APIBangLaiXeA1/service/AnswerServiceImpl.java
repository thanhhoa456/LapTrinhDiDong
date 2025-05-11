package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.entity.Answer;
import com._4.APIBangLaiXeA1.repo.AnswerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepo answerRepository;

    @Override
    public List<Answer> getAnswersByUserId(int userId) {
        return answerRepository.findByUserId(userId);
    }

    @Override
    public List<Answer> getAnswersByUserIdAndGroupId(int userId, int groupId) {
        return answerRepository.findByUserIdAndGroupId(userId, groupId);
    }

    @Override
    @Transactional
    public Answer saveOrUpdateAnswer(Answer answer) {
        // Kiểm tra dữ liệu đầu vào
        if (answer.getUserId() <= 0 || answer.getTestQuestionId() <= 0 || answer.getSelectedAnswer() <= 0) {
            throw new IllegalArgumentException("Invalid answer data");
        }
        return answerRepository.save(answer);
    }

    @Override
    @Transactional
    public List<Answer> saveOrUpdateAnswers(List<Answer> answers) {
        // Kiểm tra dữ liệu đầu vào
        for (Answer answer : answers) {
            if (answer.getUserId() <= 0 || answer.getTestQuestionId() <= 0 || answer.getSelectedAnswer() <= 0) {
                throw new IllegalArgumentException("Invalid answer data for testQuestionId: " + answer.getTestQuestionId());
            }
        }
        return answerRepository.saveAll(answers);
    }
}