package com.example.attendancev1;

public class Progress_Info {
    private String Course;
    private String Progress;

    public Progress_Info(){}

    public Progress_Info(String course, String progress) {
        Course = course;
        Progress = progress;
    }

    public String getCourse() {
        return Course;
    }

    public void setCourse(String course) {
        Course = course;
    }

    public String getProgress() {
        return Progress;
    }

    public void setProgress(String progress) {
        Progress = progress;
    }
}
