package com.example.laixea1.dto;

public class TestsDTO {
    private int id;
    private String name;

    // Constructor
    public TestsDTO() {}

    public TestsDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}