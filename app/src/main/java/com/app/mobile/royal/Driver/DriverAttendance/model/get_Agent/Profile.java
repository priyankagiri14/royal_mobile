package com.app.mobile.royal.Driver.DriverAttendance.model.get_Agent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class Profile {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("idType")
    @Expose
    private String idType;
    @SerializedName("idNo")
    @Expose
    private String idNo;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobileNo")
    @Expose
    private String mobileNo;
    @SerializedName("altMobileNo")
    @Expose
    private String altMobileNo;
    @SerializedName("ricaUser")
    @Expose
    private String ricaUser;
    @SerializedName("ricaPassword")
    @Expose
    private String ricaPassword;
    @SerializedName("ricaGroup")
    @Expose
    private String ricaGroup;
/*    private Integer id;
    private String idType;
    private String idNo;
    private String email;
    private String mobileNo;*/
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAltMobileNo() {
        return altMobileNo;
    }

    public void setAltMobileNo(String altMobileNo) {
        this.altMobileNo = altMobileNo;
    }

    public String getRicaUser() {
        return ricaUser;
    }

    public void setRicaUser(String ricaUser) {
        this.ricaUser = ricaUser;
    }

    public String getRicaPassword() {
        return ricaPassword;
    }

    public void setRicaPassword(String ricaPassword) {
        this.ricaPassword = ricaPassword;
    }

    public String getRicaGroup() {
        return ricaGroup;
    }

    public void setRicaGroup(String ricaGroup) {
        this.ricaGroup = ricaGroup;
    }
    /*public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }*/

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
