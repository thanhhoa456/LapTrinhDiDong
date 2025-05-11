package com._4.APIBangLaiXeA1.repo;

import com._4.APIBangLaiXeA1.dto.QuestionStatsDTO;
import com._4.APIBangLaiXeA1.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Integer> {
    @Query("SELECT new com._4.APIBangLaiXeA1.dto.QuestionStatsDTO(" +
            "q.groupId, " +
            "COUNT(q), " +
            "SUM(CASE WHEN q.failingScore = true THEN 1 ELSE 0 END), " +
            "(SELECT COUNT(q2) FROM Question q2 WHERE q2.failingScore = true)) " +
            "FROM Question q GROUP BY q.groupId")
    List<QuestionStatsDTO> getQuestionStatsByGroup();
    @Query("SELECT q, g.name as groupName FROM Question q JOIN GroupQuestion g ON q.groupId = g.id")
    List<Object[]> findAllQuestionsWithGroupName();
    @Query("SELECT q, g.name as groupName FROM Question q JOIN GroupQuestion g ON q.groupId = g.id WHERE q.id = :id")
    Optional<Object[]> findQuestionWithGroupNameById(Integer id);
    @Query("SELECT q, g.name FROM Question q JOIN GroupQuestion g ON q.groupId = g.id WHERE q.groupId = :groupId")
    List<Object[]> findQuestionsByGroupId(@Param("groupId") Integer groupId);
    @Query("SELECT q, g.name FROM Question q JOIN GroupQuestion g ON q.groupId = g.id WHERE q.failingScore = true")
    List<Object[]> findCriticalQuestions();}
