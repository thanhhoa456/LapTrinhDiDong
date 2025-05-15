package com.example.laixea1.entity;

public class CategoryTest {
    private int id;
    private String title;
    private String description;
    private String status;
    private int iconResId;
    private String buttonText;

    public CategoryTest(int id, String title, String description, String status, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.iconResId = iconResId;
        this.buttonText = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}