package com.airline.model;

import java.time.LocalDate;

public class Customer {
    private int custCode;
    private LocalDate travelDate;
    private String custName;
    private String fatherName;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String telephone;
    private String profession;
    private String security;
    private String concession;

    // Default constructor
    public Customer() {}

    // Full constructor
    public Customer(int custCode, LocalDate travelDate, String custName, String fatherName,
                    String gender, LocalDate dateOfBirth, String address, String telephone,
                    String profession, String security, String concession) {
        this.custCode = custCode;
        this.travelDate = travelDate;
        this.custName = custName;
        this.fatherName = fatherName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.telephone = telephone;
        this.profession = profession;
        this.security = security;
        this.concession = concession;
    }

    // Simplified constructor for table data (FIXED)
    public Customer(int custCode, String custName, String telephone, String gender) {
        this.custCode = custCode;
        this.custName = custName;
        this.telephone = telephone;
        this.gender = gender;
        // Set default values for other fields
        this.travelDate = LocalDate.now();
        this.fatherName = "";
        this.dateOfBirth = LocalDate.now();
        this.address = "";
        this.profession = "";
        this.security = "";
        this.concession = "";
    }

    // Getters and Setters
    public int getCustCode() {
        return custCode;
    }

    public void setCustCode(int custCode) {
        this.custCode = custCode;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getConcession() {
        return concession;
    }

    public void setConcession(String concession) {
        this.concession = concession;
    }
}