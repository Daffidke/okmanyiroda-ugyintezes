package com.example.okmanyirodaugyintezes;

public class BookingDetails {
    private final String task, date, time;
    private final OfficeDetails office;

    public BookingDetails(String task, String date, String time, OfficeDetails office){
        this.task = task;
        this.date = date;
        this.time = time;
        this.office = office;
    }

    // GETTERS SETTERS
    public OfficeDetails getOffice() {
        return office;
    }

    public String getTask() {
        return task;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
