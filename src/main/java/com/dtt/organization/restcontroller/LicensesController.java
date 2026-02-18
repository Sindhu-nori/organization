package com.dtt.organization.restcontroller;

import com.dtt.organization.service.iface.LicensesIface;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.SoftwareLicensesDTO;

@RestController
public class LicensesController {
	
	final static String CLASS = "LicensesController";
	Logger logger = LoggerFactory.getLogger(LicensesController.class);

	@Autowired
	LicensesIface licensesIface;
	@PostMapping("/api/post/generatelicenses")
	public ApiResponses applyForGenerateLicenses(@RequestBody SoftwareLicensesDTO softwareLicensesDTO, @RequestHeader HttpHeaders httpHeaders) {
		logger.info(CLASS+" apply for generate license :: "+softwareLicensesDTO);
		return licensesIface.applyForGenerateLicenses(softwareLicensesDTO,httpHeaders);
	}

	@GetMapping("/api/download/license/{ouid}/{type}")
	public ApiResponses downloadLicence(@PathVariable("ouid") String ouid, @PathVariable("type") String type){
		logger.info(CLASS+" downloadLicence :: "+ouid+" type :: "+type);
		return licensesIface.downloadLicense(ouid,type);
	}

	@GetMapping("/api/get-All/licenses/by/ouid/{Ouid}")
	public ApiResponses licensesIface(@PathVariable("Ouid") String Ouid){
		logger.info(CLASS+" licensesIface ouid :: "+Ouid);
		return licensesIface.getLicenseByOuid(Ouid);
	}

	@GetMapping("/api/get-All/licenses/VG/by/ouid/{Ouid}")
	public ApiResponses licensesIfacevg(@PathVariable("Ouid") String Ouid){
		logger.info(CLASS+" licensesIface ouid :: "+Ouid);
		return licensesIface.getLicenseByOuidVG(Ouid);
	}



	@GetMapping("/api/get/list/licenses")
	public ApiResponses getListForGenerateLicense(){
		return licensesIface.getListForGenerateLicense();
	}

	@PostMapping("/api/send/email")
	public ApiResponses sendEmailToAdmin(@RequestBody SoftwareLicensesDTO softwareLicensesDTO) {
		logger.info(CLASS+" sendEmailToAdmin :: "+softwareLicensesDTO);
		return licensesIface.sendEmailToAdmin(softwareLicensesDTO);
	}
	
	@GetMapping("/api/get/adminemail/list")
	public ApiResponses getAdminEmailList() {
		return  licensesIface.getAdminEmailList();
	}
	
	@PostMapping("/api/post/add/deviceid")
	public ApiResponses addDeviceIdOfLicense(@RequestParam String applicationName, @RequestParam List<String> deviceID) {
		logger.info(CLASS+" addDeviceIdOfLicense :: "+applicationName+" deviceID List :: "+deviceID);
		return licensesIface.addDeviceIdOfLicense(applicationName,deviceID);
	}
	
	@PostMapping("/api/post/update/deviceid")
	public ApiResponses updateDeviceIdOfLicense(@RequestParam String applicationName, @RequestParam String olddeviceID, @RequestParam String newdeviceID) {
		logger.info(CLASS+" updateDeviceIdOfLicense :: "+applicationName+" oldDeviceID :: "+olddeviceID +" newDeviceID :: "+newdeviceID);
		return licensesIface.updateDeviceIdOfLicense(applicationName,olddeviceID,newdeviceID);
	}
	
	@GetMapping("/api/get/deviceid/{clientId}")
	public ApiResponses getDeviceID(@PathVariable String clientId) {
		logger.info(CLASS+" getDeviceID :: "+clientId);
		return licensesIface.getDeviceID(clientId);
	}
	
	@GetMapping("/api/get/deviceIdDetaisl/{applicationName}")
	public ApiResponses getDeviceIdDetails(@PathVariable String applicationName) {
		logger.info(CLASS+" getDeviceIdDetails :: "+applicationName);
		return licensesIface.getDeviceIdDetails(applicationName);
	}
	
	@DeleteMapping("/api/delete/license-device-record/{deviceId}/{applicationName}")
	public ApiResponses deleteSubscriberBySuid(@PathVariable String deviceId, @PathVariable String applicationName) {
		logger.info(CLASS+" deleteSubscriberBySuid :: deviceID :: "+deviceId+" application name :: "+applicationName);
		return licensesIface.deleteRecordByDeviceID(deviceId,applicationName);
	}
}
