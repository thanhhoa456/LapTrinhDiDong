package com.example.laixea1.entity;

import java.io.Serializable;

public class Answer implements Serializable {
    private String text;
    private boolean isCorrect;
    private boolean isSelected; // Thêm biến để lưu trạng thái đã chọn

    public Answer(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.isSelected = false; // Mặc định chưa được chọn
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}