package com.e2esp.dezynish.enums;

/**
 * Created by Zain on 2/20/17.
 */
public enum SigningMethod {
    HMACSHA1("HMAC-SHA1"), HMACSHA256("HMAC-SHA256");

    private String val;

    SigningMethod(String s) {
        val = s;
    }

    public String getVal() {
        return val;
    }

}
