package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.dto.QuestionDTO;
import com._4.APIBangLaiXeA1.dto.QuestionStatsDTO;
import com._4.APIBangLaiXeA1.entity.Question;
import com._4.APIBangLaiXeA1.repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepo questionRepository;

    public List<QuestionDTO> getAllQuestionsWithDetails() {
        List<Object[]> results = questionRepository.findAllQuestionsWithGroupName();

        return results.stream().map(result -> {
            Question question = (Question) result[0];
            String groupName = (String) result[1];

            QuestionDTO dto = new QuestionDTO();
            dto.setId(question.getId());
            dto.setGroupId(question.getGroupId());
            dto.setGroupName(groupName);
            dto.setQuestion(question.getQuestion());
            dto.setOption1(question.getOption1());
            dto.setOption2(question.getOption2());
            dto.setOption3(question.getOption3());
            dto.setOption4(question.getOption4());
            dto.setAnswer(question.getAnswer());
            dto.setImage(question.getImage());
            dto.setExplainQuestion(question.getExplainQuestion());
            dto.setFailingScore(question.isFailingScore());

            return dto;
        }).collect(Collectors.toList());
    }



    public Optional<QuestionDTO> getQuestionById(Integer id) {
        Optional<Object[]> result = questionRepository.findQuestionWithGroupNameById(id);

        return result.map(res -> {
            // Kiểm tra nếu res là mảng lồng nhau
            Object[] data = res;
            if (res[0] instanceof Object[]) {
                data = (Object[]) res[0]; // Lấy mảng con
            }

            // Kiểm tra và ép kiểu
            if (!(data[0] instanceof Question)) {
                throw new IllegalStateException("Expected Question object but found: " + data[0].getClass().getName());
            }
            if (!(data[1] instanceof String)) {
                throw new IllegalStateException("Expected String for groupName but found: " + data[1].getClass().getName());
            }

            Question question = (Question) data[0];
            String groupName = (String) data[1];

            QuestionDTO dto = new QuestionDTO();
            dto.setId(question.getId());
            dto.setGroupId(question.getGroupId());
            dto.setGroupName(groupName);
            dto.setQuestion(question.getQuestion());
            dto.setOption1(question.getOption1());
            dto.setOption2(question.getOption2());
            dto.setOption3(question.getOption3());
            dto.setOption4(question.getOption4());
            dto.setAnswer(question.getAnswer());
            dto.setImage(question.getImage());
            dto.setExplainQuestion(question.getExplainQuestion());
            dto.setFailingScore(question.isFailingScore());

            return dto;
        });
    }
    public List<QuestionDTO> getQuestionsByGroupId(Integer groupId) {
        List<Object[]> results = questionRepository.findQuestionsByGroupId(groupId);

        return results.stream().map(result -> {
            Question question = (Question) result[0];
            String groupName = (String) result[1];

            QuestionDTO dto = new QuestionDTO();
            dto.setId(question.getId());
            dto.setGroupId(question.getGroupId());
            dto.setGroupName(groupName);
            dto.setQuestion(question.getQuestion());
            dto.setOption1(question.getOption1());
            dto.setOption2(question.getOption2());
            dto.setOption3(question.getOption3());
            dto.setOption4(question.getOption4());
            dto.setAnswer(question.getAnswer());
            dto.setImage(question.getImage());
            dto.setExplainQuestion(question.getExplainQuestion());
            dto.setFailingScore(question.isFailingScore());

            return dto;
        }).collect(Collectors.toList());
    }

    // Lấy danh sách câu hỏi điểm liệt
    public List<QuestionDTO> getCriticalQuestions() {
        List<Object[]> results = questionRepository.findCriticalQuestions();

        return results.stream().map(result -> {
            Question question = (Question) result[0];
            String groupName = (String) result[1];

            QuestionDTO dto = new QuestionDTO();
            dto.setId(question.getId());
            dto.setGroupId(question.getGroupId());
            dto.setGroupName(groupName);
            dto.setQuestion(question.getQuestion());
            dto.setOption1(question.getOption1());
            dto.setOption2(question.getOption2());
            dto.setOption3(question.getOption3());
            dto.setOption4(question.getOption4());
            dto.setAnswer(question.getAnswer());
            dto.setImage(question.getImage());
            dto.setExplainQuestion(question.getExplainQuestion());
            dto.setFailingScore(question.isFailingScore());

            return dto;
        }).collect(Collectors.toList());
    }
    public List<QuestionStatsDTO> getQuestionStatsByGroup() {
        return questionRepository.getQuestionStatsByGroup();
    }
}