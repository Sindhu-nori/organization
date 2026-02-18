/*
 * @copyright (DigitalTrust Technologies Private Limited, Hyderabad) 2021, 
 * All rights reserved.
 */
package com.dtt.organization.exception;

import java.util.HashMap;

import com.dtt.organization.response.entity.ServiceResponse;



// TODO: Auto-generated Javadoc
/**
 * The Class ErrorCodes.
 */
public class ErrorCodes {

	/** The response. */
	public static ServiceResponse response = null;

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public static ServiceResponse getResponse() {
		return response;
	}

	/**
	 * Sets the response.
	 *
	 * @param response the new response
	 */
	public static void setResponse(ServiceResponse response) {
		ErrorCodes.response = response;
	}

	/** The message mapping. */
	private static HashMap<String, String> messageMapping = null;

	/** The code mapping. */
	private static HashMap<String, String> codeMapping = null;

	/** The e ob 01. */
	public static String E_OB_01 = "E_OB_01";

	/** The e ob 02. */
	public static String E_OB_02 = "E_OB_02";

	/** The e ob 03. */
	public static String E_OB_03 = "E_OB_03";

	/** The e ob 04. */
	public static String E_OB_04 = "E_OB_04";

	/** The e ra 11. */
	public static String E_RA_11 = "E_RA_11";

	/** The e ra 12. */
	public static String E_RA_12 = "E_RA_12";

	/** The e ra 13. */
	public static String E_RA_13 = "E_RA_13";

	/** The e ra 14. */
	public static String E_RA_14 = "E_RA_14";

	/** The e ra 15. */
	public static String E_RA_15 = "E_RA_15";

	/** The e ra 16. */
	public static String E_RA_16 = "E_RA_16";

	/** The e ra 17. */
	public static String E_RA_17 = "E_RA_17";

	/** The e ra 18. */
	public static String E_RA_18 = "E_RA_18";

	/** The e ra 19. */
	public static String E_RA_19 = "E_RA_19";

	/** The e ra 20. */
	public static String E_RA_20 = "E_RA_20";

	/** The e ra 21. */
	public static String E_RA_21 = "E_RA_21";

	/** The e ra 22. */
	public static String E_RA_22 = "E_RA_22";

	/** The e ra 23. */
	public static String E_RA_23 = "E_RA_23";

	/** The e ra 24. */
	public static String E_RA_24 = "E_RA_24";

	/** The e ra 25. */
	public static String E_RA_25 = "E_RA_25";

	/** The e ra 26. */
	public static String E_RA_26 = "E_RA_26";

	/** The e ra 27. */
	public static String E_RA_27 = "E_RA_27";

	/** The e ra 28. */
	public static String E_RA_28 = "E_RA_28";

	/** The e ra 29. */
	public static String E_RA_29 = "E_RA_29";

	/** The e ra 30. */
	public static String E_RA_30 = "E_RA_30";

	/** The e ra 31. */
	public static String E_RA_31 = "E_RA_31";

	/** The e ra 32. */
	public static String E_RA_32 = "E_RA_32";

	/** The e ra 33. */
	public static String E_RA_33 = "E_RA_33";

	/** The e ra 34. */
	public static String E_RA_34 = "E_RA_34";

	/** The e ra 35. */
	public static String E_RA_35 = "E_RA_35";

	/** The e ra 36. */
	public static String E_RA_36 = "E_RA_36";

	/** The e ra 37. */
	public static String E_RA_37 = "E_RA_37";

	/** The e ra 38. */
	public static String E_RA_38 = "E_RA_38";

	/** The e ra 39. */
	public static String E_RA_39 = "E_RA_39";

	/** The e ra 100. */
	public static String E_RA_100 = "E_RA_100";

	/** The e ra 101. */
	public static String E_RA_101 = "E_RA_101";

	/** The e ra 102. */
	public static String E_RA_102 = "E_RA_102";

	/** The e ra 103. */
	public static String E_RA_103 = "E_RA_103";

