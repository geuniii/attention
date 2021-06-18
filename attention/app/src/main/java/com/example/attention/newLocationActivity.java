package com.example.attention;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class newLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView loCategoryView,loNameView;
    private EditText loNameEdit;
    private Spinner loSpinner;
    String userId="";
    double latitude = 0;
    double longitude = 0;
    String studyStart="";



    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlocation_activity);

        loCategoryView = (TextView) findViewById(R.id.loCategory_view);
        loNameView = (TextView) findViewById(R.id.loName_view);

        loNameEdit = (EditText) findViewById(R.id.loName_edit);

        loSpinner = (Spinner) findViewById(R.id.loCategory_sp);

        findViewById(R.id.loMake_btn).setOnClickListener((View.OnClickListener) this);

    }

    private void newLocation() {

        String loName = loNameEdit.getText().toString().trim();

        String loCategory= loSpinner.getSelectedItem().toString();

        HashMap<String, String> map = new HashMap<>();
        userId= MyGlobals.getInstance().getData();
        latitude = MyGlobals2.getInstance().getData1();
        longitude = MyGlobals2.getInstance().getData2();

        //

        studyStart=getCurrentTimeStamp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (loName.isEmpty()) {
                loNameEdit.setError("Study name is required");
                loNameEdit.requestFocus();
                return;
            }

        }

        Call<ResponseBody> call = retrofitClient
                .getInstance()
                .getRetrofitInterface()
                .newLocation(loCategory,loName,userId,latitude,longitude,studyStart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String responseS = response.body().toString();
                Toast.makeText(newLocationActivity.this,
                        "장소가 등록되었습니다", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Toast.makeText(newLocationActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.loMake_btn:
                newLocation();
                break;
        }
    }


}



