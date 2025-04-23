package com.example.okmanyirodaugyintezes;

import java.io.Serializable;

public class UserDetails implements Serializable {
    private String FullName;
    private String PhoneNumber;
    private String PostalAddress;

    public UserDetails(String FullName, String PhoneNumber, String PostalAddress){
        this.FullName = FullName;
        this.PhoneNumber = PhoneNumber;
        this.PostalAddress = PostalAddress;
    }

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
}
