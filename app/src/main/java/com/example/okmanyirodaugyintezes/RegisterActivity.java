package com.example.okmanyirodaugyintezes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.SharedPreferences;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    // CONSTS
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(RegisterActivity.class.getPackage()).toString();

    // GLOBAL VARIABLES
    private EditText fullNameEditText, emailEditText, phoneEditText, addressEditText, passwordEditText, passwordConfirmEditText;
    private Button registerButton;
    private ProgressBar registerProgressBar;
    private FirebaseAuth Auth;
    private FirebaseFirestore db;

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

        // protection for non-intended usage
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 76) {
            finish();
        }

        // prepare for authentication
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // handling inputs
        this.fullNameEditText = findViewById(R.id.fullNameEditText);
        this.emailEditText = findViewById(R.id.emailEditText);
        this.phoneEditText = findViewById(R.id.phoneNumberEditText);
        this.addressEditText = findViewById(R.id.addressEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
        this.passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);
        this.registerProgressBar = findViewById(R.id.registerProgressBar);
        this.registerButton = findViewById(R.id.registerButton);

        // SAVED FIELDS - without password confirmation
        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailEditText.setText(email);
        passwordEditText.setText(password);

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {

        String email = emailEditText.getText().toString().trim().toLowerCase();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirm = passwordConfirmEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim().toLowerCase();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim().toLowerCase();

        // validity checks
        boolean check = true;
        if (!password.equals(passwordConfirm)) {
            showErrorWithFadeIn(passwordEditText, "A két megadott jelszó nem egyezik meg");
            showErrorWithFadeIn(passwordConfirmEditText, "A két megadott jelszó nem egyezik meg");
            Log.e(LOG_TAG, "A két jelszó nem egyezik meg.");
            check = false;
        }
        if (!isNameValid(fullName)) {
            showErrorWithFadeIn(fullNameEditText, "Adja meg helyesen a teljes nevét (pl. Nagy Sándor)");
            Log.e(LOG_TAG, "Hibás / hiányzó név");
            check = false;
        }
        if (!isEmailValid(email)) {
            showErrorWithFadeIn(emailEditText, "Adja meg helyesen az e-mail címét (pl. example@example.com)");
            Log.e(LOG_TAG, "Hibás / hiányzó email");
            check = false;
        }
        if (!isPhoneValid(phone)) {
            showErrorWithFadeIn(phoneEditText, "Adja meg helyesen a telefonszámát (pl. 06207365539)");
            Log.e(LOG_TAG, "Hibás / hiányzó telefonszám");
            check = false;
        }
        if (address.isEmpty()) {
            showErrorWithFadeIn(addressEditText, "Adja meg a lakcímét");
            Log.e(LOG_TAG, "Hiányzó lakcím");
            check = false;
        }
        if (passwordConfirm.isEmpty()) {
            showErrorWithFadeIn(passwordConfirmEditText, "Erősítse meg a jelszavát!");
            Log.e(LOG_TAG, "Hiányzó jelszó megerősítés");
            check = false;
        }
        if (password.isEmpty() || password.length() < 6) {
            showErrorWithFadeIn(passwordEditText, "Adjon meg egy 6 karakternél hosszabb jelszót");
            Log.e(LOG_TAG, "Hiányzó / helytelen jelszó");
            check = false;
        }

        // REGISTERING TO FIREBASE AND FIRESTORE
        if (check) {
            registerProgressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);

            Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = Auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fullName", reformatString(fullName));
                        userData.put("address", reformatString(address));
                        userData.put("phone", phone);

                        db.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    registerProgressBar.setVisibility(View.GONE);
                                    registerButton.setEnabled(true);
                                    Log.d(LOG_TAG, "Felhasználó sikeresen létrehozva.");
                                    Toast.makeText(RegisterActivity.this, "Sikeres regisztráció", Toast.LENGTH_SHORT).show();
                                    goToMainPage();
                                })
                                .addOnFailureListener(e -> {
                                    registerProgressBar.setVisibility(View.GONE);
                                    registerButton.setEnabled(true);
                                    Log.e(LOG_TAG, "Felhasználó létrehozása sikertelen.", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció", Toast.LENGTH_SHORT).show();
                                    user.delete();
                                });
                    }
                } else {
                    registerProgressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);
                    Log.d(LOG_TAG, "A felhasználó létrehozása sikertelen: ", task.getException());
                    Toast.makeText(RegisterActivity.this, "Sikertelen regisztráció", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void cancel(View view) {
        finish();
    }

    private void goToMainPage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // INPUT VALIDITY CHECK METHODS
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

    String reformatString(String fullName) {
        StringBuilder result = new StringBuilder();
        String[] splitName = fullName.split(" ");
        for (String name : splitName) {
            StringBuilder sb = new StringBuilder(name);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            result.append(sb).append(" ");
        }
        return result.toString().trim();
    }

    // EditText Invalid Input Fade-In Animation
    private void showErrorWithFadeIn(EditText editText, String errorMsg) {
        Drawable errorIcon = ContextCompat.getDrawable(this, R.drawable.error_icon);
        if (errorIcon != null) {
            errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
        }
        editText.setError(errorMsg, errorIcon);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        editText.startAnimation(fadeIn);
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