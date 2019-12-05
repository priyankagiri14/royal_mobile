package com.app.mobile.royal.FetchOneAgent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AgentBody {

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
    @SerializedName("authority")
    @Expose
    private AgentAuthority authority;
    @SerializedName("attachments")
    @Expose
    private List<AgentAttachment> attachments = null;
    @SerializedName("parentId")
    @Expose
    private Integer parentId;
    @SerializedName("balance")
    @Expose
    private Double balance;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("profile")
    @Expose
    private AgentProfile profile;
    @SerializedName("address")
    @Expose
    private AgentAddress address;
    @SerializedName("warehouse")
    @Expose
    private AgentWarehouse warehouse;
    @SerializedName("parent")
    @Expose
    private AgentParent parent;
    @SerializedName("paymentAccounts")
    @Expose
    private List<Object> paymentAccounts = null;
    @SerializedName("commissions")
    @Expose
    private List<AgentCommission> commissions = null;

    public List<AgentCommission> getCommissions() {
        return commissions;
    }

    public void setCommissions(List<AgentCommission> commissions) {
        this.commissions = commissions;
    }
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

    public AgentAuthority getAuthority() {
        return authority;
    }

    public void setAuthority(AgentAuthority authority) {
        this.authority = authority;
    }

    public List<AgentAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AgentAttachment> attachments) {
        this.attachments = attachments;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
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

    public AgentProfile getProfile() {
        return profile;
    }

    public void setProfile(AgentProfile profile) {
        this.profile = profile;
    }

    public AgentAddress getAddress() {
        return address;
    }

    public void setAddress(AgentAddress address) {
        this.address = address;
    }

    public AgentParent getParent() {
        return parent;
    }

    public void setParent(AgentParent parent) {
        this.parent = parent;
    }

    public AgentWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(AgentWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public List<Object> getPaymentAccounts() {
        return paymentAccounts;
    }

    public void setPaymentAccounts(List<Object> paymentAccounts) {
        this.paymentAccounts = paymentAccounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
