package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.dto.TestQuestionWithDetails;
import com._4.APIBangLaiXeA1.entity.TestQuestions;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestQuestionsRepo extends JpaRepository<TestQuestions, Integer> {

    @Query("SELECT " +
            "tq.id AS id, " +
            "tq.topicsId AS topicsId, " +
            "q.id AS questionId, " +
            "q.question AS question, " +
            "q.option1 AS option1, " +
            "q.option2 AS option2, " +
            "q.option3 AS option3, " +
            "q.option4 AS option4, " +
            "q.answer AS answer, " +
            "q.image AS image, " +
            "q.explainQuestion AS explainQuestion, " +
            "q.failingScore AS failingScore " +
            "FROM TestQuestions tq JOIN Question q ON tq.questionId = q.id " +
            "WHERE tq.topicsId = :topicsId")
    List<TestQuestionWithDetails> findTestQuestionsWithQuestionByTopicsId(@Param("topicsId") int topicsId);
}
