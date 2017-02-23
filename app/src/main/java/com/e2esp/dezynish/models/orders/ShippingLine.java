package com.e2esp.dezynish.models.orders;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Zain on 2/17/2017.
 */
public class ShippingLine {

    @SerializedName("method_id")
    private String methodId;
    @SerializedName("method_title")
    private String methodTitle;
    private String total;

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

}
