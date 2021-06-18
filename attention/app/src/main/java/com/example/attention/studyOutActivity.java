package com.example.attention;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class studyOutActivity extends AppCompatActivity implements View.OnClickListener  {

    ListView listView;
    List<String> list;
    String currentId;
    String studyName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studyout_activity);
        listView = (ListView)findViewById(R.id.study_list);

        list = new ArrayList<>();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data= (String) parent.getAdapter().getItem(position);
                System.out.println(data+"탈퇴");
                studyName = data;
                alert();

            }
        });

        HashMap<String, String> map1 = new HashMap<>();
        currentId=MyGlobals.getInstance().getData();

        map1.put("userId",currentId);

        Call<List<StudySearchResult>> call =  retrofitClient
                .getInstance().getRetrofitInterface().selectStudy(map1);

        call.enqueue(new Callback<List<StudySearchResult>>() {
            @Override
            public void onResponse( Call<List<StudySearchResult>> call, Response<List<StudySearchResult>> response) {

                List<StudySearchResult>  result=response.body();
                if (response.code() == 200) {
                   /* Toast.makeText(studyOutActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지*/


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);

                    for(int x=0;x<result.size();x++){

                        list.add(result.get(x).getstudyName());
                    }
                    adapter.notifyDataSetChanged();


                } else if (response.code() == 400) {
                    Toast.makeText(studyOutActivity.this,
                            "ERROR", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<StudySearchResult>> call, Throwable t) {
                Toast.makeText(studyOutActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });


    }
    private void alert(){

        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                R.style.MyAlertDialogStyle);

        oDialog.setMessage("스터디를 탈퇴하시겠습니까?")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i("Dialog", "취소");
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        eraseStudy();
                    }
                })
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }

    private void eraseStudy(){

        HashMap<String, String> map2 = new HashMap<>();
        currentId=MyGlobals.getInstance().getData();
        map2.put("userId",currentId);
        map2.put("studyName",studyName);

        Call<ResponseBody> call2 =  retrofitClient
                .getInstance().getRetrofitInterface().studyOut(map2);

        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    Toast.makeText(studyOutActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지

                    Intent intent = new Intent(
                            getApplicationContext(),SelectStudyActivity.class);
                    startActivity(intent);

                } else if (response.code() == 400) {
                    Toast.makeText(studyOutActivity.this,
                            "ERROR", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(studyOutActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지
            }
        });

    };


    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.back_button:
                Intent intent = new Intent(
                        getApplicationContext(),SelectStudyActivity.class);
                startActivity(intent);
                break;

        }
    }
}