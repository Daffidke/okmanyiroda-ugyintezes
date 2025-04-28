package com.example.okmanyirodaugyintezes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private List<String> bookingsThisDay, timeSlots;
    private TextView taskTextView, officeAddressTextView, availableTextView;
    private EditText datePicker;
    private Spinner timeSpinner, officeSpinner;
    private String officeId, selectedDate, selectedTime;
    private FirebaseFirestore db;
    private FirebaseUser user;

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

        // INIT
        Intent intent = getIntent();
        String task = intent.getStringExtra("task");

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        taskTextView = findViewById(R.id.taskTextView);
        datePicker = findViewById(R.id.datePicker);
        timeSpinner = findViewById(R.id.timeSpinner);
        officeSpinner = findViewById(R.id.officeSpinner);
        officeAddressTextView = findViewById(R.id.officeAddressTextView);
        availableTextView = findViewById(R.id.availableTextView);
        user = auth.getCurrentUser();
        taskTextView.setText(task);
        fetchOffices();

        // DEFAULT STATE - TODAY
        Calendar calendar_today = Calendar.getInstance();
        selectedDate = formatDate(calendar_today.get(Calendar.YEAR), calendar_today.get(Calendar.MONTH), calendar_today.get(Calendar.DAY_OF_MONTH));
        datePicker.setText(selectedDate);
        fetchBookings(selectedDate);

        // CHANGING DATE
        datePicker.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CheckoutActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                datePicker.setText(selectedDate);
                fetchBookings(selectedDate);
            }, year, month, day
            );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        // CHANGING OFFICE
        officeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // update bookings in the selected office
                OfficeDetails selectedOffice = (OfficeDetails) parent.getItemAtPosition(position);
                officeId = selectedOffice.getOfficeID();
                fetchBookings(selectedDate);

                // update office address
                String updateOfficeAddressTextView = "Cím: " + selectedOffice.getAddress();
                officeAddressTextView.setText(updateOfficeAddressTextView);

                // update availability in the selected office for selected time
                String updateAvailableTextView = "Férőhelyek száma: " + availableSpace(selectedTime) + " fő.";
                availableTextView.setText(updateAvailableTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // CHANGING TIME
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTime = (String) parent.getItemAtPosition(position);
                String updateAvailableTextView = "Férőhelyek száma: " + availableSpace(selectedTime) + " fő.";
                availableTextView.setText(updateAvailableTextView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // INSERT RESERVATION TO FIRESTORE
    public void insertReservation(View view) {
        String date = selectedDate;
        String time = selectedTime;
        String email = user.getEmail();
        String task = taskTextView.getText().toString();

        Map<String, Object> booking = new HashMap<>();
        booking.put("date", date);
        booking.put("time", time);
        booking.put("email", email);
        booking.put("task", task);
        booking.put("office_id", officeId);

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Log.d(LOG_TAG, "Le sikerült foglalni az időpontot");
                    Toast.makeText(this, "Sikeres foglalás", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.d(LOG_TAG, "Nem sikerült lefoglalni az időpontot: " + e.getMessage());
                    Toast.makeText(this, "Sikertelen foglalás", Toast.LENGTH_SHORT).show();
                });
    }

    // GET BOOKINGS FROM FIRESTORE ON SELECTED DATE
    private void fetchBookings(String selectedDate) {
        timeSlots = generateTimeSlots();
        bookingsThisDay = new ArrayList<>();

        db.collection("bookings")
                .whereEqualTo("office_id", officeId)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String time = doc.getString("time");
                        bookingsThisDay.add(time);
                    }

                    // SHOW AVAILABLE ONLY
                    List<String> availableSlots = new ArrayList<>();
                    for(String time : timeSlots){
                        if(availableSpace(time) > 0){
                            availableSlots.add(time);
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

    private int availableSpace(String selectedTime) {
        int count = 5;
        for (String time : bookingsThisDay) {
            if (selectedTime.equals(time)) {
                count--;
            }
        }
        return count;
    }

    private void updateTimeSlotSpinner(List<String> slots) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, slots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);
    }

    private void updateOfficeSpinner(List<OfficeDetails> offices) {
        ArrayAdapter<OfficeDetails> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, offices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        officeSpinner.setAdapter(adapter);
    }

    public String formatDate(int year, int month, int day) {
        month++;
        String monthZero = "";
        String dayZero = "";
        if (month < 10) {
            monthZero = "0";
        }
        if (day < 10) {
            dayZero = "0";
        }
        return year + "." + monthZero + month + "." + dayZero + day + ".";
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