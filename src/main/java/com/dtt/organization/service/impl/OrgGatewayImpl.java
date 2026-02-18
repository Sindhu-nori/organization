package com.dtt.organization.service.impl;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.constant.Constant;
import com.dtt.organization.dto.*;
import com.dtt.organization.enums.CertificateStatus;
import com.dtt.organization.model.*;
import com.dtt.organization.repository.*;
import com.dtt.organization.service.iface.OrgGatewayIface;
import com.dtt.organization.util.AppUtil;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrgGatewayImpl implements OrgGatewayIface {
	@Autowired
	OrgContactsEmailRepository orgContactsEmailRepository;

	@Autowired
	OrganizationDetailsRepository organizationDetailsRepository;

	@Autowired
	OrganizationSignatureTemplatesRepository signatureTemplatesRepository;

	@Autowired
	OrganizationCertificatesRepository organizationCertificatesRepository;

	@Autowired
	OrgEmailDomainRepository orgEmailDomainRepository;

	@Autowired
	SubscriberViewRepository subscriberViewRepository;

	@Autowired
	OrganizationServiceImpl organizationService;

	@Autowired
	RestTemplate restTemplate;

	@Value("${orgLink.notifyurl}")
	private String orgLinkUrl;

	@Override
	public ApiResponses getBusinessUsers(String orgId) {
		System.out.println("Org id :: " + orgId);
		List<OrgContactsEmail> orgContactsEmailList = orgContactsEmailRepository.findByOrganizationUid(orgId);
		if (orgContactsEmailList == null || orgContactsEmailList.isEmpty()) {
			return AppUtil.createApiResponse(false, "No Business Users Found", null);
		}
		List<OrgUser> ResponseList = new ArrayList<>();
		for (OrgContactsEmail orgUsers : orgContactsEmailList) {
//            List<OrgContactsEmail> orgUsers = orgContactsEmailRepository.findByOrganizationUidAndEmail(orgUsers1.getOrganizationUid(), orgUsers1.getEmployeeEmail());

			OrgUser orgContactsEmailnew = new OrgUser();
			orgContactsEmailnew.setStatus(CertificateStatus.ACTIVE.toString());
			orgContactsEmailnew.setUgpassEmail(orgUsers.getUgpassEmail());
			orgContactsEmailnew.setSignaturePhoto(orgUsers.getSignaturePhoto());
			orgContactsEmailnew.setTemplate(orgUsers.isTemplate());
			orgContactsEmailnew.setDelegate(orgUsers.isDelegate());
			orgContactsEmailnew.setOrgContactsEmailId(orgUsers.getOrgContactsEmailId());
			orgContactsEmailnew.setSignatory(orgUsers.isSignatory());
			orgContactsEmailnew.setSubscriberUid(orgUsers.getSubscriberUid());
			orgContactsEmailnew.setUgpassUserLinkApproved(orgUsers.isUgpassUserLinkApproved());
			orgContactsEmailnew.setOrganizationUid(orgUsers.getOrganizationUid());
			orgContactsEmailnew.setNationalIdNumber(orgUsers.getNationalIdNumber());
			orgContactsEmailnew.setEmployeeEmail(orgUsers.getEmployeeEmail());
			orgContactsEmailnew.setBulksign(orgUsers.isBulksign());
			orgContactsEmailnew.setDesignation(orgUsers.getDesignation());
			orgContactsEmailnew.seteSealPrepatory(orgUsers.iseSealPreparatory());
			orgContactsEmailnew.setMobileNumber(orgUsers.getMobileNumber());
			orgContactsEmailnew.setPassportNumber(orgUsers.getPassportNumber());
			orgContactsEmailnew.seteSealSignatory(orgUsers.iseSealSignatory());
			orgContactsEmailnew.setDigitalFormPrivilege(orgUsers.isDigitalFormPrivilege());
//            orgContactsEmailnew.setLsaPrivilege(orgUsers.isLsaPrivilege());
			ResponseList.add(orgContactsEmailnew);
//            orgContactsEmailRepository.save(orgContactsEmailnew);
		}

		return AppUtil.createApiResponse(true, "Users List", ResponseList);
	}

	@Override
	public ApiResponses getBusinessUserById(Integer id) {
		try {
			System.out.println("Primary Key ID :: " + id);
			Optional<OrgContactsEmail> orgContactsEmail = orgContactsEmailRepository.findById(id);
			if (orgContactsEmail == null) {
				return AppUtil.createApiResponse(false, "User Not Found", null);
			} else {

				OrgUser orgContactsEmailnew = new OrgUser();
				orgContactsEmailnew.setStatus(CertificateStatus.ACTIVE.toString());
				orgContactsEmailnew.setUgpassEmail(orgContactsEmail.get().getUgpassEmail());
				orgContactsEmailnew.setSignaturePhoto(orgContactsEmail.get().getSignaturePhoto());
				orgContactsEmailnew.setTemplate(orgContactsEmail.get().isTemplate());
				orgContactsEmailnew.setDelegate(orgContactsEmail.get().isDelegate());
				orgContactsEmailnew.setOrgContactsEmailId(orgContactsEmail.get().getOrgContactsEmailId());
				orgContactsEmailnew.setSignatory(orgContactsEmail.get().isSignatory());
				orgContactsEmailnew.setSubscriberUid(orgContactsEmail.get().getSubscriberUid());
				orgContactsEmailnew.setUgpassUserLinkApproved(orgContactsEmail.get().isUgpassUserLinkApproved());
				orgContactsEmailnew.setOrganizationUid(orgContactsEmail.get().getOrganizationUid());
				orgContactsEmailnew.setNationalIdNumber(orgContactsEmail.get().getNationalIdNumber());
				orgContactsEmailnew.setEmployeeEmail(orgContactsEmail.get().getEmployeeEmail());
				orgContactsEmailnew.setBulksign(orgContactsEmail.get().isBulksign());
				orgContactsEmailnew.setDesignation(orgContactsEmail.get().getDesignation());
				orgContactsEmailnew.seteSealPrepatory(orgContactsEmail.get().iseSealPreparatory());
				orgContactsEmailnew.setMobileNumber(orgContactsEmail.get().getMobileNumber());
				orgContactsEmailnew.setPassportNumber(orgContactsEmail.get().getPassportNumber());
				orgContactsEmailnew.seteSealSignatory(orgContactsEmail.get().iseSealSignatory());
				orgContactsEmailnew.setDigitalFormPrivilege(orgContactsEmail.get().isDigitalFormPrivilege());
//                orgContactsEmailnew.setLsaPrivilege(orgContactsEmail.get().isLsaPrivilege());

				return AppUtil.createApiResponse(true, "Found", orgContactsEmailnew);

			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "User Not Found", null);
		}

	}

	@Override
	public ApiResponses updateBusinessUser(OrgUser orgUser) {
		try {
			Optional<OrgContactsEmail> orgContactsEmail = Optional.ofNullable(orgContactsEmailRepository
					.getOrganisationByUidAndEmail(orgUser.getOrganizationUid(), orgUser.getEmployeeEmail()));

			if (orgContactsEmail.isPresent()) {
				OrgContactsEmail orgContactsEmail1 = orgContactsEmail.get();
				orgContactsEmail1.setDelegate(orgUser.isDelegate());
				orgContactsEmail1.setEmployeeEmail(orgUser.getEmployeeEmail());
				orgContactsEmail1.setBulksign(orgUser.isBulksign());
				orgContactsEmail1.setOrgContactsEmailId(orgContactsEmail1.getOrgContactsEmailId());
				orgContactsEmail1.setDesignation(orgUser.getDesignation());
				orgContactsEmail1.seteSealPreparatory(orgUser.iseSealPrepatory());
				orgContactsEmail1.seteSealSignatory(orgUser.iseSealSignatory());
				orgContactsEmail1.setMobileNumber(orgUser.getMobileNumber());
				orgContactsEmail1.setPassportNumber(orgUser.getPassportNumber());
				orgContactsEmail1.setNationalIdNumber(orgUser.getNationalIdNumber());
				orgContactsEmail1.setOrganizationUid(orgUser.getOrganizationUid());
				orgContactsEmail1.setSignatory(orgUser.isSignatory());
				orgContactsEmail1.setTemplate(orgUser.isTemplate());
				orgContactsEmail1.setSignaturePhoto(orgUser.getSignaturePhoto());
				orgContactsEmail1.setUgpassEmail(orgUser.getUgpassEmail());
				orgContactsEmail1.setInitial(orgUser.getInitial());
				orgContactsEmail1.setDigitalFormPrivilege(orgUser.isDigitalFormPrivilege());

				OrgContactsEmail contactsEmail = orgContactsEmailRepository.save(orgContactsEmail1);

				return AppUtil.createApiResponse(true, "Successfully Updated Business User", contactsEmail);
			} else {
				return AppUtil.createApiResponse(false, "User Not Found", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}
	}

	@Override
	public ApiResponses getEsealLogo(String orgUid) {
		try {
			if (orgUid == null) {
				return AppUtil.createApiResponse(false, "The Organisation Id cannot be null", null);
			}
			OrganizationDetails orgDetails = organizationDetailsRepository.findByOrganizationUid(orgUid);
			if (orgDetails == null) {
				return AppUtil.createApiResponse(false, "There is no organisation exist with given Organisation Id",
						null);
			}
			return AppUtil.createApiResponse(true, "Successfully fetched Eseal Logo", orgDetails.geteSealImage());

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}
	}

	@Override
	public ApiResponses updateEsealLogo(UpdateEsealDto updateEsealDto) {
		try {
			if (updateEsealDto.getOrgUid() == null) {
				return AppUtil.createApiResponse(false, "Organisation Id cannot be null", null);
			}
			if (updateEsealDto.geteSealImage() == null) {
				return AppUtil.createApiResponse(false, "Eseal Logo that need to be updated cannot be null", null);
			}
			OrganizationDetails organizationDetails = organizationDetailsRepository
					.findByOrganizationUid(updateEsealDto.getOrgUid());
			if (organizationDetails == null) {
				return AppUtil.createApiResponse(false, "There is no organisation exist with given Organisation Id",
						null);
			}
			organizationDetails.seteSealImage(updateEsealDto.geteSealImage());
			organizationDetails.setAuthorizedLetterForSignatories(updateEsealDto.getAuthorizedLetterForSignatories());
			organizationDetailsRepository.save(organizationDetails);
			return AppUtil.createApiResponse(true, "Successfully updated Eseal Logo", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}
	}

	@Override
	public ApiResponses updateSignatureTemplatesById(SignatureTemplateUpdateDto signatureTemplateUpdateDto) {
		try {
			if (signatureTemplateUpdateDto.getOrganizationUid() == null) {
				return AppUtil.createApiResponse(false, "Organisation Id cannot be null", null);
			}
			if (signatureTemplateUpdateDto.getEsealSignatureTemplateId() == 0
					|| signatureTemplateUpdateDto.getSignatureTemplateId() == 0) {
				return AppUtil.createApiResponse(false, "Any Of the Signature Templates cannot be null", null);
			}
			if (signatureTemplatesRepository
					.getUserSignatureTemplatesDetails(signatureTemplateUpdateDto.getOrganizationUid()) == null) {
				return AppUtil.createApiResponse(false, "There is no record in the database for given organisation ID",
						null);
			}

			OrganizationSignatureTemplates signatureTemplates = signatureTemplatesRepository
					.getOrgSignatureDetailsByType(signatureTemplateUpdateDto.getOrganizationUid(), "SIGN");
			signatureTemplates.setTemplateId(signatureTemplateUpdateDto.getSignatureTemplateId());
			signatureTemplatesRepository.save(signatureTemplates);
			OrganizationSignatureTemplates templates = signatureTemplatesRepository
					.getOrgSignatureDetailsByType(signatureTemplateUpdateDto.getOrganizationUid(), "ESEAL");
			templates.setTemplateId(signatureTemplateUpdateDto.getEsealSignatureTemplateId());
			signatureTemplatesRepository.save(templates);
			return AppUtil.createApiResponse(true, "Templates updated Successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}
	}

	@Override
	public ApiResponses getSignatureTemplateById(String orgUid) {
		try {
			OrganizationSignatureTemplates signatureTemplates = signatureTemplatesRepository
					.getOrgSignatureDetailsByType(orgUid, "SIGN");
			OrganizationSignatureTemplates templates = signatureTemplatesRepository.getOrgSignatureDetailsByType(orgUid,
					"ESEAL");
			SignatureTemplateUpdateDto signatureTemplateUpdateDto = new SignatureTemplateUpdateDto();
			signatureTemplateUpdateDto.setEsealSignatureTemplateId(templates.getTemplateId());
			signatureTemplateUpdateDto.setSignatureTemplateId(signatureTemplates.getTemplateId());
			signatureTemplateUpdateDto.setOrganizationUid(orgUid);
			return AppUtil.createApiResponse(true, "fetched templates successfully", signatureTemplateUpdateDto);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}

	}

	@Override
	public ApiResponses addMultipleBusinessUsers(List<OrgUser> orgUserList) {
		try {
			List<OrgContactsEmail> orgSubscriberEmail = new ArrayList<OrgContactsEmail>();
			for (OrgUser orgUsers : orgUserList) {
				List<OrgContactsEmail> orgContactsEmail = orgContactsEmailRepository
						.findByOrganizationUidAndEmail(orgUsers.getOrganizationUid(), orgUsers.getEmployeeEmail());
				if (!orgContactsEmail.isEmpty()) {
					return AppUtil.createApiResponse(false, "Duplicate Employee email Found!", null);
				}
				if (!orgUsers.getPassportNumber().isEmpty()) {
					List<OrgContactsEmail> orgContactsEmailListByPassport = orgContactsEmailRepository
							.getSubscriberDetailsByOuidAndPassport(orgUsers.getPassportNumber(),
									orgUsers.getOrganizationUid());
					if (!orgContactsEmailListByPassport.isEmpty()) {
						return AppUtil.createApiResponse(false, "Duplicate Business User found", null);
					}
				}
				if (!orgUsers.getNationalIdNumber().isEmpty()) {
					List<OrgContactsEmail> orgContactsEmailListByNiN = orgContactsEmailRepository
							.getSubscriberDetailsByOuidAndNin(orgUsers.getNationalIdNumber(),
									orgUsers.getOrganizationUid());
					if (!orgContactsEmailListByNiN.isEmpty()) {
						return AppUtil.createApiResponse(false, "Duplicate Business User found", null);
					}
				}
				if (!orgUsers.getUgpassEmail().isEmpty()) {
					List<OrgContactsEmail> orgContactsEmailListByMail = orgContactsEmailRepository
							.getSubscriberDetailsByOuidAndEmail(orgUsers.getUgpassEmail(),
									orgUsers.getOrganizationUid());
					if (!orgContactsEmailListByMail.isEmpty()) {
						return AppUtil.createApiResponse(false, "Duplicate Business User found", null);
					}
				}
				if (!orgUsers.getMobileNumber().isEmpty()) {
					List<OrgContactsEmail> orgContactsEmailListByNumber = orgContactsEmailRepository
							.getSubscriberDetailsByOuidAndNUMBER(orgUsers.getMobileNumber(),
									orgUsers.getOrganizationUid());
					if (!orgContactsEmailListByNumber.isEmpty()) {
						return AppUtil.createApiResponse(false, "Duplicate Business User found", null);
					}
				}

				OrgContactsEmail orgContactsEmailnew = new OrgContactsEmail();
				orgContactsEmailnew.setStatus(CertificateStatus.ACTIVE.toString());
				orgContactsEmailnew.setUgpassEmail(orgUsers.getUgpassEmail());
				orgContactsEmailnew.setSignaturePhoto(orgUsers.getSignaturePhoto());
				orgContactsEmailnew.setTemplate(orgUsers.isTemplate());
				orgContactsEmailnew.setDelegate(orgUsers.isDelegate());
				orgContactsEmailnew.setOrgContactsEmailId(orgUsers.getOrgContactsEmailId());
				orgContactsEmailnew.setSignatory(orgUsers.isSignatory());
				orgContactsEmailnew.setSubscriberUid(orgUsers.getSubscriberUid());
				orgContactsEmailnew.setUgpassUserLinkApproved(orgUsers.isUgpassUserLinkApproved());
				orgContactsEmailnew.setOrganizationUid(orgUsers.getOrganizationUid());
				orgContactsEmailnew.setNationalIdNumber(orgUsers.getNationalIdNumber());
				orgContactsEmailnew.setEmployeeEmail(orgUsers.getEmployeeEmail());
				orgContactsEmailnew.setBulksign(orgUsers.isBulksign());
				orgContactsEmailnew.setDesignation(orgUsers.getDesignation());
				orgContactsEmailnew.seteSealPreparatory(orgUsers.iseSealPrepatory());
				orgContactsEmailnew.setMobileNumber(orgUsers.getMobileNumber());
				orgContactsEmailnew.setPassportNumber(orgUsers.getPassportNumber());
				orgContactsEmailnew.seteSealSignatory(orgUsers.iseSealSignatory());
				orgContactsEmailnew.setInitial(orgUsers.getInitial());
				orgContactsEmailnew.setDigitalFormPrivilege(orgUsers.isDigitalFormPrivilege());
				handleUserNotifications(orgContactsEmailnew);
				orgContactsEmailRepository.save(orgContactsEmailnew);
				orgSubscriberEmail.add(orgContactsEmailnew);
			}
			return AppUtil.createApiResponse(true, "Business Users Added Successfully", orgSubscriberEmail);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}
	}

	@Override
	public ApiResponses updateOrganisationEGSpoc(EnterpriseGatewayOrganisationUpdateDto gatewayOrganisationUpdateDto) {
		try {

//			System.out.println("agent url:::::::"+gatewayOrganisationUpdateDto.getAgentUrl());
			if (gatewayOrganisationUpdateDto.getOrganizationUid() == null
					|| gatewayOrganisationUpdateDto.getOrganizationUid().isEmpty()) {
				return AppUtil.createApiResponse(false, "Organisation Uid Cannot be null", null);
			}
			OrganizationDetails organizationDetails = organizationDetailsRepository
					.findByOrganizationUid(gatewayOrganisationUpdateDto.getOrganizationUid());
			if (organizationDetails == null) {
				return AppUtil.createApiResponse(false, "There is no Organisation Existed with given Organisation Uid",
						null);
			}
//			if (gatewayOrganisationUpdateDto.getAgentUrl() == null
//					|| gatewayOrganisationUpdateDto.getAgentUrl().isEmpty()) {
//				organizationDetails.setAgentUrl(organizationDetails.getAgentUrl());
//			} else {
//				organizationDetails.setAgentUrl(gatewayOrganisationUpdateDto.getAgentUrl());
//			}
			if (gatewayOrganisationUpdateDto.getSpocUgpassEmail() == null
					|| gatewayOrganisationUpdateDto.getSpocUgpassEmail().isEmpty()) {
				organizationDetails.setSpocUgpassEmail(organizationDetails.getOrganizationEmail());
			} else {
				organizationDetails.setSpocUgpassEmail(gatewayOrganisationUpdateDto.getSpocUgpassEmail());
			}
			organizationDetailsRepository.save(organizationDetails);
			return AppUtil.createApiResponse(true, "Organisation Details Updated Successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}

	}

	@Override
	public ApiResponses updateOrganisationEGAgent(EnterpriseGatewayOrganisationUpdateDto gatewayOrganisationUpdateDto) {
		try {

//			System.out.println("agent url:::::::"+gatewayOrganisationUpdateDto.getAgentUrl());
			if (gatewayOrganisationUpdateDto.getOrganizationUid() == null
					|| gatewayOrganisationUpdateDto.getOrganizationUid().isEmpty()) {
				return AppUtil.createApiResponse(false, "Organisation Uid Cannot be null", null);
			}
			OrganizationDetails organizationDetails = organizationDetailsRepository
					.findByOrganizationUid(gatewayOrganisationUpdateDto.getOrganizationUid());
			if (organizationDetails == null) {
				return AppUtil.createApiResponse(false, "There is no Organisation Existed with given Organisation Uid",
						null);
			}
			if (gatewayOrganisationUpdateDto.getAgentUrl() == null
					|| gatewayOrganisationUpdateDto.getAgentUrl().isEmpty()) {
				organizationDetails.setAgentUrl(organizationDetails.getAgentUrl());
			} else {
				organizationDetails.setAgentUrl(gatewayOrganisationUpdateDto.getAgentUrl());
			}
//			if (gatewayOrganisationUpdateDto.getSpocUgpassEmail() == null
//					|| gatewayOrganisationUpdateDto.getSpocUgpassEmail().isEmpty()) {
//				organizationDetails.setSpocUgpassEmail(organizationDetails.getOrganizationEmail());
//			} else {
//				organizationDetails.setSpocUgpassEmail(gatewayOrganisationUpdateDto.getSpocUgpassEmail());
//			}
			organizationDetailsRepository.save(organizationDetails);
			return AppUtil.createApiResponse(true, "Organisation Details Updated Successfully", null);
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, e.getMessage(), null);
		}

	}

	@Override
	public ApiResponses getOrganizationCertificateDetailsByOrgUid(String orgUid) {
		try {
			OrganizationCertificates organizationCertificates = organizationCertificatesRepository
					.findByorganizationUid(orgUid);
			if (organizationCertificates == null) {
				return AppUtil.createApiResponse(true, "Organization certificate details not found", null);
			} else {
				return AppUtil.createApiResponse(true, "Organization certificate details found Successfully",
						organizationCertificates);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong. please try after sometime", null);
		}
	}

	@Override
	public ApiResponses deleteBusinessUser(String orgId, String email) {
		try {
			if (orgId.isEmpty() || orgId == null) {
				return AppUtil.createApiResponse(false, "Organisation Id cannot be null or empty", null);
			}
			if (email.isEmpty() || email == null) {
				return AppUtil.createApiResponse(false, "email cannot be null or empty", null);
			}

			Optional<OrgContactsEmail> orgContactsEmail = Optional
					.ofNullable(orgContactsEmailRepository.getOrganisationByUidAndEmail(orgId, email));
			if (orgContactsEmail.isPresent()) {
				orgContactsEmailRepository.delete(orgContactsEmail.get());
				return AppUtil.createApiResponse(true, "Business User Deleted Successfully", null);
			} else {
				return AppUtil.createApiResponse(false, "Business User Not Found", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "something went wrong", null);
		}
	}

	@Override
	public ApiResponses updateEmailDomain(UpdateEmailDomainDto updateEmailDomainDto) {
		try {
			if (updateEmailDomainDto.getOrganizationUid().isEmpty()
					) {
				return AppUtil.createApiResponse(false, "Organisation Uid  cannot be null", null);
			}

			OrganizationDetails organizationDetails = organizationDetailsRepository
					.findByOrganizationUid(updateEmailDomainDto.getOrganizationUid());
			if (organizationDetails == null) {
				return AppUtil.createApiResponse(false, "No data found with given organisation id", null);
			}
			OrganizationEmailDomain organizationEmailDomain = orgEmailDomainRepository
					.findByOrganizationUid(updateEmailDomainDto.getOrganizationUid());


			organizationEmailDomain.setEmailDomain(updateEmailDomainDto.getEmailDomain());
			organizationEmailDomain.setUpdatedOn(AppUtil.getDate());
			orgEmailDomainRepository.save(organizationEmailDomain);
			return AppUtil.createApiResponse(true, "Email Domain Updated Successfully",
					updateEmailDomainDto.getEmailDomain());
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong", null);
		}
	}

	@Override
	public ApiResponses getEmailDomain(String ouid) {
		try {
			if (ouid.isEmpty()) {
				return AppUtil.createApiResponse(false, "Organisation Uid cannot be null", null);
			}
			OrganizationDetails organizationDetails = organizationDetailsRepository.findByOrganizationUid(ouid);
			if (organizationDetails == null) {
				return AppUtil.createApiResponse(false, "No data found with given organisation id", null);
			}
			OrganizationEmailDomain organizationEmailDomain = orgEmailDomainRepository.findByOrganizationUid(ouid);
			return AppUtil.createApiResponse(true, "Email domain fetched successfully",
					organizationEmailDomain.getEmailDomain());
		} catch (Exception e) {
			e.printStackTrace();
			return AppUtil.createApiResponse(false, "Something went wrong", null);
		}
	}

	private void handleUserNotifications(OrgContactsEmail orgUser) {
		try {

			SubscriberView subscriberView = null;

			if (orgUser.getUgpassEmail() != null && !orgUser.getUgpassEmail().isEmpty()) {

				subscriberView = subscriberViewRepository.findByUgpassMail(orgUser.getUgpassEmail());
			} else if (orgUser.getMobileNumber() != null && !orgUser.getMobileNumber().isEmpty()) {
				subscriberView = subscriberViewRepository.findByMobile(orgUser.getMobileNumber());
			} else if (orgUser.getPassportNumber() != null && !orgUser.getPassportNumber().isEmpty()) {
				subscriberView = subscriberViewRepository.findByIdDocNumber(orgUser.getPassportNumber());
			} else if (orgUser.getNationalIdNumber() != null && !orgUser.getNationalIdNumber().isEmpty()) {
				subscriberView = subscriberViewRepository.findByIdDocNumber(orgUser.getNationalIdNumber());
			}

			if (subscriberView != null) {
				System.out.println("SUBSCRIBER VIEW"+subscriberView);
				sendNotification(subscriberView.getDisplayName(), subscriberView.getFcmToken(), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void sendNotification(String fullName, String fcm, boolean link) {



		// Create notification headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Set notification body
		NotificationDTO notificationBody = new NotificationDTO();
		notificationBody.setTo(fcm);
		notificationBody.setPriority(Constant.HIGH);

		// Set notification data
		NotificationDataDTO dataDTO = new NotificationDataDTO();
		dataDTO.setTitle(Constant.HI + fullName);

		// Set organization link status message
		Map<String, String> orgLinkStatus = new HashMap<>();
		if (link) {
			dataDTO.setBody(Constant.ORGANIZATION_LINK_MESSAGE);
			orgLinkStatus.put(Constant.ORGLINKSTATUS, Constant.PENDING);
		} else {
			dataDTO.setBody(Constant.LINKED_SUCCESSFULLY);
			orgLinkStatus.put(Constant.ORGLINKSTATUS, Constant.SUCCESS);
		}

		// Set notification context and attach to data
		NotificationContextDTO contextDTO = new NotificationContextDTO();
		contextDTO.setpREF_ORG_LINK(orgLinkStatus);
		dataDTO.setNotificationContext(contextDTO);
		notificationBody.setData(dataDTO);

		// Create the request entity
		HttpEntity<Object> requestEntity = new HttpEntity<>(notificationBody, headers);


		// Send the request
		try {
			ResponseEntity<Object> res = restTemplate.exchange(orgLinkUrl, HttpMethod.POST, requestEntity,
					Object.class);
			if (res.getStatusCodeValue() == 200) {
				System.out.println("NOTIFICATION SENT");
			} else {
				System.out.println("NOTIFICATION SENT FAILED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}