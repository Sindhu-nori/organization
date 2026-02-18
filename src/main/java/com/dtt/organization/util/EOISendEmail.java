package com.dtt.organization.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.EmailReqDto;
import com.dtt.organization.model.TrustedStakeholder;

public class EOISendEmail implements Runnable {

	private static final String CLASS = EOISendEmail.class.getSimpleName();
	Logger logger = LoggerFactory.getLogger(EOISendEmail.class);
	
	private final List<TrustedStakeholder> trustedStakeholdersList;

	String emailBaseUrl;
	RestTemplate restTemplate;
	String link;

	public EOISendEmail(List<TrustedStakeholder> trustedStakeholdersList, String emailBaseUrl,
			RestTemplate restTemplate, String link) {
		this.trustedStakeholdersList = trustedStakeholdersList;
		this.emailBaseUrl = emailBaseUrl;
		this.restTemplate = restTemplate;
		this.link = link;
	}

	@Override
	public void run() {
	    String methodName = Utility.getMethodName();
	    logger.info("{} - {}: Starting email sending process", CLASS, methodName);

	    try {
	        String url = emailBaseUrl;

	        for (TrustedStakeholder trustedStakeholder : trustedStakeholdersList) {
	            TimeUnit.SECONDS.sleep(4);  // Delay to space out the requests

	            // Prepare email request
	            EmailReqDto emailReqDto = createEmailRequest(trustedStakeholder);
	            HttpEntity<Object> requestEntity = new HttpEntity<>(emailReqDto);

	            logger.info("{} - {}: Sending email to: {}", CLASS, methodName, emailReqDto.getEmailId());

	            try {
	                ResponseEntity<ApiResponses> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ApiResponses.class);
	                handleResponse(res, emailReqDto);
	            } catch (Exception e) {
	                logger.error("{} - {}: Error while sending email to: {}: {}", CLASS, methodName, emailReqDto.getEmailId(), e.getMessage(), e);
	            }
	        }
	    } catch (Exception e) {
	        logger.error("{} - {}: Error in email sending process: {}", CLASS, methodName, e.getMessage(), e);
	    }
	}

	private EmailReqDto createEmailRequest(TrustedStakeholder trustedStakeholder) {
	    EmailReqDto emailReqDto = new EmailReqDto();
	    TrustedStakeholder stakeholder = new TrustedStakeholder();
	    stakeholder.setName(trustedStakeholder.getName());
	    stakeholder.setReferenceId(trustedStakeholder.getReferenceId());
	    emailReqDto.setLink(link);
	    emailReqDto.setEmailId(trustedStakeholder.getSpocUgpassEmail());
	    emailReqDto.setTrustedStakeholder(stakeholder);
	    return emailReqDto;
	}

	private void handleResponse(ResponseEntity<ApiResponses> res, EmailReqDto emailReqDto) {
	    if (res.getStatusCodeValue() == 200) {
	        logger.info("{} - Email sent successfully to: {}", CLASS, emailReqDto.getEmailId());
	    } else if (res.getStatusCodeValue() == 400) {
	        logger.warn("{} - Bad Request while sending email to: {}", CLASS, emailReqDto.getEmailId());
	    } else if (res.getStatusCodeValue() == 500) {
	        logger.error("{} - Internal Server Error while sending email to: {}", CLASS, emailReqDto.getEmailId());
	    } else {
	        logger.error("{} - Unexpected response while sending email to: {}: Status code: {}", CLASS, emailReqDto.getEmailId(), res.getStatusCodeValue());
	    }
	}


}
