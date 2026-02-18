package com.dtt.organization.dto;

import java.io.Serializable;

public class OrgBucketDto implements Serializable{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private int id;


    private String bucketId;


    private int orgBucketConfig;



    private int totalDS;


    private int totalEDS;


    private String createdOn;


    private String updatedOn;


    private String status;


    private String closedBy;


    private String sponsorId;


    private String closedOn;


    private boolean paymentRecieved;


    private int remainingDSAfterPayment;


    private int remainingEDSAfterPayment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public int getOrgBucketConfig() {
        return orgBucketConfig;
    }

    public void setOrgBucketConfig(int orgBucketConfig) {
        this.orgBucketConfig = orgBucketConfig;
    }

    public int getTotalDS() {
        return totalDS;
    }

    public void setTotalDS(int totalDS) {
        this.totalDS = totalDS;
    }

    public int getTotalEDS() {
        return totalEDS;
    }

    public void setTotalEDS(int totalEDS) {
        this.totalEDS = totalEDS;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getClosedOn() {
        return closedOn;
    }

    public void setClosedOn(String closedOn) {
        this.closedOn = closedOn;
    }

    public boolean isPaymentRecieved() {
        return paymentRecieved;
    }

    public void setPaymentRecieved(boolean paymentRecieved) {
        this.paymentRecieved = paymentRecieved;
    }

    public int getRemainingDSAfterPayment() {
        return remainingDSAfterPayment;
    }

    public void setRemainingDSAfterPayment(int remainingDSAfterPayment) {
        this.remainingDSAfterPayment = remainingDSAfterPayment;
    }

    public int getRemainingEDSAfterPayment() {
        return remainingEDSAfterPayment;
    }

    public void setRemainingEDSAfterPayment(int remainingEDSAfterPayment) {
        this.remainingEDSAfterPayment = remainingEDSAfterPayment;
    }

    @Override
    public String toString() {
        return "OrgBucketDto{" +
                "id=" + id +
                ", bucketId='" + bucketId + '\'' +
                ", orgBucketConfig=" + orgBucketConfig +
                ", totalDS=" + totalDS +
                ", totalEDS=" + totalEDS +
                ", createdOn='" + createdOn + '\'' +
                ", updatedOn='" + updatedOn + '\'' +
                ", status='" + status + '\'' +
                ", closedBy='" + closedBy + '\'' +
                ", sponsorId='" + sponsorId + '\'' +
                ", closedOn='" + closedOn + '\'' +
                ", paymentRecieved=" + paymentRecieved +
                ", remainingDSAfterPayment=" + remainingDSAfterPayment +
                ", remainingEDSAfterPayment=" + remainingEDSAfterPayment +
                '}';
    }
}


