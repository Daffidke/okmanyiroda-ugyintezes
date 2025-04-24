package com.example.okmanyirodaugyintezes;

import androidx.annotation.NonNull;

public class OfficeDetails {
    private String OfficeID;
    private String Name;
    private String Address;

    public OfficeDetails(String OfficeID, String Name, String Address) {
        this.OfficeID = OfficeID;
        this.Name = Name;
        this.Address = Address;
    }

    @NonNull
    @Override
    public String toString(){
        return Name;
    }

    public String getOfficeID() {
        return OfficeID;
    }

    public void setOfficeID(String officeID) {
        OfficeID = officeID;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
