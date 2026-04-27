package com.example.application.data;

import jakarta.persistence.*;

@Entity
public class Courses extends AbstractEntity {

    private String name;


    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teachers teacher;


    private String about;
    private Integer difficulty;
    private Integer studentCount;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Teachers getTeacher() {
        return teacher;
    }

    public void setTeacher(Teachers teacher) {
        this.teacher = teacher;
    }

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }
    public Integer getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }
    public Integer getStudentCount() {
        return studentCount;
    }
    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

}
