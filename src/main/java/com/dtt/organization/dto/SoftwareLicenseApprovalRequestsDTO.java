package com.dtt.organization.dto;

public class SoftwareLicenseApprovalRequestsDTO {

    private int id;

    private String ouid;

    private String appid;

    private String licenseType;

    private String createdOn;

    private String updatedOn;

    private String approvalStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOuid() {
        return ouid;
    }

    public void setOuid(String ouid) {
        this.ouid = ouid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Override
    public String toString() {
        return "SoftwareLicenseApprovalRequestsDTO{" +
                "id=" + id +
                ", ouid='" + ouid + '\'' +
                ", appid='" + appid + '\'' +
                ", licenseType='" + licenseType + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", updatedOn='" + updatedOn + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                '}';
    }
}
