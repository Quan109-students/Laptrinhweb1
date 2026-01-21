package com.example.studentmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sinhvien", schema = "dbo")
public class Student {

    @Id
    @Column(name = "[StudentID]")
    private Integer studentID;

    @Column(name = "[FullName]")
    private String fullName;

    @Column(name = "[Gender]")
    private String gender;

    @Column(name = "[BirthDate]")
    private LocalDate birthDate;

    @Column(name = "[Major]")
    private String major;

    @Column(name = "[Address]")
    private String address;

    public Student() {
    }

    public Student(Integer studentID, String fullName, String gender, LocalDate birthDate, String major, String address) {
        this.studentID = studentID;
        this.fullName = fullName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.major = major;
        this.address = address;
    }

    public Integer getStudentID() {
        return studentID;
    }

    public void setStudentID(Integer studentID) {
        this.studentID = studentID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
