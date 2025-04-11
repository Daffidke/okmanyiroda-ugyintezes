package com.example.okmanyirodaugyintezes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    // CONSTS
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();

    // GLOBAL VARIABLES
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private FirebaseAuth Auth;

    // METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // protection for not intended usage
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 76) {
            finish();
        }

        // prepare for authentication
        Auth = FirebaseAuth.getInstance();

        // handling inputs
        this.fullNameEditText = findViewById(R.id.fullNameEditText);
        this.emailEditText = findViewById(R.id.emailEditText);
        this.phoneEditText = findViewById(R.id.phoneNumberEditText);
        this.addressEditText = findViewById(R.id.addressEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
        this.passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);

        // SAVED FIELDS - without password confirmation
        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailEditText.setText(email);
        passwordEditText.setText(password);

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        String fullName = fullNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();
        // TODO: save user details to database

        // password check
        if (!password.equals(passwordConfirm)) {
            passwordEditText.setError("A két megadott jelszó nem egyezik meg");
            passwordConfirmEditText.setError("A két megadott jelszó nem egyezik meg");
            Log.e(LOG_TAG, "A két megadott jelszó nem egyezik meg.");
            return;
        }

        // validity checks
        boolean check = true;
        if (!isNameValid(fullName)) {
            fullNameEditText.setError("Adja meg a teljes nevét");
            check = false;
        }
        if (!isEmailValid(email)) {
            emailEditText.setError("Adjon meg egy létező E-mail címet");
            check = false;
        }
        if (!isPhoneValid(phone)) {
            phoneEditText.setError("Adjon meg egy létező telefonszámot");
            check = false;
        }
        if (address.isEmpty()) {
            addressEditText.setError("Adjon meg egy létező lakcímet");
            check = false;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("Adjon meg egy 6 karakternél hosszabb jelszót");
            check = false;
        }

        // REGISTERING TO FIREBASE
        if (check) {
            Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "Felhasználó sikeresen létrehozva.");
                        goToMainPage();
                    } else {
                        Log.d(LOG_TAG, "A felhasználó létrehozása sikertelen: ", task.getException());
                        Toast.makeText(RegisterActivity.this, "A felhasználó létrehozása sikertelen: ", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    public void cancel(View view) {
        finish();
    }

    private void goToMainPage(/* registered user */) {
        Intent intent = new Intent(this, ReservationActivity.class);
        startActivity(intent);
    }

    // VALIDITY CHECK METHODS
    boolean isEmailValid(String email) {
        return email != null && !email.isEmpty() &&
                email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    boolean isPhoneValid(String phone) {
        return phone != null && !phone.isEmpty() &&
                phone.matches("^[+]?[0-9]{10,15}$");
    }

    boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() &&
                name.matches("^(?=.*\\s)[A-Za-zÁÉÍÓÖŐÚÜŰáéíóöőúüű\\s'-]{2,50}$");
    }

    // OVERRIDES

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}