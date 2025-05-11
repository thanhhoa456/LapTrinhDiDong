package com.example.laixea1.dto;

import java.io.Serializable;

public class QuestionDTO implements Serializable {
    private int id;
    private int groupId;
    private String groupName;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int answer;
    private String image;
    private String explainQuestion;
    private boolean failingScore;

    // Constructor mặc định
    public QuestionDTO() {
    }

    // Constructor đầy đủ
    public QuestionDTO(int id, int groupId, String groupName, String question, String option1, String option2,
                       String option3, String option4, int answer, String image, String explainQuestion, boolean failingScore) {
        this.id = id;
        this.groupId = groupId;
        this.groupName = groupName;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.answer = answer;
        this.image = image;
        this.explainQuestion = explainQuestion;
        this.failingScore = failingScore;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExplainQuestion() {
        return explainQuestion;
    }

    public void setExplainQuestion(String explainQuestion) {
        this.explainQuestion = explainQuestion;
    }

    public boolean isFailingScore() {
        return failingScore;
    }

    public void setFailingScore(boolean failingScore) {
        this.failingScore = failingScore;
    }
}