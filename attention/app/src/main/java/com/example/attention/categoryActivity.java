package com.example.attention;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.ViewGroup;

public class categoryActivity extends Fragment {



    List<DataEntry> data = new ArrayList<>();

    boolean IsResponse=false;
    String userId="";
    View view;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_activity, container, false);

        HashMap<String, String> map = new HashMap<>();
        userId= MyGlobals.getInstance().getData();

        map.put("userId",userId);

        Call<List<categoryChartResult>> call= retrofitClient.getInstance().getRetrofitInterface().categoryChart(map);

        call.enqueue(new Callback<List<categoryChartResult>>() {
            @Override
            public void onResponse(Call<List<categoryChartResult>> call, Response<List<categoryChartResult>> response) {
                List<categoryChartResult>  result =response.body();

                if (response.code() == 200) {

                    Pie pie = AnyChart.pie();


                    for(int x=0;x<result.size();x++){

                        data.add(new ValueDataEntry(result.get(x).getCategory(), result.get(x).getCategoryFull()));

                    }

                    pie.data(data);
                    AnyChartView anyChartView = (AnyChartView)view.findViewById(R.id.category_chart_view);
                    anyChartView.setChart(pie);

                    data.clear();



                } else if (response.code() == 400) {


                }

            }

            @Override
            public void onFailure(Call<List<categoryChartResult>> call, Throwable t) {

            }
        });

        return view;

    }
}