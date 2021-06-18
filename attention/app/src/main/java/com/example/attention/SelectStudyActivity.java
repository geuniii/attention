package com.example.attention;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectStudyActivity extends AppCompatActivity {

    ListView listView;
    Button outBtn;
    List<String> list;
    String currentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_study_activity);
        listView = (ListView)findViewById(R.id.study_list);
        outBtn = (Button)findViewById(R.id.out_btn);


        list = new ArrayList<>();


        HashMap<String, String> map = new HashMap<>();
        currentId=MyGlobals.getInstance().getData();


        map.put("userId",currentId);



        Call<List<StudySearchResult>> call =  retrofitClient
                .getInstance().getRetrofitInterface().selectStudy(map);

        call.enqueue(new Callback<List<StudySearchResult>>() {
            @Override
            public void onResponse( Call<List<StudySearchResult>> call, Response<List<StudySearchResult>> response) {

                List<StudySearchResult>  result=response.body();
                if (response.code() == 200) {
                   /* Toast.makeText(SelectStudyActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지*/


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);

                    for(int x=0;x<result.size();x++){

                        list.add(result.get(x).getstudyName());
                    }
                    adapter.notifyDataSetChanged();


                } else if (response.code() == 400) {
                    Toast.makeText(SelectStudyActivity.this,
                            "ERROR", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<StudySearchResult>> call, Throwable t) {
                Toast.makeText(SelectStudyActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });

        outBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), studyOutActivity.class);   // 인텐트 처리

                startActivity(intent);
                // TODO : click event
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data= (String) parent.getAdapter().getItem(position);
                System.out.println(data+"에서 공부방 진입");
                Intent intent = new Intent(getApplication(), locationActivity.class);   // 인텐트 처리
                intent.putExtra("studyName", data);
                startActivity(intent);

            }
        });



    }
}