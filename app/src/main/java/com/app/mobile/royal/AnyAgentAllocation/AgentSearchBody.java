package com.app.mobile.royal.AnyAgentAllocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgentSearchBody {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("customerId")
    @Expose
    private String customerId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("lastPasswordResetDate")
    @Expose
    private String lastPasswordResetDate;
    @SerializedName("authority")
    @Expose
    private AgentSearchAuthority authority;
    @SerializedName("attachments")
    @Expose
    private List<AgentSearchAttachment> attachments = null;
    @SerializedName("parentId")
    @Expose
    private Integer parentId;
    @SerializedName("warehouseId")
    @Expose
    private Integer warehouseId;
    @SerializedName("balance")
    @Expose
    private Integer balance;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("profile")
    @Expose
    private AgentSearchProfile profile;
    @SerializedName("warehouse")
    @Expose
    private AgentSearchWarehouse warehouse;
    @SerializedName("name")
    @Expose
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(String lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public AgentSearchAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(AgentSearchAuthority authority) {
        this.authority = authority;
    }

    public List<AgentSearchAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AgentSearchAttachment> attachments) {
        this.attachments = attachments;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AgentSearchProfile getProfile() {
        return profile;
    }

    public void setProfile(AgentSearchProfile profile) {
        this.profile = profile;
    }

    public AgentSearchWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(AgentSearchWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
