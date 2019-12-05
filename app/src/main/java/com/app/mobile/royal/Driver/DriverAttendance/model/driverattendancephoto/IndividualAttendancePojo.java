package com.app.mobile.royal.Driver.DriverAttendance.model.driverattendancephoto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IndividualAttendancePojo {

    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("attachments")
    @Expose
    private List<IndividualAttendanceAttachment> attachments = null;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<IndividualAttendanceAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<IndividualAttendanceAttachment> attachments) {
        this.attachments = attachments;
    }

}
