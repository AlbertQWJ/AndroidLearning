package com.example.myapplicationexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.myapplicationexample.databinding.ActivityUsersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                   if (task.isSuccessful() && task.getResult() != null) {
                       List<AppUser> appUsers = new ArrayList<>();
                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                           if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                               continue;
                           }
                           AppUser appUser = new AppUser();
                           appUser.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                           appUser.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                           appUser.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                           appUser.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                           appUsers.add(appUser);
                       }
                       if (appUsers.size() > 0) {
                           UsersAdapter usersAdapter = new UsersAdapter(appUsers);
                           binding.usersRecyclerView.setAdapter(usersAdapter);
                           binding.usersRecyclerView.setVisibility(View.VISIBLE);
                       }else {
                           showErrorMessage();
                       }
                   }else {
                       showErrorMessage();
                   }
                });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s","No User Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}