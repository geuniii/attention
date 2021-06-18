
package com.example.attention;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class registerActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText editTextID,editTextPW,editTextAge;
    private RadioGroup genderRadio;
    private Character gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        editTextID = findViewById(R.id.id_edit);
        editTextPW = findViewById(R.id.pw_edit);
        editTextAge = findViewById(R.id.age_edit);
        genderRadio = findViewById(R.id.gender_rg);

        editTextID.setTypeface(Typeface.DEFAULT);
        editTextPW.setTypeface(Typeface.DEFAULT);
        editTextAge.setTypeface(Typeface.DEFAULT);


        findViewById(R.id.done_btn).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.back_button).setOnClickListener((View.OnClickListener) this);


    }

    private void userSignUp(){

        String id = editTextID.getText().toString().trim();
        String pw = editTextPW.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        int checkGender = genderRadio.getCheckedRadioButtonId();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if(id.isEmpty()){
                editTextID.setError("ID is required");
                editTextID.requestFocus();
                return;
            }
            if(pw.isEmpty()){
                editTextPW.setError("Password is required");
                editTextPW.requestFocus();
                return;
            }
            if(age.isEmpty()){
                editTextAge.setError("Age is required");
                editTextAge.requestFocus();
                return;
            }

            if(checkGender==-1) {
                String message = "Check your gender";
                Toast.makeText(registerActivity.this, message, Toast.LENGTH_LONG).show();
                return;
            }else {
                switch(checkGender){
                    case R.id.woman_rb:
                        gender = 'w';
                        break;
                    case R.id.man_rb:
                        gender = 'm';
                        break;

                }
            }

        }

        Call<ResponseBody> call = retrofitClient
                .getInstance()
                .getRetrofitInterface()
                .register(id,pw,age,gender);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try {
                        String responseS = response.body().string();
                        Toast.makeText(registerActivity.this,responseS,Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(registerActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }

    private void findRadioButton(int checkGender) {



    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.done_btn:
                userSignUp();
                break;
            case R.id.back_button:
                Intent intent = new Intent(
                        getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;
        }
    }

}
