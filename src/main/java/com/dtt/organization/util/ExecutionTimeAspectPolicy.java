package com.dtt.organization.util;

import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.dtt.organization.model.SubscriberDevice;
import com.dtt.organization.model.SubscriberDeviceHistory;
import com.dtt.organization.repository.SubscriberDeviceHistoryRepoIface;
import com.dtt.organization.repository.SubscriberDeviceRepoIface;




@Aspect
@Component
public class ExecutionTimeAspectPolicy {
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
    SubscriberDeviceRepoIface subscriberDeviceRepoIface;

	@Autowired
	SubscriberDeviceHistoryRepoIface subscriberDeviceHistoryRepoIface;
	
	@Pointcut("execution(* com.dtt.organization.restcontroller.OrganizationController.getOrgList(..))")
	private void forgetOrgList() {
	};
	
	@Pointcut("execution(* com.dtt.organization.restcontroller.OrganizationController.linkEmail(..))")
	private void forlinkEmail() {
	};
	
	@Pointcut("execution(* com.dtt.organization.restcontroller.OrganizationController.sendOtp(..))")
	private void forsendOtp() {
	};
	
	
	@Around("forgetOrgList() || forlinkEmail() || forsendOtp()")																																											// methods
	public Object controllerPolicy(ProceedingJoinPoint joinPoint) throws Throwable {
		return checkPolicy(joinPoint);
	}
	
	private Object checkPolicy(ProceedingJoinPoint joinPoint) throws Throwable {
		String methodName = joinPoint.getSignature().toShortString();
		String deviceUid = "";
		String appVersion = "";
		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) arg;
				
				deviceUid = httpServletRequest.getHeader("deviceId");
				appVersion = httpServletRequest.getHeader("appVersion");
				

				break;
			}
		}
		
		Optional<SubscriberDeviceHistory> subscriberDeviceHistoryOptional = Optional.ofNullable(subscriberDeviceHistoryRepoIface.findBydeviceUid(deviceUid));
		SubscriberDevice subscriberDeviceDetails = subscriberDeviceRepoIface.findBydeviceUid(deviceUid);
		SubscriberDevice checkSubscriberDetails = null;
		Object result;
		boolean checkPolicy = true;
		boolean deviceEmpty = false;
		if(appVersion == null || appVersion.equals("") || appVersion == "") {
			System.out.println("appVersion is empty");
			deviceEmpty = true;
			
		}else if (subscriberDeviceHistoryOptional.isPresent()) {
			
			//Optional<SubscriberDevice> subscriberDevice = Optional.ofNullable(subscriberDeviceRepoIface.findBydeviceUidAndStatus(deviceUid,"ACTIVE"));
			checkSubscriberDetails = subscriberDeviceRepoIface.getSubscriber(subscriberDeviceHistoryOptional.get().getSubscriberUid());
			SubscriberDevice subscriberDevice = subscriberDeviceRepoIface.findBydeviceUidAndStatus(deviceUid,"ACTIVE");
			if(subscriberDevice == null) {
				checkPolicy = false;
				
			}else if(subscriberDevice.getDeviceStatus() == "DISABLED" || subscriberDevice.getDeviceStatus().equalsIgnoreCase("DISABLED")) {
				checkPolicy = false;
				System.out.println(" subscriberDeviceHistoryOptional is present ");
				
			}else {
				checkPolicy = true;
			}
			
		}else if(subscriberDeviceDetails == null) {
			System.out.println(" subscriberDeviceDetails is null");
			checkPolicy = false;
		}else if(subscriberDeviceDetails.getDeviceStatus() == "DISABLED" || subscriberDeviceDetails.getDeviceStatus().equalsIgnoreCase("DISABLED")) {
			checkPolicy = false;
			System.out.println("inside else if DISABLED");
			
		}else if(subscriberDeviceDetails.getDeviceStatus() == "ACTIVE" || subscriberDeviceDetails.getDeviceStatus().equalsIgnoreCase("ACTIVE")) {
			checkPolicy = true;
			System.out.println("inside else if active");

		}else {
			checkPolicy = false;
			System.out.println("inside else");
		}
		
		if(deviceEmpty) {
			result = AppUtil.createApiResponse(false,messageSource.getMessage("api.error.please.update.your.app", null, Locale.ENGLISH),null);
		}else if (checkPolicy) {
			result = joinPoint.proceed();
		}else {
			
			//Account registered on new device, services temporarily disabled on this device.
			if(subscriberDeviceDetails == null && checkSubscriberDetails == null ) {
				result = AppUtil.createApiResponse(false,
						messageSource.getMessage("api.error.subscriber.not.found", null, Locale.ENGLISH),
						null);
			}else {
				result = AppUtil.createApiResponse(false,
						messageSource.getMessage("api.error.account.registered.on.new.device.services.disabled.on.this.device", null, Locale.ENGLISH),
						null);
			}
			

//			result = AppUtil.createApiResponse(false,"We apologize for any inconvenience.  You can use the service after " +remainHour+ " hours, as it seems you changed your Device.",null);
		}

		return result;
	}

}
