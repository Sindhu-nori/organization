package com.dtt.organization.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name="software_licenses_history")
@NamedQuery(name="SoftwareLicensesHistory.findAll", query="SELECT s FROM SoftwareLicensesHistory s")
public class SoftwareLicensesHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "ouid")
	private String ouid;
	
	@Column(name = "software_name")
	private String appid;
	
	@Column(name="created_date_time")
	private String createdDateTime;
	
	@Column(name="updated_date_time")
	private String updatedDateTime;
	
	@Column(name="license_info")
	private String licenseInfo;
	
	@Column(name="issued_on")
	private String issuedOn;
	
	@Column(name="valid_upto")
	private String validUpto;
	
	@Column(name="license_type")
	private String licenseType;
	
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
	public String getCreated_date_time() {
		return createdDateTime;
	}
	public void setCreated_date_time(String created_date_time) {
		this.createdDateTime = created_date_time;
	}
	public String getUpdated_date_time() {
		return updatedDateTime;
	}
	public void setUpdated_date_time(String updated_date_time) {
		this.updatedDateTime = updated_date_time;
	}
	public String getLicense_info() {
		return licenseInfo;
	}
	public void setLicense_info(String license_info) {
		this.licenseInfo = license_info;
	}
	public String getIssued_on() {
		return issuedOn;
	}
	public void setIssued_on(String issued_on) {
		this.issuedOn = issued_on;
	}
	public String getValid_upto() {
		return validUpto;
	}
	public void setValid_upto(String valid_upto) {
		this.validUpto = valid_upto;
	}
	public String getLicense_type() {
		return licenseType;
	}
	public void setLicense_type(String license_type) {
		this.licenseType = license_type;
	}
	@Override
	public String toString() {
		return "SoftwareLicensesHistory [id=" + id + ", ouid=" + ouid + ", appid=" + appid + ", created_date_time="
				+ createdDateTime + ", updated_date_time=" + updatedDateTime + ", license_info=" + licenseInfo
				+ ", issued_on=" + issuedOn + ", valid_upto=" + validUpto + ", license_type=" + licenseType + "]";
	}
}
