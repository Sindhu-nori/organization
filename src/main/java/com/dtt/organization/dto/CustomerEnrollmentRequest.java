package com.dtt.organization.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerEnrollmentRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("client_identifier")
	private String clientIdentifier;

	@JsonProperty("customer")
	private Customer customer;

	public String getClientIdentifier() {
		return clientIdentifier;
	}

	public void setClientIdentifier(String clientIdentifier) {
		this.clientIdentifier = clientIdentifier;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	// ---- Inner Customer Class ----
	public static class Customer implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@JsonProperty("customer_code")
		private String customerCode;
		
		@JsonProperty("stakeholder_type")
		private String stakeholderType;

		@JsonProperty("customer_type")
		private String customerType;

		@JsonProperty("arabic_name")
		private String arabicName;

		@JsonProperty("english_name")
		private String englishName;

		@JsonProperty("identification_number")
		private String identificationNumber;

		@JsonProperty("address")
		private String address;

		@JsonProperty("email")
		private String email;

		@JsonProperty("mobile")
		private String mobile;

		@JsonProperty("password")
		private String password;

		// ---- Getters & Setters ----
		public String getCustomerCode() {
			return customerCode;
		}

		public void setCustomerCode(String customerCode) {
			this.customerCode = customerCode;
		}

		public String getCustomerType() {
			return customerType;
		}

		public void setCustomerType(String customerType) {
			this.customerType = customerType;
		}

		public String getArabicName() {
			return arabicName;
		}

		public void setArabicName(String arabicName) {
			this.arabicName = arabicName;
		}

		public String getEnglishName() {
			return englishName;
		}

		public void setEnglishName(String englishName) {
			this.englishName = englishName;
		}

		public String getIdentificationNumber() {
			return identificationNumber;
		}

		public void setIdentificationNumber(String identificationNumber) {
			this.identificationNumber = identificationNumber;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getStakeholderType() {
			return stakeholderType;
		}

		public void setStakeholderType(String stakeholderType) {
			this.stakeholderType = stakeholderType;
		}

		@Override
		public String toString() {
			return "[customerCode=" + customerCode + ", stakeholderType=" + stakeholderType + ", customerType="
					+ customerType + ", arabicName=" + arabicName + ", englishName=" + englishName
					+ ", identificationNumber=" + identificationNumber + ", address=" + address + ", email=" + email
					+ ", mobile=" + mobile + ", password=" + password + "]";
		}

		
	}

	@Override
	public String toString() {
		return "[clientIdentifier=" + clientIdentifier + ", customer=" + customer + "]";
	}

}
