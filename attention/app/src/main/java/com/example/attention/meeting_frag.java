package com.example.attention;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class meeting_frag extends Fragment // Fragment 클래스를 상속받아야한다
{

    private View view;
    EditText search_edit;
    ImageButton search_btn;
    ListView listView;
    List<String> list;
    Button new_btn;



    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.meeting_frag,container,false);


        search_edit = (EditText)view.findViewById(R.id.search_edit);
        search_edit.setTypeface(Typeface.DEFAULT);
        search_btn = (ImageButton)view.findViewById(R.id.search_btn);

        listView = (ListView)view.findViewById(R.id.search_res);

        list = new ArrayList<>();

        //getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new defaultFrag()).commit();

        new_btn = (Button)view.findViewById(R.id.new_btn);

        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), newStudy_Activity.class);   // 인텐트 처리
                startActivity(intent);
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String searchStudy=search_edit.getText().toString();


                HashMap<String, String> map = new HashMap<>();


                System.out.println("보낼 메세지 :"+searchStudy);

                map.put("studyName",searchStudy);





                Call<List<StudySearchResult>> call=retrofitClient
                        .getInstance().getRetrofitInterface().executeStudySearch(map);
                //Call<Void> call = retrofitInterface.executeTest(map);


                call.enqueue(new Callback<List<StudySearchResult>>() {
                    @Override
                    public void onResponse(Call<List<StudySearchResult>> call, Response<List<StudySearchResult>> response) {
                        List<StudySearchResult>  result=response.body();

                        if (response.code() == 200) {
                            list.clear();

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
                            listView.setAdapter(adapter);




                            for(int x=0;x<result.size();x++){

                                list.add(result.get(x).getstudyName());


                                System.out.println(result.get(x).getstudyName());

                            }
                            System.out.println("@@@@@@@@@@@@응답 성공@@@@@@@@@@@@@");
                            adapter.notifyDataSetChanged();






                        } else if (response.code() == 400) {

                            // System.out.println(result.getUserId()+"님은 빨강");

                        }

                    }

                    @Override
                    public void onFailure(Call<List<StudySearchResult>> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(),
                                Toast.LENGTH_LONG).show();//안드 팝업메시지

                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data= (String) parent.getAdapter().getItem(position);
                System.out.println(data+" 가 선택됨"+data+"전송");

/*
                gaipStudyFrag fragment= new gaipStudyFrag();
                Bundle bundle = new Bundle();
                bundle.putString("studyName", data);
                fragment.setArguments(bundle);
*/


                // getChildFragmentManager().beginTransaction().replace(R.id.relativeLayout, new gaipStudyFrag()).commit();

                //getChildFragmentManager().beginTransaction().replace(R.id.child_fragment, new gaipStudyFrag().newInstance(data)).commit();

                /*
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.relativeLayout, new gaipStudyFrag().newInstance(data)).commit();

                */


                Log.i("frag 이동","frag이동");



                Intent intent = new Intent(getActivity(), gaipStudyActivity.class);   // 인텐트 처리
                intent.putExtra("studyName", data);
                startActivity(intent);


            }
        });




        return view;
    }




}