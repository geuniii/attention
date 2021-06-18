package com.example.attention;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.attention.MyGlobals.*;

public class locationActivity extends AppCompatActivity implements AutoPermissionsListener, GoogleMap.OnMarkerClickListener {

    SupportMapFragment mapFragment;
    GoogleMap map;

    MarkerOptions myLocationMarker;

    MarkerOptions allLocationMarker;
    TextView locationView;
    String currentId;
    double clickLatitude;
    double clickLongitude;

    private String tmp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Intent intent = getIntent(); /*데이터 수신*/

        String studyname = intent.getExtras().getString("studyName");
        HashMap<String, String> map2 = new HashMap<>();


        map2.put("studyName",studyname);





        Call<String> call =  retrofitClient
                .getInstance().getRetrofitInterface().studyNameToId(map2);


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse( Call<String> call, Response<String> response) {

                tmp =response.body();

                // List<StudySearchResult>  result=response.body();
                if (response.code() == 200) {


                    System.out.println("스터디아이디 "+ tmp);



                } else if (response.code() == 400) {

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {


            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("Map", "지도 준비됨.");
                map = googleMap;

            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button locationBtn = findViewById(R.id.location_btn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationService();
            }
        });


        Button locationSearchBtn = findViewById(R.id.locationSearch_btn);
        locationSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationService();
            }
        });


        Button loRegistBtn = findViewById(R.id.loRegist_btn);
        loRegistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), newLocationActivity.class);
                startActivity(intent);
            }
        });

        Button faceBtn = findViewById(R.id.face_btn);
        faceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(), attentionFace.class);
                startActivity(intent);
            }
        });



        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    public void startLocationService() {
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location==null){
                location=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                MyGlobals2.getInstance().setData1(latitude);
                MyGlobals2.getInstance().setData2(longitude);

                String message = "최근 위치 -> Latitude : " + latitude + "\nLongitude:" + longitude;
                Log.d("Map1", message);
            }
            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, gpsListener);

            Toast.makeText(getApplicationContext(), "내 위치확인 요청함",
                    Toast.LENGTH_SHORT).show();

            gpsListener.onLocationChanged(location);




        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void LocationService() {

        HashMap<String, String> map = new HashMap<>();
        currentId=MyGlobals.getInstance().getData();
        map.put("userId",currentId);

        Call<List<locationResult>> call =  retrofitClient
                .getInstance().getRetrofitInterface().locationSearch(map);

        call.enqueue(new Callback<List<locationResult>>() {
            @Override
            public void onResponse(Call<List<locationResult>> call, Response<List<locationResult>> response) {
                List<locationResult> result=response.body();
                if (response.code() == 200) {
                    for(int x=0;x<result.size();x++) {
                        LatLng Point = new LatLng(result.get(x).getLatitude(),result.get(x).getLongitude());
                        System.out.println("이름: "+result.get(x).getLoName()+"\n"+
                                "경도: "+result.get(x).getLatitude()+"\n"+
                                "위도: "+result.get(x).getLongitude()+"\n");

                        showAllLocationMarker(Point,result.get(x).getLoName());
                    }

                } else if (response.code() == 400) {
                    Toast.makeText(locationActivity.this,
                            "ERROR", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<locationResult>> call, Throwable t) {

            }
        });



    }



    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            String message = "내 위치 -> Latitude : "+ latitude + "\nLongitude:"+ longitude;
            Log.d("Map2", message);

            //소수점 셋째자리까지 정도면 한 건물 내부로 보면 될듯

            showCurrentLocation(latitude, longitude);
        }

        public void onProviderDisabled(String provider) { }

        public void onProviderEnabled(String provider) { }

        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    private void showCurrentLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        showMyLocationMarker(curPoint);
    }

    private void showMyLocationMarker(LatLng curPoint) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(curPoint);
            myLocationMarker.title("● 내 위치\n");
            myLocationMarker.snippet("● GPS로 확인한 위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation));
            map.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(curPoint);
        }
    }
    private void showAllLocationMarker(LatLng Point,String name) {
        allLocationMarker = new MarkerOptions();
        allLocationMarker.position(Point);
        allLocationMarker.title(name);
        allLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationsearch));
        map.addMarker(allLocationMarker);
        map.setOnMarkerClickListener(this);
    }

    private void alert(final double clickLatitude, final double clickLongitude){

        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                R.style.MyAlertDialogStyle);

        oDialog.setMessage("공부를 시작하시겠습니까?")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.i("Dialog", "취소");
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(
                                getApplicationContext(), TrackActivity.class);
                        intent.putExtra("studyId",tmp);
                        System.out.println("@@@clicklatitude 찍음"+clickLatitude);

                        String latitude=Double.toString(clickLatitude);
                        String longitude=Double.toString(clickLongitude);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        startActivity(intent);
                    }
                })
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        clickLatitude = marker.getPosition().latitude;
        clickLongitude = marker.getPosition().longitude;
        alert(clickLatitude,clickLongitude);


        return true;
    }


}
