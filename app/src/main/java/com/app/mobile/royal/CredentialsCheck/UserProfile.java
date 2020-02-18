package com.app.mobile.royal.CredentialsCheck;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UserProfile {

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
    @SerializedName("ricaUser")
    @Expose
    private String ricaUser;
    @SerializedName("ricaPassword")
    @Expose
    private String ricaPassword;
    @SerializedName("ricaGroup")
    @Expose
    private String ricaGroup;
    @SerializedName("isRica")
    @Expose
    private Boolean isRica = false;

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

    public Boolean getIsRica() {
        return isRica;
    }

    public void setIsRica(Boolean isRica) {
        this.isRica = isRica;
    }


}