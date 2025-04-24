package com.example.okmanyirodaugyintezes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {
    // CONSTS
    private static final String LOG_TAG = CheckoutActivity.class.getName();

    // GLOBAL VARIABLES
    private TextView taskTextView;
    private EditText datePicker;
    private TextView officeAddressTextView;
    private Spinner timeSpinner;
    private Spinner officeSpinner;
    private String officeId;
    private FirebaseFirestore db;
    private List<String> timeSlots;
    private String selectedDate;
    private int available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String task = intent.getStringExtra("task");

        db = FirebaseFirestore.getInstance();

        taskTextView = findViewById(R.id.taskTextView);
        datePicker = findViewById(R.id.datePicker);
        timeSpinner = findViewById(R.id.timeSpinner);
        officeSpinner = findViewById(R.id.officeSpinner);
        officeAddressTextView = findViewById(R.id.officeAddressTextView);

        taskTextView.setText(task);
        //datePicker.setMinDate(System.currentTimeMillis() - 1000);

        fetchOffices();

        officeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OfficeDetails selectedOffice = (OfficeDetails) parent.getItemAtPosition(position);
                officeId = selectedOffice.getOfficeID();
                fetchAvailableTimeSlots(selectedDate);
                String updateOfficeAddressTextView = "Cím: " + selectedOffice.getAddress();
                officeAddressTextView.setText(updateOfficeAddressTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        datePicker.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CheckoutActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                String monthZero = "";
                String dayZero = "";
                if (selectedMonth + 1 < 10) {
                    monthZero = "0";
                }
                if (selectedDay < 10) {
                    dayZero = "0";
                }
                selectedDate = selectedYear + "." + monthZero + (selectedMonth + 1) + "." + dayZero + selectedDay + ".";
                datePicker.setText(selectedDate);
                fetchAvailableTimeSlots(selectedDate);}, year, month, day
            );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // TODAY
        Calendar calendar = Calendar.getInstance();
        String monthZero = "";
        String dayZero = "";
        if (calendar.get(Calendar.MONTH) + 1 < 10) {
            monthZero = "0";
        }
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            dayZero = "0";
        }
        String month = monthZero + (calendar.get(Calendar.MONTH) + 1);
        String day = dayZero + calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate = calendar.get(Calendar.YEAR) + "." + month + "." + day + ".";
        fetchAvailableTimeSlots(selectedDate);


    }

    // GET BOOKINGS FROM FIRESTORE ON SELECTED DATE
    private void fetchAvailableTimeSlots(String selectedDate) {
        timeSlots = generateTimeSlots();

        db.collection("bookings")
                .whereEqualTo("office_id", officeId)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Integer> bookingCounts = new HashMap<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String time = doc.getString("time");
                        bookingCounts.put(time, bookingCounts.merge(time, 1, Integer::sum));
                    }

                    List<String> availableSlots = new ArrayList<>();
                    for (String time : timeSlots) {
                        available = 5;
                        for (Map.Entry<String, Integer> entry : bookingCounts.entrySet()) {
                            if (entry.getKey().equals(time)) {
                                available--;
                            }
                        }
                        if (available > 0) {
                            availableSlots.add(time + " - " + available + " hely");
                        }
                    }

                    updateTimeSlotSpinner(availableSlots);
                })
                .addOnFailureListener(e -> {
                    Log.d(LOG_TAG, "Nem sikerült betölteni a foglalásokat");
                    Toast.makeText(this, "Nem sikerült betölteni az időpontokat", Toast.LENGTH_SHORT).show();
                });

    }

    private void fetchOffices() {
        db.collection("offices")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<OfficeDetails> offices = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String id = doc.getId();
                        String officeName = doc.getString("office");
                        String address = doc.getString("address");
                        offices.add(new OfficeDetails(id, officeName, address));
                    }

                    updateOfficeSpinner(offices);
                    Log.d(LOG_TAG, "Adatok lekérve");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Nem sikerült lekérni az okmányirodákat ", Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Nem sikerült lekérni az okmányirodákat: " + e.getMessage());
                });
    }

    private void updateTimeSlotSpinner(List<String> slots) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, slots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
    }

    private void updateOfficeSpinner(List<OfficeDetails> offices) {
        ArrayAdapter<OfficeDetails> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, offices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        officeSpinner.setAdapter(adapter);
    }

    private List<String> generateTimeSlots() {
        List<String> slots = new ArrayList<>();
        for (int i = 8; i <= 16; i++) {
            String zero = "";
            if (i < 10) {
                zero = "0";
            }
            slots.add(zero + i + ":00:00");
        }
        return slots;
    }

    public void cancel(View view) {
        finish();
    }
}