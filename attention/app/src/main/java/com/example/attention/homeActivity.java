package com.example.attention;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class homeActivity extends AppCompatActivity{


    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private meeting_frag meeting;
    private home_frag home;
    private study_frag study;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.meetingItem:
                        setFrag(0);
                        break;
                    case R.id.homeItem:
                        setFrag(1);

                        break;
                    case R.id.studyItem:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        meeting = new meeting_frag();
        home = new home_frag();
        study = new study_frag();
        setFrag(0);// 첫 프래그먼트 화면 지정

    }

    // 프레그먼트 교체
    private void setFrag(int n)
    {
        fm = getSupportFragmentManager();
        ft= fm.beginTransaction();
        switch (n)
        {
            case 0:
                ft.replace(R.id.Main_Frame,meeting);
                ft.commit();
                break;

            case 1:
                ft.replace(R.id.Main_Frame,home);
                ft.commit();
                break;

            case 2:
                ft.replace(R.id.Main_Frame,study);
                ft.commit();
                break;

        }
    }
}


