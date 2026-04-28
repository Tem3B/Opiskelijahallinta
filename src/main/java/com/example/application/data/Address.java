package com.example.application.data;

import jakarta.persistence.*;

@Entity
public class Address extends AbstractEntity {

    private String street;
    private String city;
    private String postalCode;
    private String country;
    private String buildingNumber;

    @OneToOne(mappedBy = "address")
    private Students student;

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public Students getStudent() {
        return student;
    }
    public void setStudent(Students student) {
        this.student = student;
    }
    public String getBuildingNumber() {
        return buildingNumber;
    }
    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }
}
