package com.dtt.organization.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
//import java.util.Base64;

//import java.security.NoSuchAlgorithmException;
//import java.security.PrivateKey;
//import java.security.spec.InvalidKeySpecException;
//import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.dtt.organization.dto.BizAppCreateDto;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;


import org.hibernate.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.EmailDto;
import com.dtt.organization.dto.SoftwareLicensesDTO;
import com.dtt.organization.model.LicenseDeviceList;
import com.dtt.organization.model.OrganizationDetails;
import com.dtt.organization.model.OrganizationDetailsForClient;
import com.dtt.organization.model.SoftwareLicenseApprovalRequests;
import com.dtt.organization.model.SoftwareLicenses;
import com.dtt.organization.model.SoftwareLicensesHistory;
import com.dtt.organization.model.Subscriber;
import com.dtt.organization.repository.LicenseDeviceListRepo;
import com.dtt.organization.repository.OrganizationDetailsForClientRepoIface;
import com.dtt.organization.repository.OrganizationDetailsRepository;
import com.dtt.organization.repository.SoftwareLicenseApprovalRequestsRepo;
import com.dtt.organization.repository.SoftwareLicensesHistoryRepo;
import com.dtt.organization.repository.SoftwareLicensesRepository;
import com.dtt.organization.repository.SubscriberRepository;
import com.dtt.organization.service.iface.LicensesIface;
import com.dtt.organization.util.AppUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.DAESService;
import ug.daes.Result;

@Service
public class LicensesImpl implements LicensesIface {

	@Autowired
	SoftwareLicensesRepository softwareLicensesRepository;

	@Value(value = "${apply.forgenerate.licenses}")
	private boolean generateLicensesAdmin;

	private String privateKey;

	@Autowired
	SoftwareLicensesHistoryRepo softwareLicensesHistoryRepo;

	@Autowired
	OrganizationDetailsForClientRepoIface organizationDetailsForClientRepoIface;

	@Autowired
	SoftwareLicenseApprovalRequestsRepo softwareLicenseApprovalRequestsRepo;

	@Autowired
	OrganizationDetailsRepository organizationDetailsRepository;

	@Autowired
	SubscriberRepository subscriberRepository;

	@Autowired
	LicenseDeviceListRepo licenseDeviceListRepo;

	@Autowired
	public RestTemplate restTemplate;

	@Value(value = "${url.admin.emaillist}")
	private String url;

	@Value(value = "${max.adminEmails}")
	private int noOfAdminEmail;

	@Value(value = "${privatekey.for.license}")
	private String privatekey;

	@Value(value = "${send.email.url}")
	private String sendEmail;

	@Value(value = "${send.email.adminURL}")
	private String sendEmailAdmin;

	@Value("${save.client}")
	String saveClient;

	@Value("${application.uri}")
	String applicationUri;

	@Value("${logout.uri}")
	String logoutUri;

	@Value("${redirect.uri}")
	String redirectUri;

	@Value("${file.crt}")
	String crtFile;

//	@Autowired
//    PasswordUtils decrypt;

	private static final long VALIDITY_IN_MILLISECONDS = 24 * 60 * 60 * 1000;// 24 hrs

