package com.ats.screening.dto;

public class ScreeningResultEvent {
    private String name;
    private double score;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}