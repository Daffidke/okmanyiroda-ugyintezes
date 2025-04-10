package com.example.okmanyirodaugyintezes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    // CONSTS
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 76;

    // FIELDS
    EditText fullNameEditText;
    EditText usernameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText addressEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;


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

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if (secret_key != 76) {
            finish();
        }

        // INPUT
        this.fullNameEditText = findViewById(R.id.fullNameEditText);
        this.usernameEditText = findViewById(R.id.usernameEditText);
        this.emailEditText = findViewById(R.id.emailEditText);
        this.phoneEditText = findViewById(R.id.phoneNumberEditText);
        this.addressEditText = findViewById(R.id.addressEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
        this.passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);

        // SAVED FIELDS - without confirmation
        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");

        usernameEditText.setText(username);
        passwordEditText.setText(password);

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view){
        String fullName = fullNameEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Log.e(LOG_TAG, "A két megadott jelszó nem egyezik meg.");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + fullName + ", felhasználóneve: " + username + ", e-mail: " + email);
        // TODO: Regisztrációs funkció

        goToMainPage();
    }

    public void cancel(View view){
        finish();
    }

    private void goToMainPage(/* registered user */) {
        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
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