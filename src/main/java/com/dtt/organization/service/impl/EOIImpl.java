package com.dtt.organization.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.EmailReqDto;
import com.dtt.organization.dto.OTPResponseDTO;
import com.dtt.organization.dto.RegisterOrganizationDTO;
import com.dtt.organization.dto.TrustedStakeholderDto;
import com.dtt.organization.dto.TrustedStakeholderRequestDto;
import com.dtt.organization.model.OrganizationDetails;
import com.dtt.organization.model.TrustedStakeholder;
import com.dtt.organization.repository.TrustedStakeholdersRepository;
import com.dtt.organization.service.iface.EOIIface;
import com.dtt.organization.util.AppUtil;
import com.dtt.organization.util.EOISendEmail;
import com.fasterxml.jackson.databind.ObjectMapper;

import ug.daes.DAESService;
import ug.daes.Result;

@Service
public class EOIImpl implements EOIIface {
	
	Logger logger = LoggerFactory.getLogger(EOIImpl.class);
	
	final static String CLASS = "EOIImpl";
	
	@Value(value = "${nira.api.timetolive}")
	private int timeToLive;
	
	@Value(value = "${email.url}")
	private String emailBaseUrl;
	
	@Value(value = "${eoi.portal.link}")
	private String link;

	@Autowired
	OrganizationServiceImpl organizationService;
	@Autowired
	TrustedStakeholdersRepository trustedStakeholdersRepository;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	MessageSource messageSource;

	public String generateReferenceUniqueId() {
		UUID referenceuid = UUID.randomUUID();
		return referenceuid.toString();
	}

