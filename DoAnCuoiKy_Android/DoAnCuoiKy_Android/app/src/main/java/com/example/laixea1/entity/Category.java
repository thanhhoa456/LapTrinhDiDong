package com.example.laixea1.entity;

public class Category {
    private int id; // Để lưu groupId
    private String title;
    private String description;
    private int completed;
    private int total;
    private int iconResId;

    public Category(int id, String title, String description, int completed, int total, int iconResId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.total = total;
        this.iconResId = iconResId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getCompleted() {
        return completed;
    }

    public int getTotal() {
        return total;
    }

    public int getIconResId() {
        return iconResId;
    }
}