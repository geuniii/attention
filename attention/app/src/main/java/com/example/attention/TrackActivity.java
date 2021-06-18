package com.example.attention;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrackActivity extends AppCompatActivity {

    // Intent intent = getIntent(); /*데이터 수신*/

    // String "0" = intent.getExtras().getString("studyId");

    String STUDYID;


    private static final String TAG = "TrackActivity";
    private Activity mContext;

    TextView textView;
    TextView textView2;

    //For looking logs
    ArrayAdapter adapter;
    ArrayList<String> list = new ArrayList<>();
    PointF pos;

    CameraSource cameraSource;

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();
    public int opentime; //집중한 시간
    public int closetime; //눈을 감은 시간
    public float x; //인식된 얼굴의 중심 x,y좌표
    public float y;
    public int counter;
    public String startTime;
    private View view;
    String currentId="";

    String longitude;
    String latitude;


    float min;
    float max;


    //SoundPool soundPool;
    //int[] alarmSound = new int[1];



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

    public void start(){

        HashMap<String, String> map = new HashMap<>();
        currentId=MyGlobals.getInstance().getData();

        System.out.println("currentId"+currentId);

        map.put("studyId", STUDYID);//스터디 선택해서 들어가야함->오키(6/14)
        map.put("greenTime", String.valueOf(opentime));
        map.put("fullTime", String.valueOf(counter));
        map.put("userId",currentId);
        map.put("startTime",startTime);
        map.put("latitude",latitude);
        map.put("longitude",longitude);

        // map.put("endTime",endTime);




        Call<Void> call =  retrofitClient
                .getInstance().getRetrofitInterface().executeTest(map);


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    /*Toast.makeText(TrackActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지*/
                    System.out.println("석세스");



                } else if (response.code() == 400) {
                 /*   Toast.makeText(TrackActivity.this,
                            "Already registered", Toast.LENGTH_LONG).show();*/
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TrackActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });

    }


    public void close(View view){
        String endTime=getCurrentTimeStamp();

        HashMap<String, String> map = new HashMap<>();


        currentId=MyGlobals.getInstance().getData();

        System.out.println("currentId"+currentId);

        map.put("studyId", STUDYID);//스터디 선택해서 ->오키(6/14)
        map.put("greenTime", String.valueOf(opentime));
        map.put("fullTime", String.valueOf(counter));
        map.put("userId",currentId);
        map.put("startTime",startTime);
        map.put("endTime",endTime);



        Call<Void> call =  retrofitClient
                .getInstance().getRetrofitInterface().executeTestEnd(map);



        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    /*Toast.makeText(TrackActivity.this,
                            "successfully", Toast.LENGTH_LONG).show();//안드 팝업메시지*/
                    System.out.println("석세스");


                    Intent intent = new Intent(getApplicationContext(),homeActivity.class);
                    startActivity(intent);



                } else if (response.code() == 400) {
                    Toast.makeText(TrackActivity.this,
                            "Already registered", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TrackActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();//안드 팝업메시지

            }
        });



    }



   // public int count=0;//얼굴이 경계를 벗어난 동안의 시간
    StringBuilder GreenUserStatus=new StringBuilder();
    StringBuilder RedUserStatus=new StringBuilder();



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
    /*
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

     */




    @Override
    //메인 액티비티가 실행되면 호출되는 메소드 입니다.카메라의 권한을 허용받았는지 검사하고 앱 화면에 TextView를 등록하며
    // createCameraSource()함수를 실행합니다.이 함수에서 얼굴을 감지하고 눈을 탐지하는 기능을 구현했습니다.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            String result = extras.getString("studyId");
            String result2 = extras.getString("latitude");
            String result3 = extras.getString("longitude");

            System.out.println("인텐트 값 무사히 받아옴 studyId :"+result);
            System.out.println("인텐트 값 무사히 받아옴 studyId :"+result2);
            System.out.println("인텐트 값 무사히 받아옴 studyId :"+result3);
            STUDYID=result;
            latitude=result2;
            longitude=result3;
        }



        TimerTask tt = new TimerTask() {
            //TimerTask 추상클래스를 선언하자마자 run()을 강제로 정의하도록 한다.
            @Override
            public void run() {
                /////////////////// 추가한 코드 ////////////////////


                counter++;
                //////////////////////////////////////////////////
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 0, 1000);

        startTime=getCurrentTimeStamp();

        Intent intent2 = getIntent();

        start();




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
        }
        else {



            findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {




                    HashMap<String, String> map = new HashMap<>();


                    map.put("currentTime", getCurrentTimeStamp());
                    map.put("studyId",STUDYID);//사용자가 여러 스터디 가입시 하나 선택해서 들어오면 그에 맞게 스터디 아이디 부여하도록 변경->오키(6/14)
                    map.put("userId",currentId);


                    Call<List<searchResult>> call=retrofitClient
                            .getInstance().getRetrofitInterface().executeGreenSearch(map);
                    //Call<Void> call = retrofitInterface.executeTest(map);


                    call.enqueue(new Callback<List<searchResult>>() {
                        @Override
                        public void onResponse(Call<List<searchResult>> call, Response<List<searchResult>> response) {
                            List<searchResult>  result=response.body();

                            if (response.code() == 200) {
                                TextView textView3 = (TextView)findViewById(R.id.textView3);

                                // System.out.println(result.getUserId()+"님은 초록");
                                for(int x=0;x<result.size();x++){



                                    GreenUserStatus.append(result.get(x).getUserId()+" 님 공부중\n");
                                    System.out.println(result.get(x).getUserId()+" 님 공부중");


                                }
                                textView3.setTextColor(Color.parseColor("#45e64b"));
                                textView3.setText(GreenUserStatus);
                                GreenUserStatus.setLength(0);

                            } else if (response.code() == 400) {

                                // System.out.println(result.getUserId()+"님은 빨강");

                            }

                        }

                        @Override
                        public void onFailure(Call<List<searchResult>> call, Throwable t) {
                            Toast.makeText(TrackActivity.this, t.getMessage(),
                                    Toast.LENGTH_LONG).show();//안드 팝업메시지

                        }
                    });








                    HashMap<String, String> map2 = new HashMap<>();
                    map2.put("studyId",STUDYID);//선택한 스터디 ->오키(6/14)
                    map2.put("userId",currentId);




                    Call<List<searchResult>> call2=retrofitClient
                            .getInstance().getRetrofitInterface().executeuserIdsearch(map2);
                    call2.enqueue(new Callback<List<searchResult>>() {
                        @Override
                        public void onResponse(Call<List<searchResult>> call2, Response<List<searchResult>> response) {
                            List<searchResult>  result =response.body();


                            if (response.code() == 200) {
                                TextView textView4 = (TextView)findViewById(R.id.textView4);

                                System.out.println("옦께이");




                                for(int x=0;x<result.size();x++){
                                    RedUserStatus.append(result.get(x).getUserId()+" 님 공부종료\n");


                                    System.out.println(result.get(x).getUserId()+" 님 공부종료");


                                }




                                textView4.setTextColor(Color.parseColor("#ff0000"));
                                textView4.setText(RedUserStatus);
                                RedUserStatus.setLength(0);

                            } else if (response.code() == 400) {


                            }

                        }

                        @Override
                        public void onFailure(Call<List<searchResult>> call, Throwable t) {
                            Toast.makeText(TrackActivity.this, t.getMessage(),
                                    Toast.LENGTH_LONG).show();//안드 팝업메시지

                        }
                    });


                }

            });


            textView = findViewById(R.id.textView);
            textView2 = findViewById(R.id.textView2);
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);


            createCameraSource();
        }




    }



    // EyesTracker 클래스는 google vision api를 이용하여 눈을 탐지합니다.
    private class EyesTracker extends Tracker<Face> {

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        int width = dm.widthPixels;

        private final float THRESHOLD = 0.9f;
        private final float LeftTHRESHOLD = (float) width; //얼굴이 화면에서 왼쪽으로 벗어났는지 체크하는 경계값
        private final float RightTHRESHOLD = (float) width/100; //얼굴이 화면에서 오른쪽으로 벗어났는지 체크하는 경계값


        /*Face Detetor에서 얼굴 요소를 가져와 눈을 뜨고 있는지 검사하여 집중시간을 계산합니다 */
        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {

            x = face.getPosition().x + (face.getWidth() / 2);
            y = face.getPosition().y + (face.getHeight() / 2);
            System.out.println("인식된 얼굴의 중심점 (x,y) : "+x+","+y);

                if(x<LeftTHRESHOLD) {

                    if (x>RightTHRESHOLD){
                    /*얼굴이 화면에서 왼쪽,오른쪽 경계값보다 작으면( 즉 얼굴이 집중한다라고 판단되는 위치에 존재하면)
                     집중시간을 1초씩 증가합니다. */
                        if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                        Log.i(TAG, "onUpdate: 눈을 뜸");
                        opentime += 1;
                        System.out.println("웃는정도 :"+face.getIsSmilingProbability());
                        chkAttention(face.getIsSmilingProbability());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textView.setTextColor(0xAA45e64b);
                        //FaceOutCount(count);

                        System.out.println("집중 시간"+opentime);

                        showStatus("집중 시간 " + Timer(opentime),1);
                        showStatus("",2);
                    }else {
                        textView2.setTextColor(0xAAFF4081);
                        //count+=1;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textView.setTextColor(0xAA000000);
                        showStatus("집중 시간" + Timer(opentime),1);
                        //showStatus("눈을 뜨고 있지 않습니다",2);

                    }
                }else {
                    textView2.setTextColor(0xAAFF4081);
                    //count+=1;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    textView.setTextColor(0xAA000000);
                    showStatus("집중 시간" + Timer(opentime),1);
                    showStatus("얼굴이 인식되지 않습니다",2);


                }

            }
            else {
                closetime+=1;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("눈감음"+closetime);
                textView.setTextColor(0xAA000000);
                showStatus("눈을 감고 있는 시간 "+Timer(closetime),1);


                Log.i(TAG, "onUpdate: 눈을 감음");
                //soundPool.play(alarmSound[1],1,1,1,0,1);


            }
        }

        private void chkAttention(final float isSmilingProbability) {

            Call<ResponseBody> call = retrofitClient
                    .getInstance()
                    .getRetrofitInterface()
                    .attentionChk(currentId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {



                    try {
                        String res = response.body().string();
                        System.out.println("체크어텐션"+response.body().string());
                        //System.out.println(res.substring(1,10)+"/"+res.substring(13,22));
                        min= Float.parseFloat( res.substring(1,7));
                        max= Float.parseFloat( res.substring(9,15));


                        System.out.println("min :"+min);
                        System.out.println("max :"+max);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(isSmilingProbability>max){
                        Toast.makeText(getApplicationContext(),"집중이 흐트러짐",Toast.LENGTH_LONG).show();
                        //soundPool.play(alarmSound[0],1,1,1,0,1);
                    }
                    // Toast.makeText(getApplicationContext(),responseS,Toast.LENGTH_LONG).show();


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }


        /*FaceDetetor에서 얼굴이 검출되지 않으면 시행되는 메소드*/
        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);

            Log.i(TAG, "onMissing: 카메라 얼굴감지 실패");
            //System.out.println("(x,y) : "+x+","+y);

            textView.setTextColor(0xAA000000);
            textView2.setTextColor(0xAAFF4081);
            showStatus("집중 시간 " + Timer(opentime),1);
            showStatus("얼굴이 인식되지 않습니다 ",2);
            //count+=1;


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
                    textView.setText(message);

                }else {
                    textView2.setText(message);
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




    /*
  protected  void onStart(){
        super.onStart();
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          AudioAttributes audioAttributes = new AudioAttributes.Builder()
                  .setUsage(AudioAttributes.USAGE_GAME)
                  .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                  .build();

          soundPool = new SoundPool.Builder()
                  .setAudioAttributes(audioAttributes)
                  .setMaxStreams(6)
                  .build();

      }else {
          soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);
      }

      for (int i=0; i<alarmSound.length; i++) {
          alarmSound[i] = soundPool.load(getApplicationContext(), R.raw.alarm_2 + i, 1);

      }


  }

    protected void onStop() {
        super.onStop();
        soundPool.release();

    }

     */

}