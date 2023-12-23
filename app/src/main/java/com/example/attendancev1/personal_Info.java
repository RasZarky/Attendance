package com.example.attendancev1;

public class personal_Info {
    private String FirstName;
    private String LastName;
    private String Password;
    private String IndexNumber;
    private String Programme;
    private String Position;
    private String Level;

    public personal_Info(){}
    public personal_Info(String firstName, String lastName, String password, String indexNumber, String programme, String position, String level) {
        FirstName = firstName;
        LastName = lastName;
        Password = password;
        IndexNumber = indexNumber;
        Programme = programme;
        Position = position;
        Level = level;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIndexNumber() {
        return IndexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        IndexNumber = indexNumber;
    }

    public String getProgramme() {
        return Programme;
    }

    public void setProgramme(String programme) {
        Programme = programme;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }
}
