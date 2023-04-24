package com.example.myapplicationexample;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new ChatsFragment();
            case 1: return new MusicFragment();
            case 2: return new MomentsFragment();
            case 3: return new SettingsFragment();
            default: return new ChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
