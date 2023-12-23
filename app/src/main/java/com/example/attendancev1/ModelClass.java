package com.example.attendancev1;

 public class ModelClass {
    private String txtProgramme;
    private String txtCourse;
    private String txtLevel;
    private String txtProgress;

    ModelClass(String txtProgramme, String txtCourse, String txtLevel, String txtProgress){
        this.txtProgramme = txtProgramme;
        this.txtCourse = txtCourse;
        this.txtLevel = txtLevel;
        this.txtProgress = txtProgress;
    }

    public String getTxtProgramme() {
        return txtProgramme;
    }

    public String getTxtCourse() {
        return txtCourse;
    }

    public String getTxtLevel() {
        return txtLevel;
    }

    public String getTxtProgress() {
        return txtProgress;
    }
}
