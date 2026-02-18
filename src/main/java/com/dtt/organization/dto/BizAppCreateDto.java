package com.dtt.organization.dto;



import java.util.List;

public class BizAppCreateDto {
    private String ApplicationType;

    private String ApplicationName;

    private String ApplicationUri;

    private String RedirectUri;

    private String GrantTypes;

    private String Scopes;

    private List GrantTypesList;

    private List ScopesList;

    private String LogoutUri;

    private String OrganizationId;

    private String Base64Cert;

    private String AuthSchemaId;

    public String getAuthSchemaId() {
        return AuthSchemaId;
    }

    public void setAuthSchemaId(String authSchemaId) {
        AuthSchemaId = authSchemaId;
    }

    //    private List transactionProfileRequests;

    public String getApplicationType() {
        return ApplicationType;
    }

    public void setApplicationType(String applicationType) {
        ApplicationType = applicationType;
    }

    public String getApplicationName() {
        return ApplicationName;
    }

    public void setApplicationName(String applicationName) {
        ApplicationName = applicationName;
    }

    public String getApplicationUri() {
        return ApplicationUri;
    }

    public void setApplicationUri(String applicationUri) {
        ApplicationUri = applicationUri;
    }

    public String getRedirectUri() {
        return RedirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        RedirectUri = redirectUri;
    }

    public String getGrantTypes() {
        return GrantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        GrantTypes = grantTypes;
    }

    public String getScopes() {
        return Scopes;
    }

    public void setScopes(String scopes) {
        Scopes = scopes;
    }

    public List getGrantTypesList() {
        return GrantTypesList;
    }

    public void setGrantTypesList(List grantTypesList) {
        GrantTypesList = grantTypesList;
    }

    public List getScopesList() {
        return ScopesList;
    }

    public void setScopesList(List scopesList) {
        ScopesList = scopesList;
    }

    public String getLogoutUri() {
        return LogoutUri;
    }

    public void setLogoutUri(String logoutUri) {
        LogoutUri = logoutUri;
    }

    public String getOrganizationId() {
        return OrganizationId;
    }

    public void setOrganizationId(String organizationId) {
        OrganizationId = organizationId;
    }

    public String getBase64Cert() {
        return Base64Cert;
    }

    public void setBase64Cert(String base64Cert) {
        Base64Cert = base64Cert;
    }


    @Override
    public String toString() {
        return "BizAppCreateDto{" +
                "ApplicationType='" + ApplicationType + '\'' +
                ", ApplicationName='" + ApplicationName + '\'' +
                ", ApplicationUri='" + ApplicationUri + '\'' +
                ", RedirectUri='" + RedirectUri + '\'' +
                ", GrantTypes='" + GrantTypes + '\'' +
                ", Scopes='" + Scopes + '\'' +
                ", GrantTypesList=" + GrantTypesList +
                ", ScopesList=" + ScopesList +
                ", LogoutUri='" + LogoutUri + '\'' +
                ", OrganizationId='" + OrganizationId + '\'' +
                ", Base64Cert='" + Base64Cert + '\'' +
                ", AuthSchemaId='" + AuthSchemaId + '\'' +
                '}';
    }
}