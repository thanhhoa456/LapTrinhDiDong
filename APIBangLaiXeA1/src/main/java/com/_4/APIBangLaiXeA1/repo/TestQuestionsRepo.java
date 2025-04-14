package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.TestQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestQuestionsRepo extends JpaRepository<TestQuestions, Integer> {
}