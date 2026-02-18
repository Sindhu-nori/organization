package com.dtt.organization.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrganizationPaymentHistoryDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String subscriberSuid;
	private boolean paymentForOrganization;
	private String transactionReferenceId;
	private String aggregatorAcknowledgementId;
	private String paymentStatus;
	private String paymentCategory;
	private LocalDateTime createdOn;
	public String getSubscriberSuid() {
		return subscriberSuid;
	}
	public void setSubscriberSuid(String subscriberSuid) {
		this.subscriberSuid = subscriberSuid;
	}
	public boolean isPaymentForOrganization() {
		return paymentForOrganization;
	}
	public void setPaymentForOrganization(boolean paymentForOrganization) {
		this.paymentForOrganization = paymentForOrganization;
	}
	public String getTransactionReferenceId() {
		return transactionReferenceId;
	}
	public void setTransactionReferenceId(String transactionReferenceId) {
		this.transactionReferenceId = transactionReferenceId;
	}
	public String getAggregatorAcknowledgementId() {
		return aggregatorAcknowledgementId;
	}
	public void setAggregatorAcknowledgementId(String aggregatorAcknowledgementId) {
		this.aggregatorAcknowledgementId = aggregatorAcknowledgementId;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getPaymentCategory() {
		return paymentCategory;
	}
	public void setPaymentCategory(String paymentCategory) {
		this.paymentCategory = paymentCategory;
	}
	public LocalDateTime getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}
	@Override
	public String toString() {
		return "OrganizationPaymentHistoryDto [subscriberSuid=" + subscriberSuid + ", paymentForOrganization="
				+ paymentForOrganization + ", transactionReferenceId=" + transactionReferenceId
				+ ", aggregatorAcknowledgementId=" + aggregatorAcknowledgementId + ", paymentStatus=" + paymentStatus
				+ ", paymentCategory=" + paymentCategory + ", createdOn=" + createdOn + "]";
	}
	
	


}
