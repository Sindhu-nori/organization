package com.dtt.organization.restcontroller;


import com.dtt.organization.config.SentryClientExceptions;
import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SentryController {

	@Autowired
	SentryClientExceptions sentryClientExceptions;

	@GetMapping("api/get/service/sentry")
	public ApiResponses getServiceStatusSentry() {
		String suid = null;
		try {

			suid = generateSubscriberUniqueId();
			// Sentry.setTag("subscriber_id", suid);
			// Sentry.setTag("SentryController", "getServiceStatusSentry");
//			Sentry.init("https://0d2c6531ad364a1496cca0b8fa737dea@monitor.digitaltrusttech.com/4");
			sentryClientExceptions.captureTags(suid, "SentryController","getServiceStatusSentry" );

			throw new Exception("SERVICE IS DOWN");

		} catch (Exception e) {
			System.out.println("EE" + e);

			sentryClientExceptions.captureExceptions(e);

			return AppUtil.createApiResponse(false, "down", null);
		}

	}

	public String generateSubscriberUniqueId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}



}