	/** The e ra 200. */
	public static String E_RA_200 = "E_RA_200";

	/** The e ra 500. */
	public static String E_RA_500 = "E_RA_500";

	/** The e ra 501. */
	public static String E_RA_501 = "E_RA_501";

	/** The e subscriber data not found. */
	public static String E_SUBSCRIBER_DATA_NOT_FOUND = "Subscriber data not found";

	/** The e organization data not found. */
	public static String E_ORGANIZATION_DATA_NOT_FOUND = "Organization data not found";
	
	/** The e subscriber status data not found. */
	public static String E_SUBSCRIBER_STATUS_DATA_NOT_FOUND = "Subscriber status data not found";
	
	
	/** The e organization status data not found. */
	public static String E_ORGANIZATION_STATUS_DATA_NOT_FOUND = "Organization status data not found";
	
	/** The e subscriber device data not found. */
	public static String E_SUBSCRIBER_DEVICE_DATA_NOT_FOUND = "Subscriber device data not found";

	/** The e subscriber not onboarded. */
	public static String E_SUBSCRIBER_NOT_ONBOARDED = "Subscriber not onboarded";

	/** The e subscriber ra data not found. */
	public static String E_SUBSCRIBER_RA_DATA_NOT_FOUND = "Subscriber RA data not found";

	/** The e subscriber certificates are active. */
	public static String E_SUBSCRIBER_CERTIFICATES_ARE_ACTIVE = "Subscriber certificates are active";

	/** The e subscriber certificates are revoked. */
	public static String E_SUBSCRIBER_CERTIFICATES_ARE_REVOKED = "Subscriber certificates are revoke";
	
	/** The e organization certificates are revoked. */
	public static String E_ORGANIZATION_CERTIFICATES_ARE_REVOKED = "Organization certificates are revoke";
	

	/** The e subscriber certificates are expired. */
	public static String E_SUBSCRIBER_CERTIFICATES_ARE_EXPIRED = "Subscriber certificates are expired";

	/** The e subscriber issue signing certificate failed. */
	public static String E_SUBSCRIBER_ISSUE_SIGNING_CERTIFICATE_FAILED = "Issuing signing certificate failed";

	/** The e subscriber issue authentication certificate failed. */
	public static String E_SUBSCRIBER_ISSUE_AUTHENTICATION_CERTIFICATE_FAILED = "Issuing authentication certificate failed";

	/** The e transaction type not found. */
	public static String E_TRANSACTION_TYPE_NOT_FOUND = "Transaction type not found";

	/** The e request data is not valid. */
	public static String E_REQUEST_DATA_IS_NOT_VALID = "Request data is not valid";

	/** The e certificates not issued. */
	public static String E_CERTIFICATES_NOT_ISSUED = "Certificates are not issued";

	/** The e ra server not running. */
	public static String E_RA_SERVER_NOT_RUNNING = "RA server not running";

	/** The e transaction handler not running. */
	public static String E_TRANSACTION_HANDLER_NOT_RUNNING = "Transaction handler not running";

	/** The e ra subscriber complete details not found. */
	public static String E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND = "Subscriber complete details not found";

	/** The e active certificate not found. */
	public static String E_ACTIVE_CERTIFICATE_NOT_FOUND = "Active certificate not found";

	/** The e pin matched with old pin. */
	public static String E_PIN_MATCHED_WITH_OLD_PIN = "Pin matched with old pin";

	/** The e certificate type not found. */
	public static String E_CERTIFICATE_TYPE_NOT_FOUND = "Certificate type not found";

	/** The e log integrity failed. */
	public static String E_LOG_INTEGRITY_FAILED = "Log integrity failed";

	/** The e ra post request failed. */
	public static String E_RA_POST_REQUEST_FAILED = "RA post request failed";

	/** The e something went wrong. */
	public static String E_SOMETHING_WENT_WRONG = "Something went wrong";

