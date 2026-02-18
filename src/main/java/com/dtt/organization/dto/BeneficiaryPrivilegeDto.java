package com.dtt.organization.dto;

public class BeneficiaryPrivilegeDto {


    private int privilegeId;

    private String privilegeServiceName;

    private String privilegeServiceDisplay_name;

    private String status;

    public int getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(int privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getPrivilegeServiceName() {
        return privilegeServiceName;
    }

    public void setPrivilegeServiceName(String privilegeServiceName) {
        this.privilegeServiceName = privilegeServiceName;
    }

    public String getPrivilegeServiceDisplay_name() {
        return privilegeServiceDisplay_name;
    }

    public void setPrivilegeServiceDisplay_name(String privilegeServiceDisplay_name) {
        this.privilegeServiceDisplay_name = privilegeServiceDisplay_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isChargable() {
        return isChargable;
    }

    public void setChargable(boolean chargable) {
        isChargable = chargable;
    }

    private boolean isChargable;

    @Override
    public String toString() {
        return "BeneficiaryPrivilegeDto{" +
                "privilegeId=" + privilegeId +
                ", privilegeServiceName='" + privilegeServiceName + '\'' +
                ", privilegeServiceDisplay_name='" + privilegeServiceDisplay_name + '\'' +
                ", status='" + status + '\'' +
                ", isChargable=" + isChargable +
                '}';
    }
}
