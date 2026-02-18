package com.dtt.organization.dto;

import java.io.Serializable;

public class BenificiariesRespDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BenificiariesResponseDto benificiariesResponseDtos;

	public BenificiariesResponseDto getBenificiariesResponseDtos() {
		return benificiariesResponseDtos;
	}

	public void setBenificiariesResponseDtos(BenificiariesResponseDto benificiariesResponseDtos) {
		this.benificiariesResponseDtos = benificiariesResponseDtos;
	}

	@Override
	public String toString() {
		return "BenificiariesRespDto [benificiariesResponseDtos=" + benificiariesResponseDtos + "]";
	}
}
