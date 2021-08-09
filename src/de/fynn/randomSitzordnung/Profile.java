package de.fynn.randomSitzordnung;

import java.util.List;

public class Profile {

    private String name;
    private List<Student> students;
    private String room = "";
    private String courseNumber = "";

    public Profile(String name, List<Student> students){
        this.name = name;
        this.students = students;
    }

    public void setRoom(String room){
        this.room = room;
    }

    public void setCourseNumber(String courseNumber){
        this.courseNumber = courseNumber;
    }

    public String getName() {
        return name;
    }

    public List<Student> getStudents() {
        return students;
    }

    public String getRoom() {
        return room;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

}
