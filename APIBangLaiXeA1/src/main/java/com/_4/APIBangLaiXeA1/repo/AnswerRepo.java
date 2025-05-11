package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepo extends JpaRepository<Answer, Integer> {
    List<Answer> findByUserId(int userId);

    @Query("SELECT a FROM Answer a JOIN Question q ON a.testQuestionId = q.id WHERE a.userId = :userId AND q.groupId = :groupId")
    List<Answer> findByUserIdAndGroupId(int userId, int groupId);
}