package com.example.myapplicationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

//Old Version
//    BottomNavigationView bottomNavigationView;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.navigation_chats:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.navigation_music:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.navigation_moments:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.navigation_settings:
                        viewPager2.setCurrentItem(3);
                        break;
                }
                return false;
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_chats).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_music).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_moments).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_settings).setChecked(true);
                        break;}
                super.onPageSelected(position);
            }
        });

//Old Version
//        bottomNavigationView = findViewById(R.id.bottomNav);
//
//        if (savedInstanceState == null){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new ChatsFragment()).commit();
//        }
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Fragment fragment = null;
//
//                switch (item.getItemId()){
//                    case R.id.navigation_chats:
//                        fragment = new ChatsFragment();
//                        break;
//                    case R.id.navigation_music:
//                        fragment = new MusicFragment();
//                        break;
//                    case R.id.navigation_moments:
//                        fragment = new MomentsFragment();
//                        break;
//                    case R.id.navigation_settings:
//                        fragment = new SettingsFragment();
//                        break;
//                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
//
//                return true;
//            }
//        });

    }
}