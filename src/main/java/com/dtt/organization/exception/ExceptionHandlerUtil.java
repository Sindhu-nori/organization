package com.dtt.organization.exception;

import java.util.Locale;

import com.dtt.organization.constant.ApiResponses;
import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import com.dtt.organization.util.AppUtil;
import com.dtt.organization.util.Utility;

@Component
public class ExceptionHandlerUtil extends Exception {

	private static final String CLASS = ExceptionHandlerUtil.class.getSimpleName();
	final static Logger logger = LoggerFactory.getLogger(ExceptionHandlerUtil.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MessageSource messageSource;

	public ExceptionHandlerUtil(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public static ApiResponses handleException(Exception e) {
		String errorMessage = "Something went wrong. Please try again later.";

		String errorCode = ErrorCode.GENERIC_ERROR.getCode(); // Default error code

		// Handle specific SQL-related exceptions
		if (e instanceof JDBCConnectionException) {
			errorCode = ErrorCode.CONNECTION_ERROR.getCode();
			logger.error("{} - {} : Database connection error occurred: {}", CLASS, Utility.getMethodName(),e.getMessage());
		} else if (e instanceof ConstraintViolationException) {
			errorCode = ErrorCode.DATABASE_ERROR.getCode();
			logger.error("{} - {} : Constraint violation error occurred: {}", CLASS, Utility.getMethodName(),e.getMessage());
		} else if (e instanceof DataException || e instanceof LockAcquisitionException
				|| e instanceof PessimisticLockException || e instanceof QueryTimeoutException
				|| e instanceof SQLGrammarException || e instanceof GenericJDBCException) {
			errorCode = ErrorCode.DATABASE_ERROR.getCode();
			logger.error("{} - {} : Database-related error occurred: {}", CLASS, Utility.getMethodName(),e.getMessage());
		}else if ( e instanceof OrgnizationServiceException) {
			logger.error("{} - {} : OrgnizationServiceException: {}", CLASS, Utility.getMethodName(),e.getMessage());
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}else {
			logger.error("{} - {} : An unexpected error occurred: {}", CLASS, Utility.getMethodName(), e.getMessage());
		}

		// Add the error code to the message
		String formattedMessage = String.format("%s [ErrorCode: %s]", errorMessage, errorCode);

		// Log the response being returned
		logger.info("{} - {} : Returning error response: errorCode={}, message={}", CLASS, Utility.getMethodName(),errorCode, formattedMessage);

		// Return the error response with the formatted message
		return createErrorResponse(errorCode, formattedMessage);
	}

//	public ApiResponse handleHttpException(HttpStatusCodeException e) {
//		HttpStatus status = HttpStatus.valueOf(e.getRawStatusCode());
//		String errorCode = ErrorCode.map.getOrDefault(status.value(), ErrorCode.UNKNOWN_ERROR.getCode());
//		String errorMessage = ErrorCode.getMessageByCode(errorCode);
//		// Format the response message
//		String formattedMessage = String.format("HTTP Error: %s - %s (%s)", status.value(), errorMessage, errorCode);
//		logger.error("{} - {} : HTTP exception occurred: status={}, errorCode={}, message={}", CLASS,
//				Utility.getMethodName(), status.value(), errorCode, e.getMessage());
//		logger.info("{} - {} : Returning HTTP error response: errorCode={}, message={}", CLASS, Utility.getMethodName(),
//				errorCode, formattedMessage);
//		// Return the response
//		return AppUtil.createApiResponse(false, formattedMessage, null);
//	}
	
	public ApiResponses handleHttpException(Exception e) {
	    String errorCode;
	    String errorMessage;
	    
	    if (e instanceof HttpStatusCodeException) {
	        HttpStatusCodeException httpEx = (HttpStatusCodeException) e;
	        HttpStatus status = HttpStatus.valueOf(httpEx.getRawStatusCode());
	        
	        // Map HTTP status codes to custom error codes
	        switch (status) {
	            case BAD_REQUEST:
	                errorCode = ErrorCode.BAD_REQUEST.getCode();
	                errorMessage = ErrorCode.BAD_REQUEST.getMessage();
	                break;
	            case UNAUTHORIZED:
	                errorCode = ErrorCode.REST_CLIENT_ERROR.getCode();
	                errorMessage = ErrorCode.REST_CLIENT_ERROR.getMessage();
	                break;
	            case FORBIDDEN:
	                errorCode = ErrorCode.REST_CLIENT_ERROR.getCode();
	                errorMessage = ErrorCode.REST_CLIENT_ERROR.getMessage();
	                break;
	            case NOT_FOUND:
	                errorCode = ErrorCode.REST_CLIENT_ERROR.getCode();
	                errorMessage = ErrorCode.REST_CLIENT_ERROR.getMessage();
	                break;
	            case INTERNAL_SERVER_ERROR:
	                errorCode = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
	                errorMessage = ErrorCode.INTERNAL_SERVER_ERROR.getMessage();
	                break;
	            case SERVICE_UNAVAILABLE:
	                errorCode = ErrorCode.SERVICE_UNAVAILABLE.getCode();
	                errorMessage = ErrorCode.SERVICE_UNAVAILABLE.getMessage();
	                break;
	            case GATEWAY_TIMEOUT:
	                errorCode = ErrorCode.SERVICE_UNAVAILABLE.getCode();
	                errorMessage = ErrorCode.SERVICE_UNAVAILABLE.getMessage();
	                break;
	            default:
	                // For other HTTP status codes that we don't explicitly handle
	                errorCode = ErrorCode.UNKNOWN_ERROR.getCode();
	                errorMessage = ErrorCode.UNKNOWN_ERROR.getMessage();
	        }
	        
	        // Format the response message for HTTP error
	        String formattedMessage = String.format("HTTP Error: %s - %s (%s)", status.value(), errorMessage, errorCode);
	        logger.error("{} - {} : HTTP exception occurred: status={}, errorCode={}, message={}", CLASS,
	                Utility.getMethodName(), status.value(), errorCode, e.getMessage());
	        logger.info("{} - {} : Returning HTTP error response: errorCode={}, message={}", CLASS, Utility.getMethodName(),
	                errorCode, formattedMessage);
	        return AppUtil.createApiResponse(false, formattedMessage, null);
	    } else if (e instanceof ResourceAccessException) {
	        // Handle network issues such as timeouts, connection errors, etc.
	        errorCode = ErrorCode.REST_CONNECTION_ERROR.getCode();
	        errorMessage = ErrorCode.REST_CONNECTION_ERROR.getMessage();
	        
	        // Format the response message for network error
	        String formattedMessage = String.format("Network Error: %s (%s)", errorMessage, errorCode);
	        logger.error("{} - {} : Network exception occurred: message={}", CLASS, Utility.getMethodName(), e.getMessage());
	        logger.info("{} - {} : Returning network error response: errorCode={}, message={}", CLASS, Utility.getMethodName(),
	                errorCode, formattedMessage);
	        return AppUtil.createApiResponse(false, formattedMessage, null);
	    } else {
	        // Handle other unexpected exceptions
	        errorCode = ErrorCode.UNKNOWN_ERROR.getCode();
	        errorMessage = ErrorCode.UNKNOWN_ERROR.getMessage();
	        
	        // Generic error message for unexpected exceptions
	        String formattedMessage = String.format("Unexpected Error: %s (%s)", errorMessage, errorCode);
	        logger.error("{} - {} : Unexpected exception occurred: message={}", CLASS, Utility.getMethodName(), e.getMessage());
	        logger.info("{} - {} : Returning unexpected error response: errorCode={}, message={}", CLASS, Utility.getMethodName(),
	                errorCode, formattedMessage);
	        return AppUtil.createApiResponse(false, formattedMessage, null);
	    }
	}


	
	public OrgnizationServiceException orgnizationServiceException(String messageKey) {
        String message = messageSource.getMessage(messageKey, null, Locale.ENGLISH);
        return new OrgnizationServiceException(message);
    }
	
	public OrgnizationServiceException orgnizationServiceCustomrException(String messageKey) {
        return new OrgnizationServiceException(messageKey);
    }

	// Method to create error response
	public static ApiResponses createErrorResponse(String errorCode, String errorMessage) {
		ApiResponses response = new ApiResponses();
		response.setSuccess(false);
		response.setMessage(errorMessage);
		response.setResult(null);
		return response;
	}

	// You can also add a method to create success responses if needed
	public ApiResponses createSuccessResponse(String successMessage, Object result) {
		ApiResponses response = new ApiResponses();
		String successMeg = messageSource.getMessage(successMessage, null, Locale.ENGLISH);
		response.setSuccess(true); // Indicates success
		response.setMessage(successMeg); // Set success message
		response.setResult(result); // Set the result data
		return response;
	}

		public ApiResponses createErrorResponse(String messageKey) {
		String errorMessage = messageSource.getMessage(messageKey, null, Locale.ENGLISH);
		// Log the error message
		logger.error("Error response created with message: {}", errorMessage);
		// Return the response with the message and default error code
		return AppUtil.createApiResponse(false, errorMessage, null);
	}

}
