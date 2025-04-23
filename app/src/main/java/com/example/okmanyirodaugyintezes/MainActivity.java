package com.example.okmanyirodaugyintezes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // CONSTS
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = Objects.requireNonNull(MainActivity.class.getPackage()).toString();
    private static final int SECRET_KEY = 76;


    // GLOBAL VARIABLES
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SharedPreferences preferences;
    private FirebaseAuth Auth;
    private ProgressBar loginProgressBar;


    // METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // make the status bar the same color as the design
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getThemeColor(this, androidx.appcompat.R.attr.colorPrimary));

        // init
        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        this.emailEditText = findViewById(R.id.emailEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
        this.loginProgressBar = findViewById(R.id.loginProgressBar);
        this.loginButton = findViewById(R.id.loginButton);

        // prepare for authentication
        Auth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // validity check
        boolean checks = true;
        if (email.isEmpty()) {
            showErrorWithFadeIn(emailEditText);
            Log.e(LOG_TAG, "Kitöltetlen e-mail mező!");
            checks = false;
        }
        if (password.isEmpty()) {
            showErrorWithFadeIn(passwordEditText);
            Log.e(LOG_TAG, "Kitöltetlen jelszó mező!");
            checks = false;
        }

        // LOG IN WITH FIREBASE
        if (checks) {
            loginProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                loginProgressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres bejelentkezés");
                    Toast.makeText(MainActivity.this, "Sikeres bejelentkezés", Toast.LENGTH_LONG).show();
                    goToMainPage();
                } else {
                    loginProgressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    Log.e(LOG_TAG, "Sikertelen bejelentkezés");
                    Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // EditText Invalid Input Fade-In Animation
    private void showErrorWithFadeIn(EditText editText) {
        Drawable errorIcon = ContextCompat.getDrawable(this, R.drawable.error_icon);
        if (errorIcon != null) {
            errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
        }
        editText.setError("Töltsd ki ezt a mezőt", errorIcon);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        editText.startAnimation(fadeIn);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    private void goToMainPage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    // Getting theme color
    public static int getThemeColor(Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attrResId, typedValue, true);
        return typedValue.data;
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

        // pass the saved variables to register activity
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", emailEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        editor.apply();

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