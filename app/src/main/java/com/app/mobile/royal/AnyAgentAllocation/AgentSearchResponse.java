package com.app.mobile.royal.AnyAgentAllocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgentSearchResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("body")
    @Expose
    private List<AgentSearchBody> body = null;
    @SerializedName("page")
    @Expose
    private AgentSearchPage page;
    @SerializedName("errorCode")
    @Expose
    private Object errorCode;
    @SerializedName("error")
    @Expose
    private Object error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public List<AgentSearchBody> getBody() {
        return body;
    }

    public void setBody(List<AgentSearchBody> body) {
        this.body = body;
    }

    public AgentSearchPage getPage() {
        return page;
    }

    public void setPage(AgentSearchPage page) {
        this.page = page;
    }

    public Object getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Object errorCode) {
        this.errorCode = errorCode;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
