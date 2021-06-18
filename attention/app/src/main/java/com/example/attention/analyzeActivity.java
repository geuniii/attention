package com.example.attention;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Radar;
import com.anychart.core.radar.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.MarkerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class analyzeActivity extends AppCompatActivity {

    String userId;
    private TextView textview6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analyze_activity);

        userId = MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        TextView title = findViewById(R.id.title_text);
        title.setText(userId+"'s Study Report");

        ImageButton back = findViewById(R.id.back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textview6 = findViewById(R.id.textView6);


        /////////////attentionRanking///////////////////////

        StringBuilder attention =new StringBuilder();

        TextView percentText = findViewById(R.id.attention_percent1);


        Call<List<attentionRankingResult>> call =  retrofitClient
                .getInstance().getRetrofitInterface().attentionRanking(map);

        call.enqueue(new Callback<List<attentionRankingResult>>() {
            @Override
            public void onResponse(Call<List<attentionRankingResult>> call, Response<List<attentionRankingResult>> response) {
                if (response.code() == 200) {
                    Toast.makeText(analyzeActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();

                    List<attentionRankingResult> result1  = response.body();

                    for(int x = 0;x<result1.size();x++){

                        attention.append(result1.get(x).getUpDown()+" "+result1.get(x).getPercentage()+"%");
                        percentText.setText(attention);
                        System.out.println(result1.get(x).getUpDown()+" "+result1.get(x).getPercentage()+"%");

                    }
                }
            }

            @Override
            public void onFailure(Call<List<attentionRankingResult>> call, Throwable t) {

                Toast.makeText(analyzeActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


        //////////////////////////ageAttention////////////////////////////////////////

        StringBuilder group = new StringBuilder();
        StringBuilder percent = new StringBuilder();

        TextView group_text = findViewById(R.id.attention_text2);
        TextView agePercent_text = findViewById(R.id.attention_percent2);

        Call<List<ageAttentionResult>> call2 = retrofitClient
                .getInstance().getRetrofitInterface().ageAttention(map);

        call2.enqueue(new Callback<List<ageAttentionResult>>() {
            @Override
            public void onResponse(Call<List<ageAttentionResult>> call, Response<List<ageAttentionResult>> response) {

                if(response.code() == 200) {
                    Toast.makeText(analyzeActivity.this, "success", Toast.LENGTH_LONG).show();

                    List<ageAttentionResult> result2 = response.body();

                    System.out.println("result: "+result2);

                    for(int x = 0; x<result2.size(); x++) {


                        group.append("같은 연령대인  <"+result2.get(x).getGroup()+"> 중 당신의 집중력은");
                        group_text.setText(group);
                        percent.append(result2.get(x).getUpDown()+" "+result2.get(x).getPercent()+"%");
                        agePercent_text.setText(percent);

                        System.out.println(result2.get(x).getUpDown()+" "+result2.get(x).getPercent()+"%");

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ageAttentionResult>> call, Throwable t) {
                Toast.makeText(analyzeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //////////////////////attentionCategory///////////////////////////////////

        StringBuilder category =new StringBuilder();

        TextView categoryText = findViewById(R.id.attention_text3);

        StringBuilder categoryPercent =new StringBuilder();

        TextView categoryPercentText = findViewById(R.id.attention_percent3);

        Call<List<attentionCategoryResult>> call3 = retrofitClient
                .getInstance().getRetrofitInterface().attentionCategory(map);

        call3.enqueue(new Callback<List<attentionCategoryResult>>() {
            @Override
            public void onResponse(Call<List<attentionCategoryResult>> call, Response<List<attentionCategoryResult>> response) {

                if (response.code() == 200) {
                    Toast.makeText(analyzeActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();

                    List<attentionCategoryResult> result3  = response.body();

                    System.out.println("result 길이:"+result3.size());

                    for(int x = 0;x<result3.size();x++){

                        category.append("< "+result3.get(x).getCategory()+" >  공부 시간 :  "+
                                result3.get(x).getUpDown()+" "+result3.get(x).getPercentage()+"%\n\n");

                        //categoryPercent.append(result3.get(x).getUpDown()+" "+result3.get(x).getPercentage()+"%\n\n\n");

                        categoryText.setText(category);
                        //categoryPercentText.setText(categoryPercent);

                        /*

                        if(x==result3.size()-1){
                            categoryText.setText(category);
                            categoryPercentText.setText(categoryPercent);
                        }

                        */

                    }


                }
            }
            @Override
            public void onFailure(Call<List<attentionCategoryResult>> call, Throwable t) {

                Toast.makeText(analyzeActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        //////////////////////////////////PlaceRecommend////////////////////////////////////////////////


        AnyChartView anyChartView = findViewById(R.id.any_chart_view);



        Call<List<PlaceRecommendResult>> call4 = retrofitClient
                .getInstance()
                .getRetrofitInterface()
                .executePlaceRecommend(map);

        call4.enqueue(new Callback<List<PlaceRecommendResult>>() {
            @Override
            public void onResponse(Call<List<PlaceRecommendResult>> call4, Response<List<PlaceRecommendResult>> response) {
                List<PlaceRecommendResult> result = response.body();
                int max=result.get(0).greentime;
                int min=result.get(0).greentime;

                int maxIdx = 0;
                int minIdx = 0;
                Radar radar = AnyChart.radar();

                radar.title("장소별 집중도");

                radar.yScale().minimum(0d);
                radar.yScale().minimumGap(0d);
                radar.yScale().ticks().interval(50d);

                radar.xAxis().labels().padding(5d, 5d, 5d, 5d);

                radar.legend()
                        .align(Align.CENTER)
                        .enabled(true);

                List<DataEntry> data = new ArrayList<>();
                if (response.code() == 200) {
                    System.out.println("장소별 집중도 result.size "+result.size());

                    for(int a=0;a<result.size();a++){
                        if(max<result.get(a).greentime){
                            max=result.get(a).greentime;
                            maxIdx=a;

                        }
                        if(min>result.get(a).greentime){
                            min=result.get(a).greentime;
                            minIdx=a;
                        }


                        System.out.println("받은 결과 loName : "+result.get(a).loCategory);

                        data.add(new CustomDataEntry(result.get(a).loCategory, result.get(a).greentime, result.get(a).fulltime));


                    }

                    Log.v(" switch (result.size())", String.valueOf(result.size()));

                    int x=result.size();

                    Log.v("x", String.valueOf(x));

                    switch (x){

                        case 0:
                            Log.v("case0","aaaaaaaaaa");
                            data.add(new CustomDataEntry("null1", 0,0));
                            data.add(new CustomDataEntry("null2", 0,0));
                            data.add(new CustomDataEntry("null3", 0,0));
                            data.add(new CustomDataEntry("null4", 0,0));
                            data.add(new CustomDataEntry("null5", 0,0));
                            break;
                        case 1:
                            Log.v("case1","1111111111111");

                            data.add(new CustomDataEntry("null1", 0,0));
                            data.add(new CustomDataEntry("null2", 0,0));
                            data.add(new CustomDataEntry("null3", 0,0));
                            data.add(new CustomDataEntry("null4", 0,0));
                            break;
                        case 2:
                            Log.v("case2","22222222222222");

                            data.add(new CustomDataEntry("null1", 0,0));
                            data.add(new CustomDataEntry("null2", 0,0));
                            data.add(new CustomDataEntry("null3", 0,0));
                            break;
                        case 3:
                            Log.v("case3","33333333333333");

                            data.add(new CustomDataEntry("null1", 0,0));
                            data.add(new CustomDataEntry("null2", 0,0));
                            break;
                        case 4:
                            Log.v("case4","444444444444");

                            data.add(new CustomDataEntry("null1", 0,0));
                            break;
                        default:
                            Log.v("case5","5555555555555555");
                            break;

                    }






                    Set set = Set.instantiate();
                    set.data(data);
                    Mapping shamanData = set.mapAs("{ x: 'x', value: 'value' }");
                    Mapping warriorData = set.mapAs("{ x: 'x', value: 'value2' }");

                    Line shamanLine = radar.line(shamanData);
                    shamanLine.name("집중시간");
                    shamanLine.markers()
                            .enabled(true)
                            .type(MarkerType.CIRCLE)
                            .size(3d);

                    Line warriorLine = radar.line(warriorData);
                    warriorLine.name("공부시간");
                    warriorLine.markers()
                            .enabled(true)
                            .type(MarkerType.CIRCLE)
                            .size(3d);

                    radar.tooltip().format("Value: {%Value}");

                    anyChartView.setChart(radar);

                    System.out.println(result.get(maxIdx).loCategory);
                    textview6.setText(result.get(maxIdx).loCategory);




                }




            }

            @Override
            public void onFailure(Call<List<PlaceRecommendResult>> call4, Throwable t) {

            }

        });




    }
    private class CustomDataEntry extends ValueDataEntry {
        public CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);

        }
    }


}