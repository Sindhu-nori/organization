package com.dtt.organization.service.impl;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.OrgBucketConfigDTO;

import com.dtt.organization.dto.OrgClientAppConfigDto;
import com.dtt.organization.model.OrgBucketConfig;
import com.dtt.organization.model.OrgBuckets;

import com.dtt.organization.model.OrgClientAppConfig;
import com.dtt.organization.repository.OrgBucketConfigRepo;
import com.dtt.organization.repository.OrgBucketsRepo;

import com.dtt.organization.repository.OrgClientAppConfigRepo;
import com.dtt.organization.service.iface.OrgBucketsIface;
import com.dtt.organization.util.AppUtil;

import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrgBucketImpl implements OrgBucketsIface {

   @Autowired
    OrgBucketsRepo orgBucketsRepo;

    @Autowired
    OrgBucketConfigRepo orgBucketConfigRepo;

    @Autowired
    OrgClientAppConfigRepo orgClientAppConfigRepo;





    @Override
    public ApiResponses getBucketDetailsById(int id) {

        try{
            if(id==0){
                return AppUtil.createApiResponse(false,"id cannot be null",null);
            }
            OrgBuckets orgBuckets = orgBucketsRepo.getBucketDetailsById(id);
            return AppUtil.createApiResponse(true,"Fetched BucketDetails By id",orgBuckets);


        }catch(Exception e){
            e.printStackTrace();
            return AppUtil.createApiResponse(false,"Something went wrong",null);

        }

    }


    @Override
    public ApiResponses getAllBucketConfigListByOuid(String ouid) {
        try{
            if(ouid!=null){

                return AppUtil.createApiResponse(true,"Fetched successfully",orgBucketConfigRepo.getAllBucketConfigListByOuid(ouid));

            }
        }catch (Exception e){
            e.printStackTrace();
            return AppUtil.createApiResponse(false,"An Exception Occurred.Please Try After Sometime",null);

        }

        return null;
    }

    @Override
    public ApiResponses getBucketConfigByAppid(String appId) {
        try{
            if(appId!=null){

                return AppUtil.createApiResponse(true,"Fetched successfully",orgBucketConfigRepo.getBucketsConfigByAppid(appId));

            }
        }catch (Exception e){
            e.printStackTrace();
            return AppUtil.createApiResponse(false,"An Exception Occurred.Please Try After Sometime",null);

        }

        return null;
    }

    @Override
    public ApiResponses getBucketsListByOuid(String ouid) {
            try{

                if(!ouid.equals("null")){
                    System.out.println("Inside if");
                    List<OrgBucketConfig> res = orgBucketConfigRepo.getBucketConfigListByOuid(ouid);
                    return AppUtil.createApiResponse(true,"Fetched successfully",res);

                }
                else{
                    System.out.println("Inside else");
                    List<OrgBucketConfig> res1 = orgBucketConfigRepo.getOrgBucketConfigList();
                    return AppUtil.createApiResponse(true,"Fetched successfully",res1);
                }
            }catch (Exception e){
            e.printStackTrace();
            return AppUtil.createApiResponse(false,"An Exception Occurred.Please Try After Sometime",null);

        }


    }


    @Override
    public ApiResponses getBucketHistoryByBucketId(String bucketId) {
        System.out.println("BucketId"+bucketId);
        try{
            System.out.println("Inside try");

            if(bucketId ==null || bucketId.isEmpty()){
                return AppUtil.createApiResponse(false,"Bucket id should be null",null);
            }
            else{
                System.out.println("Inside else");
                List<OrgBuckets> orgBuckets = orgBucketsRepo.findBucketHistoryListByBucketId(bucketId);

                return AppUtil.createApiResponse(true,"Bucket History Fetched Successfully",orgBuckets);
            }

        }catch (Exception e){
            return AppUtil.createApiResponse(false,"Something went wrong",null);
        }
    }

    @Override
    public ApiResponses addOrgBucketConfig(OrgBucketConfigDTO orgBucketConfigDTO) {
        try {
        	System.out.println("orgBucketConfigDTO "+orgBucketConfigDTO);
            if(orgBucketConfigDTO.getAppId()==null ||  orgBucketConfigDTO.getAppId().equals("") ){
                return AppUtil.createApiResponse(false,"Appid can't be null",null);
            }

            Optional<OrgBucketConfig> orgBucketConfig = Optional.ofNullable(orgBucketConfigRepo.getBucketsConfigByAppid(orgBucketConfigDTO.getAppId()));

            //CHECK DUPLICATE IN org_bucket_config IF PRESENT THROW ERROR
            if(orgBucketConfig.isPresent()){
                return AppUtil.createApiResponse(false,"Appid already existed ",null);
            }

            //CHECK DUPLICATE IN org_client_app_config IF Duplicate just active that row status
            Optional<OrgClientAppConfig> orgClientAppConfig = Optional.ofNullable(orgClientAppConfigRepo.getByAppidAndConfigValue(orgBucketConfigDTO.getAppId()));

            if(orgClientAppConfig.isPresent()){
                orgClientAppConfig.get().setStatus("ACTIVE");
                orgClientAppConfig.get().setCreatedOn(AppUtil.getDate().toString());
                orgClientAppConfig.get().setUpdatedOn(AppUtil.getDate().toString());
                orgClientAppConfigRepo.save(orgClientAppConfig.get());
            }
            else {

                OrgClientAppConfig orgClientAppConfigToStore = new OrgClientAppConfig();

                orgClientAppConfigToStore.setAppId(orgBucketConfigDTO.getAppId());
                orgClientAppConfigToStore.setOrgId(orgBucketConfigDTO.getOrgId());
                orgClientAppConfigToStore.setConfigValue("SPONSOR_ID_AND_BUCKET_ID");
                orgClientAppConfigToStore.setStatus("ACTIVE");
                orgClientAppConfigToStore.setCreatedOn(AppUtil.getDate().toString());
                orgClientAppConfigToStore.setUpdatedOn(AppUtil.getDate().toString());

                orgClientAppConfigRepo.save(orgClientAppConfigToStore);
            }



            OrgBucketConfig orgBucketConfig1 = new OrgBucketConfig();

            orgBucketConfig1.setOrgId(orgBucketConfigDTO.getOrgId());
            orgBucketConfig1.setAppId(orgBucketConfigDTO.getAppId());
            orgBucketConfig1.setLabel(orgBucketConfigDTO.getLabel());
            orgBucketConfig1.setBucketClosingMessage(orgBucketConfigDTO.getBucketClosingMessage());
            orgBucketConfig1.setCreatedOn(AppUtil.getDate().toString());
            orgBucketConfig1.setUpdatedOn(AppUtil.getDate().toString());
            orgBucketConfig1.setStatus("ACTIVE");
            orgBucketConfig1.setAdditionalDs(orgBucketConfigDTO.getAdditionalDs());
            orgBucketConfig1.setAdditionalEds(orgBucketConfigDTO.getAdditionalEds());
            orgBucketConfig1.setOrgName(orgBucketConfigDTO.getOrgName());

            orgBucketConfigRepo.save(orgBucketConfig1);

            return AppUtil.createApiResponse(true,"Organisation bucket configuration saved successfully ",null);

        } catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
                | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "Database Exception Occurred", null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "An Exception occurred", null);
        }
    }

    @Override
    public ApiResponses updateBucketConfigById(OrgBucketConfigDTO orgBucketConfigDTO) {
        try{

            if(orgBucketConfigDTO.getId()==0 ){
                return AppUtil.createApiResponse(false, "Id cannot be empty",null);
            }

            OrgBucketConfig orgBucketConfig = orgBucketConfigRepo.findByid(orgBucketConfigDTO.getId());

            if(orgBucketConfig==null){
                return AppUtil.createApiResponse(false, "No record found",null);
            }
            else {

                OrgClientAppConfig orgClientAppConfig = new OrgClientAppConfig();

                orgBucketConfig.setOrgId(orgBucketConfigDTO.getOrgId());
                orgBucketConfig.setOrgName(orgBucketConfigDTO.getOrgName());
                orgBucketConfig.setAppId(orgBucketConfigDTO.getAppId());
                orgBucketConfig.setLabel(orgBucketConfigDTO.getLabel());
                orgBucketConfig.setBucketClosingMessage(orgBucketConfigDTO.getBucketClosingMessage());
                orgBucketConfig.setUpdatedOn(AppUtil.getDate());
                orgBucketConfig.setAdditionalDs(orgBucketConfigDTO.getAdditionalDs());
                orgBucketConfig.setAdditionalEds(orgBucketConfigDTO.getAdditionalEds());
                orgBucketConfig.setStatus(orgBucketConfigDTO.getStatus());
                orgClientAppConfig.setStatus(orgBucketConfigDTO.getStatus());
                orgBucketConfigRepo.save(orgBucketConfig);
                orgClientAppConfigRepo.save(orgClientAppConfig);


                return AppUtil.createApiResponse(true, "Record updated successfully", null);

            }


        }catch (Exception e){
            e.printStackTrace();
            return AppUtil.createApiResponse(false,"An Exception Occurred.Please Try After Sometime",null);

        }


    }

    @Override
    public ApiResponses getBucketHistoryByBucketConfigId(int bucketConfigId) {
        try{
            if(bucketConfigId==0){
                return AppUtil.createApiResponse(false,"bucketconfig id cannot be null",null);
            }
            else {
                List<OrgBuckets> orgBucketsHistory = orgBucketsRepo.findBucketHistoryListByBucketConfigId((long) bucketConfigId);
                System.out.println("gfuybk"+ orgBucketsHistory);
                return AppUtil.createApiResponse(true,"Fetched Successfully",orgBucketsHistory);
            }
        }catch(Exception e){
            e.printStackTrace();
            return AppUtil.createApiResponse(false,"An Exception Occurred.Please Try After Sometime",null);
        }
    }

    @Override
    public ApiResponses getBucketConfigByid(int id) {

        try{
            if(id==0){
                return AppUtil.createApiResponse(false,"id Can't be null or zero",null);
            }else {

                OrgBucketConfig res = orgBucketConfigRepo.findByid(id);

                if(res==null){
                    return AppUtil.createApiResponse(false,"Record not found for given id",null);
                }else {
                    return AppUtil.createApiResponse(true,"Record Fetched Successfully",res);
                }

            }
        }catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
                | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "Database Exception Occurred", null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "An Exception occurred", null);
        }
    }

    @Override
    public ApiResponses addOrgClientAppConfig(OrgClientAppConfigDto orgClientAppConfigDto) {

        try{

            if(orgClientAppConfigDto.getOrgId()==null || orgClientAppConfigDto.getAppId()==null || orgClientAppConfigDto.getConfigValue()==null){
                return AppUtil.createApiResponse(false,"AppId and organization id and config_value Can't be null or zero",null);
            }



            if(orgClientAppConfigRepo.checkDuplicateByAppid(orgClientAppConfigDto.getAppId())!=null){
                return AppUtil.createApiResponse(false," Duplicate Record found for given AppId",null);
            }

            OrgClientAppConfig orgClientAppConfig = new OrgClientAppConfig();

            orgClientAppConfig.setId(orgClientAppConfigDto.getId());
            orgClientAppConfig.setAppId(orgClientAppConfigDto.getAppId());
            orgClientAppConfig.setOrgId(orgClientAppConfigDto.getOrgId());
            orgClientAppConfig.setConfigValue(orgClientAppConfigDto.getConfigValue());
            orgClientAppConfig.setStatus("ACTIVE");
            orgClientAppConfig.setCreatedOn(AppUtil.getDate());
            orgClientAppConfig.setUpdatedOn(AppUtil.getDate());

            orgClientAppConfigRepo.save(orgClientAppConfig);

            return AppUtil.createApiResponse(true, "Record saved successfully", null);
        }catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
                | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "Database Exception Occurred", null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "An Exception occurred", null);
        }
    }

    @Override
    public ApiResponses EnableDisableSponsorship(int id) {

        try{

            if(id==0){
                return AppUtil.createApiResponse(false," id cannot be zero or null",null);
            }

            OrgClientAppConfig record = orgClientAppConfigRepo.findById(id).get();


            if(record.getStatus().equals("INACTIVE")){
                orgClientAppConfigRepo.changeStatusToActiveById(id);
                return AppUtil.createApiResponse(true,"Activated Successfully",null);
            }

            orgClientAppConfigRepo.changeStatusToInactiveById(id);
            return AppUtil.createApiResponse(true,"Deactivated Successfully",null);


        }catch (JDBCConnectionException | ConstraintViolationException | DataException | LockAcquisitionException
                | PessimisticLockException | QueryTimeoutException | SQLGrammarException | GenericJDBCException ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "Database Exception Occurred", null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return AppUtil.createApiResponse(false, "An Exception occurred", null);
        }

    }


}