	/** The e native request failed. */
	public static String E_NATIVE_REQUEST_FAILED = "Native request failed";

	/** The e invalid request. */
	public static String E_INVALID_REQUEST = "Invalid request";

	/** The e signing certificate pin not set. */
	public static String E_SIGNING_CERTIFICATE_PIN_NOT_SET = "Signing certificate pin not set";

	/** The e authentication certificate pin not set. */
	public static String E_AUTHENTICATION_CERTIFICATE_PIN_NOT_SET = "Authenticate certificate pin not set";

	/** The e revoke reason not found. */
	public static String E_REVOKE_REASON_NOT_FOUND = "Revoke reason not found";

	/** The e certificate revocation failed. */
	public static String E_CERTIFICATE_REVOCATION_FAILED = "Certificate revocation failed";

	/** The e nin not found. */
	public static String E_NIN_NOT_FOUND = "NIN not found";

	/** The e passport not found. */
	public static String E_PASSPORT_NOT_FOUND = "Passport not found";

	/** The e email not found. */
	public static String E_EMAIL_NOT_FOUND = "Email not found";

	/** The e mobile number not found. */
	public static String E_MOBILE_NUMBER_NOT_FOUND = "Mobile Number not found";

	/** The e subscriber not active. */
	public static String E_SUBSCRIBER_NOT_ACTIVE = "Subscriber not active";

	/** The e pin not matched with old pin. */
	public static String E_PIN_NOT_MATCHED_WITH_OLD_PIN = "Pin not matched with old pin";

	/** The e signing pin not matched. */
	public static String E_SIGNING_PIN_NOT_MATCHED = "Signing pin not matched";

	/** The e auth pin not matched. */
	public static String E_AUTH_PIN_NOT_MATCHED = "Authentication pin not matched";

	/** The e new signing pin matched with old signing pin. */
	public static String E_NEW_SIGNING_PIN_MATCHED_WITH_OLD_SIGNING_PIN = "New signing pin matched with old signing pin";

	/** The e new signing pin matched with current authentication pin. */
	public static String E_NEW_SIGNING_PIN_MATCHED_WITH_CURRENT_AUTHENTICATION_PIN = "New signing pin matched with current authentication pin";

	/** The e new authentication pin matched with old authentication pin. */
	public static String E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_OLD_AUTHENTICATION_PIN = "New authentication pin matched with old authentication pin";

	/** The e new authentication pin matched with current signing pin. */
	public static String E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_CURRENT_SIGNING_PIN = "New authentication pin matched with current signing pin";

