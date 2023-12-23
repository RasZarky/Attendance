package com.example.attendancev1;

public class Class_Info {
    private String Programme;
    private String Course;
    private String Level;

    public Class_Info(){}

    public Class_Info(String programme, String course, String level) {
        Programme = programme;
        Course = course;
        Level = level;
    }

    public String getProgramme() {
        return Programme;
    }

    public void setProgramme(String programme) {
        Programme = programme;
    }

    public String getCourse() {
        return Course;
    }

    public void setCourse(String course) {
        Course = course;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }
}