	@Override
	public ApiResponses registerTrustedOrganizationEOIPortal(RegisterOrganizationDTO registerOrganizationDTO,
															 String referenceId) {
		try {
			logger.info(CLASS + " registerTrustedOrganizationEOIPortal referenceId {}", referenceId);
			TrustedStakeholder trustedStakeholder = trustedStakeholdersRepository.findByReferenceId(referenceId);
			if (trustedStakeholder == null) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.reference.id",null, Locale.ENGLISH), (Object) null);
			} else if (trustedStakeholder.isStatus() == true) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.reference.id.organizatio.already.onboarded",null, Locale.ENGLISH),
						(Object) null);
			} else {
				if (registerOrganizationDTO.getSpocUgpassEmail() == null) {
					return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.spocmail.cant.be.null",null, Locale.ENGLISH), (Object) null);
				}
				if (registerOrganizationDTO.getOrgUserList().size() == 0) {
					return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.orguserlist.cant.be.null",null, Locale.ENGLISH),
							(Object) null);
				}
				if (!trustedStakeholder.getSpocUgpassEmail().equals(registerOrganizationDTO.getSpocUgpassEmail())) {
					return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.spoc.is.not.authorized.to.onboard.this.organization",null, Locale.ENGLISH),
							(Object) null);
				} else {
					ApiResponses res = organizationService.registerOrganization(registerOrganizationDTO);
					if (res.isSuccess()) {
						ObjectMapper objectMapper = new ObjectMapper();
						
						
						String s = objectMapper.writerWithDefaultPrettyPrinter()
								.writeValueAsString(res.getResult());
						OrganizationDetails organizationDetails = objectMapper.readValue(s, OrganizationDetails.class);
						trustedStakeholder.setOnboardingTime(AppUtil.getCurrentDate());
						trustedStakeholder.setStatus(true);
						trustedStakeholder.setOrganizationUid(organizationDetails.getOrganizationUid());
						trustedStakeholdersRepository.save(trustedStakeholder);

						// send email to spoc for onboard for EGP
						// trustedStakeholder.getSpocUgpassEmail()
						ApiResponses response = sendEmailToSpoc(registerOrganizationDTO.getSpocUgpassEmail());

						return AppUtil.createApiResponse(true,
								messageSource.getMessage("api.response.organization.onboarded.purchase.eseal",null, Locale.ENGLISH),
								res.getResult());
					}
				}

			}
		}
		catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException |
			   PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e)
		{
			e.printStackTrace();
			logger.error(CLASS + " registerTrustedOrganizationEOIPortal() :: IN DATABASE EXCEPTION {}" , e.getMessage());
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.try.after.sometime",null, Locale.ENGLISH), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.info(CLASS + "registerTrustedOrganizationEOIPortal >> ERROR>> " + e.getMessage());
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.try.after.sometime",null, Locale.ENGLISH), null);
		}
		return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.organization.onboarding.failed",null, Locale.ENGLISH), (Object) null);
	}

	// without reference id under development
	@Override
	public ApiResponses registerTrustedOrganizationEOI(RegisterOrganizationDTO registerOrganizationDTO) {
		try {
			logger.info(CLASS + " registerTrustedOrganizationEOI request {}", registerOrganizationDTO);
			if (registerOrganizationDTO == null) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.cant.be.empty",null, Locale.ENGLISH), null);
			}
			logger.info(CLASS + " :: registerTrustedOrganizationEOI() :: reqbody " + registerOrganizationDTO);
			String url = "admin portal api";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Object> requestEntity = new HttpEntity<>(registerOrganizationDTO, headers);
			ResponseEntity<ApiResponses> res = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
					ApiResponses.class);
			if (res.getStatusCodeValue() == 200) {
				return AppUtil.createApiResponse(true, res.getBody().getMessage(), res);
			} else if (res.getStatusCodeValue() == 400) {
				return AppUtil.createApiResponse(false, "Bad Request", null);
			} else if (res.getStatusCodeValue() == 500) {
				return AppUtil.createApiResponse(false, "Internal server error", null);
			}
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	@Override
	public ApiResponses sendEmailOTP(String referenceId) {
		try {
			if (referenceId.trim().isEmpty()) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.reference.id.cant.be.empty",null, Locale.ENGLISH), null);
			}
			TrustedStakeholder trustedStakeholder = trustedStakeholdersRepository.findByReferenceId(referenceId);
			if (trustedStakeholder == null) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.reference.id",null, Locale.ENGLISH), null);
			}

			String emailOTP = generateOtp(5);
			EmailReqDto emailReqDto = new EmailReqDto();
			emailReqDto.setEmailOtp(emailOTP);
			// emailReqDto.setOrg(true); //Dear Customer,Please download the Ugpass app and
			// Onboard
			emailReqDto.setTtl(timeToLive);
			emailReqDto.setEmailId(trustedStakeholder.getSpocUgpassEmail());
			ApiResponses res = organizationService.sendEmailToSubscriber(emailReqDto);

			OTPResponseDTO otpResponsDto = new OTPResponseDTO();
			otpResponsDto.setEmailEncrptyOTP(encryptedString(emailOTP));
			otpResponsDto.setTtl(timeToLive);
			otpResponsDto.setMobileOTP(null);
			otpResponsDto.setEmailOTP(null);

			if (res.isSuccess()) {
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.email.send.successfully",null, Locale.ENGLISH), otpResponsDto);
			} else {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.email.send.failed",null, Locale.ENGLISH), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.please.try.after.sometime",null, Locale.ENGLISH), (Object) null);
		}
	}

	@Override
	public ApiResponses sendEmailToSpoc(String spocEmail) {

		try {
			if (spocEmail == null) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.spoc.email.cant.be.empty",null, Locale.ENGLISH), null);
			}
			EmailReqDto emailReqDto = new EmailReqDto();
			emailReqDto.setEmailId(spocEmail);
			ApiResponses res = organizationService.sendEmailToSubscriber(emailReqDto);
			if (res.isSuccess()) {
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.email.send.successfully",null, Locale.ENGLISH), null);
			} else {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.email.send.failed",null, Locale.ENGLISH), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	public String generateOtp(int maxLength) {
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			StringBuilder otp = new StringBuilder(maxLength);

			for (int i = 0; i < maxLength; i++) {
				otp.append(secureRandom.nextInt(9));
			}
			return otp.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String encryptedString(String s) {
		try {
			Result result = DAESService.encryptData(s);
			return new String(result.getResponse());
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@Override
	public ApiResponses addStakeHoldersList(TrustedStakeholderRequestDto trustedStakeholderRequestDto) {
		try {
			logger.info(CLASS + " addStakeHolders request {}", trustedStakeholderRequestDto);
			if (trustedStakeholderRequestDto == null) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.request.cant.be.empty",null, Locale.ENGLISH), null);
			} else {

				List<TrustedStakeholderDto> trustedStakeholderDtoList = trustedStakeholderRequestDto
						.getTrustedStakeholderDtosList();
				List<TrustedStakeholder> trustedStakeholdersList = new ArrayList<TrustedStakeholder>();
				
				for (TrustedStakeholderDto trustedStakeholderDto : trustedStakeholderDtoList) {

					String referenceId = generateRandomSuffix(10);
					String stakeHolderType = null;
//					if (trustedStakeholderDto.getReferenceId() != "") {
//						referenceId = trustedStakeholderDto.getReferenceId();
//					} else {
//						referenceId = generateRandomSuffix(10);
////						referenceId = generateReferenceUniqueId();
//					}
					if (trustedStakeholderDto.getStakeholderType() != "") {
						stakeHolderType = trustedStakeholderDto.getStakeholderType();
					} else {
						stakeHolderType = "VENDOR";
					}
					
					TrustedStakeholder trustedStakeholder = new TrustedStakeholder();
					
					Optional<String> referredBy = Optional.ofNullable(trustedStakeholderDto.getReferredBy());
					if(referredBy.isPresent()){
						if(trustedStakeholderDto.getReferredBy().equals("")) {
							return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.referredby.cant.be.empty",null, Locale.ENGLISH), null);
						}
						trustedStakeholder.setReferredBy(trustedStakeholderDto.getReferredBy());
					}else{
						return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.referredby.cant.be.empty",null, Locale.ENGLISH), null);
						
					}
					
					trustedStakeholder.setName(trustedStakeholderDto.getName());
					trustedStakeholder.setSpocUgpassEmail(trustedStakeholderDto.getSpocUgpassEmail());
					trustedStakeholder.setReferenceId(referenceId);
					//trustedStakeholder.setOrganizationUid(trustedStakeholderDto.getOrganizationUid());
					
					trustedStakeholder.setStatus(trustedStakeholderDto.isStatus());
					//trustedStakeholder.setOnboardingTime(onboardedDate);
					trustedStakeholder.setCreationTime(AppUtil.getCurrentDate());
					trustedStakeholder.setStakeholderType(stakeHolderType);
					
					trustedStakeholdersList.add(trustedStakeholder);
					
				}
				trustedStakeholdersRepository.saveAll(trustedStakeholdersList);
				
				ExecutorService executor = Executors.newFixedThreadPool(1000);
				Runnable thread = new EOISendEmail(trustedStakeholdersList,emailBaseUrl,restTemplate,link);
				executor.execute(thread);
				executor.shutdown();

				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.stakeHolder.added.successfully",null, Locale.ENGLISH), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	@Override
	public ApiResponses getStakeHolder(String referenceId) {
		try {
			logger.info(CLASS + " getStakeHolder request refrenceId {}", referenceId);

			TrustedStakeholder trustedStakeholder = trustedStakeholdersRepository.findByReferenceId(referenceId);
			if(trustedStakeholder != null) {
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.stake.holder.found",null, Locale.ENGLISH), trustedStakeholder);
			}else {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.trusted.stakeHolder.not.found",null, Locale.ENGLISH), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	@Override
	public ApiResponses getAllStakeHolder(String referredBy, String stakeholderType) {
		try {
			logger.info(CLASS+" getAllStakeHolder orgId {} and stakeholdertype {}", referredBy,stakeholderType);
			List<TrustedStakeholder> trustedStakeholders = null;
			if (referredBy != "") {
				trustedStakeholders = trustedStakeholdersRepository.getAllTrustedStakeHolderByOrgId(referredBy);
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.stake.holder.found",null, Locale.ENGLISH), trustedStakeholders);
			} else if (stakeholderType != "") {
				trustedStakeholders = trustedStakeholdersRepository
						.getAllTrustedStakeHolderByStakeHolderType(stakeholderType);
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.stake.holder.found",null, Locale.ENGLISH), trustedStakeholders);
			} else {
				trustedStakeholders = trustedStakeholdersRepository.getAllTrustedStakeHolder();
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.stake.holder.found",null, Locale.ENGLISH), trustedStakeholders);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	@Override
	public ApiResponses updateStakeHolder(TrustedStakeholderDto trustedStakeHolder) {
		try {
			logger.info(CLASS + " updateStakeHolder request {}", trustedStakeHolder);
			Optional<TrustedStakeholderDto> stakeHolder = Optional.ofNullable(trustedStakeHolder);
			if (stakeHolder.isPresent()) {
				if (stakeHolder.get().getReferenceId() == "") {
					return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.provide.reference.id.for.update.record",null, Locale.ENGLISH), null);
				} else {
					TrustedStakeholder trustedStake = trustedStakeholdersRepository
							.findByReferenceId(stakeHolder.get().getReferenceId());
					if (trustedStake != null) {
						trustedStake.setName(stakeHolder.get().getName());
						trustedStake.setSpocUgpassEmail(stakeHolder.get().getSpocUgpassEmail());
						trustedStakeholdersRepository.save(trustedStake);
						return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.update.stakeholder",null, Locale.ENGLISH), null);
					} else {
						return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.trusted.stakeHolder.not.found",null, Locale.ENGLISH), null);
					}
				}
			} else {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.trustedStakeHolder.cant.be.null.or.empty",null, Locale.ENGLISH), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	@Override
	public ApiResponses getStakeHolderList(String spocEmail) {
		try {
			List<TrustedStakeholder> trustedStakeholders = trustedStakeholdersRepository.getStakeHolderList(spocEmail);
			if(trustedStakeholders.size() == 0) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.trusted.stakeHolder.not.found",null, Locale.ENGLISH), null);
			}else {
				return AppUtil.createApiResponse(true, messageSource.getMessage("api.response.trusted.stake.holder.found",null, Locale.ENGLISH), trustedStakeholders);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin",null, Locale.ENGLISH), null);
		}
	}

	public String generateRandomSuffix(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(characters.charAt(random.nextInt(characters.length())));
		}
		return sb.toString();
	}

}
