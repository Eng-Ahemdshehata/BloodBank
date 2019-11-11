
package com.ashehata.bloodbank.data.model.donationDetials;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DonationDetials {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private DonationDetialsData data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DonationDetialsData getData() {
        return data;
    }

    public void setData(DonationDetialsData data) {
        this.data = data;
    }

}
