package com.example.attention;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(

            @Field("userid") String userid,
            @Field("password") String password,
            @Field("age") String age,
            @Field("gender") Character gender

    );

    @FormUrlEncoded
    @POST("login")
    Call<loginResult> login(
            @Field("userid") String userid,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("newStudy")
    Call<ResponseBody> newStudy(
            @Field("category") String category,
            @Field("studyName") String studyName,
            @Field("startDate") String startDate,
            @Field("endDate") String endDate,
            @Field("userId") String userId
    );


    @FormUrlEncoded
    @POST("attentionChk")
    Call<ResponseBody> attentionChk(
            @Field("userId") String userId
    );




    @POST("enterStudy")
    Call<enterResult> enterStudy(@Body HashMap<String ,String > map);

    @POST("resisterStudy")
    Call<Void> resisterStudy(@Body HashMap<String ,String > map);

    @POST("/test")
    Call<Void> executeTest(@Body HashMap<String ,String > map);

    @POST("/testEnd")

    Call<Void> executeTestEnd(@Body HashMap<String ,String > map);
    @POST("/Greensearch")

    Call<List<searchResult>> executeGreenSearch(@Body HashMap<String,String> map);

    @POST("/userIdsearch")
    Call<List<searchResult>> executeuserIdsearch(@Body HashMap<String,String> map);

    @POST("/StudySearch")
    Call<List<StudySearchResult>> executeStudySearch(@Body HashMap<String,String> map);

    @POST("/GroupChart")
    Call<List<GroupChartResult>> executeGroupChart(@Body HashMap<String,String> map);

    @POST("/greenByFull")
    Call<List<Result_day_GreenByFull>> greenByFull(@Body HashMap<String,String> map);


    @POST("/attentionMinMax")
    Call<Void> attentionMinMax(@Body HashMap<String, String> map);


    @POST("/categoryChart")
    Call<List<categoryChartResult>> categoryChart(@Body HashMap<String,String> map);

    @POST("/selectStudy")
    Call<List<StudySearchResult>> selectStudy(@Body HashMap<String,String> map);

    @POST("/studyNameToId")
    Call<String> studyNameToId(@Body HashMap<String, String> map);

    @FormUrlEncoded
    @POST("newLocation")
    Call<ResponseBody> newLocation(
            @Field("loCategory") String loCategory,
            @Field("loName") String studyName,
            @Field("userId") String userId,
            @Field("latitude") Double latitude,
            @Field("longitude") Double longitude,
            @Field("studyStart") String studyStart);

    @POST("/locationSearch")
    Call<List<locationResult>> locationSearch(@Body HashMap<String,String> map);

    @POST("/studyOut")
    Call<ResponseBody> studyOut(@Body HashMap<String,String> map);


    @POST("/WeeklyChart")
    Call<List<WeeklyFulltimeResult>> executeWeeklyChart(@Body HashMap<String,String> map);

    @POST("/attentionRanking")
    Call<List<attentionRankingResult>> attentionRanking(@Body HashMap<String,String> map);

    @POST("/PlaceRecommend")
    Call<List<PlaceRecommendResult>> executePlaceRecommend(@Body HashMap<String,String> map);

    @POST("/myStudyList")
    Call<List<StudySearchResult>> myStudyList(@Body HashMap<String,String> map);
    @POST("/attentionCategory")
    Call<List<attentionCategoryResult>> attentionCategory(@Body HashMap<String,String> map);
    @POST("/ageAttention")
    Call<List<ageAttentionResult>> ageAttention(@Body HashMap<String,String> map);
    @POST("/todayStudy")
    Call<List<todayResult>> todayStudy(@Body HashMap<String,String> map);


}







