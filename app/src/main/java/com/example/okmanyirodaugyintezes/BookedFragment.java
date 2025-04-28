package com.example.okmanyirodaugyintezes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookedFragment extends Fragment {
    // CONSTS
    private static final String LOG_TAG = BookedFragment.class.getName();
    private static final String ARG_USER = "user";

    // GLOBAL VARIABLES
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private List<BookingDetails> bookings;
    private BookingAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseUser user;
    RecyclerView recyclerView;
    private UserDetails userDetails;

    public BookedFragment() {
        super(R.layout.fragment_booked);
    }

    // GET ARGUMENTS FROM ACTIVITY
    public static BookedFragment newInstance(UserDetails userDetails) {
        BookedFragment fragment = new BookedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, userDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDetails = (UserDetails) getArguments().getSerializable(ARG_USER);
        }
    }

    // MAKING CHANGES ON UI
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init
        emptyTextView = view.findViewById(R.id.emptyTextView);
        progressBar = view.findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        bookings = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookingAdapter(bookings, this::deleteBooking);
        recyclerView.setAdapter(adapter);

        fetchUserBookings();
    }

    // FETCH USER'S BOOKINGS FROM FIRESTORE BASED ON EMAIL
    private void fetchUserBookings(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("bookings")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String task = doc.getString("task");
                        String date = doc.getString("date");
                        String time = doc.getString("time");
                        String office_id = doc.getString("office_id");

                        fetchOffice(office_id, officeDetails -> {
                            BookingDetails booking = new BookingDetails(task, date, time, officeDetails);
                            bookings.add(booking);
                            adapter.notifyItemInserted(bookings.size() - 1);

                            emptyTextView.setVisibility(View.GONE);
                            Log.d(LOG_TAG, "Időpontok betöltése sikeres");
                        });
                    }
                    progressBar.setVisibility(View.GONE);

                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d(LOG_TAG, "Nem sikerült betölteni a foglalásokat");
                });
    }

    // FETCH OFFICE DETAILS
    public interface OfficeFetchCallback { void onOfficeFetched(OfficeDetails officeDetails);}
    private void fetchOffice(String office_id, OfficeFetchCallback callback){
        db.collection("offices").document(office_id).get().addOnSuccessListener(officeDoc -> {
            String office = officeDoc.getString("office");
            String address = officeDoc.getString("address");
            OfficeDetails officeDetails = new OfficeDetails(office_id, office, address);
            callback.onOfficeFetched(officeDetails);
        });
    }

    // DELETE BOOKING
    private void deleteBooking(BookingDetails booking, int position) {
        db.collection("bookings")
                .whereEqualTo("email", user.getEmail())
                .whereEqualTo("task", booking.getTask())
                .whereEqualTo("date", booking.getDate())
                .whereEqualTo("time", booking.getTime())
                .whereEqualTo("office_id", booking.getOffice().getOfficeID())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        db.collection("bookings").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> {
                                    View viewToAnimate = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position)).itemView;
                                    viewToAnimate.animate()
                                            .translationX(viewToAnimate.getWidth())
                                            .alpha(0)
                                            .setDuration(300)
                                            .withEndAction(() -> {
                                                if(position >= 0 && position < bookings.size()){
                                                    bookings.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                    if (bookings.isEmpty()) {
                                                        emptyTextView.setVisibility(View.VISIBLE);
                                                    }
                                                    Log.d(LOG_TAG, "Foglalás sikeresen törölve");
                                                }
                                            })
                                            .start();
                                })
                                .addOnFailureListener(e -> Log.d(LOG_TAG, "Foglalás törlése sikertelen"));
                    }
                })
                .addOnFailureListener(e -> Log.d(LOG_TAG, "Nem találtuk a törölni kívánt foglalást"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booked, container, false);
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