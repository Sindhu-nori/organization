package com.dtt.organization.dto;

import java.io.Serializable;

import com.dtt.organization.model.SoftwareLicenses;

public class LicensesListDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SoftwareLicenses softwareLicenses;
	
	private String organizationName;

	public SoftwareLicenses getSoftwareLicenses() {
		return softwareLicenses;
	}

	public void setSoftwareLicenses(SoftwareLicenses softwareLicenses) {
		this.softwareLicenses = softwareLicenses;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Override
	public String toString() {
		return "LicensesListDto [softwareLicenses=" + softwareLicenses + ", organizationName=" + organizationName + "]";
	}

}
