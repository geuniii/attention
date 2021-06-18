package com.example.attention;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class home_frag extends Fragment // Fragment 클래스를 상속받아야한다
{

    private View view;
    Button test_btn, test_btn2, test_btn3,analyze_btn;
    String userId;

    private FragmentManager fm;
    private FragmentTransaction ft;

    boolean IsResponse=false;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.home_frag,container,false);

        userId = MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        StringBuilder time =new StringBuilder();
        TextView todayTime = view.findViewById(R.id.todayStudy_text);

        Call<List<todayResult>> call =  retrofitClient
                .getInstance().getRetrofitInterface().todayStudy(map);

        call.enqueue(new Callback<List<todayResult>>() {
            @Override
            public void onResponse(Call<List<todayResult>> call, Response<List<todayResult>> response) {

                if (response.code() == 200) {
                    Toast.makeText(getActivity(),
                            "successfully", Toast.LENGTH_LONG).show();

                    List<todayResult> result  = response.body();
                    System.out.println("result: "+result);

                    for(int x = 0;x<result.size();x++){

                        time.append(result.get(x).hour+"시간 "+result.get(x).minute+"분");
                        todayTime.setText(time);
                        System.out.println(result.get(x).hour+"시간 "+result.get(x).minute+"분");

                    }
                }

            }

            @Override
            public void onFailure(Call<List<todayResult>> call, Throwable t) {

            }
        });


        getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new day_GreenByFull()).commit();

        //하위버튼
        LinearLayout subButton1 = (LinearLayout) view.findViewById(R.id.subButton1);
        LinearLayout subButton2 = (LinearLayout) view.findViewById(R.id.subButton2);
        LinearLayout subButton3 = (LinearLayout) view.findViewById(R.id.subButton3);
        LinearLayout subButton4 = (LinearLayout) view.findViewById(R.id.subButton4);

        //클릭 이벤트
        subButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new day_GreenByFull()).commit();
            }
        });

        subButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new WeeklyFullTime()).commit();
            }
        });

        subButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new groupChartActivity()).commit();
            }
        });

        subButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new categoryActivity()).commit();
            }
        });

        Button analyze_btn = (Button)view.findViewById(R.id.analyze_btn);
        analyze_btn.setText(userId+"님의 공부 패턴을 알아보세요        →");

        analyze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplication(), analyzeActivity.class);
                startActivity(intent);

            }
        });


        /*
        test_btn = (Button)view.findViewById(R.id.test_btn);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new attentionRanking()).commit();

            }
        });

        test_btn2 = (Button)view.findViewById(R.id.test_btn2);
        test_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new PlaceRecommend()).commit();
                Log.v("test","플레이스 들어감");

            }
        });


        test_btn3 = (Button)view.findViewById(R.id.test_btn3);
        test_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new attentionCategory()).commit();

            }
        });

        Button test_btn4 = (Button) view.findViewById(R.id.test_btn4);
        test_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new ageAttention()).commit();

            }
        });



         */



        return view;
    }
}