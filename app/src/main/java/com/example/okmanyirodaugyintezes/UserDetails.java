package com.example.okmanyirodaugyintezes;

import java.io.Serializable;

public class UserDetails implements Serializable {
    private String FullName, PhoneNumber, PostalAddress;

    public UserDetails(String FullName, String PhoneNumber, String PostalAddress){
        this.FullName = FullName;
        this.PhoneNumber = PhoneNumber;
        this.PostalAddress = PostalAddress;
    }

    public UserDetails(UserDetails u){
        this.FullName = u.getFullName();
        this.PhoneNumber = u.getPhoneNumber();
        this.PostalAddress = u.getPostalAddress();
    }

    // GETTER SETTER
    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPostalAddress() {
        return PostalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        PostalAddress = postalAddress;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }


    // USER INPUT VALIDITY CHECK METHODS
    public boolean isPhoneValid(String phone) {
        return phone != null && !phone.isEmpty() &&
                phone.matches("^[+]?[0-9]{10,15}$");
    }

    public boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() &&
                name.matches("^(?=.*\\s)[A-Za-zÁÉÍÓÖŐÚÜŰáéíóöőúüű\\s'-]{2,50}$");
    }

    public String reformatString(String fullName) {
        StringBuilder result = new StringBuilder();
        String[] splitName = fullName.split(" ");
        for (String name : splitName) {
            StringBuilder sb = new StringBuilder(name);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            result.append(sb).append(" ");
        }
        return result.toString().trim();
    }
}
