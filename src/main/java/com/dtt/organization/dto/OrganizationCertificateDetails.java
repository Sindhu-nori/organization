package com.dtt.organization.dto;

import java.io.Serializable;
import java.util.Date;

public class OrganizationCertificateDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date cerificateExpiryDate;
	private Date certificateIssueDate;
	private String certificateStatus;
	private String certificateType;
	public Date getCerificateExpiryDate() {
		return cerificateExpiryDate;
	}
	public void setCerificateExpiryDate(Date cerificateExpiryDate) {
		this.cerificateExpiryDate = cerificateExpiryDate;
	}
	public Date getCertificateIssueDate() {
		return certificateIssueDate;
	}
	public void setCertificateIssueDate(Date certificateIssueDate) {
		this.certificateIssueDate = certificateIssueDate;
	}
	public String getCertificateStatus() {
		return certificateStatus;
	}
	public void setCertificateStatus(String certificateStatus) {
		this.certificateStatus = certificateStatus;
	}
	public String getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}
	@Override
	public String toString() {
		return "OrganizationCertificateDetails [cerificateExpiryDate=" + cerificateExpiryDate
				+ ", certificateIssueDate=" + certificateIssueDate + ", certificateStatus=" + certificateStatus
				+ ", certificateType=" + certificateType + "]";
	}
	
	
//	@Override
//    public String toString() {
//        return "OrganizationCertificateDetails{" +
//                "cerificate_expiry_date='" + cerificate_expiry_date + '\'' +
//                ", certificate_issue_date=" + certificate_issue_date +
//                ", certificate_status=" + certificate_status +
//                ", certificate_type=" + certificate_type +
//                '}';
//    }
}
