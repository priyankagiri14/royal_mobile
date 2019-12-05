package com.app.mobile.royal.Driver.DriverAttendance.model.driverattendancephoto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IndividualAttendanceAttachment {

    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
