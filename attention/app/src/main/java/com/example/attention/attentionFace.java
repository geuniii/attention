package com.example.attention;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Collections;


public class attentionFace extends AppCompatActivity implements View.OnClickListener{


    //For looking logs
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();
    PointF pos;

    private static final String TAG = "attentionFace";
    CameraSource cameraSource;

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();
    public int opentime; //집중한 시간
    public int closetime; //눈을 감은 시간
    public float x; //인식된 얼굴의 중심 x,y좌표
    public float y;
    public int counter;
    public String startTime;
    private View view;

    List<Float> listData = new ArrayList<Float>();
    TextView attentionText;
    Button close;

    String currentId=MyGlobals.getInstance().getData();





    public int count=0;//얼굴이 경계를 벗어난 동안의 시간



    public String Timer(int tmp){//눈뜬 시간을 초단위를 hh:mm:ss 로 변환

        int h=tmp/3600;
        tmp=tmp%3600;
        int m=tmp/60;
        int s=tmp%60;


        String hhmmss=(Integer.toString(h)+":"+Integer.toString(m)+":"+Integer.toString(s));


        return hhmmss;
    }

    /*FaceOutCount() 는 얼굴이 경계값에서 벗어난 시간을 매개변수(int cnt)로 받아
    얼굴이 벗어난 시간이 10초 미만이면 집중시간에 벗어난 시간을 더해주고
    opentime=opentime+cnt;

    10초 이상이면 집중한 시간으로 판단하지 않습니다. */
    public void FaceOutCount(int cnt){
        if(cnt>10) {
            System.out.println("10초 이상 얼굴이 경계를 벗어남");
            count=0;
        }else{
            // System.out.println("10초 미만이므로 집중시간에 추가");

            opentime=opentime+cnt;
            count=0;

        }

    }



