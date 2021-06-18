
package com.example.attention;

import com.google.gson.annotations.SerializedName;

public class loginResult {

    private String message;
    private boolean error;
    private user user;
    private int resultCode;


    public loginResult(boolean error, String message, com.example.attention.user user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public com.example.attention.user getUser() {
        return user;
    }
}
