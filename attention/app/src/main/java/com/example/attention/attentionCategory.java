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

public class attentionCategory extends Fragment {

    String userId;
    View view;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.attention_category, container, false);


        userId = MyGlobals.getInstance().getData();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        StringBuilder percentage =new StringBuilder();

        TextView percentText = view.findViewById(R.id.percent_text1);

        Call<List<attentionCategoryResult>> call = retrofitClient
                .getInstance().getRetrofitInterface().attentionCategory(map);

        call.enqueue(new Callback<List<attentionCategoryResult>>() {
            @Override
            public void onResponse(Call<List<attentionCategoryResult>> call, Response<List<attentionCategoryResult>> response) {

                if (response.code() == 200) {
                    Toast.makeText(getActivity(),
                            "successfully", Toast.LENGTH_LONG).show();

                    List<attentionCategoryResult> result  = response.body();

                    System.out.println("result 길이:"+result.size());
                    String string=" ";

                    for(int x = 0;x<result.size();x++){

                        percentage.append("< "+result.get(x).getCategory()+
                                " >  공부 시간 \n"+result.get(x).getUpDown()+" "+result.get(x).getPercentage()+"%.\n\n\n");
                        System.out.println("< "+result.get(x).getCategory()+
                                " >  공부 시간 \n 상위 "+result.get(x).getPercentage()+"% 입니다.\n\n\n");
                        string +=percentage;

                        System.out.println(x+" "+result.size());

                        if(x==result.size()-1){
                            percentText.setText(percentage);
                        }
                    }


                }
            }
            @Override
            public void onFailure(Call<List<attentionCategoryResult>> call, Throwable t) {

                Toast.makeText(getActivity(), t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });


        return view;

    }




}