	static {
		messageMapping = new HashMap<String, String>();
		messageMapping.put(E_SUBSCRIBER_DATA_NOT_FOUND, E_OB_01);
		messageMapping.put(E_SUBSCRIBER_STATUS_DATA_NOT_FOUND, E_OB_02);
		messageMapping.put(E_SUBSCRIBER_NOT_ONBOARDED, E_OB_03);
		messageMapping.put(E_SUBSCRIBER_DEVICE_DATA_NOT_FOUND, E_OB_04);

		messageMapping.put(E_SUBSCRIBER_RA_DATA_NOT_FOUND, E_RA_11);
		messageMapping.put(E_SUBSCRIBER_CERTIFICATES_ARE_ACTIVE, E_RA_12);
		messageMapping.put(E_SUBSCRIBER_CERTIFICATES_ARE_REVOKED, E_RA_13);
		messageMapping.put(E_SUBSCRIBER_CERTIFICATES_ARE_EXPIRED, E_RA_14);
		messageMapping.put(E_SUBSCRIBER_ISSUE_SIGNING_CERTIFICATE_FAILED, E_RA_15);
		messageMapping.put(E_SUBSCRIBER_ISSUE_AUTHENTICATION_CERTIFICATE_FAILED, E_RA_16);
		messageMapping.put(E_TRANSACTION_TYPE_NOT_FOUND, E_RA_17);
		messageMapping.put(E_REQUEST_DATA_IS_NOT_VALID, E_RA_18);
		messageMapping.put(E_CERTIFICATES_NOT_ISSUED, E_RA_19);
		messageMapping.put(E_ACTIVE_CERTIFICATE_NOT_FOUND, E_RA_20);
		messageMapping.put(E_PIN_MATCHED_WITH_OLD_PIN, E_RA_21);
		messageMapping.put(E_CERTIFICATE_TYPE_NOT_FOUND, E_RA_22);
		messageMapping.put(E_LOG_INTEGRITY_FAILED, E_RA_23);
		messageMapping.put(E_SIGNING_CERTIFICATE_PIN_NOT_SET, E_RA_24);
		messageMapping.put(E_REVOKE_REASON_NOT_FOUND, E_RA_25);
		messageMapping.put(E_CERTIFICATE_REVOCATION_FAILED, E_RA_26);
		messageMapping.put(E_NIN_NOT_FOUND, E_RA_27);
		messageMapping.put(E_PASSPORT_NOT_FOUND, E_RA_28);
		messageMapping.put(E_EMAIL_NOT_FOUND, E_RA_29);
		messageMapping.put(E_MOBILE_NUMBER_NOT_FOUND, E_RA_30);
		messageMapping.put(E_SUBSCRIBER_NOT_ACTIVE, E_RA_31);
		messageMapping.put(E_AUTHENTICATION_CERTIFICATE_PIN_NOT_SET, E_RA_32);
		messageMapping.put(E_PIN_NOT_MATCHED_WITH_OLD_PIN, E_RA_33);
		messageMapping.put(E_SIGNING_PIN_NOT_MATCHED, E_RA_34);
		messageMapping.put(E_AUTH_PIN_NOT_MATCHED, E_RA_35);
		messageMapping.put(E_NEW_SIGNING_PIN_MATCHED_WITH_OLD_SIGNING_PIN, E_RA_36);
		messageMapping.put(E_NEW_SIGNING_PIN_MATCHED_WITH_CURRENT_AUTHENTICATION_PIN, E_RA_37);
		messageMapping.put(E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_OLD_AUTHENTICATION_PIN, E_RA_38);
		messageMapping.put(E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_CURRENT_SIGNING_PIN, E_RA_39);
		messageMapping.put(E_RA_SERVER_NOT_RUNNING, E_RA_100);
		messageMapping.put(E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND, E_RA_101);
		messageMapping.put(E_INVALID_REQUEST, E_RA_102);
		messageMapping.put(E_TRANSACTION_HANDLER_NOT_RUNNING, E_RA_103);
		messageMapping.put(E_RA_POST_REQUEST_FAILED, E_RA_200);
		messageMapping.put(E_NATIVE_REQUEST_FAILED, E_RA_500);
		messageMapping.put(E_SOMETHING_WENT_WRONG, E_RA_501);
	}