	@Override
	public ApiResponses applyForGenerateLicenses(SoftwareLicensesDTO softwareLicensesDTO, HttpHeaders httpHeaders) {
		try {
			//System.out.println("softwareLicensesDTO :: "+softwareLicensesDTO);
			if (softwareLicensesDTO.getOuid() != null) {
				String validUptoDate = null;
				String issuedOn = null;
				String licenseBase64 = null;
				String[] result = softwareLicensesDTO.getApplicationType().split("_");
				String lastRecord = result[result.length - 1];
				String[] applicationName = softwareLicensesDTO.getApplicationType().split("_" + lastRecord);
				String softwareName = applicationName[0];

				SoftwareLicenses softwareLicenses = new SoftwareLicenses();

				SoftwareLicenseApprovalRequests softwareLicenseApprovalRequests = new SoftwareLicenseApprovalRequests();

				softwareLicenses = softwareLicensesRepository.findByOuidAndLicenseType(softwareLicensesDTO.getOuid(),
						softwareLicensesDTO.getLicenseType());

				softwareLicenseApprovalRequests = softwareLicenseApprovalRequestsRepo.getSoftwareDetails(
						softwareLicensesDTO.getOuid(), softwareLicensesDTO.getLicenseType(), softwareName);

				OrganizationDetails organizationDetails = organizationDetailsRepository
						.findByOrganizationUid(softwareLicensesDTO.getOuid());


				if (softwareLicensesDTO.getLicenseType().equals("COMMERICAL")) {  //PROD
					LocalDate currentDate = LocalDate.now();
					// Get the date after 30 days
					LocalDate dateAfter30Days = currentDate.plusDays(365);
					validUptoDate = dateAfter30Days.toString();
					issuedOn = currentDate.toString();
				} else {
					// softwareLicensesDTO.getLicenseType().equals("TRIAL")
					LocalDate currentDate = LocalDate.now();
					// Get the date after 30 days
					LocalDate dateAfter30Days = currentDate.plusDays(30);
					validUptoDate = dateAfter30Days.toString();
					issuedOn = currentDate.toString();
				}

				if (generateLicensesAdmin) {

					licenseBase64 = generateLicenses(softwareLicensesDTO, LocalDate.now().toString(), validUptoDate,
							softwareLicensesDTO.getLicenseType());
					if (softwareLicenses == null) {
						SoftwareLicenses softwareLicensesmodel = new SoftwareLicenses();
						SoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel = new SoftwareLicenseApprovalRequests();
						softwareLicensesmodel.setAppid(softwareName);
						softwareLicensesmodel.setCreatedDateTime(AppUtil.getDate());
						softwareLicensesmodel.setIssuedOn(issuedOn);
						softwareLicensesmodel.setUpdatedDateTime(AppUtil.getDate());
						softwareLicensesmodel.setValidUpTo(validUptoDate);
						softwareLicensesmodel.setOuid(softwareLicensesDTO.getOuid());
						softwareLicensesmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicensesmodel.setLicenceStatus("ACTIVE");
						softwareLicensesmodel.setLicenseInfo(licenseBase64);
						softwareLicensesmodel.setApplicationName(softwareLicensesDTO.getApplicationType());
						softwareLicensesmodel.setOrganizationName(organizationDetails.getOrganizationName());

						softwareLicenseApprovalRequestsmodel.setApprovalStatus("ACTIVE");
						softwareLicenseApprovalRequestsmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenseApprovalRequestsmodel.setUpdatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequestsmodel.setOuid(softwareLicensesDTO.getOuid());
						softwareLicenseApprovalRequestsmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenseApprovalRequestsmodel.setCreatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequestsmodel.setAppid(softwareName);

						softwareLicensesRepository.save(softwareLicensesmodel);
						softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);
						sendEmail(softwareLicensesDTO);
						return AppUtil.createApiResponse(true, "Licenses generated successfully",
								softwareLicensesmodel);

					} else {
						SoftwareLicensesHistory softwareLicensesHistory = new SoftwareLicensesHistory();

						String todayDate = LocalDate.now().toString();
						String year = todayDate.substring(0, 4);
						String month = todayDate.substring(5, 7);
						String date = todayDate.substring(8, 10);
//						System.out.println(year + month + date);
						// softwareLicensesHistory.setId(softwareLicenses.getId());
						System.out.println(year + "-" + month + "-" + date);
						String validDate = softwareLicenses.getValidUpTo();
						String yearValid = validDate.substring(0, 4);
						String monthValid = validDate.substring(5, 7);
						String dateValid = validDate.substring(8, 10);
						System.out.println(yearValid + "-" + monthValid + "-" + dateValid);
						if (Integer.parseInt(yearValid) > Integer.parseInt(year)) {
							return AppUtil.createApiResponse(false,
									"You already have an active license for requested product", null);
						} else if (Integer.parseInt(yearValid) == Integer.parseInt(year)
								&& Integer.parseInt(monthValid) > Integer.parseInt(month)) {
							return AppUtil.createApiResponse(false,
									"You already have an active license for requested product", null);
						} else if (Integer.parseInt(yearValid) == Integer.parseInt(year)
								&& Integer.parseInt(monthValid) == Integer.parseInt(month)
								&& Integer.parseInt(dateValid) > Integer.parseInt(date)) {
							return AppUtil.createApiResponse(false,
									"You already have an active license for requested product", null);
						}
						softwareLicensesHistory.setAppid(softwareLicenses.getAppid());
						softwareLicensesHistory.setOuid(softwareLicenses.getOuid());

						// make licenses
						// softwareLicensesHistory.setLicense_info(softwareLicenses.getLicense_info());

						softwareLicensesHistory.setLicense_type(softwareLicenses.getLicenseType());
						softwareLicensesHistory.setCreated_date_time(softwareLicenses.getCreatedDateTime());
						softwareLicensesHistory.setIssued_on(softwareLicenses.getIssuedOn());
						softwareLicensesHistory.setUpdated_date_time(softwareLicenses.getUpdatedDateTime());
						softwareLicensesHistory.setValid_upto(softwareLicenses.getValidUpTo());

						softwareLicenses.setAppid(softwareName);
						softwareLicenses.setCreatedDateTime(AppUtil.getDate());
						softwareLicenses.setIssuedOn(issuedOn);
						softwareLicenses.setUpdatedDateTime(AppUtil.getDate());
						softwareLicenses.setValidUpTo(validUptoDate);
						softwareLicenses.setOuid(softwareLicensesDTO.getOuid());
						softwareLicenses.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenses.setLicenceStatus("ACTIVE");
						softwareLicenses.setApplicationName(softwareLicensesDTO.getApplicationType());
						softwareLicenses.setOrganizationName(organizationDetails.getOrganizationName());

						softwareLicenses.setLicenseInfo(licenseBase64);

						softwareLicenseApprovalRequests.setApprovalStatus("ACTIVE");
						softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenseApprovalRequests.setUpdatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequests.setOuid(softwareLicensesDTO.getOuid());
						softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
						softwareLicenseApprovalRequests.setCreatedDateTime(AppUtil.getDate());
						softwareLicenseApprovalRequests.setAppid(softwareName);

						softwareLicensesHistoryRepo.save(softwareLicensesHistory);
						softwareLicensesRepository.save(softwareLicenses);

						softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequests);
						sendEmail(softwareLicensesDTO);
						return AppUtil.createApiResponse(true, "Licenses generated successfully", softwareLicenses);
					}

				} else {
					String admin = httpHeaders.getFirst("admin");
					if (admin == null) {

						if (softwareLicenses == null) {
							SoftwareLicenses softwareLicensesmodel = new SoftwareLicenses();

							SoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel = new SoftwareLicenseApprovalRequests();
							softwareLicensesmodel.setAppid(softwareName);
							softwareLicensesmodel.setCreatedDateTime(AppUtil.getDate());
							// softwareLicensesmodel.setIssued_on(issuedOn);
							softwareLicensesmodel.setUpdatedDateTime(AppUtil.getDate());
							// softwareLicensesmodel.setValid_upto(validUptoDate);
							softwareLicensesmodel.setOuid(softwareLicensesDTO.getOuid());
							softwareLicensesmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicensesmodel.setLicenceStatus("APPLIED");
							softwareLicensesmodel.setApplicationName(softwareLicensesDTO.getApplicationType()); // softwareLicensesmodel.setLicenseInfo(licenseBase64);
							softwareLicensesmodel.setOrganizationName(organizationDetails.getOrganizationName());

							softwareLicenseApprovalRequestsmodel.setApprovalStatus("APPLIED");
							softwareLicenseApprovalRequestsmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenseApprovalRequestsmodel.setUpdatedDateTime(AppUtil.getDate());
							softwareLicenseApprovalRequestsmodel.setOuid(softwareLicensesDTO.getOuid());
							softwareLicenseApprovalRequestsmodel.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenseApprovalRequestsmodel.setCreatedDateTime(AppUtil.getDate());
							softwareLicenseApprovalRequestsmodel.setAppid(softwareName);

							softwareLicensesRepository.save(softwareLicensesmodel);
							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);

							sendEmailToAdmin(softwareLicensesDTO);
							return AppUtil.createApiResponse(true,
									"Your license request has been submitted, and you will be notified when it is approved.", null);
							/*
							 * return AppUtil.createApiResponse(true,
							 * "Your license request accepted. We will inform you once it was generated by our admin"
							 * , null);
							 */

						} else {

							if (softwareLicenses.getLicenceStatus().equals("APPLIED")) {
								return AppUtil.createApiResponse(false, "You have applied for this license", null);
							}

							SoftwareLicensesHistory softwareLicensesHistory = new SoftwareLicensesHistory();
							String todayDate = LocalDate.now().toString();
							String year = todayDate.substring(0, 4);
							String month = todayDate.substring(5, 7);
							String date = todayDate.substring(8, 10);
//							System.out.println(year + month + date);
							// softwareLicensesHistory.setId(softwareLicenses.getId());
							System.out.println(year + "-" + month + "-" + date);
							String validDate = softwareLicenses.getValidUpTo();
							String yearValid = validDate.substring(0, 4);
							String monthValid = validDate.substring(5, 7);
							String dateValid = validDate.substring(8, 10);
							System.out.println(yearValid + "-" + monthValid + "-" + dateValid);
							if (Integer.parseInt(yearValid) > Integer.parseInt(year)) {
								return AppUtil.createApiResponse(false,
										"You already have an active license for requested product", null);
							} else if (Integer.parseInt(yearValid) == Integer.parseInt(year)
									&& Integer.parseInt(monthValid) > Integer.parseInt(month)) {
								return AppUtil.createApiResponse(false,
										"You already have an active license for requested product", null);
							} else if (Integer.parseInt(yearValid) == Integer.parseInt(year)
									&& Integer.parseInt(monthValid) == Integer.parseInt(month)
									&& Integer.parseInt(dateValid) > Integer.parseInt(date)) {
								return AppUtil.createApiResponse(false,
										"You already have an active license for requested product", null);
							}

							// history table
							softwareLicensesHistory.setAppid(softwareName);
							softwareLicensesHistory.setOuid(softwareLicenses.getOuid());


							softwareLicensesHistory.setLicense_type(softwareLicenses.getLicenseType());
							softwareLicensesHistory.setCreated_date_time(softwareLicenses.getCreatedDateTime());
							softwareLicensesHistory.setIssued_on(softwareLicenses.getIssuedOn());
							softwareLicensesHistory.setUpdated_date_time(softwareLicenses.getUpdatedDateTime());
							softwareLicensesHistory.setValid_upto(softwareLicenses.getValidUpTo());

							// main table
							softwareLicenses.setAppid(softwareName);
							softwareLicenses.setCreatedDateTime(AppUtil.getDate());
							// softwareLicenses.setIssued_on(issuedOn);
							softwareLicenses.setUpdatedDateTime(AppUtil.getDate());
							// softwareLicenses.setValidUpTo(validUptoDate);
							softwareLicenses.setOuid(softwareLicensesDTO.getOuid());
							softwareLicenses.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenses.setLicenceStatus("APPLIED");
							softwareLicenses.setApplicationName(softwareLicensesDTO.getApplicationType());
							softwareLicenses.setOrganizationName(organizationDetails.getOrganizationName());

							// softwareLicenses.setLicense_info(licenseBase64);

							softwareLicenseApprovalRequests.setApprovalStatus("APPLIED");
							softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenseApprovalRequests.setUpdatedDateTime(AppUtil.getDate());
							// softwareLicenseApprovalRequests.setOuid(softwareLicensesDTO.getOuid());
							// softwareLicenseApprovalRequests.setLicenseType(softwareLicensesDTO.getLicenseType());
							softwareLicenseApprovalRequests.setCreatedDateTime(AppUtil.getDate());
							// softwareLicenseApprovalRequests.setAppid(softwareLicensesDTO.getAppid());

							softwareLicensesHistoryRepo.save(softwareLicensesHistory);
							softwareLicensesRepository.save(softwareLicenses);

							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequests);
							sendEmailToAdmin(softwareLicensesDTO);
							return AppUtil.createApiResponse(true,
									"Your license request has been submitted, and you will be notified when it is approved.", null);
							/*
							 * return AppUtil.createApiResponse(true,
							 * "Your license request accepted. We will inform you once it was generated by our admin."
							 * , null);
							 */
						}
					}
					else
						System.out.println("responseee::::" + softwareLicenses);

