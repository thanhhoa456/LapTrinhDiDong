package com._4.APIBangLaiXeA1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private int id;
    private int groupId;
    private String groupName;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int answer;
    private byte[] image;
    private String explainQuestion;
    private boolean failingScore;
}