	static {
		codeMapping = new HashMap<String, String>();
		codeMapping.put(E_OB_01, E_SUBSCRIBER_DATA_NOT_FOUND);
		codeMapping.put(E_OB_02, E_SUBSCRIBER_STATUS_DATA_NOT_FOUND);
		codeMapping.put(E_OB_03, E_SUBSCRIBER_NOT_ONBOARDED);
		codeMapping.put(E_OB_04, E_SUBSCRIBER_DEVICE_DATA_NOT_FOUND);

		codeMapping.put(E_RA_11, E_SUBSCRIBER_RA_DATA_NOT_FOUND);
		codeMapping.put(E_RA_12, E_SUBSCRIBER_CERTIFICATES_ARE_ACTIVE);
		codeMapping.put(E_RA_13, E_SUBSCRIBER_CERTIFICATES_ARE_REVOKED);
		codeMapping.put(E_RA_14, E_SUBSCRIBER_CERTIFICATES_ARE_EXPIRED);
		codeMapping.put(E_RA_15, E_SUBSCRIBER_ISSUE_SIGNING_CERTIFICATE_FAILED);
		codeMapping.put(E_RA_16, E_SUBSCRIBER_ISSUE_AUTHENTICATION_CERTIFICATE_FAILED);
		codeMapping.put(E_RA_17, E_TRANSACTION_TYPE_NOT_FOUND);
		codeMapping.put(E_RA_18, E_REQUEST_DATA_IS_NOT_VALID);
		codeMapping.put(E_RA_19, E_CERTIFICATES_NOT_ISSUED);
		codeMapping.put(E_RA_20, E_ACTIVE_CERTIFICATE_NOT_FOUND);
		codeMapping.put(E_RA_21, E_PIN_MATCHED_WITH_OLD_PIN);
		codeMapping.put(E_RA_22, E_CERTIFICATE_TYPE_NOT_FOUND);
		codeMapping.put(E_RA_23, E_LOG_INTEGRITY_FAILED);
		codeMapping.put(E_RA_24, E_SIGNING_CERTIFICATE_PIN_NOT_SET);
		codeMapping.put(E_RA_25, E_REVOKE_REASON_NOT_FOUND);
		codeMapping.put(E_RA_26, E_CERTIFICATE_REVOCATION_FAILED);
		codeMapping.put(E_RA_27, E_NIN_NOT_FOUND);
		codeMapping.put(E_RA_28, E_PASSPORT_NOT_FOUND);
		codeMapping.put(E_RA_29, E_EMAIL_NOT_FOUND);
		codeMapping.put(E_RA_30, E_MOBILE_NUMBER_NOT_FOUND);
		codeMapping.put(E_RA_31, E_SUBSCRIBER_NOT_ACTIVE);
		codeMapping.put(E_RA_32, E_AUTHENTICATION_CERTIFICATE_PIN_NOT_SET);
		codeMapping.put(E_RA_33, E_PIN_NOT_MATCHED_WITH_OLD_PIN);
		codeMapping.put(E_RA_34, E_SIGNING_PIN_NOT_MATCHED);
		codeMapping.put(E_RA_35, E_AUTH_PIN_NOT_MATCHED);
		codeMapping.put(E_RA_36, E_NEW_SIGNING_PIN_MATCHED_WITH_OLD_SIGNING_PIN);
		codeMapping.put(E_RA_37, E_NEW_SIGNING_PIN_MATCHED_WITH_CURRENT_AUTHENTICATION_PIN);
		codeMapping.put(E_RA_38, E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_OLD_AUTHENTICATION_PIN);
		codeMapping.put(E_RA_39, E_NEW_AUTHENTICATION_PIN_MATCHED_WITH_CURRENT_SIGNING_PIN);
		codeMapping.put(E_RA_100, E_RA_SERVER_NOT_RUNNING);
		codeMapping.put(E_RA_101, E_RA_SUBSCRIBER_COMPLETE_DETAILS_NOT_FOUND);
		codeMapping.put(E_RA_102, E_INVALID_REQUEST);
		codeMapping.put(E_RA_103, E_TRANSACTION_HANDLER_NOT_RUNNING);

		codeMapping.put(E_RA_200, E_RA_POST_REQUEST_FAILED);
		codeMapping.put(E_RA_500, E_NATIVE_REQUEST_FAILED);
		codeMapping.put(E_RA_501, E_SOMETHING_WENT_WRONG);
	}

	/**
	 * Gets the error code.
	 *
	 * @param message the message
	 * @return the error code
	 */
	public static String getErrorCode(String message) {
		String errorCode = messageMapping.get(message);
		if (errorCode != null)
			return errorCode;
		else
			return response.getError_code();
	}

	/**
	 * Gets the error message.
	 *
	 * @param errorCode the error code
	 * @return the error message
	 */
	public static String getErrorMessage(String errorCode) {
		String errorMessage = codeMapping.get(errorCode);
		if (errorMessage != null)
			return errorMessage;
		else
			return response.getError_message();
	}
}
