
package com.ashehata.bloodbank.data.model.postDetials;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDetials {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private PostDetialsData data;

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

    public PostDetialsData getData() {
        return data;
    }

    public void setData(PostDetialsData data) {
        this.data = data;
    }

}
