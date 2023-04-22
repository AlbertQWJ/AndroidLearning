package com.example.myapplicationexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new ChatsFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.navigation_chats:
                        fragment = new ChatsFragment();
                        break;
                    case R.id.navigation_music:
                        fragment = new MusicFragment();
                        break;
                    case R.id.navigation_moments:
                        fragment = new MomentsFragment();
                        break;
                    case R.id.navigation_settings:
                        fragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();

                return true;
            }
        });

    }
}