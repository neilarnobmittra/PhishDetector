package com.example.phishdetector.model;

public class ScanHistory {
    private String text;
    private String risk;
    private int score;
    private String explanation;
    private String action;
    private long timestamp;

    public ScanHistory() {
    }

    public ScanHistory(String text, String risk, int score, String explanation, String action, long timestamp) {
        this.text = text;
        this.risk = risk;
        this.score = score;
        this.explanation = explanation;
        this.action = action;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