					OrganizationDetailsForClient organizationDetailsForClient =
							organizationDetailsForClientRepoIface
									.getClientId(softwareLicensesDTO.getApplicationType(), softwareLicensesDTO.getOuid());

					System.out.println("response from repo to get the client id " + organizationDetailsForClient);


					if (organizationDetailsForClient != null) {

						softwareLicensesDTO.setClientId(organizationDetailsForClient.getClientId());
						System.out.println("Using existing ClientId: " + softwareLicensesDTO.getClientId());

					}
					else {

						BizAppCreateDto bizAppCreateDto = new BizAppCreateDto();

						String str = softwareLicenses.getApplicationName();
						String[] parts = str.split("_");
						int num = Integer.parseInt(parts[2]);

						String url = saveClient;

						bizAppCreateDto.setApplicationName(softwareLicenses.getApplicationName());
						bizAppCreateDto.setApplicationType("Regular Web Application");
						bizAppCreateDto.setApplicationUri(applicationUri + num);
						bizAppCreateDto.setGrantTypes("authorization_code");
						bizAppCreateDto.setLogoutUri(logoutUri + num);
						bizAppCreateDto.setOrganizationId(softwareLicenses.getOuid());
						bizAppCreateDto.setRedirectUri(redirectUri + num);
						bizAppCreateDto.setScopes("openid urn:idp:digitalid:profile");
						bizAppCreateDto.setBase64Cert(crtFile);

						List<String> grantList = Arrays.asList("authorization_code", "authorization_code_with_pkce");
						bizAppCreateDto.setGrantTypesList(grantList);

						List<String> scopesList = Arrays.asList("openid", "urn:idp:digitalid:profile");
						bizAppCreateDto.setScopesList(scopesList);

						bizAppCreateDto.setAuthSchemaId("0");

						HttpHeaders headers = new HttpHeaders();
						headers.setContentType(MediaType.APPLICATION_JSON);

						HttpEntity<Object> reqEntity = new HttpEntity<>(bizAppCreateDto, headers);

						ResponseEntity<ApiResponses> response = restTemplate.exchange(
								url,
								HttpMethod.POST,
								reqEntity,
								ApiResponses.class
						);

						ApiResponses res = response.getBody();

						if (!res.isSuccess()) {
							System.out.println("message: " + res);
							return AppUtil.createApiResponse(false, res.getMessage(), null);
						}
					}

