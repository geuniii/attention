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
import androidx.fragment.app.FragmentTransaction;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ageAttention extends Fragment {
    String userId;
    View view;



    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ageattention, container, false);



        userId = MyGlobals.getInstance().getData();
        HashMap<String, String>map = new HashMap<>();
        map.put("userId", userId);

        StringBuilder agecompare = new StringBuilder();

        TextView agecompareText = view.findViewById(R.id.agecompare_text);

        Call<List<ageAttentionResult>> call = retrofitClient
                .getInstance().getRetrofitInterface().ageAttention(map);

        call.enqueue(new Callback<List<ageAttentionResult>>() {
            @Override
            public void onResponse(Call<List<ageAttentionResult>> call, Response<List<ageAttentionResult>> response) {

                if(response.code() == 200) {
                    Toast.makeText(getActivity(), "success", Toast.LENGTH_LONG).show();

                    List<ageAttentionResult> result = response.body();

                    System.out.println("result: "+result);

                    for(int x = 0; x<result.size(); x++) {
                        agecompare.append("상위 "+result.get(x).getPercent()+"%입니다. \n");
                        agecompareText.setText(agecompare);
                        System.out.println("상위 "+result.get(x).getPercent()+"% 입니다.\n");

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ageAttentionResult>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),"You just enter the ageAttention onFailure()", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
