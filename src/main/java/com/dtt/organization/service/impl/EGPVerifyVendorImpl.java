package com.dtt.organization.service.impl;

import java.util.Base64;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.service.iface.EGPVerifyVendorIFace;
import com.dtt.organization.util.AppUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EGPVerifyVendorImpl implements EGPVerifyVendorIFace {
	
    static final String CLASS = "EGPVerifyVendorImpl";
    Logger logger = LoggerFactory.getLogger(EGPVerifyVendorImpl.class);
    
    @Value("${verify.egp.vendorId}")
    private String verifyEGPVendorURL;
    
    @Value("${egp.authorization.username}")
    private String eGPUserName;
    
    @Value("${egp.authorization.password}")
    private String eGPPasswrod;
    
    @Value("${egp.org.id}")
    private String egpOrgId;
    
    @Autowired
    public RestTemplate restTemplate;
    
    @Autowired
    MessageSource messageSource;

    public EGPVerifyVendorImpl() {
    }

    public ApiResponses verifyByEgpForVendor(String vendorId, String orgId) {
        ResponseEntity<Object> res = null;

        try {
            logger.info("EGPVerifyVendorImplverifyByEgpForVendor vendorID , organizationID {} , {}", vendorId, orgId);
            if (egpOrgId.equals(orgId)) {
                ObjectMapper objectMapper = new ObjectMapper();
                String reqBody = getVerifyVendorEmailRequest(vendorId);
                String eGPURL = verifyEGPVendorURL + "api/users/validate";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBasicAuth(eGPUserName, eGPPasswrod);
                HttpEntity<Object> requestEntity = new HttpEntity(reqBody, headers);
                res = restTemplate.exchange(eGPURL, HttpMethod.POST, requestEntity, Object.class, new Object[0]);
                logger.info("EGPVerifyVendorImpl verifyByEgpForVendor egp api response {} ", res);
                if (res.getStatusCodeValue() != 200 && res.getStatusCodeValue() != 201) {
                    return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.vendor.not.found", null, Locale.ENGLISH), null);
                } else {
                    JsonNode jsonNode = objectMapper.valueToTree(res.getBody());
                    if(jsonNode.get("account_exists").asBoolean() && jsonNode.get("is_provider").asBoolean()) {
                    	return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.vendor.verified.successfully", null, Locale.ENGLISH), null);
                    }else {
                    	return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.vendor", (Object[])null, Locale.ENGLISH), res.getBody());
                    }
                  //  return jsonNode.get("account_exists").asBoolean() && jsonNode.get("is_provider").asBoolean() ? AppUtil.createApiResponse(true, messageSource.getMessage("api.response.vendor.verified.successfully", (Object[])null, Locale.ENGLISH), res.getBody()) : AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.vendor", (Object[])null, Locale.ENGLISH), res.getBody());
                }
            } else {
                return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.vendor.not.found", (Object[])null, Locale.ENGLISH), (Object)null);
            }
        } catch (IllegalArgumentException var10) {
            IllegalArgumentException e = var10;
            e.printStackTrace();
            return AppUtil.createApiResponse(false, "Invalid input format.", (Object)null);
        } catch (IllegalStateException var11) {
            IllegalStateException e = var11;
            e.printStackTrace();
            return AppUtil.createApiResponse(false, "State violation: Operation is not valid in the current context.", (Object)null);
        } catch (NullPointerException var12) {
            NullPointerException e = var12;
            e.printStackTrace();
            return AppUtil.createApiResponse(false, e.getMessage(), (Object)null);
        } catch (HttpClientErrorException var13) {
            HttpClientErrorException e = var13;
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.parameter", (Object[])null, Locale.ENGLISH), (Object)null);
            } else {
                return e.getStatusCode() == HttpStatus.REQUEST_TIMEOUT ? AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.not.completed.because.of.request.time.out", (Object[])null, Locale.ENGLISH), (Object)null) : AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.not.completed.because.error.code", (Object[])null, Locale.ENGLISH) + e.getStatusCode(), (Object)null);
            }
        } catch (HttpServerErrorException var14) {
            HttpServerErrorException e = var14;
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.not.completed.because.of.internal.server.error", (Object[])null, Locale.ENGLISH), (Object)null);
            } else if (e.getStatusCode() == HttpStatus.BAD_GATEWAY) {
                return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.not.completed.because.of.bad.gateway", (Object[])null, Locale.ENGLISH), (Object)null);
            } else {
                return e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE ? AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.not.completed.because.of.service.unavailable", (Object[])null, Locale.ENGLISH), (Object)null) : AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.not.completed.because.error.code", (Object[])null, Locale.ENGLISH) + e.getStatusCode(), (Object)null);
            }
        } catch (Exception var15) {
            Exception e = var15;
            e.printStackTrace();
            return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin", (Object[])null, Locale.ENGLISH), (Object)null);
        }
    }

    public String getVerifyVendorEmailRequest(String email) {
        return "{\"email\":\"" + email + "\"}";
    }

    public String getBasicAuth() {
        String userCredentials = "nita-u:1}1E<$kEMcZ3VQ*f";
        String basicAuth = new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        return basicAuth;
    }
}
