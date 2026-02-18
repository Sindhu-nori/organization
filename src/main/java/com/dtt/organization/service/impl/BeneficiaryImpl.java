package com.dtt.organization.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.BenificiariesDto;
import com.dtt.organization.dto.BenificiariesRespDto;
import com.dtt.organization.dto.BenificiariesResponseDto;
import com.dtt.organization.enums.SponsorType;
import com.dtt.organization.model.BeneficiariedPrivilegeService;
import com.dtt.organization.model.BeneficiaryInfoView;
import com.dtt.organization.model.BeneficiaryValidity;
import com.dtt.organization.model.Benificiaries;
import com.dtt.organization.model.Subscriber;
import com.dtt.organization.repository.BeneficiariedPrivilegeServiceRepo;
import com.dtt.organization.repository.BeneficiariesRepo;
import com.dtt.organization.repository.BeneficiaryInfoViewRepo;
import com.dtt.organization.repository.BeneficiaryValidityRepository;
import com.dtt.organization.repository.SubscriberFcmTokenRepoIface;
import com.dtt.organization.repository.SubscriberRepository;
import com.dtt.organization.service.iface.BeneficiaryIface;
import com.dtt.organization.util.AppUtil;
import com.dtt.organization.util.LinkedBeneficiraryWorkerThread;

@Service
public class BeneficiaryImpl implements BeneficiaryIface {
	
	final static String CLASS = "BeneficiaryImpl";
	Logger logger = LoggerFactory.getLogger(BeneficiaryImpl.class);

	@Autowired
	BeneficiariesRepo beneficiariesRepo;

	@Autowired
	BeneficiaryValidityRepository beneficiaryValidityRepository;

	@Autowired
	BeneficiariedPrivilegeServiceRepo beneficiariedPrivilegeServiceRepo;

	@Autowired
	SubscriberRepository subscriberRepository;

	@Autowired
	BeneficiaryInfoViewRepo beneficiaryInfoViewRepo;

//	@Autowired
//	SubscriberViewRepository subscriberViewRepository;
	
	@Autowired
	SubscriberFcmTokenRepoIface subscriberFcmTokenRepoIface;

	@Value("${return.multiple.sponsors}")
	private boolean returnMultipleSponsors;

	@Value("${orgLink.notifyurl}")
	private String sendNotificationURL;

	@Value("${sponsor.linked.msg}")
	private String sponsorLinkedMessage;
	
	@Value("${egp.org.id}")
	private String egpOrgId;
	
	@Autowired
	public RestTemplate restTemplate;
	
	@Autowired
	MessageSource messageSource;


