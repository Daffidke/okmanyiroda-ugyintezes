package com.example.okmanyirodaugyintezes;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ReservationFragment extends Fragment {
    // CONSTS
    private static final String LOG_TAG = ReservationFragment.class.getName();
    private static final String ARG_USER = "user";

    // GLOBAL VARIABLES
    private UserDetails userDetails;

    public ReservationFragment() {
        super(R.layout.fragment_reservation);
    }

    // GET ARGUMENTS FROM ACTIVITY
    public static ReservationFragment newInstance(UserDetails user) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDetails = (UserDetails) getArguments().getSerializable(ARG_USER);
            Log.d(LOG_TAG, "User sikeresen lekérve");
        }
    }

    // MAKING CHANGES ON UI
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        Button button4 = view.findViewById(R.id.button4);
        Button button5 = view.findViewById(R.id.button5);
        TextView fullNameTextView = view.findViewById(R.id.fullNameTextView);

        // SHOW FULL NAME ON LOAD
        if (userDetails != null) {fullNameTextView.setText(userDetails.getFullName());}

        // SHAKE ANIMATION ON LOAD
        ImageView calendarImageView = view.findViewById(R.id.calendar);
        ObjectAnimator animator = ObjectAnimator.ofFloat(calendarImageView, "rotation", 0f, 10f, -10f, 5f, -5f, 0f);
        animator.setDuration(1200);
        animator.start();

        // PASS THE TASK TO CHECKOUT ACTIVITY
        View.OnClickListener buttonClickListener = v -> {
            if (v instanceof Button) {
                String selectedText = ((Button) v).getText().toString();

                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra("task", selectedText);
                startActivity(intent);
            }
        };

        button1.setOnClickListener(buttonClickListener);
        button2.setOnClickListener(buttonClickListener);
        button3.setOnClickListener(buttonClickListener);
        button4.setOnClickListener(buttonClickListener);
        button5.setOnClickListener(buttonClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_USER, userDetails); // Save user details if needed
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            userDetails = (UserDetails) savedInstanceState.getSerializable(ARG_USER); // Restore it
        }
    }
}