package com.dtt.organization.dto;



public class BeneficiaryValidityDto {

    private int id;

    private int beneficiaryId;


   // private int privilege_service_id;
    private boolean validityApplicable;

    private String validFrom;

    private String validUpto;

    private String status;

    private String createdOn;

    private String updatedOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(int beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }





    public boolean isValidityApplicable() {
        return validityApplicable;
    }

    public void setValidityApplicable(boolean validityApplicable) {
        this.validityApplicable = validityApplicable;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(String validUpto) {
        this.validUpto = validUpto;
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

    @Override
    public String toString() {
        return "BeneficiaryValidityDto{" +
                "id=" + id +
                ", beneficiaryId=" + beneficiaryId +
                ", validityApplicable=" + validityApplicable +
                ", validFrom=" + validFrom +
                ", validUpto=" + validUpto +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }
}
