package com.example.attention;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class study_frag extends Fragment   // Fragment 클래스를 상속받아야한다
{

    private View view;
    ListView listView;
    Button outBtn;
    List<String> list;
    String currentId;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.study_frag,container,false);

        listView = (ListView)view.findViewById(R.id.study_list);
        outBtn = (Button)view.findViewById(R.id.out_btn);

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
                  /*  Toast.makeText(getActivity(),
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지*/


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);

                    for(int x=0;x<result.size();x++){

                        list.add(result.get(x).getstudyName());
                    }
                    adapter.notifyDataSetChanged();


                } else if (response.code() == 400) {
                    Toast.makeText(getActivity(),
                            "ERROR", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<StudySearchResult>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });

        outBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplication(), studyOutActivity.class);   // 인텐트 처리

                startActivity(intent);
                // TODO : click event
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data= (String) parent.getAdapter().getItem(position);
                System.out.println(data+"에서 공부방 진입");
                Intent intent = new Intent(getActivity().getApplication(), locationActivity.class);   // 인텐트 처리
                intent.putExtra("studyName", data);
                startActivity(intent);

            }
        });



        return view;


    }
}