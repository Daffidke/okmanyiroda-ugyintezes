package com.example.okmanyirodaugyintezes;

public class OfficeDetails {
    private final String OfficeID, Name, Address;

    public OfficeDetails(String OfficeID, String Name, String Address) {
        this.OfficeID = OfficeID;
        this.Name = Name;
        this.Address = Address;
    }

    public String getOfficeID() {
        return OfficeID;
    }
    public String getAddress() {
        return Address;
    }

    public String getName() {
        return Name;
    }
}