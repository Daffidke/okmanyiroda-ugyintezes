package com.example.okmanyirodaugyintezes;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReservationActivity extends AppCompatActivity {

    // CONSTS
    private static final String LOG_TAG = ReservationActivity.class.getName();

    // GLOBAL VARIABLES
    private TextView fullNameTextView;
    private FirebaseUser user;
    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.fullNameTextView = findViewById(R.id.fullNameTextView);

        // VALIDATING USER
        Auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            fullNameTextView.setText(user.getEmail());
            Log.d(LOG_TAG, "Sikeres bejelentkezés");
        } else {
            Log.d(LOG_TAG, "Sikertelen bejelentkezés");
            finish();
        }

        // SHAKE ANIMATION ON LOAD
        ImageView calendarImageView = findViewById(R.id.calendar);
        ObjectAnimator animator = ObjectAnimator.ofFloat(calendarImageView, "rotation", 0f, 10f, -10f, 5f, -5f, 0f);
        animator.setDuration(1200);
        animator.start();
    }


    // LOG OUT
    public void signOut(View view) {
        try {
            Auth.signOut();
            Log.d(LOG_TAG, "Felhasználó sikeresen kijelentkezett.");

            startActivity(new Intent(ReservationActivity.this, MainActivity.class));
            finish();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Sikertelen kijelentkezés: " + e.getMessage());
        }
    }
}