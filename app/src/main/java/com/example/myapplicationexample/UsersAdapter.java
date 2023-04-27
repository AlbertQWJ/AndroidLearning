package com.example.myapplicationexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationexample.databinding.ItemContainerAppuserBinding;

import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<AppUser> appUsers;

    public UsersAdapter(List<AppUser> appUsers) {
        this.appUsers = appUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerAppuserBinding itemContainerAppuserBinding = ItemContainerAppuserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerAppuserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(appUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return appUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerAppuserBinding binding;

        UserViewHolder(ItemContainerAppuserBinding itemContainerAppuserBinding) {
            super(itemContainerAppuserBinding.getRoot());
            binding = itemContainerAppuserBinding;
        }

        void setUserData(AppUser appUser) {
            binding.textName.setText(appUser.name);
            binding.textEmail.setText(appUser.email);
            binding.imageProfile.setImageBitmap(getUserImage(appUser.image));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
