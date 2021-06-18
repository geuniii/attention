package com.example.attention;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class attentionRanking extends Fragment {

    String userId;
    View view;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attentionranking, container, false);


        userId = MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        StringBuilder percentage =new StringBuilder();

        TextView percentText = view.findViewById(R.id.percent_text);


        Call<List<attentionRankingResult>> call =  retrofitClient
                .getInstance().getRetrofitInterface().attentionRanking(map);

        call.enqueue(new Callback<List<attentionRankingResult>>() {
            @Override
            public void onResponse(Call<List<attentionRankingResult>> call, Response<List<attentionRankingResult>> response) {
                if (response.code() == 200) {
                    Toast.makeText(getActivity(),
                            "successfully", Toast.LENGTH_LONG).show();

                    List<attentionRankingResult> result  = response.body();

                    for(int x = 0;x<result.size();x++){

                            percentage.append("상위 "+result.get(x).getPercentage()+"% 입니다.\n");
                            percentText.setText(percentage);
                            System.out.println("상위"+result.get(x).getPercentage()+"% 입니다.\n");

                    }
                }
            }

            @Override
            public void onFailure(Call<List<attentionRankingResult>> call, Throwable t) {

                Toast.makeText(getActivity(), t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


        return view;

}




}
