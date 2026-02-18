package com.dtt.organization.dto;

import com.dtt.organization.model.Benificiaries;

import java.util.List;

public class BeneficiaryListDto {

    private List<Benificiaries> benificiariesList;

    private boolean manageByAdmin;

    public List<Benificiaries> getBenificiariesList() {
        return benificiariesList;
    }

    public void setBenificiariesList(List<Benificiaries> benificiariesList) {
        this.benificiariesList = benificiariesList;
    }

    public boolean isManageByAdmin() {
        return manageByAdmin;
    }

    public void setManageByAdmin(boolean manageByAdmin) {
        this.manageByAdmin = manageByAdmin;
    }
}
