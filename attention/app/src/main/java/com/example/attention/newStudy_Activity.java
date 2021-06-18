package com.example.attention;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class newStudy_Activity extends Activity implements View.OnClickListener {

    private TextView categoryView,studyView,startView,endView;
    private EditText studyEditView,startEditView,endEditView;
    private Spinner spinner;
    String userId="";
    private int studyId;

    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.newstudy);

        categoryView = (TextView) findViewById(R.id.category_view);
        studyView = (TextView) findViewById(R.id.study_view);
        startView = (TextView) findViewById(R.id.start_view);
        endView = (TextView) findViewById(R.id.end_view);

        studyEditView = (EditText) findViewById(R.id.study_edit);
        startEditView = (EditText) findViewById(R.id.start_edit);
        endEditView = (EditText) findViewById(R.id.end_edit);

        spinner = (Spinner) findViewById(R.id.category_sp);
        findViewById(R.id.make_btn).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.close_btn).setOnClickListener((View.OnClickListener) this);

        ArrayList arrayList = new ArrayList<>();
        arrayList.add("어학");
        arrayList.add("자격증 시험");
        arrayList.add("공무원 시험");
        arrayList.add("학교 시험");
        arrayList.add("취업 준비");

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        /*setAdapter()함수하는 순간 초기화되기때문에  newStudy() 함수 안에 있으면 매번 카테고리 선택이 초기화되므로 매번 어학으로 들어가게 됨
         */
    }

    private void newStudy() {

        String studyName = studyEditView.getText().toString().trim();
        String startDate = startEditView.getText().toString().trim();
        String endDate = endEditView.getText().toString().trim();
/*
        ArrayList arrayList = new ArrayList<>();
        arrayList.add("어학");
        arrayList.add("자격증 시험");
        arrayList.add("공무원 시험");
        arrayList.add("학교 시험");
        arrayList.add("취업 준비");

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        스터디 가입시, 카테고리에 공무원이라고 클릭하고 가입버튼 누르면 매번 어학이라고 가입됨
*/


        String category= spinner.getSelectedItem().toString();

        HashMap<String, String> map = new HashMap<>();
        userId= MyGlobals.getInstance().getData();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (studyName.isEmpty()) {
                studyEditView.setError("Study name is required");
                studyEditView.requestFocus();
                return;
            }

            if (startDate.isEmpty()) {
                startEditView.setError("Start date is required");
                startEditView.requestFocus();
                return;
            }

            if (endDate.isEmpty()) {
                endEditView.setError("End date is required");
                endEditView.requestFocus();
                return;
            }

        }
        Log.v("서버로 전송될 카테고리",spinner.getSelectedItem().toString());

        Call<ResponseBody> call = retrofitClient
                .getInstance()
                .getRetrofitInterface()
                .newStudy(category,studyName,startDate,endDate,userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String responseS = response.body().toString();
                Toast.makeText(newStudy_Activity.this,responseS,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(newStudy_Activity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.make_btn:
                newStudy();
                break;
            case R.id.close_btn:
                finish();
                break;
        }
    }

}





