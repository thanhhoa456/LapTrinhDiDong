package com.example.laixea1.dto;


public class GroupQuestionDTO {
    private int id;
    private String name;
    // Add these if the API provides them
    private String description;
    private Integer completed;
    private Integer total;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCompleted() { return completed; }
    public void setCompleted(Integer completed) { this.completed = completed; }
    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }
}