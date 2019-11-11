
package com.ashehata.bloodbank.data.model.postFavourite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostFavourite {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private PostFavouriteData data;

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

    public PostFavouriteData getData() {
        return data;
    }

    public void setData(PostFavouriteData data) {
        this.data = data;
    }

}
