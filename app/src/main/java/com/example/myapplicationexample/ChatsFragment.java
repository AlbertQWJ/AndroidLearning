package com.example.myapplicationexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplicationexample.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_settings,container,false);

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // Inflate the layout for this fragment
        setListeners();
        return view;
    }

    private void setListeners() {
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), UsersActivity.class)));
    }
}