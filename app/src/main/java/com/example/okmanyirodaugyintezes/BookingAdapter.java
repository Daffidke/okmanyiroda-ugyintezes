package com.example.okmanyirodaugyintezes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder>{
    private final List<BookingDetails> bookingList;
    private final OnBookingDeleteListener deleteListener;

    public BookingAdapter(List<BookingDetails> bookingList, OnBookingDeleteListener deleteListener) {
        this.bookingList = bookingList;
        this.deleteListener = deleteListener;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, timeTextView, officeTextView, addressTextView, taskTextView;
        ImageButton deleteButton;

        BookingViewHolder(View view) {
            super(view);

            taskTextView = view.findViewById(R.id.taskTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            officeTextView = view.findViewById(R.id.officeTextView);
            addressTextView = view.findViewById(R.id.addressTextView);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingDetails booking = bookingList.get(position);
        holder.taskTextView.setText(booking.getTask());
        holder.dateTextView.setText(booking.getDate());
        holder.timeTextView.setText(booking.getTime());
        holder.officeTextView.setText(booking.getOffice().getName());
        holder.addressTextView.setText(booking.getOffice().getAddress());

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (deleteListener != null && currentPosition != RecyclerView.NO_POSITION) {
                deleteListener.onBookingDelete(booking, currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public interface OnBookingDeleteListener {
        void onBookingDelete(BookingDetails booking, int position);
    }
}
