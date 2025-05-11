package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {
}