package com._4.APIBangLaiXeA1.service;

import com._4.APIBangLaiXeA1.dto.GroupQuestionDTO;
import com._4.APIBangLaiXeA1.entity.GroupQuestion;
import com._4.APIBangLaiXeA1.repo.GroupQuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupQuestionService {

    @Autowired
    private GroupQuestionRepo groupQuestionRepository;

    public List<GroupQuestionDTO> getAllGroupQuestions() {
        List<GroupQuestion> groupQuestions = groupQuestionRepository.findAll();
        return groupQuestions.stream().map(groupQuestion -> {
            GroupQuestionDTO dto = new GroupQuestionDTO();
            dto.setId(groupQuestion.getId());
            dto.setName(groupQuestion.getName());
            return dto;
        }).collect(Collectors.toList());
    }
}