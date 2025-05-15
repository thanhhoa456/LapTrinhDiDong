package com.example.laixea1.dto;

public class TestQuestionsDTO {
    private int id;              // ID của TestQuestions
    private int topicsId;        // ID của topic (đề thi)
    private int questionId;      // ID của câu hỏi
    private String question;     // Nội dung câu hỏi
    private String option1;      // Lựa chọn 1
    private String option2;      // Lựa chọn 2
    private String option3;      // Lựa chọn 3
    private String option4;      // Lựa chọn 4
    private int answer;          // Đáp án đúng (1-4)
    private byte[] image;        // Hình ảnh (nếu có)
    private String explainQuestion; // Giải thích câu hỏi
    private boolean failingScore;   // Là câu điểm liệt hay không

    // Constructor
    public TestQuestionsDTO() {}

    public TestQuestionsDTO(int id, int topicsId, int questionId, String question,
                            String option1, String option2, String option3, String option4,
                            int answer, byte[] image, String explainQuestion, boolean failingScore) {
        this.id = id;
        this.topicsId = topicsId;
        this.questionId = questionId;
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

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicsId() {
        return topicsId;
    }

    public void setTopicsId(int topicsId) {
        this.topicsId = topicsId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getExplainQuestion() {
        return explainQuestion;
    }

    public void setExplainQuestion(String explainQuestion) {
        this.explainQuestion = explainQuestion;
    }

    public boolean getFailingScore() {
        return failingScore;
    }

    public void setFailingScore(boolean failingScore) {
        this.failingScore = failingScore;
    }
}