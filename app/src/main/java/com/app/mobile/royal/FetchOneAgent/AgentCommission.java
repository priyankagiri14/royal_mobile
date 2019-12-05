package com.app.mobile.royal.FetchOneAgent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgentCommission {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("network")
    @Expose
    private String network;
    @SerializedName("dailyRate")
    @Expose
    private Double dailyRate;
    @SerializedName("simCost")
    @Expose
    private Double simCost;
    @SerializedName("activationCom")
    @Expose
    private Double activationCom;
    @SerializedName("ogr")
    @Expose
    private Double ogr;
    @SerializedName("cib")
    @Expose
    private Double cib;
    @SerializedName("sims")
    @Expose
    private Integer sims;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getSimCost() {
        return simCost;
    }

    public void setSimCost(Double simCost) {
        this.simCost = simCost;
    }

    public Double getActivationCom() {
        return activationCom;
    }

    public void setActivationCom(Double activationCom) {
        this.activationCom = activationCom;
    }

    public Double getOgr() {
        return ogr;
    }

    public void setOgr(Double ogr) {
        this.ogr = ogr;
    }

    public Double getCib() {
        return cib;
    }

    public void setCib(Double cib) {
        this.cib = cib;
    }

    public Integer getSims() {
        return sims;
    }

    public void setSims(Integer sims) {
        this.sims = sims;
    }
}
