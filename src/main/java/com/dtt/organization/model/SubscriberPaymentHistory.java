package com.dtt.organization.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.security.Timestamp;

//public class SubscriberPaymentHistory {
//}
@Entity
@Table(name = "subscriber_payment_history")  // This is your DB view
public class SubscriberPaymentHistory {

    @Id
    @Column(name = "transaction_reference_id") // Use a unique field from the view
    private String transactionReferenceId;

    @Column(name = "subscriber_suid")
    private String subscriberSuid;

    @Column(name = "payment_info")
    private String paymentInfo;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_category")
    private String paymentCategory;

    @Column(name = "subscriber_status")
    private String subscriberStatus;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "organization_id")
    private String organizationId;
}
