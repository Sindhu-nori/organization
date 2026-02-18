package com.dtt.organization.dto;

import java.io.Serializable;
import java.util.List;

import com.dtt.organization.model.BeneficiaryValidity;
import com.dtt.organization.model.Benificiaries;

public class BenificiariesResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Benificiaries benificiaries;

	List<BeneficiaryValidity> beneficiaryValidity;

	public Benificiaries getBenificiaries() {
		return benificiaries;
	}

	public void setBenificiaries(Benificiaries benificiaries) {
		this.benificiaries = benificiaries;
	}

	public List<BeneficiaryValidity> getBeneficiaryValidity() {
		return beneficiaryValidity;
	}

	public void setBeneficiaryValidity(List<BeneficiaryValidity> beneficiaryValidity) {
		this.beneficiaryValidity = beneficiaryValidity;
	}

	@Override
	public String toString() {
		return "BenificiariesResponseDto [benificiaries=" + benificiaries + ", beneficiaryValidity="
				+ beneficiaryValidity + "]";
	}

}
