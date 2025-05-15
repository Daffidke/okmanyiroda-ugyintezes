package com.example.okmanyirodaugyintezes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class UserEditFragment extends Fragment {
    // CONSTS
    private static final String LOG_TAG = UserEditFragment.class.getName();

    // GLOBAL VARIABLES
    private EditText fullNameEditText, phoneNumberEditText, postalAddressEditText;
    private UserDetails userDetails;
    private UserViewModel userViewModel;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Button updateButton;
    private ProgressBar updateProgressBar;

    public UserEditFragment() {
        super(R.layout.fragment_user_edit);
    }

    // GET ARGUMENTS FROM ACTIVITY
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    // MAKING CHANGES ON UI
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        postalAddressEditText = view.findViewById(R.id.postalAddressEditText);
        updateProgressBar = view.findViewById(R.id.updateProgressBar);
        updateButton = view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> update());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // PASS USER DETAILS ON LOAD
        userViewModel.getUserDetails().observe(getViewLifecycleOwner(), userDetails -> {
            if (userDetails != null) {
                this.userDetails = new UserDetails(userDetails);
                fullNameEditText.setText(userDetails.getFullName());
                phoneNumberEditText.setText(userDetails.getPhoneNumber());
                postalAddressEditText.setText(userDetails.getPostalAddress());
            }
        });
    }

    // UPDATING USER DETAILS
    private void update() {
        String fullName = fullNameEditText.getText().toString().trim().toLowerCase();
        String phone = phoneNumberEditText.getText().toString().trim();
        String address = postalAddressEditText.getText().toString().trim().toLowerCase();

        // validity check
        boolean check = true;
        if (!userDetails.isNameValid(fullName)) {
            showErrorWithFadeIn(fullNameEditText, "Adja meg helyesen a teljes nevét (pl. Nagy Sándor)");
            Log.e(LOG_TAG, "Hibás / hiányzó név");
            check = false;
        }
        if (!userDetails.isPhoneValid(phone)) {
            showErrorWithFadeIn(phoneNumberEditText, "Adja meg helyesen a telefonszámát (pl. 06207365539)");
            Log.e(LOG_TAG, "Hibás / hiányzó telefonszám");
            check = false;
        }
        if (address.isEmpty()) {
            showErrorWithFadeIn(postalAddressEditText, "Adja meg a lakcímét");
            Log.e(LOG_TAG, "Hiányzó lakcím");
            check = false;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.d(LOG_TAG, "Nincs bejelentkezve a felhasználó.");
            return;
        }
        if(check){
            updateProgressBar.setVisibility(View.VISIBLE);
            updateProgressBar.setEnabled(false);

            String userId = user.getUid();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("fullName", userDetails.reformatString(fullName));
            userMap.put("phone", phone);
            userMap.put("address", userDetails.reformatString(address));

            firestore.collection("users").document(userId)
                    .update(userMap)
                    .addOnSuccessListener(aVoid -> {
                        updateProgressBar.setVisibility(View.GONE);
                        updateButton.setEnabled(true);

                        userDetails.setFullName(userDetails.reformatString(fullName));
                        userDetails.setPhoneNumber(phone);
                        userDetails.setPostalAddress(userDetails.reformatString(address));

                        Log.d(LOG_TAG, "Felhasználó sikeresen módosítva.");
                        Toast.makeText(getContext(), "Sikeres módosítás!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        updateProgressBar.setVisibility(View.GONE);
                        updateButton.setEnabled(true);
                        Log.d(LOG_TAG, "Sikertelen módosítás" + e.getMessage());
                        Toast.makeText(getContext(), "Sikertelen módosítás", Toast.LENGTH_SHORT).show();
                    });
        }
        else{
            Log.d(LOG_TAG, "Rossz input");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_edit, container, false);
    }

    // EditText Invalid Input Fade-In Animation
    private void showErrorWithFadeIn(EditText editText, String errorMsg) {
        Drawable errorIcon = ContextCompat.getDrawable(requireContext(), R.drawable.error_icon);
        if (errorIcon != null) {
            errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
        }
        editText.setError(errorMsg, errorIcon);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        editText.startAnimation(fadeIn);
    }
}