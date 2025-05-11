package com._4.APIBangLaiXeA1.service;


import com._4.APIBangLaiXeA1.dto.TestsDTO;
import com._4.APIBangLaiXeA1.entity.Tests;
import com._4.APIBangLaiXeA1.repo.TestsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestsService {

    @Autowired
    private TestsRepo testsRepository;

    public List<TestsDTO> getAllTests() {
        List<Tests> tests = testsRepository.findAll();
        return tests.stream().map(test -> {
            TestsDTO dto = new TestsDTO();
            dto.setId(test.getId());
            dto.setName(test.getName());
            return dto;
        }).collect(Collectors.toList());
    }
}