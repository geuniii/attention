

package com.example.attention;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Toast;

import java.text.Collator;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class loginActivity extends AppCompatActivity implements View.OnClickListener{


   // private Retrofit retrofit;
    //private RetrofitInterface retrofitInterface;

    private EditText editTextID;
    private EditText editTextPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        editTextID = findViewById(R.id.id_edit);
        editTextPW = findViewById(R.id.pw_edit);

        editTextID.setTypeface(Typeface.DEFAULT);
        editTextPW.setTypeface(Typeface.DEFAULT);

        findViewById(R.id.login_btn).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.back_button).setOnClickListener((View.OnClickListener) this);

    }

    private void userLogin(){

        String id = editTextID.getText().toString().trim();
        String pw = editTextPW.getText().toString().trim();
        MyGlobals.getInstance().setData(id);



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
        }
        Call <loginResult> call = retrofitClient
                .getInstance().getRetrofitInterface().login(id,pw);
        call.enqueue(new Callback<loginResult>() {
            @Override
            public void onResponse(Call<loginResult> call, Response<loginResult> response) {
                loginResult loginResult = response.body();

                String msg=loginResult.getMessage();
                System.out.println(msg);
                if(msg.equals("Login Success")){
                    System.out.println(".로그인성공");

                    Toast.makeText(loginActivity.this, loginResult.getMessage(),Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(
                            getApplicationContext(),homeActivity.class);
                    startActivity(intent);




                }else{
                    Toast.makeText(loginActivity.this,loginResult.getMessage(),Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<loginResult> call, Throwable t) {

                Toast.makeText(loginActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.login_btn:
                userLogin();
                break;

            case R.id.back_button:
                Intent intent = new Intent(
                        getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;

        }
    }

}


