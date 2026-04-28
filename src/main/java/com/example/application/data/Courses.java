package com.example.application.data;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Courses extends AbstractEntity {

    private String name;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teachers teacher;

    @ManyToMany(mappedBy = "courses")
    private Set<Students> students = new HashSet<>();

    private String about;
    private Integer difficulty;

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

    public Set<Students> getStudents() {
        return students;
    }
    public void setStudents(Set<Students> students) {
        this.students = students;
    }

}
