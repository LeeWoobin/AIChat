package com.crazyup.utils.retrofit2;

import android.os.Bundle;

/**
 * Created by jeongmin on 17. 3. 20.
 */

public class ApiError {
    private int statusCode;
    private String message;
    private String error;
    private int responseCode;
    private String responseMessage;

    public ApiError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return this.responseCode + ":" + this.statusCode + ":" + this.message + ":" + error;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public void setResponse(int code, String message) {
        this.setResponseCode(code);
        this.setResponseMessage(message);
    }

    public void buildMessage(Bundle bundle) {
        bundle.putInt("responseCode", this.responseCode);
        bundle.putString("responseMessage", this.responseMessage);
        bundle.putInt("statusCode", this.statusCode);
        bundle.putString("message", this.message);
        bundle.putString("error", this.error);
    }
}