					 licenseBase64 = generateLicenses(softwareLicensesDTO, LocalDate.now().toString(), validUptoDate,
									softwareLicensesDTO.getLicenseType());

					softwareLicenses.setIssuedOn(issuedOn);
					softwareLicenses.setUpdatedDateTime(LocalDate.now().toString());
					softwareLicenses.setValidUpTo(validUptoDate);
					softwareLicenses.setLicenceStatus("ACTIVE");

					softwareLicenses.setLicenseInfo(licenseBase64);

					softwareLicenseApprovalRequests.setApprovalStatus("ACTIVE");
					softwareLicenseApprovalRequests.setUpdatedDateTime(AppUtil.getDate());
					softwareLicenseApprovalRequests.setOuid(softwareLicensesDTO.getOuid());
					softwareLicenseApprovalRequests.setCreatedDateTime(AppUtil.getDate());

					softwareLicensesRepository.save(softwareLicenses);
					softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequests);

					organizationDetails.setManageByAdmin(false);
					organizationDetailsRepository.save(organizationDetails);

					sendEmail(softwareLicensesDTO);

					return AppUtil.createApiResponse(true, "Licenses generated successfully", softwareLicenses);

				}

			} else {
				return AppUtil.createApiResponse(true, "Organization Uid coming null or empty", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
//		return AppUtil.createApiResponse(false, "Something went wrong", null);
	}

	@Override
	public ApiResponses downloadLicense(String ouid, String type) {
		try {
			SoftwareLicenses softwareLicenses = softwareLicensesRepository.findByOuidAndLicenseType(ouid, type);
			if (softwareLicenses == null) {
				return AppUtil.createApiResponse(false, "Data Not Found", null);
			}
			return AppUtil.createApiResponse(true, "License File Download Successfully",
					softwareLicenses.getLicenseInfo());

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "An Exception Occurred" + e.getMessage(), null);
		}
	}

