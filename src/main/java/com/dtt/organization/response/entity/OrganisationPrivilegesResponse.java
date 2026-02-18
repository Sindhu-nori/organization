package com.dtt.organization.response.entity;

//import com.dtt.organization.dto.OrganisationPrivilegesDto;
import com.dtt.organization.model.OrganizationPrivileges;

import java.util.List;

public class OrganisationPrivilegesResponse {




    private boolean walletCertificateStatus;

    private List<String> privileges;

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public boolean isWalletCertificateStatus() {
        return walletCertificateStatus;
    }

    public void setWalletCertificateStatus(boolean walletCertificateStatus) {
        this.walletCertificateStatus = walletCertificateStatus;
    }
}