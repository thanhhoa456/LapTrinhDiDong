package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Integer> {
}