package com.example.attention;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.anychart.AnyChartView;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class day_GreenByFull extends Fragment {


    View view;
    TextView todayView;
    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.day_green_by_full, container, false);

        HashMap<String, String> map = new HashMap<>();

        TextView todayView  = (TextView) view.findViewById(R.id.today_title);

        String currentId=MyGlobals.getInstance().getData();

        map.put("userId",currentId);




        Call<List<Result_day_GreenByFull>> call=retrofitClient.getInstance().getRetrofitInterface().greenByFull(map);
        //Call<Void> call = retrofitInterface.executeTest(map);


        call.enqueue(new Callback<List<Result_day_GreenByFull>>() {
            @Override
            public void onResponse(Call<List<Result_day_GreenByFull>> call, Response<List<Result_day_GreenByFull>> response) {
                List<Result_day_GreenByFull>  result=response.body();

                List<DataEntry> seriesData = new ArrayList<>();

                if (response.code() == 200) {




                    for(int x=0;x<result.size();x++){



                        seriesData.add(new CustomDataEntry(result.get(x).getTime(), result.get(x).getGreen(), result.get(x).getFull() ));

                        System.out.println(result.get(x));

                    }
                    System.out.println("@@@@@@@@@@@@응답 성공@@@@@@@@@@@@@");
                    //AnyChartView anyChartView = findViewById(R.id.any_chart_view);
                    AnyChartView anyChartView = (AnyChartView)view.findViewById(R.id.any_chart_view);
                    // anyChartView.setProgressBar(findViewById(R.id.progress_bar));

                    Cartesian cartesian = AnyChart.line();

                    cartesian.animation(true);

                    cartesian.padding(10d, 20d, 5d, 20d);

                    cartesian.crosshair().enabled(true);
                    cartesian.crosshair()
                            .yLabel(true)
                            // TODO ystroke
                            .yStroke((Stroke) null, null, null, (String) null, (String) null);

                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                    cartesian.title("오늘 공부시간 vs  집중시간");

                    cartesian.yAxis(0).title("시간(분단위)");
                    cartesian.xAxis(0).title("시작 시간");
                    cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);





                    Set set = Set.instantiate();
                    set.data(seriesData);

                    Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
                    Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");


                    Line series1 = cartesian.line(series1Mapping);
                    series1.name("집중시간");
                    series1.hovered().markers().enabled(true);
                    series1.hovered().markers()
                            .type(MarkerType.CIRCLE)
                            .size(4d);
                    series1.tooltip()
                            .position("right")
                            .anchor(Anchor.LEFT_CENTER)
                            .offsetX(5d)
                            .offsetY(5d);

                    Line series2 = cartesian.line(series2Mapping);
                    series2.name("공부시간");
                    series2.hovered().markers().enabled(true);
                    series2.hovered().markers()
                            .type(MarkerType.CIRCLE)
                            .size(4d);
                    series2.tooltip()
                            .position("right")
                            .anchor(Anchor.LEFT_CENTER)
                            .offsetX(5d)
                            .offsetY(5d);


                    anyChartView.setChart(cartesian);



                } else if (response.code() == 400) {

                    // System.out.println(result.getUserId()+"님은 빨강");

                }

            }

            @Override
            public void onFailure(Call<List<Result_day_GreenByFull>> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), t.getMessage(),
                // Toast.LENGTH_LONG).show();//안드 팝업메시지
                //Toast.makeText(this, t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });



/*

        ////////////////////////////categoryActivity//////////////////////////////////////

        List<DataEntry> data = new ArrayList<>();

        Call<List<categoryChartResult>> call2= retrofitClient.getInstance().getRetrofitInterface().categoryChart(map);

        call2.enqueue(new Callback<List<categoryChartResult>>() {
            @Override
            public void onResponse(Call<List<categoryChartResult>> call, Response<List<categoryChartResult>> response) {
                List<categoryChartResult>  result2 =response.body();

                if (response.code() == 200) {

                    Pie pie = AnyChart.pie();


                    for(int x=0;x<result2.size();x++){

                        data.add(new ValueDataEntry(result2.get(x).getCategory(), result2.get(x).getCategoryFull()));

                    }

                    pie.data(data);
                    AnyChartView anyChartView2 = (AnyChartView)view.findViewById(R.id.category_chart_view);
                    anyChartView2.setChart(pie);

                    data.clear();



                } else if (response.code() == 400) {


                }

            }

            @Override
            public void onFailure(Call<List<categoryChartResult>> call, Throwable t) {

            }
        });

*/


        return view;
    }

    private void setContentView(int day_green_by_full) {
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2", value2);

        }

    }








}





