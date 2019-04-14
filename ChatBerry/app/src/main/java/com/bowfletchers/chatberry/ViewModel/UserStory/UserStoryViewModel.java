package com.bowfletchers.chatberry.ViewModel.UserStory;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.bowfletchers.chatberry.ClassLibrary.FirebaseInstances;
import com.bowfletchers.chatberry.DataSource.FirebaseQueryDataOnce;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class UserStoryViewModel extends ViewModel {

    FirebaseQueryDataOnce userStoryLiveData;

    public LiveData<DataSnapshot> getUserStoryById(String userId) {
        DatabaseReference userStoryRef = FirebaseInstances.getDatabaseReference("/users/" + userId + "/story");
        userStoryLiveData = new FirebaseQueryDataOnce(userStoryRef);
        return userStoryLiveData;
    }
}
