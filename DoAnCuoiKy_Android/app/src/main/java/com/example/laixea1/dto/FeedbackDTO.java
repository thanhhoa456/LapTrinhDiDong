package com.example.laixea1.dto;

public class FeedbackDTO {
    private int id;
    private int userId;
    private int soSao;
    private String noiDung;

    public FeedbackDTO() {}

    public FeedbackDTO(int id, int userId, int soSao, String noiDung) {
        this.id = id;
        this.userId = userId;
        this.soSao = soSao;
        this.noiDung = noiDung;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getSoSao() { return soSao; }
    public void setSoSao(int soSao) { this.soSao = soSao; }
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String noiDung) { this.noiDung = noiDung; }
}