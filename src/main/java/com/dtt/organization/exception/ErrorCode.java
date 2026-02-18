package com.dtt.organization.exception;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {

	// General error codes
    GENERIC_ERROR("E0001", "Something went wrong. Please try again later."),
    CONNECTION_ERROR("E0002", "We are experiencing technical difficulties. Please try again after a while."),
    DATABASE_ERROR("E0003", "There seems to be an issue with the data you provided. Please check and try again."),
    VALIDATION_ERROR("E0004", "Validation failed."),

 // HTTP-specific error codes
    BAD_REQUEST("E400", "The request was invalid. Please check your input and try again."),
    UNAUTHORIZED("E401", "You are not authorized to access this resource. Please check your credentials."),
    FORBIDDEN("E403", "You do not have permission to access this resource."),
    NOT_FOUND("E404", "The resource you are looking for could not be found."),
    INTERNAL_SERVER_ERROR("E500", "Something went wrong on our end. We're working to resolve it. Please try again later."),
    SERVICE_UNAVAILABLE("E503", "The service is temporarily unavailable. Please try again later."),
    REST_CONNECTION_ERROR("E1001", "Unable to connect to the service. Please check your connection and try again."),
    REST_CLIENT_ERROR("E1002", "There was an issue with your request. Please verify the data and try again."),
    REST_SERVER_ERROR("E1003", "An error occurred while processing your request. Please try again later."),
    UNKNOWN_ERROR("E9999", "An unknown error occurred. Please try again later or contact support."),
	
	//PKI Service 
	E_ORGANIZATION_CERTIFICATES_ARE_REVOKED("E005","Organization certificates are revoked"),
	E_REVOKE_REASON_NOT_FOUND("E006", "Revoke reason not found"),
	E_ORGANIZATION_DATA_NOT_FOUND("E007", "Organization data not found"),
	E_ORGANIZATION_STATUS_DATA_NOT_FOUND("E008", "Organization status data not found"),
	E_REQUEST_DATA_IS_NOT_VALID("E009", "Request data is not valid"),
	E_TRANSACTION_TYPE_NOT_FOUND("E0010", "Transaction type not found"),
	E_WRAPPED_KEY_NOT_FOUND("E0011", "Wrapped key not found"),
	E_CERTIFICATE_TYPE_NOT_FOUND("E0012", "Certificate type not found"),
	E_ACTIVE_CERTIFICATE_NOT_FOUND("E0013", "Active certificate not found");
	
	
	// Map for HTTP status codes to error codes
    public static final Map<Integer, String> map = new HashMap<>();

    static {
        // Map common HTTP status codes to specific error codes
    	map.put(400, BAD_REQUEST.getCode());  // BAD_REQUEST
        map.put(401, UNAUTHORIZED.getCode());  // UNAUTHORIZED
        map.put(403, FORBIDDEN.getCode());  // FORBIDDEN
        map.put(404, NOT_FOUND.getCode());  // NOT_FOUND
        map.put(500, INTERNAL_SERVER_ERROR.getCode());  // INTERNAL_SERVER_ERROR
        map.put(503, SERVICE_UNAVAILABLE.getCode());  // SERVICE_UNAVAILABLE
        map.put(408, REST_CONNECTION_ERROR.getCode());  // REQUEST_TIMEOUT
    }


	private final String code;
	private final String message;

	ErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public static String getMessageByCode(String code) {
		for (ErrorCode errorCode : ErrorCode.values()) {
			if (errorCode.getCode().equals(code)) {
				return errorCode.getMessage();
			}
		}
		return "Unknown error code"; // Default message if the code is not found
	}
}