	@Override
	public ApiResponses addBeneficiary(BenificiariesDto benificiariesDto) {
		try {
			
			BenificiariesResponseDto benificiariesResponseDto = new BenificiariesResponseDto();

			if (benificiariesDto.getSponsorDigitalId() == null || benificiariesDto.getSponsorType() == null || benificiariesDto.getBeneficiaryType() == null) {
				return AppUtil.createApiResponse(false, "SponsorDigitalId and SponsorType and BeneficiaryType cannot be null", null);
			}

			if(benificiariesDto.getBeneficiaryDigitalId() == null && benificiariesDto.getBeneficiaryNin() == null && benificiariesDto.getBeneficiaryPassport() == null && benificiariesDto.getBeneficiaryUgpassEmail() == null && benificiariesDto.getBeneficiaryMobileNumber() == null) {
				return AppUtil.createApiResponse(false, "At least one of NIN, passport, mobile number, or ugpass email must be required", null);
			}

			if (benificiariesDto.getBeneficiaryValidities().isEmpty()) {
				return AppUtil.createApiResponse(false, "Atleast one service should be selected", null);

			}
			for (BeneficiaryValidity beneficiaryValidity1 : benificiariesDto.getBeneficiaryValidities()) {


					if (beneficiaryValidity1.isValidityApplicable()) {
						if (beneficiaryValidity1.getValidFrom() == null || beneficiaryValidity1.getValidFrom() == ""
								|| beneficiaryValidity1.getValidUpTo() == null
								|| beneficiaryValidity1.getValidUpTo() == "") {
							return AppUtil.createApiResponse(false, "Valid from and valid upto is required", null);
						}
					}
				}

			if (benificiariesDto.getBeneficiaryNin() != null) {
				if (beneficiariesRepo.findDuplicateBeneficiariesByNIN(benificiariesDto.getSponsorDigitalId(),
						benificiariesDto.getBeneficiaryNin()) != null) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}

			if (benificiariesDto.getBeneficiaryPassport() != null) {
				if (beneficiariesRepo.findDuplicateBeneficiariesByPassport(benificiariesDto.getSponsorDigitalId(),
						benificiariesDto.getBeneficiaryPassport()) != null) {
					return AppUtil.createApiResponse(false, "Beneficiary already existsd", null);
				}
			}
			if (benificiariesDto.getBeneficiaryMobileNumber() != null) {
				if (beneficiariesRepo.findDuplicateBeneficiariesByMobileNumber(benificiariesDto.getSponsorDigitalId(),
						benificiariesDto.getBeneficiaryMobileNumber()) != null) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}

			}
			if (benificiariesDto.getBeneficiaryOfficeEmail() != null) {
				if (beneficiariesRepo.findDuplicateBeneficiariesByOfficeEmail(benificiariesDto.getSponsorDigitalId(),
						benificiariesDto.getBeneficiaryOfficeEmail()) != null) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}
			if (benificiariesDto.getBeneficiaryUgpassEmail() != null) {
				if (beneficiariesRepo.findDuplicateBeneficiariesByUgPassEmail(benificiariesDto.getSponsorDigitalId(),
						benificiariesDto.getBeneficiaryUgpassEmail()) != null) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}
			if (benificiariesDto.getBeneficiaryDigitalId() != null) {
				if (beneficiariesRepo.findDuplicateBeneficiariesByBeneficiaryDigitalId(benificiariesDto.getSponsorDigitalId(),
						benificiariesDto.getBeneficiaryDigitalId()) != null) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}

			Benificiaries benificiaries = new Benificiaries();

			benificiaries.setBeneficiaryPassport(benificiariesDto.getBeneficiaryPassport());

			benificiaries.setBeneficiaryType(benificiariesDto.getBeneficiaryType());

			benificiaries.setBeneficiaryName(benificiariesDto.getBeneficiaryName());
			benificiaries.setSponsorName(benificiariesDto.getSponsorName());
			benificiaries.setUpdatedOn(AppUtil.getDate());
			benificiaries.setCreatedOn(AppUtil.getDate());

			benificiaries.setSponsorType(benificiariesDto.getSponsorType());

			benificiaries.setStatus("ACTIVE");
			benificiaries.setSponsorDigitalId(benificiariesDto.getSponsorDigitalId());
			benificiaries.setSponsorExternalId(benificiariesDto.getSponsorExternalId());
			benificiaries.setBeneficiaryDigitalId(benificiariesDto.getBeneficiaryDigitalId());
			benificiaries.setSignaturePhoto(benificiariesDto.getSignaturePhoto());
			benificiaries.setDesignation(benificiariesDto.getDesignation());

			if (benificiariesDto.getSponsorType().equals(SponsorType.ORGANIZATION)) {
				benificiaries.setSponsorPaymentPriorityLevel(2);
			} else {
				benificiaries.setSponsorPaymentPriorityLevel(1);
			}
			benificiaries.setBeneficiaryNin(benificiariesDto.getBeneficiaryNin());
			benificiaries.setBeneficiaryMobileNumber(benificiariesDto.getBeneficiaryMobileNumber());
			benificiaries.setBeneficiaryOfficeEmail(benificiariesDto.getBeneficiaryOfficeEmail());
			benificiaries.setBeneficiaryUgPassEmail(benificiariesDto.getBeneficiaryUgpassEmail());
			benificiaries.setBeneficiaryConsentAcquired(benificiariesDto.isBeneficiaryConsentAcquired());
			Benificiaries benificiariesDb = beneficiariesRepo.save(benificiaries);
//
			benificiariesResponseDto.setBenificiaries(benificiariesDb);
			List<BeneficiaryValidity> beneficiaryValidities = new ArrayList<BeneficiaryValidity>();
			for (BeneficiaryValidity beneficiaryValidity : benificiariesDto.getBeneficiaryValidities()) {
				
				BeneficiaryValidity beneficiaryValidityDB = new BeneficiaryValidity();
				beneficiaryValidityDB.setBeneficiaryId(benificiariesDb.getId());
				beneficiaryValidityDB.setCreatedOn(AppUtil.getDate());
				beneficiaryValidityDB.setUpdatedOn(AppUtil.getDate());
				beneficiaryValidityDB.setStatus("ACTIVE");
				beneficiaryValidityDB.setPrivilegeServiceId(beneficiaryValidity.getPrivilegeServiceId());
				beneficiaryValidityDB.setValidityApplicable(beneficiaryValidity.isValidityApplicable());
				beneficiaryValidityDB.setValidUpTo(beneficiaryValidity.getValidUpTo());
				beneficiaryValidityDB.setValidFrom(beneficiaryValidity.getValidFrom());
				beneficiaryValidityRepository.save(beneficiaryValidityDB);
				
				beneficiaryValidities.add(beneficiaryValidityDB);

			}
			
			benificiariesResponseDto.setBeneficiaryValidity(beneficiaryValidities);

			ExecutorService executor = Executors.newFixedThreadPool(1000);
			Runnable worker = new LinkedBeneficiraryWorkerThread(benificiariesDb, subscriberRepository,
					beneficiariesRepo, subscriberFcmTokenRepoIface, sendNotificationURL, sponsorLinkedMessage);
			executor.execute(worker);
			executor.shutdown();

			return AppUtil.createApiResponse(true, "Beneficiary Created Successfully", benificiariesResponseDto);
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	@Override
	public ApiResponses findPrivilegeByStatus() {
		try {
			List<BeneficiariedPrivilegeService> beneficiariedPrivilegeServices = beneficiariedPrivilegeServiceRepo
					.findPrivilegeByStatus();
			return AppUtil.createApiResponse(true, "privileges fetched successfully", beneficiariedPrivilegeServices);
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	@Override
	public ApiResponses getAllBeneficiaries() {
		try {
			return AppUtil.createApiResponse(true, "Fetched All beneficiaries successfully",
					beneficiariesRepo.findAll());
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	@Override
	public ApiResponses getAllBeneficiariesBySponsor(String sponsorId) {
		try {

			return AppUtil.createApiResponse(true, "Fetched Beneficiaries of a particular sponsor",beneficiariesRepo.getAllBeneficiariesBySponsorByDigitalId(sponsorId));

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	@Override
	public ApiResponses getBeneficiaryById(int id) {
		try {
			if (id == 0) {
				return AppUtil.createApiResponse(false, "Beneficiary Id cannot be zero", null);
			}
			if (!beneficiariesRepo.findById(id).isPresent()) {
				return AppUtil.createApiResponse(false, "No record found with given Id", null);
			}
//			Benificiaries benificiaries = beneficiariesRepo.findById(id).get();
//			BenificiariesDto benificiariesDto = new BenificiariesDto();
//			benificiariesDto.setId(id);
//			benificiariesDto.setUpdatedOn(benificiaries.getUpdatedOn());
//			benificiariesDto.setStatus(benificiaries.getStatus());
//			benificiariesDto.setCreatedOn(benificiaries.getCreatedOn());
//			benificiariesDto.setBeneficiaryDigitalId(benificiaries.getBeneficiaryDigitalId());
//			benificiariesDto.setBeneficiaryNin(benificiaries.getBeneficiaryNin());
//			benificiariesDto.setSponsorType(benificiaries.getSponsorType());
//			benificiariesDto.setSponsorDigitalId(benificiaries.getSponsorDigitalId());
//			benificiariesDto.setBeneficiaryPassport(benificiaries.getBeneficiaryPassport());
//			benificiariesDto.setBeneficiaryType(benificiaries.getBeneficiaryType());
//			benificiariesDto.setSponsorExternalId(benificiaries.getSponsorExternalId());
//			benificiariesDto.setSponsorPaymentPriorityLevel(benificiaries.getSponsorPaymentPriorityLevel());
//			benificiariesDto.setCreatedOn(benificiaries.getCreatedOn());
//			benificiariesDto.setDesignation(benificiaries.getDesignation());
//			benificiariesDto.setBeneficiaryConsentAcquired(benificiaries.isBeneficiaryConsentAcquired());
//			benificiariesDto.setSponsorPaymentPriorityLevel(benificiaries.getSponsorPaymentPriorityLevel());
//			List<BeneficiariedPrivilegeService> beneficiariedPrivilegeServiceList = new ArrayList<>();
//			for (BeneficiaryValidity beneficiaryValidity : beneficiaryValidityRepository
//					.findAllBeneficiaryValiditybybenefeciaryId(id)) {
//
//				beneficiariedPrivilegeServiceList.add(beneficiariedPrivilegeServiceRepo
//						.findPrivilegeById(beneficiaryValidity.getPrivilegeServiceId()));
//
//			}
//
//			List<BeneficiaryValidity> beneficiaryValidity = beneficiaryValidityRepository
//					.findAllBeneficiaryValiditybybenefeciaryId(id);
//			benificiariesDto.setBeneficiaryValidities(beneficiaryValidity);
//			benificiariesDto.setBeneficiariedPrivilegeList(beneficiariedPrivilegeServiceList);
			
			Benificiaries benificiaries= beneficiariesRepo.findById(id).get();
			BenificiariesDto benificiariesDto= new BenificiariesDto();
			benificiariesDto.setId(id);
			benificiariesDto.setUpdatedOn(benificiaries.getUpdatedOn());
			benificiariesDto.setStatus(benificiaries.getStatus());
			benificiariesDto.setCreatedOn(benificiaries.getCreatedOn());
			benificiariesDto.setBeneficiaryDigitalId(benificiaries.getBeneficiaryDigitalId());
			benificiariesDto.setSponsorName(benificiaries.getSponsorName());
			benificiariesDto.setBeneficiaryName(benificiaries.getBeneficiaryName());
			benificiariesDto.setBeneficiaryNin(benificiaries.getBeneficiaryNin());
			benificiariesDto.setSponsorType(benificiaries.getSponsorType());
			benificiariesDto.setBeneficiaryMobileNumber(benificiaries.getBeneficiaryMobileNumber());
			benificiariesDto.setBeneficiaryOfficeEmail(benificiaries.getBeneficiaryOfficeEmail());
			benificiariesDto.setBeneficiaryUgpassEmail(benificiaries.getBeneficiaryUgPassEmail());
			benificiariesDto.setBeneficiaryConsentAcquired(benificiaries.isBeneficiaryConsentAcquired());
			benificiariesDto.setSignaturePhoto(benificiaries.getSignaturePhoto());
			benificiariesDto.setDesignation(benificiaries.getDesignation());

			benificiariesDto.setSponsorDigitalId(benificiaries.getSponsorDigitalId());
			benificiariesDto.setBeneficiaryPassport(benificiaries.getBeneficiaryPassport());
			benificiariesDto.setBeneficiaryType(benificiaries.getBeneficiaryType());
			benificiariesDto.setSponsorExternalId(benificiaries.getSponsorExternalId());
			benificiariesDto.setSponsorPaymentPriorityLevel(benificiaries.getSponsorPaymentPriorityLevel());
			benificiariesDto.setCreatedOn(benificiaries.getCreatedOn());
			benificiariesDto.setDesignation(benificiaries.getDesignation());
			benificiariesDto.setBeneficiaryConsentAcquired(benificiaries.isBeneficiaryConsentAcquired());
			benificiariesDto.setSponsorPaymentPriorityLevel(benificiaries.getSponsorPaymentPriorityLevel());
			List<BeneficiariedPrivilegeService> beneficiariedPrivilegeServiceList= new ArrayList<>();
			for(BeneficiaryValidity  beneficiaryValidity: beneficiaryValidityRepository.findAllBeneficiaryValiditybybenefeciaryId(id)){

			    beneficiariedPrivilegeServiceList.add(beneficiariedPrivilegeServiceRepo.findPrivilegeById(beneficiaryValidity.getPrivilegeServiceId()));

			}

			List<BeneficiaryValidity>  beneficiaryValidity=beneficiaryValidityRepository.findAllBeneficiaryValiditybybenefeciaryId(id);
			benificiariesDto.setBeneficiaryValidities(beneficiaryValidity);
			benificiariesDto.setBeneficiariedPrivilegeList(beneficiariedPrivilegeServiceList);

			return AppUtil.createApiResponse(true, "Fetched record successfully", benificiariesDto);
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

//	@Override
//	public ApiResponse updateBeneficiary(BenificiariesDto benificiariesDto) {
//
//		try {
//
//			BenificiariesResponseDto benificiariesResponseDto = new BenificiariesResponseDto();
//			Benificiaries nin = beneficiariesRepo.findDuplicateBeneficiariesByNIN(
//					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryNin());
//			Benificiaries pass = beneficiariesRepo.findDuplicateBeneficiariesByPassport(
//					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryPassport());
//			Benificiaries mob = beneficiariesRepo.findDuplicateBeneficiariesByMobileNumber(
//					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryMobileNumber());
//			Benificiaries officeEmail = beneficiariesRepo.findDuplicateBeneficiariesByOfficeEmail(
//					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryOfficeEmail());
//			Benificiaries ugPassEmail = beneficiariesRepo.findDuplicateBeneficiariesByUgPassEmail(
//					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryUgpassEmail());
//			Benificiaries benificiaryDigitalId = beneficiariesRepo.findDuplicateBeneficiariesByBeneficiaryDigitalId(
//					benificiariesDto.getSponsorDigitalId(),benificiariesDto.getBeneficiaryDigitalId());
//
////
////			if (benificiariesDto.getSponsorDigitalId().isEmpty() && benificiariesDto.getSponsorType() != null
////					&& (benificiariesDto.getBeneficiaryNin().isEmpty()
////							|| benificiariesDto.getBeneficiaryPassport().isEmpty()
////							|| benificiariesDto.getBeneficiaryMobileNumber().isEmpty()
////							|| benificiariesDto.getBeneficiaryUgpassEmail().isEmpty())) {
////				return AppUtil.createApiResponse(false,
////						"NIN or passport  or mobile number or ugpass email  cannot be null", null);
////			}
//
//			if (benificiariesDto.getSponsorDigitalId() == null || benificiariesDto.getSponsorType() == null || benificiariesDto.getBeneficiaryType() == null) {
//				return AppUtil.createApiResponse(false, "SponsorDigitalId and SponsorType and BeneficiaryType cannot be null", null);
//			}
//
//
//			if(benificiariesDto.getBeneficiaryDigitalId() == null && benificiariesDto.getBeneficiaryNin() == null && benificiariesDto.getBeneficiaryPassport() == null && benificiariesDto.getBeneficiaryUgpassEmail() == null && benificiariesDto.getBeneficiaryMobileNumber() == null) {
//				return AppUtil.createApiResponse(false, "At least one of NIN, passport, mobile number, or ugpass email must be required", null);
//			}
//
//			if (benificiariesDto.getBeneficiaryValidities().isEmpty()) {
//				return AppUtil.createApiResponse(false, "Atleast one service should be selected", null);
//
//			}
//
//			for (BeneficiaryValidity beneficiaryValidity1 : benificiariesDto.getBeneficiaryValidities()) {
//
//
//
//					if (beneficiaryValidity1.isValidityApplicable()) {
//						if (beneficiaryValidity1.getValidFrom() == null || beneficiaryValidity1.getValidFrom() == ""
//								|| beneficiaryValidity1.getValidUpTo() == null
//								|| beneficiaryValidity1.getValidUpTo() == "") {
//							return AppUtil.createApiResponse(false, "Valid from and valid upto is required", null);
//						}
//					}
//				}
//
//			if (benificiariesDto.getBeneficiaryNin() != null) {
//				if ((nin != null) && nin.getId() != benificiariesDto.getId()) {
//					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
//				}
//			}
//
//			if ((benificiariesDto.getBeneficiaryPassport() != null)) {
//				if (pass != null && pass.getId() != benificiariesDto.getId()) {
//					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
//				}
//			}
//			if (benificiariesDto.getBeneficiaryMobileNumber() != null) {
//				if (mob != null && mob.getId() != benificiariesDto.getId()) {
//					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
//				}
//
//			}
//			if (benificiariesDto.getBeneficiaryOfficeEmail() != null) {
//				if (officeEmail != null && officeEmail.getId() != benificiariesDto.getId()) {
//					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
//				}
//			}
//			if (benificiariesDto.getBeneficiaryUgpassEmail() != null) {
//				if (ugPassEmail != null && ugPassEmail.getId() != benificiariesDto.getId()) {
//					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
//				}
//			}
//			if (benificiariesDto.getBeneficiaryDigitalId() != null) {
//				if (benificiaryDigitalId != null && benificiaryDigitalId.getId() != benificiariesDto.getId()) {
//					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
//				}
//			}
//
//
//			Benificiaries benificiaries = beneficiariesRepo.findById(benificiariesDto.getId()).get();
//
//			benificiaries.setId(benificiariesDto.getId());
//			benificiaries.setBeneficiaryPassport(benificiariesDto.getBeneficiaryPassport());
//			benificiaries.setBeneficiaryType(benificiariesDto.getBeneficiaryType());
//			benificiaries.setUpdatedOn(AppUtil.getDate());
////            benificiaries.setSponsorType(SponsorType.valueOf(benificiariesDto.getSponsorType().toString()));
//			benificiaries.setSponsorType(benificiariesDto.getSponsorType());
//			benificiaries.setStatus("ACTIVE");
//			benificiaries.setSponsorDigitalId(benificiariesDto.getSponsorDigitalId());
//			benificiaries.setSponsorExternalId(benificiariesDto.getSponsorExternalId());
//			benificiaries.setBeneficiaryDigitalId(benificiariesDto.getBeneficiaryDigitalId());
//			benificiaries.setSignaturePhoto(benificiariesDto.getSignaturePhoto());
//			benificiaries.setDesignation(benificiariesDto.getDesignation());
//
//			if (benificiariesDto.getSponsorType().equals(SponsorType.ORGANIZATION)) {
//				benificiaries.setSponsorPaymentPriorityLevel(2);
//			} else {
//				benificiaries.setSponsorPaymentPriorityLevel(1);
//			}
//			benificiaries.setBeneficiaryNin(benificiariesDto.getBeneficiaryNin());
//			benificiaries.setBeneficiaryMobileNumber(benificiariesDto.getBeneficiaryMobileNumber());
//			benificiaries.setBeneficiaryOfficeEmail(benificiariesDto.getBeneficiaryOfficeEmail());
//			benificiaries.setBeneficiaryUgPassEmail(benificiariesDto.getBeneficiaryUgpassEmail());
//			benificiaries.setBeneficiaryConsentAcquired(benificiaries.isBeneficiaryConsentAcquired());
//
//			benificiaries.setBeneficiaryName(benificiariesDto.getBeneficiaryName());
//			benificiaries.setSponsorName(benificiariesDto.getSponsorName());
//
//			Benificiaries benificiariesDb = beneficiariesRepo.save(benificiaries);
//
//			benificiariesResponseDto.setBenificiaries(benificiariesDb);
//
//			beneficiaryValidityRepository.deleteAllBeneficiaryValiditybybenefeciaryId(benificiariesDto.getId());
//
//			List<BeneficiaryValidity> validities = new ArrayList<BeneficiaryValidity>();
//			for (BeneficiaryValidity beneficiaryValidity : benificiariesDto.getBeneficiaryValidities()) {
//				BeneficiaryValidity beneficiaryValidityDB = new BeneficiaryValidity();
//				beneficiaryValidityDB.setBeneficiaryId(benificiariesDb.getId());
//				beneficiaryValidityDB.setCreatedOn(AppUtil.getDate());
//				beneficiaryValidityDB.setUpdatedOn(AppUtil.getDate());
//				beneficiaryValidityDB.setStatus("ACTIVE");
//				beneficiaryValidityDB.setPrivilegeServiceId(beneficiaryValidity.getPrivilegeServiceId());
//				beneficiaryValidityDB.setValidityApplicable(beneficiaryValidity.isValidityApplicable());
//				beneficiaryValidityDB.setValidUpTo(beneficiaryValidity.getValidUpTo());
//				beneficiaryValidityDB.setValidFrom(beneficiaryValidity.getValidFrom());
//				beneficiaryValidityRepository.save(beneficiaryValidityDB);
//
//				validities.add(beneficiaryValidityDB);
//			}
//
//			benificiariesResponseDto.setBeneficiaryValidity(validities);
//			return AppUtil.createApiResponse(true, "Beneficiary Updated Successfully", benificiariesResponseDto);
//		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
//				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
//			ex.printStackTrace();
//			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
//		}
//	}

	@Override
	public ApiResponses updateBeneficiary(BenificiariesDto benificiariesDto) {

		try {

			BenificiariesResponseDto benificiariesResponseDto = new BenificiariesResponseDto();
			Benificiaries nin = beneficiariesRepo.findDuplicateBeneficiariesByNIN(
					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryNin());
			Benificiaries pass = beneficiariesRepo.findDuplicateBeneficiariesByPassport(
					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryPassport());
			Benificiaries mob = beneficiariesRepo.findDuplicateBeneficiariesByMobileNumber(
					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryMobileNumber());
			Benificiaries officeEmail = beneficiariesRepo.findDuplicateBeneficiariesByOfficeEmail(
					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryOfficeEmail());
			Benificiaries ugPassEmail = beneficiariesRepo.findDuplicateBeneficiariesByUgPassEmail(
					benificiariesDto.getSponsorDigitalId(), benificiariesDto.getBeneficiaryUgpassEmail());
			Benificiaries benificiaryDigitalId = beneficiariesRepo.findDuplicateBeneficiariesByBeneficiaryDigitalId(
					benificiariesDto.getSponsorDigitalId(),benificiariesDto.getBeneficiaryDigitalId());

//
//			if (benificiariesDto.getSponsorDigitalId().isEmpty() && benificiariesDto.getSponsorType() != null
//					&& (benificiariesDto.getBeneficiaryNin().isEmpty()
//							|| benificiariesDto.getBeneficiaryPassport().isEmpty()
//							|| benificiariesDto.getBeneficiaryMobileNumber().isEmpty()
//							|| benificiariesDto.getBeneficiaryUgpassEmail().isEmpty())) {
//				return AppUtil.createApiResponse(false,
//						"NIN or passport  or mobile number or ugpass email  cannot be null", null);
//			}

			if (benificiariesDto.getSponsorDigitalId() == null || benificiariesDto.getSponsorType() == null || benificiariesDto.getBeneficiaryType() == null) {
				return AppUtil.createApiResponse(false, "SponsorDigitalId and SponsorType and BeneficiaryType cannot be null", null);
			}


			if(benificiariesDto.getBeneficiaryDigitalId() == null && benificiariesDto.getBeneficiaryNin() == null && benificiariesDto.getBeneficiaryPassport() == null && benificiariesDto.getBeneficiaryUgpassEmail() == null && benificiariesDto.getBeneficiaryMobileNumber() == null) {
				return AppUtil.createApiResponse(false, "At least one of NIN, passport, mobile number, or ugpass email must be required", null);
			}

			if (benificiariesDto.getBeneficiaryValidities().isEmpty()) {
				return AppUtil.createApiResponse(false, "Atleast one service should be selected", null);

			}

			for (BeneficiaryValidity beneficiaryValidity1 : benificiariesDto.getBeneficiaryValidities()) {



				if (beneficiaryValidity1.isValidityApplicable()) {
					if (beneficiaryValidity1.getValidFrom() == null || beneficiaryValidity1.getValidFrom() == ""
							|| beneficiaryValidity1.getValidUpTo() == null
							|| beneficiaryValidity1.getValidUpTo() == "") {
						return AppUtil.createApiResponse(false, "Valid from and valid upto is required", null);
					}
				}
			}

			if (benificiariesDto.getBeneficiaryNin() != null) {
				if ((nin != null) && nin.getId() != benificiariesDto.getId()) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}

			if ((benificiariesDto.getBeneficiaryPassport() != null)) {
				if (pass != null && pass.getId() != benificiariesDto.getId()) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}
			if (benificiariesDto.getBeneficiaryMobileNumber() != null) {
				if (mob != null && mob.getId() != benificiariesDto.getId()) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}

			}
			if (benificiariesDto.getBeneficiaryOfficeEmail() != null) {
				if (officeEmail != null && officeEmail.getId() != benificiariesDto.getId()) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}
			if (benificiariesDto.getBeneficiaryUgpassEmail() != null) {
				if (ugPassEmail != null && ugPassEmail.getId() != benificiariesDto.getId()) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}
			if (benificiariesDto.getBeneficiaryDigitalId() != null) {
				if (benificiaryDigitalId != null && benificiaryDigitalId.getId() != benificiariesDto.getId()) {
					return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
				}
			}


			Benificiaries benificiaries = beneficiariesRepo.findById(benificiariesDto.getId()).get();

			benificiaries.setId(benificiariesDto.getId());
			benificiaries.setBeneficiaryPassport(benificiariesDto.getBeneficiaryPassport());
			benificiaries.setBeneficiaryType(benificiariesDto.getBeneficiaryType());
			benificiaries.setUpdatedOn(AppUtil.getDate());
//            benificiaries.setSponsorType(SponsorType.valueOf(benificiariesDto.getSponsorType().toString()));
			benificiaries.setSponsorType(benificiariesDto.getSponsorType());
			benificiaries.setStatus("ACTIVE");
			benificiaries.setSponsorDigitalId(benificiariesDto.getSponsorDigitalId());
			benificiaries.setSponsorExternalId(benificiariesDto.getSponsorExternalId());
			benificiaries.setBeneficiaryDigitalId(benificiariesDto.getBeneficiaryDigitalId());
			benificiaries.setSignaturePhoto(benificiariesDto.getSignaturePhoto());
			benificiaries.setDesignation(benificiariesDto.getDesignation());

			if (benificiariesDto.getSponsorType().equals(SponsorType.ORGANIZATION)) {
				benificiaries.setSponsorPaymentPriorityLevel(2);
			} else {
				benificiaries.setSponsorPaymentPriorityLevel(1);
			}
			//Subscriber subscriber=subscriberRepository.findSubscriberDetails(benificiariesDto.getBeneficiaryNin(),benificiariesDto.getBeneficiaryPassport(),benificiariesDto.getBeneficiaryUgpassEmail(),benificiariesDto.getBeneficiaryMobileNumber());
			Subscriber subscriber = subscriberRepository.findSubscriberDetails(
					benificiariesDto.getBeneficiaryNin(),
					benificiariesDto.getBeneficiaryPassport(),
					benificiariesDto.getBeneficiaryUgpassEmail(),
					benificiariesDto.getBeneficiaryMobileNumber()
			).stream().findFirst().orElse(null);

			if(subscriber!=null){

				benificiaries.setBeneficiaryDigitalId(subscriber.getSubscriberUid());
				if(subscriber.getIdDocType().equals("3")){
					benificiaries.setBeneficiaryPassport(subscriber.getIdDocNumber());
				}else{
					benificiaries.setBeneficiaryNin(subscriber.getIdDocNumber());
				}
//				benificiaries.setBeneficiaryNin(subscriber.getIdDocNumber());
				benificiaries.setBeneficiaryMobileNumber(subscriber.getMobileNumber());
				benificiaries.setBeneficiaryUgPassEmail(subscriber.getEmailId());
			}else{
				benificiaries.setBeneficiaryPassport(benificiariesDto.getBeneficiaryPassport());
				benificiaries.setBeneficiaryNin(benificiariesDto.getBeneficiaryNin());
				benificiaries.setBeneficiaryMobileNumber(benificiariesDto.getBeneficiaryMobileNumber());
				benificiaries.setBeneficiaryUgPassEmail(benificiariesDto.getBeneficiaryUgpassEmail());
			}

			benificiaries.setBeneficiaryOfficeEmail(benificiariesDto.getBeneficiaryOfficeEmail());
//			benificiaries.setBeneficiaryUgPassEmail(benificiariesDto.getBeneficiaryUgpassEmail());
			benificiaries.setBeneficiaryConsentAcquired(benificiaries.isBeneficiaryConsentAcquired());
//			benificiaries.setBeneficiaryDigitalId();
			benificiaries.setBeneficiaryName(benificiariesDto.getBeneficiaryName());
			benificiaries.setSponsorName(benificiariesDto.getSponsorName());

			Benificiaries benificiariesDb = beneficiariesRepo.save(benificiaries);

			benificiariesResponseDto.setBenificiaries(benificiariesDb);

			beneficiaryValidityRepository.deleteAllBeneficiaryValiditybybenefeciaryId(benificiariesDto.getId());

			List<BeneficiaryValidity> validities = new ArrayList<BeneficiaryValidity>();
			for (BeneficiaryValidity beneficiaryValidity : benificiariesDto.getBeneficiaryValidities()) {
				BeneficiaryValidity beneficiaryValidityDB = new BeneficiaryValidity();
				beneficiaryValidityDB.setBeneficiaryId(benificiariesDb.getId());
				beneficiaryValidityDB.setCreatedOn(AppUtil.getDate());
				beneficiaryValidityDB.setUpdatedOn(AppUtil.getDate());
				beneficiaryValidityDB.setStatus("ACTIVE");
				beneficiaryValidityDB.setPrivilegeServiceId(beneficiaryValidity.getPrivilegeServiceId());
				beneficiaryValidityDB.setValidityApplicable(beneficiaryValidity.isValidityApplicable());
				beneficiaryValidityDB.setValidUpTo(beneficiaryValidity.getValidUpTo());
				beneficiaryValidityDB.setValidFrom(beneficiaryValidity.getValidFrom());
				beneficiaryValidityRepository.save(beneficiaryValidityDB);

				validities.add(beneficiaryValidityDB);
			}

			benificiariesResponseDto.setBeneficiaryValidity(validities);
			return AppUtil.createApiResponse(true, "Beneficiary Updated Successfully", benificiariesResponseDto);
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	@Override
	public ApiResponses dlink(int id) {
		try {
			beneficiariesRepo.changeStatusById(id);
			return AppUtil.createApiResponse(true, "D-Linked Successfully", null);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

//	@Override
//	public ApiResponse verifyOnBoardingSponsor(String suid) {
//
//		try {
//			if (suid.isEmpty() || suid == null) {
//				return AppUtil.createApiResponse(false, "Suid cannot be null", null);
//			}
//			Subscriber subscriber = subscriberRepository.findBysubscriberUid(suid);
//
//			if (subscriber == null) {
//				return AppUtil.createApiResponse(false, "Subscriber is not found", null);
//			}
//
//			if (subscriber.getSubscriberUid() != null || !subscriber.getSubscriberUid().isEmpty()
//					|| subscriber.getEmailId() != null || !subscriber.getEmailId().isEmpty()
//					|| subscriber.getIdDocNumber() != null || !subscriber.getIdDocNumber().isEmpty()
//					|| subscriber.getNationalId() != null || !subscriber.getNationalId().isEmpty()
//					|| subscriber.getMobileNumber() != null || !subscriber.getMobileNumber().isEmpty()
//					|| !subscriber.getNationalId().isEmpty() || subscriber.getNationalId() != null) {
//
//				List<Benificiaries> benificiaries = beneficiariesRepo
//						.findByEmailOrPassportOrNinOrMobileNumberorBeneficiaryDigitalId(subscriber.getEmailId(),
//								subscriber.getIdDocNumber(), subscriber.getNationalId(), subscriber.getMobileNumber(),
//								subscriber.getSubscriberUid());
//
//				System.out.println("Beneifciaries" + benificiaries);
//
//				if (!benificiaries.isEmpty()) {
//					if (returnMultipleSponsors) {
//
//
//						Iterator<Benificiaries> iterator = benificiaries.iterator();
//						while (iterator.hasNext()) {
//							Benificiaries beneficiary = iterator.next();
//							if (beneficiary.getBeneficiaryUgPassEmail() != null && !beneficiary.getBeneficiaryUgPassEmail().isEmpty()) {
//								if (!subscriber.getEmailId().equals(beneficiary.getBeneficiaryUgPassEmail())) {
//									iterator.remove();
//
//								}
//							}
//							if (beneficiary.getBeneficiaryMobileNumber() != null && !beneficiary.getBeneficiaryMobileNumber().isEmpty()) {
//								if (!subscriber.getMobileNumber().equals(beneficiary.getBeneficiaryMobileNumber())) {
//									iterator.remove();
//
//								}
//							}
//							if (beneficiary.getBeneficiaryNin() != null && !beneficiary.getBeneficiaryNin().isEmpty()) {
//								if (!subscriber.getNationalId().equals(beneficiary.getBeneficiaryNin())) {
//									iterator.remove();
//
//								}
//							}
//							if (beneficiary.getBeneficiaryPassport() != null && !beneficiary.getBeneficiaryPassport().isEmpty()) {
//								if (!subscriber.getIdDocNumber().equals(beneficiary.getBeneficiaryPassport())) {
//									iterator.remove();
//
//								}
//							}
//							if (beneficiary.getBeneficiaryDigitalId() != null && !beneficiary.getBeneficiaryDigitalId().isEmpty()) {
//								if (!subscriber.getSubscriberUid().equals(beneficiary.getBeneficiaryDigitalId())) {
//									iterator.remove();
//
//								}
//							}
//
//						}
//						if(benificiaries.isEmpty()){
//							return AppUtil.createApiResponse(false, "sponsor not found", null);
//						}
//						return AppUtil.createApiResponse(true, "Sponsor Found", benificiaries);
//
//					} else {
//
//						for (Benificiaries beneficiary : benificiaries) {
//							if (beneficiary.getBeneficiaryUgPassEmail() != null && !beneficiary.getBeneficiaryUgPassEmail().isEmpty()) {
//								if (!subscriber.getEmailId().equals(beneficiary.getBeneficiaryUgPassEmail())) {
//									return AppUtil.createApiResponse(false, "Subscriber is not found", null);
//								}
//							}
//
//							if (beneficiary.getBeneficiaryMobileNumber() != null && !beneficiary.getBeneficiaryMobileNumber().isEmpty()) {
//								if (!subscriber.getMobileNumber().equals(beneficiary.getBeneficiaryMobileNumber())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "Mobile number not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//
//							if (beneficiary.getBeneficiaryNin() != null && !beneficiary.getBeneficiaryNin().isEmpty()) {
//								if (!subscriber.getNationalId().equals(beneficiary.getBeneficiaryNin())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "Nin not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//							if (beneficiary.getBeneficiaryPassport() != null && !beneficiary.getBeneficiaryPassport().isEmpty()) {
//								if (!subscriber.getIdDocNumber().equals(beneficiary.getBeneficiaryPassport())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "Passport not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//
//							if (beneficiary.getBeneficiaryDigitalId() != null && !beneficiary.getBeneficiaryDigitalId().isEmpty()) {
//								if (!subscriber.getSubscriberUid().equals(beneficiary.getBeneficiaryDigitalId())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "suid not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//
//
//						}
//
//
//						List<Benificiaries> benificiaries1 = new ArrayList<>();
//
//
//						Benificiaries beneficiaryWithHighestPriority = Collections.max(benificiaries,
//								Comparator.comparingInt(Benificiaries::getSponsorPaymentPriorityLevel));
//						benificiaries1.add(beneficiaryWithHighestPriority);
//						System.out.println(beneficiaryWithHighestPriority);
//
//						return AppUtil.createApiResponse(true, "Sponsor found", benificiaries1);
//
//					}
//				}
//
//				return AppUtil.createApiResponse(false, "Sponsor not found", null);
//
//			} else {
//				return AppUtil.createApiResponse(false, "Values cannot be null", null);
//			}
//
//		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
//				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
//			ex.printStackTrace();
//			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
//		}
//
//	}

	@Override
	public ApiResponses verifyOnBoardingSponsor(String suid) {

		try {
			if (suid.isEmpty() || suid == null) {
				return AppUtil.createApiResponse(false, "Suid cannot be null", null);
			}
			Subscriber subscriber = subscriberRepository.findBysubscriberUid(suid);

			if (subscriber == null) {
				return AppUtil.createApiResponse(false, "Subscriber is not found", null);
			}

			if (subscriber.getSubscriberUid() != null || !subscriber.getSubscriberUid().isEmpty()
					|| subscriber.getEmailId() != null || !subscriber.getEmailId().isEmpty()
					|| subscriber.getIdDocNumber() != null || !subscriber.getIdDocNumber().isEmpty()
					|| subscriber.getNationalId() != null || !subscriber.getNationalId().isEmpty()
					|| subscriber.getMobileNumber() != null || !subscriber.getMobileNumber().isEmpty()
					|| !subscriber.getNationalId().isEmpty() || subscriber.getNationalId() != null) {

				List<Benificiaries> benificiaries = beneficiariesRepo
						.findByEmailOrPassportOrNinOrMobileNumberorBeneficiaryDigitalId(subscriber.getEmailId(),
								subscriber.getIdDocNumber(), subscriber.getNationalId(), subscriber.getMobileNumber(),
								subscriber.getSubscriberUid());

				System.out.println("Beneifciaries" + benificiaries);

				if (!benificiaries.isEmpty()) {
					if (returnMultipleSponsors) {


						Iterator<Benificiaries> iterator = benificiaries.iterator();
						while (iterator.hasNext()) {
							Benificiaries beneficiary = iterator.next();
							if (beneficiary.getBeneficiaryUgPassEmail() != null && !beneficiary.getBeneficiaryUgPassEmail().isEmpty()) {
								if (!subscriber.getEmailId().equals(beneficiary.getBeneficiaryUgPassEmail())) {
									iterator.remove();
									continue;
								}
							}
							if (beneficiary.getBeneficiaryMobileNumber() != null && !beneficiary.getBeneficiaryMobileNumber().isEmpty()) {
								if (!subscriber.getMobileNumber().equals(beneficiary.getBeneficiaryMobileNumber())) {
									iterator.remove();
									continue;
								}
							}
							if (beneficiary.getBeneficiaryNin() != null && !beneficiary.getBeneficiaryNin().isEmpty()) {
								if (!subscriber.getNationalId().equals(beneficiary.getBeneficiaryNin())) {
									iterator.remove();
									continue;
								}
							}
							if (beneficiary.getBeneficiaryPassport() != null && !beneficiary.getBeneficiaryPassport().isEmpty()) {
								if (!subscriber.getIdDocNumber().equals(beneficiary.getBeneficiaryPassport())) {
									iterator.remove();
									continue;
								}
							}
							if (beneficiary.getBeneficiaryDigitalId() != null && !beneficiary.getBeneficiaryDigitalId().isEmpty()) {
								if (!subscriber.getSubscriberUid().equals(beneficiary.getBeneficiaryDigitalId())) {
									iterator.remove();
									continue;
								}
							}

						}
						if(benificiaries.isEmpty()){
							return AppUtil.createApiResponse(false, "sponsor is not found", null);
						}
						return AppUtil.createApiResponse(true, "Sponsor Found", benificiaries);

					} else {

//						for (Benificiaries beneficiary : benificiaries) {
//							if (beneficiary.getBeneficiaryUgPassEmail() != null && !beneficiary.getBeneficiaryUgPassEmail().isEmpty()) {
//								if (!subscriber.getEmailId().equals(beneficiary.getBeneficiaryUgPassEmail())) {
//									return AppUtil.createApiResponse(false, "Subscriber is not found", null);
//								}
//							}
//
//							if (beneficiary.getBeneficiaryMobileNumber() != null && !beneficiary.getBeneficiaryMobileNumber().isEmpty()) {
//								if (!subscriber.getMobileNumber().equals(beneficiary.getBeneficiaryMobileNumber())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "Mobile number not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//
//							if (beneficiary.getBeneficiaryNin() != null && !beneficiary.getBeneficiaryNin().isEmpty()) {
//								if (!subscriber.getNationalId().equals(beneficiary.getBeneficiaryNin())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "Nin not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//							if (beneficiary.getBeneficiaryPassport() != null && !beneficiary.getBeneficiaryPassport().isEmpty()) {
//								if (!subscriber.getIdDocNumber().equals(beneficiary.getBeneficiaryPassport())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "Passport not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//
//							if (beneficiary.getBeneficiaryDigitalId() != null && !beneficiary.getBeneficiaryDigitalId().isEmpty()) {
//								if (!subscriber.getSubscriberUid().equals(beneficiary.getBeneficiaryDigitalId())) {
//									//return AppUtil.createApiResponse(false, "Sponsor not found", "suid not matched");
//									return AppUtil.createApiResponse(false, "Sponsor not found", null);
//								}
//							}
//
//
//						}
//
//
//						List<Benificiaries> benificiaries1 = new ArrayList<>();
//
//
//						Benificiaries beneficiaryWithHighestPriority = Collections.max(benificiaries,
//								Comparator.comparingInt(Benificiaries::getSponsorPaymentPriorityLevel));
//						benificiaries1.add(beneficiaryWithHighestPriority);
//						System.out.println(beneficiaryWithHighestPriority);
						Iterator<Benificiaries> iterator = benificiaries.iterator();
						while (iterator.hasNext()) {
							Benificiaries beneficiary = iterator.next();
							if (beneficiary.getBeneficiaryUgPassEmail() != null && !beneficiary.getBeneficiaryUgPassEmail().isEmpty()) {
								if (!subscriber.getEmailId().equals(beneficiary.getBeneficiaryUgPassEmail())) {
									iterator.remove();

								}
							}
							if (beneficiary.getBeneficiaryMobileNumber() != null && !beneficiary.getBeneficiaryMobileNumber().isEmpty()) {
								if (!subscriber.getMobileNumber().equals(beneficiary.getBeneficiaryMobileNumber())) {
									iterator.remove();

								}
							}
							if (beneficiary.getBeneficiaryNin() != null && !beneficiary.getBeneficiaryNin().isEmpty()) {
								if (!subscriber.getNationalId().equals(beneficiary.getBeneficiaryNin())) {
									iterator.remove();

								}
							}
							if (beneficiary.getBeneficiaryPassport() != null && !beneficiary.getBeneficiaryPassport().isEmpty()) {
								if (!subscriber.getIdDocNumber().equals(beneficiary.getBeneficiaryPassport())) {
									iterator.remove();

								}
							}
							if (beneficiary.getBeneficiaryDigitalId() != null && !beneficiary.getBeneficiaryDigitalId().isEmpty()) {
								if (!subscriber.getSubscriberUid().equals(beneficiary.getBeneficiaryDigitalId())) {
									iterator.remove();

								}
							}

						}
						if(benificiaries.isEmpty()){
							return AppUtil.createApiResponse(false, "sponsor is not found", null);
						}

						Benificiaries beneficiaryWithHighestPriority = Collections.max(benificiaries,
								Comparator.comparingInt(Benificiaries::getSponsorPaymentPriorityLevel));
//						benificiaries1.add(beneficiaryWithHighestPriority);
						System.out.println(beneficiaryWithHighestPriority);
						List<Benificiaries> benificiariesList=new ArrayList<>();
						benificiariesList.add(beneficiaryWithHighestPriority);
						return AppUtil.createApiResponse(true,"Sponsor Found",benificiariesList);
					}
				}

				return AppUtil.createApiResponse(false, "Sponsor not found", null);

			} else {
				return AppUtil.createApiResponse(false, "Values cannot be null", null);
			}

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}

	}

	@Override
	public ApiResponses linkSponsor(String beneficiaryDigitalId, int id) {
		try {
			if (beneficiaryDigitalId == null || beneficiaryDigitalId.isEmpty()) {
				return AppUtil.createApiResponse(false, "Suid can't be null", null);
			}
			Subscriber subscriberforLink = subscriberRepository.getSubscriberEmail(beneficiaryDigitalId);

			if (subscriberforLink == null) {
				return AppUtil.createApiResponse(false, "No subscriber data found for given suid", null);
			}

			Benificiaries benificiaries = beneficiariesRepo.getOne(id);

			if (benificiaries == null) {
				return AppUtil.createApiResponse(false, "No beneficiary data found for given id", null);
			}

			benificiaries.setBeneficiaryNin(subscriberforLink.getNationalId());
			benificiaries.setBeneficiaryDigitalId(beneficiaryDigitalId);
			benificiaries.setBeneficiaryPassport(subscriberforLink.getIdDocNumber());
			benificiaries.setBeneficiaryMobileNumber(subscriberforLink.getMobileNumber());
			benificiaries.setBeneficiaryUgPassEmail(subscriberforLink.getEmailId());
			benificiaries.setBeneficiaryConsentAcquired(true);
			benificiaries.setBeneficiaryName(subscriberforLink.getFullName());
			beneficiariesRepo.save(benificiaries);

			return AppUtil.createApiResponse(true, "Beneficiary consent acquired successful", null);

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

//	@Override
//	public ApiResponse getAllSponsersBySuid(String BeneficiaryDigitalId) {
//
//		try {
//			if (BeneficiaryDigitalId.isEmpty() || BeneficiaryDigitalId == null) {
//				return AppUtil.createApiResponse(false, "Suid cannot be null", null);
//			}
//			Subscriber subscriber = subscriberRepository.findBysubscriberUid(BeneficiaryDigitalId);
//			System.out.println();
//
//			if (subscriber == null) {
//				return AppUtil.createApiResponse(false, "Subscriber is not found", null);
//			}
//
//			if (subscriber.getNationalId() == null && subscriber.getEmailId() == null
//					&& subscriber.getIdDocNumber() == null && subscriber.getSubscriberUid() == null) {
//				return AppUtil.createApiResponse(false, "Everything can,t be null", null);
//
//			}
//
//			List<BeneficiaryInfoView> benificiaries = beneficiaryInfoViewRepo
//					.findByEmailOrPassportOrNinOrMobileNumberOrBeneficiaryDigitalId(subscriber.getEmailId(),
//							subscriber.getNationalId(), subscriber.getIdDocNumber(), subscriber.getMobileNumber(),
//							BeneficiaryDigitalId);
//
//			System.out.println(benificiaries);
//
//			if (!benificiaries.isEmpty()) {
//
//				Iterator<BeneficiaryInfoView> iterator = benificiaries.iterator();
//				while (iterator.hasNext()) {
//					BeneficiaryInfoView beneficiary = iterator.next();
//					if (beneficiary.getBeneficiaryDigitalId() != null &&
//							!beneficiary.getBeneficiaryDigitalId().isEmpty() &&
//							!beneficiary.getBeneficiaryDigitalId().equals(BeneficiaryDigitalId)) {
//
//						iterator.remove(); // Safe removal
//					}
//				}
//
//				return AppUtil.createApiResponse(true, "Sponsor found", benificiaries);
//
//			} else {
//				return AppUtil.createApiResponse(false, "Sponsor not found", null);
//			}
//
//		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
//				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
//			ex.printStackTrace();
//			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
//		}
//
//	}

	@Override
	public ApiResponses getAllSponsersBySuid(String BeneficiaryDigitalId) {

		try {
			if (BeneficiaryDigitalId.isEmpty() || BeneficiaryDigitalId == null) {
				return AppUtil.createApiResponse(false, "Suid cannot be null", null);
			}
			Subscriber subscriber = subscriberRepository.findBysubscriberUid(BeneficiaryDigitalId);
			System.out.println("resss:::"+subscriber);

			if (subscriber == null) {
				return AppUtil.createApiResponse(false, "Subscriber is not found", null);
			}

			if (subscriber.getNationalId() == null && subscriber.getEmailId() == null
					&& subscriber.getIdDocNumber() == null && subscriber.getSubscriberUid() == null) {
				return AppUtil.createApiResponse(false, "Everything can,t be null", null);

			}

			List<BeneficiaryInfoView> benificiaries = beneficiaryInfoViewRepo
					.findByEmailOrPassportOrNinOrMobileNumberOrBeneficiaryDigitalId(subscriber.getEmailId(),
							subscriber.getNationalId(), subscriber.getIdDocNumber(), subscriber.getMobileNumber(),
							BeneficiaryDigitalId);

			System.out.println(benificiaries);
			System.out.println("size of list"+benificiaries.size());
			System.out.println("is empty:"+benificiaries.isEmpty());

			if(benificiaries.size()!=0) {
				int i=0;
				while (i<=benificiaries.size()-1) {

					if (benificiaries.get(i).getBeneficiaryUgPassEmail() != null && !benificiaries.get(i).getBeneficiaryUgPassEmail().isEmpty()) {
						if (!subscriber.getEmailId().equals(benificiaries.get(i).getBeneficiaryUgPassEmail())) {
							benificiaries.remove(benificiaries.get(i));
							System.out.println("beneficiaries size::::"+benificiaries.size());
							continue;

						}
					}
					if (benificiaries.get(i).getBeneficiaryMobileNumber() != null && !benificiaries.get(i).getBeneficiaryMobileNumber().isEmpty()) {
						if (!subscriber.getMobileNumber().equals(benificiaries.get(i).getBeneficiaryMobileNumber())) {
							benificiaries.remove(benificiaries.get(i));
							System.out.println("beneficiaries size::::"+benificiaries.size());
							continue;

						}
					}
					if (benificiaries.get(i).getBeneficiaryNin() != null && !benificiaries.get(i).getBeneficiaryNin().isEmpty()) {
						if (!subscriber.getNationalId().equals(benificiaries.get(i).getBeneficiaryNin())) {
							benificiaries.remove(benificiaries.get(i));
							System.out.println("beneficiaries size::::"+benificiaries.size());
							continue;

						}
					}
					if (benificiaries.get(i).getBeneficiaryPassport() != null && !benificiaries.get(i).getBeneficiaryPassport().isEmpty()) {
						if (!subscriber.getIdDocNumber().equals(benificiaries.get(i).getBeneficiaryPassport())) {
							benificiaries.remove(benificiaries.get(i));
							System.out.println("beneficiaries size::::"+benificiaries.size());
							continue;

						}
					}
					if (benificiaries.get(i).getBeneficiaryDigitalId() != null && !benificiaries.get(i).getBeneficiaryDigitalId().isEmpty()) {
						if (!subscriber.getSubscriberUid().equals(benificiaries.get(i).getBeneficiaryDigitalId())) {
							benificiaries.remove(benificiaries.get(i));
							System.out.println("beneficiaries size::::"+benificiaries.size());
							continue;
						}
					}

					i++;


				}

				if(benificiaries.size()==0){
					return AppUtil.createApiResponse(false, "Sponsor not found", null);
				}

				return AppUtil.createApiResponse(true, "Sponsor found", benificiaries);
			} else {

				return AppUtil.createApiResponse(false, "Sponsor not found", null);
			}

		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}

	}

	@Override
	public ApiResponses changeStatusForSSP(int id) {
		try{

			beneficiariesRepo.changeStatusForSSP(id);
			return AppUtil.createApiResponse(true,"Activated Succussfully",null);

		}catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	@Override
	public ApiResponses addMultipleBeneficiaries(List<BenificiariesDto> multipleBenificiariesDto) {
		try {
			logger.info(CLASS+" addMultipleBeneficiaries {} ",multipleBenificiariesDto);
			System.out.println(" addMultipleBeneficiaries  "+multipleBenificiariesDto.size());;
			if(multipleBenificiariesDto.isEmpty()){
				return AppUtil.createApiResponse(false,"Beneficiaries list is empty",null);
			}

			List<BenificiariesRespDto> benificiariesRespDtoList =new ArrayList<BenificiariesRespDto>();

			for (BenificiariesDto benificiariesDto : multipleBenificiariesDto) {
				
				BenificiariesResponseDto benificiariesResponseDto = new BenificiariesResponseDto();
				BenificiariesRespDto benificiariesRespDto = new BenificiariesRespDto();

				if (benificiariesDto.getSponsorDigitalId() == null || benificiariesDto.getSponsorType() == null
						|| benificiariesDto.getBeneficiaryType() == null) {
					return AppUtil.createApiResponse(false,
							"SponsorDigitalId and SponsorType and BeneficiaryType cannot be null", null);
				}

				if (benificiariesDto.getBeneficiaryDigitalId() == null && benificiariesDto.getBeneficiaryNin() == null
						&& benificiariesDto.getBeneficiaryPassport() == null
						&& benificiariesDto.getBeneficiaryUgpassEmail() == null
						&& benificiariesDto.getBeneficiaryMobileNumber() == null) {
					return AppUtil.createApiResponse(false,
							"At least one of NIN, passport, mobile number, or ugpass email must not be null", null);
				}

				if (benificiariesDto.getBeneficiaryValidities().isEmpty()) {
					return AppUtil.createApiResponse(false, "Atleast one service should be selected", benificiariesDto);

				}

				for (BeneficiaryValidity beneficiaryValidity1 : benificiariesDto.getBeneficiaryValidities()) {

					if (beneficiaryValidity1.isValidityApplicable()) {
						if (beneficiaryValidity1.getValidFrom() == null || beneficiaryValidity1.getValidFrom() == ""
								|| beneficiaryValidity1.getValidUpTo() == null
								|| beneficiaryValidity1.getValidUpTo() == "") {
							return AppUtil.createApiResponse(false, "Valid from and valid upto is required", null);
						}
					}
				}

				if (benificiariesDto.getBeneficiaryNin() != null) {
					if (beneficiariesRepo.findDuplicateBeneficiariesByNIN(benificiariesDto.getSponsorDigitalId(),
							benificiariesDto.getBeneficiaryNin()) != null) {
						return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
					}
				}

				if (benificiariesDto.getBeneficiaryPassport() != null) {
					if (beneficiariesRepo.findDuplicateBeneficiariesByPassport(benificiariesDto.getSponsorDigitalId(),
							benificiariesDto.getBeneficiaryPassport()) != null) {
						return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
					}
				}
				if (benificiariesDto.getBeneficiaryMobileNumber() != null) {
					if (beneficiariesRepo.findDuplicateBeneficiariesByMobileNumber(
							benificiariesDto.getSponsorDigitalId(),
							benificiariesDto.getBeneficiaryMobileNumber()) != null) {
						return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
					}

				}
				if (benificiariesDto.getBeneficiaryOfficeEmail() != null) {
					if (beneficiariesRepo.findDuplicateBeneficiariesByOfficeEmail(
							benificiariesDto.getSponsorDigitalId(),
							benificiariesDto.getBeneficiaryOfficeEmail()) != null) {
						return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
					}
				}
				if (benificiariesDto.getBeneficiaryUgpassEmail() != null) {
					if (beneficiariesRepo.findDuplicateBeneficiariesByUgPassEmail(
							benificiariesDto.getSponsorDigitalId(),
							benificiariesDto.getBeneficiaryUgpassEmail()) != null) {
						return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
					}
				}
				if (benificiariesDto.getBeneficiaryDigitalId() != null) {
					if (beneficiariesRepo.findDuplicateBeneficiariesByBeneficiaryDigitalId(
							benificiariesDto.getSponsorDigitalId(),
							benificiariesDto.getBeneficiaryDigitalId()) != null) {
						return AppUtil.createApiResponse(false, "Beneficiary already exists", null);
					}
				}

				Benificiaries benificiaries = new Benificiaries();

				benificiaries.setBeneficiaryPassport(benificiariesDto.getBeneficiaryPassport());

				benificiaries.setBeneficiaryType(benificiariesDto.getBeneficiaryType());

				benificiaries.setBeneficiaryName(benificiariesDto.getBeneficiaryName());
				benificiaries.setSponsorName(benificiariesDto.getSponsorName());
				benificiaries.setUpdatedOn(AppUtil.getDate());
				benificiaries.setCreatedOn(AppUtil.getDate());

				benificiaries.setSponsorType(benificiariesDto.getSponsorType());

				benificiaries.setStatus("ACTIVE");
				benificiaries.setSponsorDigitalId(benificiariesDto.getSponsorDigitalId());
				benificiaries.setSponsorExternalId(benificiariesDto.getSponsorExternalId());
				benificiaries.setBeneficiaryDigitalId(benificiariesDto.getBeneficiaryDigitalId());
				benificiaries.setSignaturePhoto(benificiariesDto.getSignaturePhoto());
				benificiaries.setDesignation(benificiariesDto.getDesignation());

				if (benificiariesDto.getSponsorType().equals(SponsorType.ORGANIZATION)) {
					benificiaries.setSponsorPaymentPriorityLevel(2);
				} else {
					benificiaries.setSponsorPaymentPriorityLevel(1);
				}
				benificiaries.setBeneficiaryNin(benificiariesDto.getBeneficiaryNin());
				benificiaries.setBeneficiaryMobileNumber(benificiariesDto.getBeneficiaryMobileNumber());
				benificiaries.setBeneficiaryOfficeEmail(benificiariesDto.getBeneficiaryOfficeEmail());
				benificiaries.setBeneficiaryUgPassEmail(benificiariesDto.getBeneficiaryUgpassEmail());
				benificiaries.setBeneficiaryConsentAcquired(false);
				Benificiaries benificiariesDb = beneficiariesRepo.save(benificiaries);
//           benificiariesList.add(benificiaries);
//
				benificiariesResponseDto.setBenificiaries(benificiariesDb);
				List<BeneficiaryValidity> beneficiaryValidities = new ArrayList<BeneficiaryValidity>();
				
				for (BeneficiaryValidity beneficiaryValidity : benificiariesDto.getBeneficiaryValidities()) {
					BeneficiaryValidity beneficiaryValidityDB = new BeneficiaryValidity();
					beneficiaryValidityDB.setBeneficiaryId(benificiariesDb.getId());
					beneficiaryValidityDB.setCreatedOn(AppUtil.getDate());
					beneficiaryValidityDB.setUpdatedOn(AppUtil.getDate());
					beneficiaryValidityDB.setStatus("ACTIVE");
					beneficiaryValidityDB.setPrivilegeServiceId(beneficiaryValidity.getPrivilegeServiceId());
					beneficiaryValidityDB.setValidityApplicable(beneficiaryValidity.isValidityApplicable());
					beneficiaryValidityDB.setValidUpTo(beneficiaryValidity.getValidUpTo());
					beneficiaryValidityDB.setValidFrom(beneficiaryValidity.getValidFrom());
					beneficiaryValidityRepository.save(beneficiaryValidityDB);
					beneficiaryValidities.add(beneficiaryValidityDB);

				}
				benificiariesResponseDto.setBeneficiaryValidity(beneficiaryValidities);
				
				benificiariesRespDto.setBenificiariesResponseDtos(benificiariesResponseDto);
				
				benificiariesRespDtoList.add(benificiariesRespDto);
				ExecutorService executor = Executors.newFixedThreadPool(1000);
				Runnable worker = new LinkedBeneficiraryWorkerThread(benificiariesDb, subscriberRepository,
						beneficiariesRepo, subscriberFcmTokenRepoIface, sendNotificationURL, sponsorLinkedMessage);
				executor.execute(worker);
				executor.shutdown();

			}
			return AppUtil.createApiResponse(true, "Beneficiary Created Successfully", benificiariesRespDtoList);



		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				 | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);

		}

	}

	@Override
	public ApiResponses getVendorsByVendorId(String vendorId) {
		try{

			List<BeneficiaryInfoView> res = beneficiaryInfoViewRepo.getVendorsByVendorId(vendorId);
			if(res==null){
				return AppUtil.createApiResponse(false, "Vendors Not Found", null);
			}

			return AppUtil.createApiResponse(true, "Vendors Fetched Successfully", res);

		}catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);

		}
	}


	@Override
	public ApiResponses linkAllSponsor(BenificiariesDto benificiariesDto) {
		try {
			if (benificiariesDto.getBeneficiaryDigitalId() == null
					&& benificiariesDto.getBeneficiaryDigitalId().isEmpty()) {
				return AppUtil.createApiResponse(false, "Suid can't be null or empty", null);
			} else {
				Subscriber subscriber = subscriberRepository
						.findBysubscriberUid(benificiariesDto.getBeneficiaryDigitalId());
				if (subscriber != null) {
					List<Benificiaries> beneficiariesList = beneficiariesRepo.findAllSponsor(
							subscriber.getEmailId(), subscriber.getIdDocNumber(),
							subscriber.getNationalId(), subscriber.getMobileNumber(),
							subscriber.getSubscriberUid());
					List<Benificiaries> beneficiaries2 = new ArrayList<Benificiaries>();
					for(Benificiaries beneficiaries : beneficiariesList) {
						beneficiaries.setBeneficiaryDigitalId(subscriber.getSubscriberUid());
						beneficiaries.setBeneficiaryName(subscriber.getFullName());
						beneficiaries.setBeneficiaryPassport(subscriber.getIdDocNumber());
						beneficiaries.setBeneficiaryNin(subscriber.getNationalId());
						beneficiaries.setBeneficiaryUgPassEmail(subscriber.getEmailId());
						beneficiaries.setBeneficiaryMobileNumber(subscriber.getMobileNumber());
						beneficiaries.setBeneficiaryConsentAcquired(true);
						beneficiaries2.add(beneficiaries);
					}
					beneficiariesRepo.saveAll(beneficiaries2);
					return AppUtil.createApiResponse(true, "Beneficiary consent acquired successful", beneficiaries2);
					
				} else {
					return AppUtil.createApiResponse(false, "Subscriber not found", null);
				}

			}
		} catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
			ex.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong please try after sometime", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. Please contact admin", null);
		}
	}

	
	@Override
	public ApiResponses verifyByEgpForVendor(String vendorId, String orgid) {
	    try{	

	    	if(egpOrgId.equals(orgid)) {
	    		Thread.sleep(2000);
		        return AppUtil.createApiResponse(true,"Vendor is verified successfully",null);
	    	}else {
	    		Thread.sleep(2000);
		        return AppUtil.createApiResponse(false,"Vendor is not verified",null);
	    	}
	    	
	    }catch (IllegalArgumentException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false,"Invalid input format.", (Object) null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false,"State violation: Operation is not valid in the current context.", (Object) null);
		} catch (NullPointerException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false,e.getMessage(), (Object) null);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.invalid.parameter", null, Locale.ENGLISH), null);
			} else if (e.getStatusCode() == HttpStatus.REQUEST_TIMEOUT) {
				return AppUtil.createApiResponse(false, messageSource.getMessage(
						"api.error.request.not.completed.because.of.request.time.out", null, Locale.ENGLISH), null);
			}
			else {
				return AppUtil.createApiResponse(false,
						messageSource.getMessage("api.error.request.not.completed.because.error.code", null,
								Locale.ENGLISH) + e.getStatusCode(),
						null);
			}
		} catch (HttpServerErrorException e) {
			e.printStackTrace();
			if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
				return AppUtil.createApiResponse(false,
						messageSource.getMessage("api.error.request.not.completed.because.of.internal.server.error",
								null, Locale.ENGLISH),
						null);
			} else if (e.getStatusCode() == HttpStatus.BAD_GATEWAY) {
				return AppUtil.createApiResponse(false, messageSource.getMessage(
						"api.error.request.not.completed.because.of.bad.gateway", null, Locale.ENGLISH), null);
			} else if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
				return AppUtil.createApiResponse(false, messageSource.getMessage(
						"api.error.request.not.completed.because.of.service.unavailable", null, Locale.ENGLISH), null);
			} else {
				return AppUtil.createApiResponse(false,
						messageSource.getMessage("api.error.request.not.completed.because.error.code", null,
								Locale.ENGLISH) + e.getStatusCode(),
						null);
			}
			
			
		}catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
				| PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.there.was.an.issue.connecting.with.our.service", null,
					Locale.ENGLISH), null);
		}
		catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, messageSource.getMessage("api.error.something.went.wrong.please.contact.admin", null,
					Locale.ENGLISH), null);
		}

	}
}
