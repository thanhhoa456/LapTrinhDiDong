package com.example.laixea1.dto;


public class QuestionStatsDTO {
    private int groupId;
    private long totalQuestions;
    private long criticalInGroup;
    private long totalCritical;

    public QuestionStatsDTO(int groupId, long totalQuestions, long criticalInGroup, long totalCritical) {
        this.groupId = groupId;
        this.totalQuestions = totalQuestions;
        this.criticalInGroup = criticalInGroup;
        this.totalCritical = totalCritical;
    }

    public int getGroupId() {
        return groupId;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public long getCriticalInGroup() {
        return criticalInGroup;
    }

    public long getTotalCritical() {
        return totalCritical;
    }
}