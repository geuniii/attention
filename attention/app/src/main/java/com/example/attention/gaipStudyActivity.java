package com.example.attention;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class gaipStudyActivity extends Activity implements View.OnClickListener {

    private TextView categoryView,studyView,startView,endView,memberView;
    private TextView categoryTextView,studyTextView,startTextView,endTextView,memberTextView;

    String currentId=MyGlobals.getInstance().getData();

    String studyName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gaipstudy);



        categoryView = (TextView) findViewById(R.id.category_view);
        studyView = (TextView) findViewById(R.id.study_view);
        startView = (TextView) findViewById(R.id.start_view);
        endView = (TextView) findViewById(R.id.end_view);
        memberView=(TextView)findViewById(R.id.member_view);


        categoryTextView = (TextView) findViewById(R.id.category_tv);
        studyTextView = (TextView) findViewById(R.id.study_tv);
        startTextView = (TextView) findViewById(R.id.start_tv);
        endTextView = (TextView) findViewById(R.id.end_tv);
        memberTextView=(TextView)findViewById(R.id.member_tv);


        findViewById(R.id.make_btn).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.close_btn).setOnClickListener((View.OnClickListener) this);


        Intent intent = this.getIntent();
        studyName = intent.getStringExtra("studyName");

        studyTextView.setText(studyName);

        HashMap<String, String> map = new HashMap<>();
        Log.v("enterStudy에 들어가는",studyName);




        map.put("studyName",studyName);




        Call<enterResult> call=retrofitClient.getInstance().getRetrofitInterface().enterStudy(map);
        //Call<Void> call = retrofitInterface.executeTest(map);


        call.enqueue(new Callback<enterResult>() {
            @Override
            public void onResponse(Call<enterResult> call, Response<enterResult> response) {


                enterResult result= response.body();

                if (response.code() == 200) {
                    //System.out.println(result.getCategory());
                    categoryTextView.setText(result.getCategory());
                    startTextView.setText(result.getStartDate());
                    endTextView.setText(result.getEndDate());
                    memberTextView.setText(result.getMember());





                } else if (response.code() == 400) {

                    // System.out.println(result.getUserId()+"님은 빨강");

                }

            }

            @Override
            public void onFailure(Call<enterResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });






    }

    private void enterStudy() {

        currentId=MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId",currentId);
        map.put("studyName",studyName);

        Call<Void> call = retrofitClient.getInstance().getRetrofitInterface().resisterStudy(map);


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {

                    System.out.println("인서트 명령 보냄");
                    Toast.makeText(getApplicationContext(), "가입 완료되었습니다.",
                            Toast.LENGTH_LONG).show();//안드 팝업메시지







                } else if (response.code() == 400) {
                    Toast.makeText(getApplicationContext(), "이미 가입되어있습니다..",
                            Toast.LENGTH_LONG).show();//안드 팝업메시지
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });

        Intent intent = new Intent(
                getApplicationContext(),homeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.make_btn:
                enterStudy();
                break;
            case R.id.close_btn:
                finish();
                break;
        }
    }

}