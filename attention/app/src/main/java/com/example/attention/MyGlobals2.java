package com.example.attention;

public class MyGlobals2 {
    private double data1;
    private double data2;

    public double getData1() {
        return data1;
    }

    public void setData1(double data1) {
        this.data1 = data1;
    }

    public double getData2() {
        return data2;
    }

    public void setData2(double data2) {
        this.data2 = data2;
    }

    private static MyGlobals2 instance = null;

    public static synchronized  MyGlobals2 getInstance(){
        if(null == instance){
            instance = new MyGlobals2();
        }

        return instance;

    }
}