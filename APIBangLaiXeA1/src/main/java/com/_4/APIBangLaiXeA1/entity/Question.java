package com._4.APIBangLaiXeA1.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "group_id", nullable = false)
    private int groupId;

    @Column(name = "question", nullable = false, columnDefinition = "LONGTEXT")
    private String question;


    @Column(name = "option1", nullable = false, columnDefinition = "LONGTEXT")
    private String option1;

    @Column(name = "option2", nullable = false, columnDefinition = "LONGTEXT")
    private String option2;

    @Column(name = "option3", columnDefinition = "LONGTEXT")
    private String option3;

    @Column(name = "option4", columnDefinition = "LONGTEXT")
    private String option4;

    @Column(name = "answer", nullable = false)
    private int answer;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "explain_question", columnDefinition = "LONGTEXT")
    private String explainQuestion;

    @Column(name = "failing_score", nullable = false)
    private boolean failingScore;

    public String getExplainQuestion() {
        return explainQuestion != null ? explainQuestion.replaceAll("\\\\n", "\n") : null;
    }
}