package com.e2esp.dezynish.enums;

/**
 * Created by Zain on 2/20/17.
 */
public enum RequestMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), HEAD("HEAD");

    private String val;

    RequestMethod(String s) {
        val = s;
    }

    public String getVal() {
        return val;
    }

}
