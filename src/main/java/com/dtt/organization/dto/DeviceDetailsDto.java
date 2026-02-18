package com.dtt.organization.dto;

import java.io.Serializable;

public class DeviceDetailsDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String organizationName;
	private String softwareName;
	private String DeviceIds;
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getSoftwareName() {
		return softwareName;
	}
	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}
	public String getDeviceIds() {
		return DeviceIds;
	}
	public void setDeviceIds(String deviceIds) {
		DeviceIds = deviceIds;
	}
	@Override
	public String toString() {
		return "DeviceDetailsDto [organizationName=" + organizationName + ", softwareName=" + softwareName
				+ ", DeviceIds=" + DeviceIds + "]";
	}
}
