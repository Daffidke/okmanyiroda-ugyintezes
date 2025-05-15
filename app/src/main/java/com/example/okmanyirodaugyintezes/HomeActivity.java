package com.example.okmanyirodaugyintezes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    // CONSTS
    private static final String LOG_TAG = HomeActivity.class.getName();

    // GLOBAL VARIABLES
    BottomNavigationView bottomNav;
    ProgressBar homeProgessBar;
    private FirebaseAuth Auth;
    private FirebaseFirestore db;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeProgessBar = findViewById(R.id.homeProgressBar);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // VALIDATING USER
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(savedInstanceState == null){
            if (user != null) {
                homeProgessBar.setVisibility(View.VISIBLE);
                fetchUserData(user.getUid());
                Log.d(LOG_TAG, "Sikeres bejelentkezés");
            } else {
                Log.d(LOG_TAG, "Sikertelen bejelentkezés");
                finish();
            }
        }
        else{
            bottomNav.setSelectedItemId(savedInstanceState.getInt("NAV_POSITION"));
        }

        // Observe ViewModel to reload fragments AFTER data is ready
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserDetails().observe(this, userDetails -> {
            if (userDetails != null && bottomNav.getSelectedItemId() == R.id.nav_reservation) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ReservationFragment())
                        .commit();
            }
        });
    }

    // GET USER DATA FROM FIRESTORE
    private void fetchUserData(String userId) {
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userDetails = new UserDetails(
                                documentSnapshot.getString("fullName"),
                                documentSnapshot.getString("phone"),
                                documentSnapshot.getString("address")
                        );
                        userViewModel.setUserDetails(userDetails); // Store in ViewModel for accessibility
                        homeProgessBar.setVisibility(View.GONE);

                        Log.d(LOG_TAG, "Adatok lekérve");

                        // Default fragment
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ReservationFragment())
                                .commit();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Nem sikerült lekérni az adatokat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Nem sikerült lekérni az adatokat: " + e.getMessage());
                });
    }

    // LOG OUT
    public void signOut(View view) {
        try {
            Auth.signOut();
            Log.d(LOG_TAG, "Sikeres kijelentkezés.");
            Toast.makeText(this, "Sikeres kijelentkezés", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Sikertelen kijelentkezés: " + e.getMessage());
        }
    }

    // BOTTOM NAVBAR
    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_reservation) {
            selectedFragment = new ReservationFragment();
        } else if (itemId == R.id.nav_booked) {
            selectedFragment = new BookedFragment();
        } else if (itemId == R.id.nav_useredit) {
            selectedFragment = new UserEditFragment();
        } else {
            return false;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();

        return true;
    };

    // SAVE NAVBAR POSITION AFTER ROTATING SCREEN (LifeCycle Hook)
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("NAV_POSITION", bottomNav.getSelectedItemId());
    }
}