package com._4.APIBangLaiXeA1.dto;

public interface TestQuestionWithDetails {
    int getId();                // ID của TestQuestions
    int getTopicsId();
    int getQuestionId();
    String getQuestion();       // Nội dung câu hỏi
    String getOption1();
    String getOption2();
    String getOption3();
    String getOption4();
    int getAnswer();
    byte[] getImage();
    String getExplainQuestion();
    boolean getFailingScore();
}
