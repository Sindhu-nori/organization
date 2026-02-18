package com.dtt.organization.service.impl;


import com.dtt.organization.constant.ApiResponses;
//import com.dtt.organization.dto.OrganisationPrivilegesDto;
import com.dtt.organization.dto.OrganisationPrivilegesRequestDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegeDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegesListDto;

import com.dtt.organization.model.OrganizationDetails;
import com.dtt.organization.model.OrganizationPrivileges;
import com.dtt.organization.model.Subscriber;
import com.dtt.organization.model.WalletSignCertificate;
import com.dtt.organization.repository.OrganisationPrivilegesRepository;
import com.dtt.organization.repository.OrganizationDetailsRepository;
import com.dtt.organization.repository.SubscriberRepository;
import com.dtt.organization.repository.WalletSignCertRepo;
import com.dtt.organization.response.entity.OrganisationPrivilegesResponse;
import com.dtt.organization.service.iface.OrganizationPrivilegesIface;
import com.dtt.organization.util.AppUtil;
import com.dtt.organization.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationPrivilegesImpl implements OrganizationPrivilegesIface {
    private static final String CLASS = OrganizationServiceImpl.class.getSimpleName();
    Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private  final WalletSignCertRepo walletSignCertRepo;


    private final OrganisationPrivilegesRepository organisationPrivilegesRepository;


    private final OrganizationDetailsRepository organizationDetailsRepository;



    private final MessageSource messageSource;

    private final SubscriberRepository subscriberRepository;

    @Value("${privileges}")
    List<String> privileges;

    public OrganizationPrivilegesImpl(WalletSignCertRepo walletSignCertRepo, OrganisationPrivilegesRepository organisationPrivilegesRepository, OrganizationDetailsRepository organizationDetailsRepository, MessageSource messageSource,SubscriberRepository subscriberRepository) {
        this.walletSignCertRepo = walletSignCertRepo;
        this.organisationPrivilegesRepository = organisationPrivilegesRepository;
        this.organizationDetailsRepository = organizationDetailsRepository;

        this.messageSource = messageSource;
        this.subscriberRepository=subscriberRepository;
    }

//    @Override
//    public ApiResponse createPrivileges(OrganisationPrivilegesDto organisationPrivilegesDto) {
//
//       try{
//           if(organisationPrivilegesDto.getOrganizationId()==null || organisationPrivilegesDto.getOrganizationId().isEmpty()){
//               return AppUtil.createApiResponse(false,"organization Id cannot be null or empty",null);
//           }
//
//           Optional<OrganizationDetails> organizationDetails= Optional.ofNullable(organizationDetailsRepository.findByOrganizationUid(organisationPrivilegesDto.getOrganizationId()));
//           if(organizationDetails.isPresent()){
//               OrganizationPrivileges organizationPrivilegesDB=organisationPrivilegesRepository.fetchPrivilegesByOrganisation(organisationPrivilegesDto.getOrganizationId());
//               if(organizationPrivilegesDB!=null){
//                   return AppUtil.createApiResponse(false,"Privileges Already exists for this Organisation",null);
//               }
//               OrganizationPrivileges organizationPrivileges=new OrganizationPrivileges();
////               organizationPrivileges.setActive(organisationPrivilegesDto.isActive());
//               organizationPrivileges.setCreatedAt(AppUtil.getDate());
//               organizationPrivileges.setModifiedAt(AppUtil.getDate());
//               organizationPrivileges.setRelyingParty(organisationPrivilegesDto.isRelyingParty());
//               organizationPrivileges.setDataProvider(organisationPrivilegesDto.isDataProvider());
//               organizationPrivileges.setDocumentIssuer(organisationPrivilegesDto.isDocumentIssuer());
//               organizationPrivileges.setDigitalVaultCertificate(organisationPrivilegesDto.isDigitalVaultCertificate());
//               organizationPrivileges.setEsealCertificate(organisationPrivilegesDto.isEsealCertificate());
//               organizationPrivileges.setDigitalEngagementServices(organisationPrivilegesDto.isDigitalEngagementServices());
//               organizationPrivileges.setOrganizationId(organisationPrivilegesDto.getOrganizationId());
//               organisationPrivilegesRepository.save(organizationPrivileges);
//               logger.info("{} - {} : Organization privileges created successfully", CLASS, Utility.getMethodName());
//               return AppUtil.createApiResponse(true,"Organization privileges created successfully",organizationPrivileges);
//           }else{
//               logger.info("{} - {} : Organization Not Found", CLASS, Utility.getMethodName());
//               return AppUtil.createApiResponse(false,"organization Not Found",null);
//           }
//       }catch (Exception e){
//           e.printStackTrace();
//           logger.error("{} - {} : Exception occurred during organization Privilege Creation: {}", CLASS,
//                   Utility.getMethodName(), e.getMessage());
//           return ExceptionHandlerUtil.handleException(e);
//       }
//
//
//    }
//
//    @Override
//    public ApiResponse updatePrivileges(OrganisationPrivilegesDto organisationPrivilegesDto) {
//        try{
//            if(organisationPrivilegesDto.getOrganizationId()==null || organisationPrivilegesDto.getOrganizationId().isEmpty()){
//                return AppUtil.createApiResponse(false,"organization Id cannot be null or empty",null);
//            }
//            Optional<OrganizationDetails> organizationDetails= Optional.ofNullable(organizationDetailsRepository.findByOrganizationUid(organisationPrivilegesDto.getOrganizationId()));
//            if(organizationDetails.isPresent()){
//                if(organisationPrivilegesDto.getId()==0 ){
//                    return AppUtil.createApiResponse(true,"Organization privileges not found",null);
//                }
////                Optional<OrganizationPrivileges> organizationPrivileges= Optional.of(organisationPrivilegesRepository.getOne(organisationPrivilegesDto.getId()));
////                Optional<OrganizationPrivileges> organizationPrivileges=Optional.of(organisationPrivilegesRepository.fetchById(organisationPrivilegesDto.getId()));
//                OrganizationPrivileges organizationPrivileges= organisationPrivilegesRepository.fetchById(organisationPrivilegesDto.getId());
//                if(organizationPrivileges==null){
//                    return AppUtil.createApiResponse(false,"Privileges Not Found ",null);
//                }
////                organizationPrivileges.setActive(organisationPrivilegesDto.isActive());
//                organizationPrivileges.setCreatedAt(AppUtil.getDate());
//                organizationPrivileges.setModifiedAt(AppUtil.getDate());
//                organizationPrivileges.setRelyingParty(organisationPrivilegesDto.isRelyingParty());
//                organizationPrivileges.setDataProvider(organisationPrivilegesDto.isDataProvider());
//                organizationPrivileges.setDocumentIssuer(organisationPrivilegesDto.isDocumentIssuer());
//                organizationPrivileges.setDigitalVaultCertificate(organisationPrivilegesDto.isDigitalVaultCertificate());
//                organizationPrivileges.setEsealCertificate(organisationPrivilegesDto.isEsealCertificate());
//                organizationPrivileges.setDigitalEngagementServices(organisationPrivilegesDto.isDigitalEngagementServices());
//                organizationPrivileges.setOrganizationId(organisationPrivilegesDto.getOrganizationId());
////                OrganizationPrivileges organizationPrivileges1=organisationPrivilegesRepository.save(organizationPrivileges.get());
//                logger.info("{} - {} : Organization privileges Updated successfully", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(true,"Organization privileges updated successfully",organizationPrivileges);
//
//            }else{
//                logger.info("{} - {} : Organization Not Found", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(false,"organization Not Found",null);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            logger.error("{} - {} : Exception occurred during organization Privilege Update: {}", CLASS,
//                    Utility.getMethodName(), e.getMessage());
//            return AppUtil.createApiResponse(false,"Sorry! There's a glitch. We're working on it, please try again shortly.",null);
//        }
//    }
//
//    @Override
//    public ApiResponse getPrivileges() {
//        try {
//
//            List<OrganizationPrivileges> privilegesList=organisationPrivilegesRepository.findAll();
//            logger.info("{} - {} : Organization Privileges Fetched", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(true, "Records retrieved successfully.", privilegesList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("{} - {} : Exception occurred during organization Privilege GET: {}", CLASS,
//                    Utility.getMethodName(), e.getMessage());
//            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
//        }
//    }
//
//    @Override
//    public ApiResponse getPrivilegesById(int id) {
//        try {
//            if(id==0){
//                logger.info("{} - {} :Organisation Privileges Not Found", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(false,"Organisation Privileges Not Found",null);
//            }
//            else{
//                OrganizationPrivileges organizationPrivileges= organisationPrivilegesRepository.fetchById(id);
//                if(organizationPrivileges==null){
//                    logger.info("{} - {} :Organisation Privileges Not Found", CLASS, Utility.getMethodName());
//                    return AppUtil.createApiResponse(false, "Organisation Privileges Not Found", null);
//                }
//                logger.info("{} - {} :Organisation Privileges Fetched By Id", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(true, "Data Retrieved Successfully", organizationPrivileges);
//
//            }
//
////            return privileges.map(organizationPrivileges -> AppUtil.createApiResponse(true, "Privileges retrieved successfully.", organizationPrivileges)).orElseGet(() -> AppUtil.createApiResponse(false, "No privileges found for the specified organization.", null));
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ID: {}", CLASS,
//                    Utility.getMethodName(), e.getMessage());
//            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
//        }
//    }
//
//    @Override
//    public ApiResponse deletePrivilege(int id) {
//        try {
//            if(id==0){
//                logger.info("{} - {} :Organisation Privileges Not Found", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(false,"Organisation Privileges Not Found",null);
//            }
//            OrganizationPrivileges organizationPrivileges= organisationPrivilegesRepository.fetchById(id);
//            if(organizationPrivileges==null){
//                logger.info("{} - {} :Organisation Privileges Not Found", CLASS, Utility.getMethodName());
//                return AppUtil.createApiResponse(false, "Organisation Privileges Not Found", null);
//            }
//            organisationPrivilegesRepository.deleteById(id);
//            logger.info("{} - {} :Organisation Privileges Deleted ", CLASS, Utility.getMethodName());
//            return AppUtil.createApiResponse(true,"Privileges Deleted successfully",null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("{} - {} : Exception occurred during organization Privilege DELETE: {}", CLASS,
//                    Utility.getMethodName(), e.getMessage());
//            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
//        }
//    }
//
//    @Override
//    public ApiResponse getPrivilegesByOrgId(String orgId) {
//        try {
//            if(orgId==null || orgId.isEmpty()){
//
//                return AppUtil.createApiResponse(false,"Organisation Id can't be null or empty ",null);
//            }
//            else{
//                OrganizationPrivileges organizationPrivileges= organisationPrivilegesRepository.fetchPrivilegesByOrganisation(orgId);
//                WalletSignCertificate walletSignCertificate=walletSignCertRepo.findByOrganizationId("ACTIVE",orgId);
//                if(organizationPrivileges==null){
//                    OrganisationPrivilegesResponse organisationPrivilegesResponse=new OrganisationPrivilegesResponse();
////                    WalletSignCertificate walletSignCertificate=walletSignCertRepo.findByOrganizationId("ACTIVE",orgId);
//                    if(walletSignCertificate!=null){
//                        organisationPrivilegesResponse.setWalletCertificateStatus(true);
//                    }
////                    organisationPrivilegesResponse.setOrganisationPrivileges();
//                    logger.info("{} - {} :Organisation Privileges Fetched By Org Id", CLASS, Utility.getMethodName());
//                    return AppUtil.createApiResponse(true, "Data Retrieved Successfully", organisationPrivilegesResponse);
//                }
//
//
//                OrganisationPrivilegesResponse organisationPrivilegesResponse=new OrganisationPrivilegesResponse();
//
//                if(walletSignCertificate!=null){
//                    organisationPrivilegesResponse.setWalletCertificateStatus(true);
//                }
//                organisationPrivilegesResponse.setOrganisationPrivileges(organizationPrivileges);
//
//                logger.info("{} - {} :Organisation Privileges Fetched By Org Id", CLASS, Utility.getMethodName());
//
//                return AppUtil.createApiResponse(true, "Data Retrieved Successfully", organisationPrivilegesResponse);
//
//            }
//
////            return privileges.map(organizationPrivileges -> AppUtil.createApiResponse(true, "Privileges retrieved successfully.", organizationPrivileges)).orElseGet(() -> AppUtil.createApiResponse(false, "No privileges found for the specified organization.", null));
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ORG ID: {}", CLASS,
//                    Utility.getMethodName(), e.getMessage());
//            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
//        }
//    }

    @Override
    public ApiResponses getPrivilegesByOrgId(String orgId) {
        try{
            OrganisationPrivilegesResponse organisationPrivilegesResponse=new OrganisationPrivilegesResponse();
            if(orgId==null || orgId.isEmpty()){

                return AppUtil.createApiResponse(false,"Organisation Id can't be null or empty ",null);
            }
            else{
                OrganizationDetails organizationDetails=organizationDetailsRepository.findByOrganizationUid(orgId);
                if(organizationDetails==null){
                    return AppUtil.createApiResponse(false,"Organization Details Not Found",null);
                }
                WalletSignCertificate walletSignCertificate=walletSignCertRepo.findByOrganizationId("ACTIVE",orgId);
                if(walletSignCertificate!=null){
                  organisationPrivilegesResponse.setWalletCertificateStatus(true);
                }
                else{
                    organisationPrivilegesResponse.setWalletCertificateStatus(false);
                }
                organisationPrivilegesResponse.setPrivileges(organisationPrivilegesRepository.fetchPrivilegesByOrganisation(orgId));
                return AppUtil.createApiResponse(true,"Data Retrieved Successfully",organisationPrivilegesResponse);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ID: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }
    }

    @Override
    public ApiResponses requestPrivilege(OrganisationPrivilegesRequestDto organisationPrivilegesRequestDto) {
        try{
            if(organisationPrivilegesRequestDto.getOrganizationId()==null || organisationPrivilegesRequestDto.getOrganizationId().isEmpty()){
                return AppUtil.createApiResponse(false, "Organisation ID cannot be null or empty", null);
            }
            OrganizationDetails organizationDetails=organizationDetailsRepository.findByOrganizationUid(organisationPrivilegesRequestDto.getOrganizationId());
            if(organizationDetails==null){
                return AppUtil.createApiResponse(false, "Organisation Not Found", null);
            }
            if(organisationPrivilegesRequestDto.getSuid()==null || organisationPrivilegesRequestDto.getSuid().isEmpty()){
                return AppUtil.createApiResponse(false, "SPOC ID cannot be null or empty", null);
            }
            Subscriber subscriber=subscriberRepository.getSubscriberEmail(organisationPrivilegesRequestDto.getSuid());
            if(subscriber==null){
                return AppUtil.createApiResponse(false, "Subscriber Not Found", null);
            }

            if(organisationPrivilegesRequestDto.getPrivileges()==null || organisationPrivilegesRequestDto.getPrivileges().isEmpty()){
                return AppUtil.createApiResponse(false, "Privileges cannot be null or empty", null);
            }
            List<OrganizationPrivileges> organizationPrivilegesList = new ArrayList<>();
            for(String privilege: organisationPrivilegesRequestDto.getPrivileges()){
                OrganizationPrivileges organizationPrivileges=organisationPrivilegesRepository.fetchByPrivilege(organisationPrivilegesRequestDto.getOrganizationId(),privilege);

                if(organizationPrivileges!=null){
                    if(!organizationPrivileges.getStatus().equals("REJECTED")){
                        return AppUtil.createApiResponse(false, "Unable to process request as privilege already exists", null);
                    }
                    else{
                        OrganizationPrivileges organizationPrivileges1=new OrganizationPrivileges();
                        organizationPrivileges1.setPrivilege(privilege);
                        organizationPrivileges1.setOrganizationName(organizationDetails.getOrganizationName());
                        organizationPrivileges1.setOrganizationId(organizationDetails.getOrganizationUid());
                        organizationPrivileges1.setCreatedBy(subscriber.getFullName());
                        organizationPrivileges1.setCreatedOn(AppUtil.getDate());
                        organizationPrivileges1.setStatus("APPLIED");
                        organizationPrivilegesList.add(organizationPrivileges1);
                    }
                }
                else{
                    OrganizationPrivileges organizationPrivileges1=new OrganizationPrivileges();
                    organizationPrivileges1.setPrivilege(privilege);
                    organizationPrivileges1.setOrganizationName(organizationDetails.getOrganizationName());
                    organizationPrivileges1.setOrganizationId(organizationDetails.getOrganizationUid());
                    organizationPrivileges1.setCreatedBy(subscriber.getFullName());
                    organizationPrivileges1.setCreatedOn(AppUtil.getDate());
                    organizationPrivileges1.setStatus("APPLIED");
                    organizationPrivilegesList.add(organizationPrivileges1);
                }
            }
            List<OrganizationPrivileges> organizationPrivilegesList1=organisationPrivilegesRepository.saveAll(organizationPrivilegesList);
            return AppUtil.createApiResponse(true,"Organization Privileges Requested Successfully",organizationPrivilegesList1);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege REQUEST: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }
    }

    @Override
    public ApiResponses updatePrivilege(UpdateOrganizationPrivilegeDto updateOrganizationPrivilegeDto) {
        try {


        OrganizationPrivileges organizationPrivileges = organisationPrivilegesRepository.fetchById(updateOrganizationPrivilegeDto.getId());
        if (organizationPrivileges == null) {
            return AppUtil.createApiResponse(false,"Organization Privileges Not Found",null);
        }
        if(organizationPrivileges.getStatus().equals("REJECTED")){
            return AppUtil.createApiResponse(false,"Cannot Update this Privilege as it's rejected",null);
        }
        if(organizationPrivileges.getStatus().equals("APPROVED")){
            if(updateOrganizationPrivilegeDto.getStatus().equals("REJECTED")){
                return AppUtil.createApiResponse(false,"cannot reject Approved privilege",null);
            }
        }
        if(organizationPrivileges.getStatus().equals("APPLIED")){
            if(updateOrganizationPrivilegeDto.getStatus().equals("SUSPENDED")){
                return AppUtil.createApiResponse(false,"Cannot Suspend Applied privilege",null);
            }
        }
        if(organizationPrivileges.getStatus().equals("SUSPENDED")){
            if(updateOrganizationPrivilegeDto.getStatus().equals("REJECTED")){
                return AppUtil.createApiResponse(false,"cannot reject Suspended privilege",null);
            }
        }
        organizationPrivileges.setStatus(updateOrganizationPrivilegeDto.getStatus());
        organizationPrivileges.setModifiedBy(updateOrganizationPrivilegeDto.getAdminName());
        organizationPrivileges.setModifiedOn(AppUtil.getDate());
        OrganizationPrivileges organizationPrivileges1=organisationPrivilegesRepository.save(organizationPrivileges);
        return AppUtil.createApiResponse(true,"Privilege Updated SuccessFully",organizationPrivileges1);
    }catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege  UPDATE: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }
    }

    @Override
    public ApiResponses getAllPrivileges() {
        try{
            return AppUtil.createApiResponse(true,"Records Fetched successfully",organisationPrivilegesRepository.getAll());
        }catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege GET ALL: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }
    }

    @Override
    public ApiResponses getOrganizationPrivilegeById(int id){
        try{
         if(id==0 || id<0){
             return AppUtil.createApiResponse(false, "Record Not Found", null);
         }
         else{
             return AppUtil.createApiResponse(true,"Record Fetched Successfully",organisationPrivilegesRepository.fetchById(id));
         }
}catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ID: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }
    }

    @Override
    public ApiResponses getPrivilegesByOrganization(String orgId) {
        try{
            if(orgId==null || orgId.isEmpty()){
                return AppUtil.createApiResponse(false, "Organisation ID cannot be null or empty", null);
            }
            OrganizationDetails organizationDetails=organizationDetailsRepository.findByOrganizationUid(orgId);
            if(organizationDetails==null){
                return AppUtil.createApiResponse(false,"Organization Not Found",null);
            }
            return AppUtil.createApiResponse(true,"Records Fetched Successfully",organisationPrivilegesRepository.fetchPrivilegesByOrganisation(orgId));
        }catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege GET BY ORGANIZATION: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }
    }

    @Override
    public ApiResponses updateOrganizationPrivilegeList(UpdateOrganizationPrivilegesListDto updateOrganizationPrivilegesListDto) {
        try{
            if(updateOrganizationPrivilegesListDto.getOrgId()==null || updateOrganizationPrivilegesListDto.getOrgId().isEmpty()){
                return AppUtil.createApiResponse(false, "Organisation ID cannot be null or empty", null);
            }
            OrganizationDetails organizationDetails=organizationDetailsRepository.findByOrganizationUid(updateOrganizationPrivilegesListDto.getOrgId());
            if(organizationDetails==null){
                return AppUtil.createApiResponse(false, "Organisation Not Found", null);
            }
            if(updateOrganizationPrivilegesListDto.getPrivileges()==null ){
                return AppUtil.createApiResponse(false, "Privileges cannot be null ", null);
            }

                organisationPrivilegesRepository.updatePrivileges(updateOrganizationPrivilegesListDto.getOrgId());
//                return AppUtil.createApiResponse(true,"Privileges Updated Successfully",null);
            if(updateOrganizationPrivilegesListDto.getPrivileges().isEmpty()){
                return AppUtil.createApiResponse(true,"Privileges Updated Successfully",null);
            }
            List<OrganizationPrivileges> organizationPrivilegesList = new ArrayList<>();

            for(String privilege: updateOrganizationPrivilegesListDto.getPrivileges()) {
                OrganizationPrivileges organizationPrivileges = organisationPrivilegesRepository.fetchByPrivilege(updateOrganizationPrivilegesListDto.getOrgId(), privilege);
                if(organizationPrivileges==null){
                    OrganizationPrivileges organizationPrivileges1=new OrganizationPrivileges();
                    organizationPrivileges1.setOrganizationId(updateOrganizationPrivilegesListDto.getOrgId());
                    organizationPrivileges1.setCreatedBy(updateOrganizationPrivilegesListDto.getModifiedBy());
                    organizationPrivileges1.setCreatedOn(AppUtil.getDate());
                    organizationPrivileges1.setOrganizationName(organizationDetails.getOrganizationName());
                    organizationPrivileges1.setPrivilege(privilege);
                    organizationPrivileges1.setStatus("APPROVED");
                    organizationPrivilegesList.add(organizationPrivileges1);
                }else{
                    organizationPrivileges.setStatus("APPROVED");
                    organizationPrivileges.setModifiedBy(updateOrganizationPrivilegesListDto.getModifiedBy());
                    organizationPrivileges.setModifiedOn(AppUtil.getDate());
                    organizationPrivilegesList.add(organizationPrivileges);
                }
            }
            List<OrganizationPrivileges> organizationPrivilegesListDB=organisationPrivilegesRepository.saveAll(organizationPrivilegesList);
            return AppUtil.createApiResponse(true,"Privileges Updated Successfully",organizationPrivilegesListDB);
            }catch (Exception e){
            e.printStackTrace();
            logger.error("{} - {} : Exception occurred during organization Privilege UPDATE ORG PRIVILEGE LIST: {}", CLASS,
                    Utility.getMethodName(), e.getMessage());
            return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
        }

    }

    @Override
    public ApiResponses getPrivilegesNames() {
     try{
        return AppUtil.createApiResponse(true,"Privileges fetched successfully",privileges);
     }catch (Exception e){
       e.printStackTrace();
         logger.error("{} - {} : Exception occurred during organization Privilege GET NAMES{}", CLASS,
                 Utility.getMethodName(), e.getMessage());
         return AppUtil.createApiResponse(false, "Unable to process your request at the moment. Please try again later.", null);
     }
    }

}