//	@Override
//	public ApiResponse getLicenseByOuid(String Ouid) {
//		try {
//			if (Ouid == null || Ouid.isEmpty()) {
//				return AppUtil.createApiResponse(false, "Organisation Id cannot be null", null);
//			}
//			List<SoftwareLicenses> softwareLicensesList = softwareLicensesRepository.findByOuid(Ouid);
//			List<SoftwareLicenses> softwareLicenses = new ArrayList<SoftwareLicenses>();
//			for (SoftwareLicenses s : softwareLicensesList) {
//				// SoftwareLicenses sf = new SoftwareLicenses();
//				if (!s.getLicenceStatus().equals("APPLIED")) {
//					if (s.getValidUpTo() != null) {
//						SimpleDateFormat sdfo = new SimpleDateFormat("dd-MM-yyyy");
//						Date expiredDate = sdfo.parse(s.getValidUpTo());
//						Date currentDate = sdfo.parse(LocalDate.now().toString());
//						s.setValidUpTo(s.getValidUpTo().substring(0, 10));
//						if (expiredDate.before(currentDate)) {
//							// String[] applicationName = s.getApplicationName().split("@");
//							// String softwareName = applicationName[0];
//
//							String[] result = s.getApplicationName().split("_");
//							String lastRecord = result[result.length - 1];
//							String[] applicationName = s.getApplicationName().split("_" + lastRecord);
//							String softwareName = applicationName[0];
//
//							// if date1<date2, prints the following statement
//							s.setLicenceStatus("EXPIRED");
//							SoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel = softwareLicenseApprovalRequestsRepo
//									.getSoftwareDetails(Ouid, s.getLicenseType(), softwareName);
//							// SoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel = new
//							// SoftwareLicenseApprovalRequests();
//							softwareLicenseApprovalRequestsmodel.setApprovalStatus("EXPIRED");
//							softwareLicensesRepository.save(s);
//							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);
//						}
//					}
//				}
//
//				softwareLicenses.add(s);
//			}
//
//			if (softwareLicensesList.isEmpty()) {
//				return AppUtil.createApiResponse(true, "No Licenses Available ", softwareLicenses);
//			}
//			return AppUtil.createApiResponse(true, "Licenses Fetched Successfully", softwareLicenses);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return AppUtil.createApiResponse(false, "An Exception Occurred", null);
//		}
//	}


	@Override
	public ApiResponses getLicenseByOuid(String Ouid) {
		try {
			if (Ouid == null || Ouid.isEmpty()) {
				return AppUtil.createApiResponse(false, "Organisation Id cannot be null", null);
			}

			List<SoftwareLicenses> softwareLicensesList = softwareLicensesRepository.findByOuid(Ouid);
			List<SoftwareLicenses> softwareLicenses = new ArrayList<>();

			SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy");

			Date currentDate = dbFormat.parse(LocalDate.now().toString());

			for (SoftwareLicenses s : softwareLicensesList) {
				if (!"APPLIED".equalsIgnoreCase(s.getLicenceStatus()) && s.getValidUpTo() != null) {

					// Parse DB format
					Date expiredDate = dbFormat.parse(s.getValidUpTo());

					// Compare with current date
					if (expiredDate.before(currentDate)) {
						String[] result = s.getApplicationName().split("_");
						String lastRecord = result[result.length - 1];
						String[] applicationName = s.getApplicationName().split("_" + lastRecord);
						String softwareName = applicationName[0];

						s.setLicenceStatus("EXPIRED");

						SoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel =
								softwareLicenseApprovalRequestsRepo.getSoftwareDetails(Ouid, s.getLicenseType(), softwareName);
						if (softwareLicenseApprovalRequestsmodel != null) {
							softwareLicenseApprovalRequestsmodel.setApprovalStatus("EXPIRED");
							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);
						}

						softwareLicensesRepository.save(s);
					}

					// Don't set this back to DB — only for API response
					s.setValidUpTo(apiFormat.format(expiredDate));
				}

				softwareLicenses.add(s);
			}

			if (softwareLicensesList.isEmpty()) {
				return AppUtil.createApiResponse(true, "No Licenses Available", softwareLicenses);
			}

			return AppUtil.createApiResponse(true, "Licenses Fetched Successfully", softwareLicenses);

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "An Exception Occurred", null);
		}
	}



	@Override
	public ApiResponses getLicenseByOuidVG(String Ouid) {
		try {
			if (Ouid == null || Ouid.isEmpty()) {
				return AppUtil.createApiResponse(false, "Organisation Id cannot be null", null);
			}

			SoftwareLicenses s = softwareLicensesRepository.findByOuidVG(Ouid);
			SoftwareLicenses softwareLicenses = new SoftwareLicenses();

			SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy");

			Date currentDate = dbFormat.parse(LocalDate.now().toString());
				if (!"APPLIED".equalsIgnoreCase(s.getLicenceStatus()) && s.getValidUpTo() != null) {
					Date expiredDate = dbFormat.parse(s.getValidUpTo());

					if (expiredDate.before(currentDate)) {
						String[] result = s.getApplicationName().split("_");
						String lastRecord = result[result.length - 1];
						String[] applicationName = s.getApplicationName().split("_" + lastRecord);
						String softwareName = applicationName[0];
						softwareLicenses.setLicenceStatus("EXPIRED");
						SoftwareLicenseApprovalRequests softwareLicenseApprovalRequestsmodel =
								softwareLicenseApprovalRequestsRepo.getSoftwareDetails(Ouid, s.getLicenseType(), softwareName);
						if (softwareLicenseApprovalRequestsmodel != null) {
							softwareLicenseApprovalRequestsmodel.setApprovalStatus("EXPIRED");
							softwareLicenseApprovalRequestsRepo.save(softwareLicenseApprovalRequestsmodel);
						}
						softwareLicensesRepository.save(s);
					}
					softwareLicenses.setValidUpTo(apiFormat.format(expiredDate));
				}

			softwareLicenses.setLicenceStatus(s.getLicenceStatus());

			if (s==null) {
				return AppUtil.createApiResponse(true, "No Licenses Available", softwareLicenses);
			}

			return AppUtil.createApiResponse(true, "Licenses Fetched Successfully", softwareLicenses);

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "An Exception Occurred", null);
		}
	}



	private String generateLicenses(SoftwareLicensesDTO softwareLicensesDTO, String issuedOn, String validUpto,
			String type) {
		try {
			System.out.println(" type " + type);
			OrganizationDetailsForClient organizationDetailsForClient = new OrganizationDetailsForClient();
			System.out.println(softwareLicensesDTO);
			String clientid="";


			organizationDetailsForClient = organizationDetailsForClientRepoIface
					.getClientId(softwareLicensesDTO.getApplicationType(), softwareLicensesDTO.getOuid());

			if(organizationDetailsForClient != null) {
				softwareLicensesDTO.setClientId(organizationDetailsForClient.getClientId());
				clientid = softwareLicensesDTO.getClientId();
			}
			
			String s = softwareLicensesDTO.licenseInfoNew(softwareLicensesDTO.getOuid(), type, "macaddress",
					softwareLicensesDTO.getClientId());
			System.out.println("josn-->" + s);
			// String enc = encrypt(s);
//			Result res = SignatureService.encryptString(s);
			Result res = DAESService.createSecureWireData(s);
			System.out.println("resssss"+res);
			String enc = new String(res.getResponse());
			System.out.println("yhbujd"+enc);
			// Encode the JSON string to Base64
//			byte[] jsonBytes = s.getBytes();
//			byte[] base64Bytes = Base64.getEncoder().encode(jsonBytes);
			return new String(enc);

		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	// public static String encrypt(String plainText) {
	// 	String secretKey = "DiGiTaLtRuStTeChNoLoGy";
	// 	try {
	// 		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	// 		IvParameterSpec ivspec = new IvParameterSpec(iv);

	// 		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	// 		KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), getSalt(plainText), 65536, 256);
	// 		SecretKey tmp = factory.generateSecret(spec);
	// 		SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

	// 		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	// 		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
	// 		return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")));
	// 	} catch (Exception e) {
	// 		System.out.println("Error while encrypting: " + e.toString());
	// 	}
	// 	return null;
	// }
	private static final String SECRET = "DiGiTaLtRuStTeChNoLoGy";
	private static final int SALT_LENGTH = 16;
	private static final int IV_LENGTH = 12; // Recommended for GCM
	private static final int TAG_LENGTH = 128;
	// 1️⃣ Generate random salt

	public static String encrypt(String plainText) {
        try {

            byte[] salt = new byte[SALT_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            // 2️⃣ Derive key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(SECRET.toCharArray(), salt, 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            // 3️⃣ Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);

            // 4️⃣ Encrypt using AES-GCM
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // 5️⃣ Combine salt + iv + ciphertext
            byte[] encryptedData = new byte[salt.length + iv.length + cipherText.length];
            System.arraycopy(salt, 0, encryptedData, 0, salt.length);
            System.arraycopy(iv, 0, encryptedData, salt.length, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, salt.length + iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedData);

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

	private static byte[] getSalt(String s) throws NoSuchAlgorithmException {
		String salt = "ssshhhhhhhhhhh!!!!boooooooooom";
		return salt.getBytes();
	}

	@Override
	public ApiResponses getListForGenerateLicense() {
		try {
			List<SoftwareLicenses> softwareLicenses = softwareLicensesRepository.getListForGenerateLicenses();
			if (softwareLicenses == null) {
				return AppUtil.createApiResponse(true, "No record found for generate licenses", null);
			} else {
				return AppUtil.createApiResponse(true, "List for generate Licenses", softwareLicenses);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

	public ApiResponses sendEmail(SoftwareLicensesDTO softwareLicensesDTO) {
		try {

			//String[] result = softwareLicensesDTO.getApplicationType().split("_");
			//String lastRecord = result[result.length - 1];
			//String[] applicationName = softwareLicensesDTO.getApplicationType().split("_" + lastRecord);
			//String softwareName = applicationName[0];

			OrganizationDetails organizationDetails = organizationDetailsRepository
					.findByOrganizationUid(softwareLicensesDTO.getOuid());

			Subscriber subscriber = subscriberRepository.getSubscriber(organizationDetails.getSpocUgpassEmail());

			List<String> listOfEmail = new ArrayList<String>();
			listOfEmail.add(organizationDetails.getSpocUgpassEmail());
			
			String emailBody = "Greetings! " + subscriber.getFullName() + ",<br>Your software license request for the organization "
					+	"\"" + organizationDetails.getOrganizationName() + "\"" + "has been approved. Please proceed to download it from the service provider portal.";

			EmailDto emailDto = new EmailDto();
			emailDto.setEmailBody(emailBody);
			emailDto.setRecipients(listOfEmail);
			emailDto.setSubject("Software License is generated successfully");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(emailDto, headers);
			ResponseEntity<ApiResponses> res = restTemplate.exchange(sendEmail, HttpMethod.POST, requestEntity,
					ApiResponses.class);

			if (res.getStatusCodeValue() == 200) {
				return AppUtil.createApiResponse(true, res.getBody().getMessage(), res.getBody().getResult());
			} else if (res.getStatusCodeValue() == 400) {
				return AppUtil.createApiResponse(false, "Bad Request", null);
			} else if (res.getStatusCodeValue() == 500) {
				return AppUtil.createApiResponse(false, "Internal server error", null);
			}
			return AppUtil.createApiResponse(true, "Email send successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false,
					"Sorry! There's a glitch. We're working on it, please try again shortly.", null);
		}
	}

	@Override
	public ApiResponses sendEmailToAdmin(SoftwareLicensesDTO softwareLicensesDTO) {
		try {
			String[] result = softwareLicensesDTO.getApplicationType().split("_");
			String lastRecord = result[result.length - 1];
			String[] applicationName = softwareLicensesDTO.getApplicationType().split("_" + lastRecord);
			String softwareName = applicationName[0];
			OrganizationDetails organizationDetails = organizationDetailsRepository
					.findByOrganizationUid(softwareLicensesDTO.getOuid());

			ApiResponses response = getAdminEmailList();

			if (response.isSuccess()) {
				List<String> listOfEmail = (List<String>) response.getResult();

				EmailDto emailDto = new EmailDto();
				if (listOfEmail.size() >= noOfAdminEmail) {
					List<String> adminEmailList = listOfEmail.subList(0, Math.min(noOfAdminEmail, listOfEmail.size()));
					emailDto.setRecipients(adminEmailList);
				} else {
					// List<String> adminEmailList = listOfEmail.subList(0, Math.min(noOfAdminEmail,
					// listOfEmail.size()));
					emailDto.setRecipients(listOfEmail);
				}
				String emailBody = "Dear Admin,<br>Greetings!<br><br>SPOC have applied for license of the software "
						+ "\"" + softwareName.replace("_", " ") + "\"" + " for the organization " + "\""
						+ organizationDetails.getOrganizationName() + "\"" + ".  Kindly do the needful.<br>";

				emailDto.setEmailBody(emailBody);
				// emailDto.setRecipients(adminEmailList);
				emailDto.setSubject("Software License Request");
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<Object> requestEntity = new HttpEntity<>(emailDto, headers);
				ResponseEntity<ApiResponses> res = restTemplate.exchange(sendEmailAdmin, HttpMethod.POST, requestEntity,
						ApiResponses.class);
				if (res.getStatusCodeValue() == 200) {
					return AppUtil.createApiResponse(true, res.getBody().getMessage(), res.getBody().getResult());
				} else if (res.getStatusCodeValue() == 400) {
					return AppUtil.createApiResponse(false, "Bad Request", null);
				} else if (res.getStatusCodeValue() == 500) {
					return AppUtil.createApiResponse(false, "Internal server error", null);
				}
				return AppUtil.createApiResponse(true, "Email send successfully", null);
			} else {
				return AppUtil.createApiResponse(false, "failed to fetch admin email", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false,
					"Sorry! There's a glitch. We're working on it, please try again shortly.", null);
		}
	}

	@Override
	public ApiResponses getAdminEmailList() {
		ResponseEntity<String> res = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String adminEmailUtl = url + "admin-portal/api/UserApi/getuserslist";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
			res = restTemplate.exchange(adminEmailUtl, HttpMethod.GET, requestEntity, String.class);
			System.out.println("status code - " + res.getStatusCodeValue());
			if (res.getStatusCodeValue() == 400) {
				return AppUtil.createApiResponse(false,
						"Invalid request. Please check your input and try again for Driving Licence Service.", null);
			} else if (res.getStatusCodeValue() == 401) {
				return AppUtil.createApiResponse(false,
						"Access denied. You are not authorized to view this content. Please authenticate for Driving Licence Service.",
						null);
			} else if (res.getStatusCodeValue() == 403) {
				return AppUtil.createApiResponse(false, "Access denied. You don't have permission to view this page.",
						null);
			} else if (res.getStatusCodeValue() == 404) {
				return AppUtil.createApiResponse(false, "Oops! The page you're looking for cannot be found.", null);
			} else if (res.getStatusCodeValue() == 415) {
				return AppUtil.createApiResponse(false,
						"It seems like you're using an unsupported content type in your request. Please choose a valid one and resubmit your request.",
						null);
			} else if (res.getStatusCodeValue() == 500) {
				return AppUtil.createApiResponse(false,
						"Something unexpected happened. Please refresh the page or try again later.", null);
			} else if (res.getStatusCodeValue() == 501) {
				return AppUtil.createApiResponse(false,
						"Service temporarily unavailable. Please try again later or contact support for assistance.",
						null);
			} else if (res.getStatusCodeValue() == 503) {
				return AppUtil.createApiResponse(false,
						"Our server is currently resting. We'll be back up and running shortly.", null);
			} else if (res.getStatusCodeValue() == 200 || res.getStatusCodeValue() == 201) {
				// System.out.println("res- " + res);

				JsonNode jsonNode = objectMapper.readTree(res.getBody());
				JsonNode resourceNode2 = jsonNode.get("resource");
				List<String> stringList = convertJsonNodeToList(resourceNode2);
				String[] recipients = stringList.toArray(new String[0]);

				return AppUtil.createApiResponse(true, "", recipients);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), (Object) null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), (Object) null);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), (Object) null);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), (Object) null);
		} catch (HttpServerErrorException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), (Object) null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), (Object) null);
		}
		return AppUtil.createApiResponse(false, "Server not reachable, please try after sometime", (Object) null);
	}

	private static List<String> convertJsonNodeToList(JsonNode jsonNode) {
		List<String> stringList = new ArrayList<>();

		// Check if JsonNode is an array
		if (jsonNode.isArray()) {
			for (JsonNode element : jsonNode) {
				stringList.add(element.asText());
			}
		}

		return stringList;
	}

	@Override
	public ApiResponses addDeviceIdOfLicense(String applicationName, List<String> deviceID) {
		try {
			if (applicationName == null || applicationName == "") {
				return AppUtil.createApiResponse(false, "Application name should not be null or empty", null);
			} else {

				OrganizationDetailsForClient organizationDetailsForClient = organizationDetailsForClientRepoIface
						.getOrganizationClientDetails(applicationName);
				if (organizationDetailsForClient != null) {

					deviceID.forEach(deviceId -> {
						LicenseDeviceList licenseDeviceListModel = new LicenseDeviceList();
						licenseDeviceListModel.setOrganizationName(organizationDetailsForClient.getOrgName());
						licenseDeviceListModel.setApplicationName(organizationDetailsForClient.getApplicationName());
						licenseDeviceListModel.setClientId(organizationDetailsForClient.getClientId());
						licenseDeviceListModel.setDeviceId(deviceId);
						licenseDeviceListModel.setCreatedDate(AppUtil.getDate());
						licenseDeviceListModel.setUpdatedDate(AppUtil.getDate());
						licenseDeviceListRepo.save(licenseDeviceListModel);
					});

					return AppUtil.createApiResponse(true, "Device ID's saved successfully", null);

				} else {
					return AppUtil.createApiResponse(false, "Organization record not found", null);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

	@Override
	public ApiResponses getDeviceID(String clientId) {
		try {
			if (clientId == null || clientId == "") {
				return AppUtil.createApiResponse(false, "Client Id should not be null or empty", null);
			} else {
				List<String> licenseDeviceList = licenseDeviceListRepo.getLicenseDeviceDetailsList(clientId);
				if (licenseDeviceList == null) {
					return AppUtil.createApiResponse(true, "No record found", null);
				} else {
					return AppUtil.createApiResponse(true, "Data fetch successfully", licenseDeviceList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

	@Override
	public ApiResponses getDeviceIdDetails(String applicationName) {
		try {
			if (applicationName == null || applicationName == "") {
				return AppUtil.createApiResponse(false, "Application name should not be null or empty", null);
			} else {
				List<LicenseDeviceList> licenseDeviceList = licenseDeviceListRepo.getLicenseDeviceList(applicationName);
				if (licenseDeviceList.size() != 0) {
					return AppUtil.createApiResponse(true, "Data fetched successfully", licenseDeviceList);

				} else {
					return AppUtil.createApiResponse(true, "No record found", licenseDeviceList);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

	@Override
	public ApiResponses updateDeviceIdOfLicense(String applicationName, String olddeviceID, String newdeviceID) {
		try {
			if (olddeviceID != null && olddeviceID != "") {
				LicenseDeviceList licenseDeviceList = licenseDeviceListRepo.getLicenseDevice(olddeviceID,
						applicationName);
				if (licenseDeviceList != null) {
					licenseDeviceList.setDeviceId(newdeviceID);
					licenseDeviceList.setUpdatedDate(AppUtil.getDate());
					licenseDeviceListRepo.save(licenseDeviceList);
					return AppUtil.createApiResponse(true, "Device ID's updated successfully", null);
				} else {
					return AppUtil.createApiResponse(false, "No record found", null);
				}
			} else {
				return AppUtil.createApiResponse(false, "Old device id should not be null or empty", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

	@Override
	public ApiResponses deleteRecordByDeviceID(String deviceId, String applicationName) {
		try {
			if (deviceId != null && deviceId != "" && applicationName != null && applicationName != "") {
				int a = licenseDeviceListRepo.deleteRecordByDeviceId(deviceId, applicationName);
				if (a == 1) {
					return AppUtil.createApiResponse(true, "Selected Device ID deleted Successfully", null);
				} else {
					return AppUtil.createApiResponse(false, "No record found", null);
				}
			} else {
				return AppUtil.createApiResponse(false, "Device id should not be null or empty", null);
			}

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please try after sometime", null);
		}
	}

}
