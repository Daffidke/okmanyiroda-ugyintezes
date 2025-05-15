package com.example.okmanyirodaugyintezes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserDetails> userDetails = new MutableLiveData<>();

    public void setUserDetails(UserDetails details) {
        userDetails.setValue(details);
    }

    public LiveData<UserDetails> getUserDetails() {
        return userDetails;
    }
}
