package com.example.attention;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class groupChartActivity extends Fragment {
    /* Retrofit retrofit;
     RetrofitInterface retrofitInterface;
     String BASE_URL = "http://192.168.0.107:3000";*/
    private View view;
    String selectStName;


    String tmp;
    int reload=0;

    List<DataEntry> data = new ArrayList<>();

    boolean IsResponse = false;


    public void ViewPieChart() {

        if (IsResponse) {
            Pie pie = AnyChart.pie();
            pie.data(data);

            AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.group_chart_view);
            anyChartView.setChart(pie);
        }

    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.groupchart_activity,container,false);


        Spinner studyNameSpinner = (Spinner) view.findViewById(R.id.studyName_sp);


        AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.group_chart_view);

       /* retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);*/
        Pie pie = AnyChart.pie();



        String currentId = MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();


        map.put("userId", currentId);
        Call<List<StudySearchResult>> call0=retrofitClient.getInstance().getRetrofitInterface().myStudyList(map);



        call0.enqueue(new Callback<List<StudySearchResult>>() {
            @Override
            public void onResponse(Call<List<StudySearchResult>> call0, Response<List<StudySearchResult>> response) {
                List<StudySearchResult> result = response.body();

                ArrayList arrayList = new ArrayList<>();

                if (response.code() == 200) {

                    for (int x = 0; x < result.size(); x++) {

                        Log.v("스터디 아디", String.valueOf(result.get(x).getStudyId()));
                        Log.v("스터디 이름", String.valueOf(result.get(x).getstudyName()));



                        arrayList.add(String.valueOf(result.get(x).getstudyName()));

                    }
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    studyNameSpinner.setAdapter(adapter);

                   /* studyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }

                    });*/

                } else if (response.code() == 400) {


                }

            }

            @Override
            public void onFailure(Call<List<StudySearchResult>> call0, Throwable t) {


            }
        });

        studyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectStName = studyNameSpinner.getSelectedItem().toString();

                Log.v("선택한 스터디 이름",selectStName);

                //study Name to Id
                HashMap<String, String> map3 = new HashMap<>();



                map3.put("studyName", selectStName);
                Call<String> call1=retrofitClient.getInstance().getRetrofitInterface().studyNameToId(map3);



                call1.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse( Call<String> call1, Response<String> response) {

                        tmp =response.body();

                        // List<StudySearchResult>  result=response.body();
                        if (response.code() == 200) {


                            Log.v("스터디아이디 ", tmp);

                            HashMap<String, String> map2 = new HashMap<>();

                            Log.v("선택한 스터디 아디",tmp);

                            map2.put("studyId", tmp);

                            Call<List<GroupChartResult>> call =  retrofitClient.getInstance().getRetrofitInterface().executeGroupChart(map2);
                            call.enqueue(new Callback<List<GroupChartResult>>() {
                                @Override
                                public void onResponse(Call<List<GroupChartResult>> call, Response<List<GroupChartResult>> response) {
                                    List<GroupChartResult> result = response.body();


                                    if (response.code() == 200) {

                                        for (int x = 0; x < result.size(); x++) {

                                            data.add(new ValueDataEntry(result.get(x).getUserId(), result.get(x).getgreenTime()));
                                            System.out.println("받음" + result.get(x).getUserId() + ", " + result.get(x).getgreenTime());


                                        }

                                        Log.v("pie.data", String.valueOf(data.size()));
                                        pie.data(data);

                                        data.clear();// 파이차트 배열 초기화(그룹원들 파이차트 한개만 보여주기)
                                        Log.v("삭제후 pie.data", String.valueOf(data.size()));




                                    } else if (response.code() == 400) {



                                    }

                                }

                                @Override
                                public void onFailure(Call<List<GroupChartResult>> call, Throwable t) {


                                }
                            });





                        } else if (response.code() == 400) {

                        }

                    }

                    @Override
                    public void onFailure(Call<String> call1, Throwable t) {


                    }
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });



        anyChartView.setChart(pie);



        return  view;
    }
}