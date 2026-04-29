package com.example.application.data;

/**
 * Search criteria
 */
public class TeacherSearchCriteria {
    private String firstName;
    private String lastName;
    private String email;
    private String courseName;

    public TeacherSearchCriteria() {
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }


    /**
     * Check search criteria
     */
    public boolean hasCriteria() {
        return firstName != null || lastName != null || email != null ||
               courseName != null;
    }
}

