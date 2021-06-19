# Attention

**집중 효율을 높이는 실시간 온라인 스터디 어플리케이션**

![attention_mini](https://user-images.githubusercontent.com/62981623/122640146-cb0e0700-d138-11eb-9c27-2d0b41f5c5bc.png)



실시간 비대면 스터디 어플리케이션을 개발하여 자동화된 학습 관리 서비스로 효율적인 학습을 돕는다.

<br/><br/><br/>

## 개발 배경

![spdlqj](https://user-images.githubusercontent.com/62981623/122647196-f6571d00-d15d-11eb-8a83-dee72a0e5279.jpg)
- 언택트 시대에 접어들면서 자택 학습 플랫폼이 요구되고 있는 상황이다.
- 대면 스터디 원을 모집하는 경우 발생할 수 있는 제약들을 극복 할 수 있어야 한다.
- 자동화된 학습 관리 서비스로 자기 주도 학습의 능률을 높일 수 있어야 한다.


<br/><br/><br/>

## 개발환경

- **개발언어** : java, javaScript, Xml

- **개발도구**  : Android SDK, JDK, MySQL, Node.js

- **IDE**  : Android Studio, Visual Studio Code, MySQL Workbench

<br/><br/><br/>

## 주요 기능

- 비대면으로 스터디 모집
- 집중 판단 알고리즘으로 집중 시간 측정
- 총 공부시간 대비 집중 시간 분석
- 당일, 당주, 과목별 학습 현황 분석
- 타 사용자들과 집중도, 공부량 비교 분석
- 개인별 학습 효율 높은 장소 분석


<br/><br/><br/>

## 서비스 구성도

![service](https://user-images.githubusercontent.com/62981623/122640699-ea5a6380-d13b-11eb-8c63-ae2ed211651b.png)


<br/><br/><br/>

##  ERD

![entity](https://user-images.githubusercontent.com/62981623/122640799-4b823700-d13c-11eb-9041-ce9f5b6e1434.png)

  
<br/><br/><br/>

## 시스템 설계도

![ss 2](https://user-images.githubusercontent.com/62981623/122645833-58f8ea80-d157-11eb-9457-0fa7fe8750d2.png)

<br/><br/><br/>

## 시연 영상

[![at](https://user-images.githubusercontent.com/62981623/122646188-e721a080-d158-11eb-9e54-274c57920ceb.jpg)
](https://www.youtube.com/watch?v=APLjOGn8fKk")

<br/><br/><br/>


## 프로젝트의 특장점

### 1. 공부시간, 집중 시간을 분리하여 기록

![rooom](https://user-images.githubusercontent.com/62981623/122640768-2f7e9580-d13c-11eb-816c-a2c5df2869d2.jpg)

기존 스터디 관리 어플리케이션들은 사용자의 공부시간 ON/OFF 수동 조정, 해당 어플리케이션의 이용 중 기기를 뒤집어 놓은 시간 측정 등 단순 공부 시간 측정만 가능하다.

→ 기존 어플리케이션과 차별화를 두어 사용자의 눈이 뜨고 있는 시간과 집중 표정 수치를 측정하여 자동으로 집중 시간 또한 측정 가능하다.

```java

 /**
         * 눈의 개폐 여부 인식
         *
         * @param detections : Face Detetor
         * @param face       : 사용자의 얼굴
         */
        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {

            x = face.getPosition().x + (face.getWidth() / 2);
            y = face.getPosition().y + (face.getHeight() / 2);

            if (x < LeftTHRESHOLD) {
                if (x > RightTHRESHOLD) {
                    //집중시간 1초씩 증가
                    if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                        opentime += 1;
                        chkAttention(face.getIsSmilingProbability());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textView.setTextColor(0xAA45e64b);

                        showStatus("집중 시간 " + Timer(opentime), 1);
                        showStatus("", 2);
                    } else {
                        textView2.setTextColor(0xAAFF4081);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        textView.setTextColor(0xAA000000);
                        showStatus("집중 시간" + Timer(opentime), 1);
                    }
                } else { // 얼굴이 10초 이상 인식되지 않음
                    textView2.setTextColor(0xAAFF4081);
                    //count+=1;

                    try { 
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    textView.setTextColor(0xAA000000);
                    showStatus("집중 시간" + Timer(opentime), 1);
                    showStatus("얼굴이 인식되지 않습니다", 2);
                }

            } else { // 눈을 감고 있는 시간이 10초 이상
                closetime += 1;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                textView.setTextColor(0xAA000000);
                showStatus("눈을 감고 있는 시간 " + Timer(closetime), 1);

            }
        }
```
<br/><br/>

### 2. 집중 흐트러짐 경고

 사용자가 공부 중 집중 흐트러짐을 알 수 있도록 개인별로 저장된 집중 표정 수치 범위에서 벗어나면 경고를 준다.

<br/><br/>

### 3.  공부 시간, 집중 시간 분석

사용자와 타 사용자들의 공부 시간, 집중시간을 연령대, 과목 별로 비교하여 공부량과 집중도 자가 점검 가능하다. 

![study](https://user-images.githubusercontent.com/62981623/122640591-5dafa580-d13b-11eb-95c6-695dbf3ffa7c.jpg)

```java
                   // 꺾은선 그래프
                    AnyChartView anyChartView = (AnyChartView) view.findViewById(R.id.any_chart_view);
                    Cartesian cartesian = AnyChart.line();
                    cartesian.animation(true);
                    cartesian.padding(10d, 20d, 5d, 20d);
                    cartesian.crosshair().enabled(true);
                    cartesian.crosshair()
                            .yLabel(true)
                            .yStroke((Stroke) null, null, null, (String) null, (String) null);
                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                    cartesian.title("오늘 공부시간 vs  집중시간");

                    cartesian.yAxis(0).title("시간(분단위)");// Y 축 : 공부 시간 
                    cartesian.xAxis(0).title("시작 시간");// X 축 : 시작 시간 
                    cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                    Set set = Set.instantiate();
                    set.data(seriesData);

                    Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
                    Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");


                    // 집중시간 그래프
                    Line series1 = cartesian.line(series1Mapping);
                    series1.name("집중시간");
                    series1.hovered().markers().enabled(true);
                    series1.hovered().markers()
                            .type(MarkerType.CIRCLE)
                            .size(4d);
                    series1.tooltip()
                            .position("right")
                            .anchor(Anchor.LEFT_CENTER)
                            .offsetX(5d)
                            .offsetY(5d);

                    // 공부시간 그래프
                    Line series2 = cartesian.line(series2Mapping);
                    series2.name("공부시간");
                    series2.hovered().markers().enabled(true);
                    series2.hovered().markers()
                            .type(MarkerType.CIRCLE)
                            .size(4d);
                    series2.tooltip()
                            .position("right")
                            .anchor(Anchor.LEFT_CENTER)
                            .offsetX(5d)
                            .offsetY(5d);


                    anyChartView.setChart(cartesian);
```

<br/><br/>

#### 4. 학습 리포트 서비스 제공
  
![report](https://user-images.githubusercontent.com/62981623/122640470-c2b6cb80-d13a-11eb-81c9-31df99420be2.jpg)

- 같은 연령대의 타 사용자들과 집중력을 비교 분석하여 백분위로 분석해준다.

- 과목 별 공부량을 백분위로 분석해 준다.

- 장소 별 집중도를 차트로 시각화하여 집중 효율이 높은 장소를 보여준다.

```java
      Call<List<ageAttentionResult>> call2 = retrofitClient
                .getInstance().getRetrofitInterface().ageAttention(map);

        call2.enqueue(new Callback<List<ageAttentionResult>>() {
            @Override
            public void onResponse(Call<List<ageAttentionResult>> call, Response<List<ageAttentionResult>> response) {

                if(response.code() == 200) {
                    Toast.makeText(analyzeActivity.this, "success", Toast.LENGTH_LONG).show();

                    List<ageAttentionResult> result2 = response.body();

                    for(int x = 0; x<result2.size(); x++) {

                        group.append("같은 연령대인  <"+result2.get(x).getGroup()+"> 중 당신의 집중력은");
                        group_text.setText(group);
                        percent.append(result2.get(x).getUpDown()+" "+result2.get(x).getPercent()+"%");
                        agePercent_text.setText(percent);

                        System.out.println(result2.get(x).getUpDown()+" "+result2.get(x).getPercent()+"%");

                    }
                }
            }
```

<br/><br/><br/>

## 문제점 및 해결방안

### 1. 얼굴 인식 문제
Python 기반 OpenCV 활용하여 눈의 개폐 여부를 인식하여 Client측에서 집중을 판단하려다 Python을 안드로이드에서 돌리는 데에 문제가 생겼다. 
<br/>
**첫번째 해결방안**
client에서 집중을 판단하지 않고, Server 측에서 판단하도록 구현하는 방법으로 해결하려 하였다. 하지만, AWS 등 외부 서버를 추가해야 했다.단순 눈의 개폐 여부를 판단하기 위해 Server를 추가하기엔 비효율적이라고 판단하여 다른 방법을 찾아야 했다.
<br/>
**두번째 해결방안**
Client측에서 집중을 판단할 수 있어야 했기 때문에, OpenCV를 포기하고 다른 API를 찾아 보았다. Google Vision API의 FaceDetector 클래스를 활용하여 눈의 개페 여부를 인식할 수 있었다.

<br/><br/>

### 2. 현재 위치 조회 문제
GPS_PROVIDER로 현재 위치를 받아오도록 구현하였지만, 추적을 실패하는 일이 잦았다. GPS_PROVIDER는 수신 상태가 약할 때 속도가 느리기 때문이다. 따라서 정확도는 떨어지지만 속도가 빠른 NETWORK_PROVIDER를 추가하였다. 두 가지 모두를 활용하여 GPS_PROVIDER로 추적에 실패하면 NETWORK_PROVIDER로 위치를 추적하게 구현하여 해결하였다.

<br/><br/>

### 3. 집중 표정 등록 문제
![은근최GO_attention_5](https://user-images.githubusercontent.com/62981623/122646506-711e3900-d15a-11eb-97f4-f1fdff90fddc.gif)

사용자 별로 집중하는 표정을 등록해야 했지만, 등록하기 위해 집중하는 표정을 지을 만한 컨텐츠가 필요했다. 최면에 걸릴 때 추에 집중하는 것을 연상하여 시계를 직접 움직이며 촬영하여 집중하는 표정을 얻을 수 있게 되었다.


<br/><br/><br/>

## 개선방안

### - Eye Tracking 기술과 접목

눈의 개폐여부를 판단하는 것에 더하여 눈의 움직임을 추적한다면 시험 감독 등 관리 감독이 필요한 부분에 감독 서비스로 활용할 수 있다.

### - 광고 서비스로 확대

사용자의 공부 효율이 높은 장소 분류에 따라 실제 장소를 추천해 준다면 광고 서비스로 확대될 수 있다.


<br/><br/><br/>

## 팀 역할

#### <박근희>

- 스터디 관리(스터디 개설/ 스터디 탈퇴)
- 현재 위치 조회 및 시각화 기능
- 공부 장소 등록 및 추천 기능
- 집중도 백분위 분석(전체 사용자별/ 연령별 집중도)
- 데이터 시각화(과목별 공부시간, 당일 총 공부시간)
- UI 디자인 및 제작
- 사용자 등록(로그인/회원가입)
- 집중 판단 알고리즘 유지 보수

<br/><br/>

#### <박성은>

- 집중판단 알고리즘 구현
- 스터디 관리(기존스터디 가입, 검색)
- 데이터 시각화(공부시간대비 집중시간, 그룹별 공부시간, 일주일 공부시간)
- 공부방 입장 
- 스터디 선택, 집중 얼굴 등록
- 현재 공부 중인 스터디 원 조회
- 집중 흐트러짐 판단
  
<br/><br/><br/>

## 참고 문헌

- **Google VISION API**   https://cloud.google.com/vision/?hl=ko

- **Google MAP API**  https://developers.google.com/maps/documentation?hl=ko

- **AnyChart API**  https://api.anychart.com/

- **네이버 카페 ‘스터디 모집’ 검색 결과** 

  https://cafe.naver.com/ca-fe/home/search/articles?q=%EC%8A%A4%ED%84%B0%EB%94%94%20%EB%AA%A8%EC%A7%91
