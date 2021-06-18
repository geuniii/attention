package com.example.attention;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ListFragment;


public class WeeklyFullTime extends Fragment {


    String fulltime;
    String day;
    private View view;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.weekly_fulltime, container, false);

        String currentId = MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();

        map.put("userId", currentId);

        Call<List<WeeklyFulltimeResult>> call = retrofitClient
                .getInstance()
                .getRetrofitInterface()
                .executeWeeklyChart(map);

        call.enqueue(new Callback<List<WeeklyFulltimeResult>>() {
            @Override
            public void onResponse(Call<List<WeeklyFulltimeResult>> call, Response<List<WeeklyFulltimeResult>> response) {
                List<WeeklyFulltimeResult> result = response.body();

                if (response.code() == 200) {
                    System.out.println("일단 받음");

                    AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
                    Cartesian cartesian = AnyChart.column();
                    List<DataEntry> data = new ArrayList<>();


                    for (int x = 0; x < result.size(); x++) {


                        System.out.println("fullTime :" + result.get(x).getFullTime() + " day :" + result.get(x).getDay());

                        if (result.get(x).getFullTime() == null) {
                            data.add(new ValueDataEntry("없음", 0));

                        } else {

                            data.add(new ValueDataEntry(result.get(x).getDay(), Integer.parseInt(result.get(x).getFullTime())));
                        }

                    }
                    System.out.println("data 길이" + data.size());
                    for (int k = 0; k < data.size(); k++) {
                        System.out.println(data.get(k));

                    }
                    Column column = cartesian.column(data);

                    column.tooltip()
                            .titleFormat("{%X}")
                            //.position(Position.CENTER_BOTTOM)
                            //  .anchor(Anchor.CENTER_BOTTOM)
                            // .offsetX(0d)
                            // .offsetY(0d)
                            .format("{%Value}초");

                    cartesian.animation(true);
                    //   cartesian.padding(10d, 20d, 5d, 20d);


                    cartesian.yAxis(0).labels().format("{%Value}초");

                    // cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                    //cartesian.interactivity().hoverMode(HoverMode.BY_X);

                    cartesian.xAxis(0).title("day");

                    cartesian.yAxis(0).title("fulltime");

                    anyChartView.setChart(cartesian);
                }


            }

            @Override
            public void onFailure(Call<List<WeeklyFulltimeResult>> call, Throwable t) {

            }

        });

        return view;


    }

}