    @Override
    public void onClick(View v) {
        HashMap<String, String> map = new HashMap<>();

        System.out.println("집중얼굴등록");
        DecimalFormat form = new DecimalFormat("#.#####");
        map.put("userId", currentId);//스터디 선택해서 들어가야함
        float mintemp = Collections.min(listData);
        float maxtemp = Collections.max(listData);
        String min = form.format(mintemp);
        String max = form.format(maxtemp);
        System.out.println("min값: "+ min+" max값: " +max);
        map.put("min",min);
        map.put("max",max);


        Call<Void> call =  retrofitClient
                .getInstance().getRetrofitInterface().attentionMinMax(map);




        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                   /* Toast.makeText(getApplication(),
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지*/
                    System.out.println("집중얼굴등록석세스");







                } else if (response.code() == 400) {
                 /*   Toast.makeText(getApplication(),
                            "Already registered", Toast.LENGTH_LONG).show();*/
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplication(), t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });



        finish();
        /*

        Intent intent = new Intent(
                getApplicationContext(),locationActivity.class);
        startActivity(intent);


         */
    }



    @Override
    //메인 액티비티가 실행되면 호출되는 메소드 입니다.카메라의 권한을 허용받았는지 검사하고 앱 화면에 TextView를 등록하며
    // createCameraSource()함수를 실행합니다.이 함수에서 얼굴을 감지하고 눈을 탐지하는 기능을 구현했습니다.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attentonface);

        TimerTask tt = new TimerTask() {
            //TimerTask 추상클래스를 선언하자마자 run()을 강제로 정의하도록 한다.
            @Override
            public void run() {
                /////////////////// 추가한 코드 ////////////////////


                counter++;
                //////////////////////////////////////////////////
            }
        };

        ImageView attention = (ImageView) findViewById(R.id.gif);
        GlideDrawableImageViewTarget gifimage=new GlideDrawableImageViewTarget(attention);
        Glide.with(this).load(R.drawable.attention).into(gifimage);

        findViewById(R.id.close).setOnClickListener((View.OnClickListener) this);





        attentionText=(TextView)findViewById(R.id.attentionText) ;

        Timer timer = new Timer();
        timer.schedule(tt, 0, 1000);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        else {

            createCameraSource();
        }




    }



    // EyesTracker 클래스는 google vision api를 이용하여 눈을 탐지합니다.
    private class EyesTracker extends Tracker<Face> {


        private final float THRESHOLD = 0.9f;
        private final float LeftTHRESHOLD = (float) 600; //얼굴이 화면에서 왼쪽으로 벗어났는지 체크하는 경계값
        private final float RightTHRESHOLD = (float) 30; //얼굴이 화면에서 오른쪽으로 벗어났는지 체크하는 경계값

        private EyesTracker() {

        }


        /*Face Detetor에서 얼굴 요소를 가져와 눈을 뜨고 있는지 검사하여 집중시간을 계산합니다 */
        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {



            x = face.getPosition().x + (face.getWidth() / 2);
            y = face.getPosition().y + (face.getHeight() / 2);
            System.out.println("인식된 얼굴의 중심점 (x,y) : "+x+","+y);



            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                //Log.i(TAG, "onUpdate: Eyes Detected");

                if(x<LeftTHRESHOLD) {

                    if (x>RightTHRESHOLD){
                    /*얼굴이 화면에서 왼쪽,오른쪽 경계값보다 작으면( 즉 얼굴이 집중한다라고 판단되는 위치에 존재하면)
                     집중시간을 1초씩 증가합니다. */
                        Log.i(TAG, "onUpdate: 눈을 뜸");
                        listData.add(face.getIsSmilingProbability());

                        System.out.println("리스트 추가함 :"+listData.toString());

                        System.out.println("min="+Collections.min(listData));
                        System.out.println("max="+Collections.max(listData));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        FaceOutCount(count);




                    }else {

                        count+=1;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }





                    }
                }else {

                    count+=1;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    showStatus("얼굴이 경계에서 벗어남",2);

                }









            }
            else {


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }






                Log.i(TAG, "onUpdate: 눈을 감음");



            }
        }





        /*FaceDetetor에서 얼굴이 검출되지 않으면 시행되는 메소드*/
        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);

            Log.i(TAG, "onMissing: 카메라 얼굴감지 실패");
            //System.out.println("(x,y) : "+x+","+y);



            showStatus("얼굴이 경계에서 벗어남 ",2);
            count+=1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }




        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    /*Face 트래커 인스턴스를 만드는 공장입니다. Face가 탐지되면 해당 항목에 대한 모든 알림을 연속 프레임에 대해 수신합니다*/
    private class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

        private FaceTrackerFactory() {

        }

        @Override
        public Tracker<Face> create(Face face) {
            return new EyesTracker();
        }
    }

    /*카메라가 시작되면 계속해서  이미지를 파이프 라인으로 보낸다.(파이프라인 이미지 참고)
CameraSource 구성 요소는 이러한 이미지를 수신하고 앞서 설정한 것처럼 초당 30 프레임으로 이미지를 Detector에 전달한다.*/

    public void createCameraSource() {//안드로이드 카메라에서 영상을 받아옵니다

        /*FaceDetector는  탐지 옵션을 지정하여 연결된 빌더 클래스를 통해 생성됩니다.*/
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)//이렇게 옵션을 지정하면 눈을 뜨고 있는지 탐지할수 있습니다.
                .setMode(FaceDetector.FAST_MODE)//이렇게 하면 얼굴을 적게 탐지하는 경향이 있으며 위치와 같은 값을 결정하는 데는 덜 정확할 수 있지만 더 빨리 실행됩니다.
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerFactory()).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)//카메리 정면일때만 얼굴 인식됨 가로모드에서도 할거면 여기 수정
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource!=null) {
            cameraSource.stop();
        }

    }

    public void showStatus(final String message,final int num) {//앱 화면에서 TextView에 메시지를 출력하는 기능입니다.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(num==1){


                }else {

                }



            }


        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource!=null) {
            cameraSource.release();
        }

    }



}