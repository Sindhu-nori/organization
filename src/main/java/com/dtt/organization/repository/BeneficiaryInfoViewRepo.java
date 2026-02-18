package com.dtt.organization.repository;

import com.dtt.organization.model.BeneficiaryInfoView;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BeneficiaryInfoViewRepo extends JpaRepository<BeneficiaryInfoView,Integer> {


//    @Query(value = "select * from beneficiary_info_view where `beneficiary_ugpass_email`=?1 OR `beneficiary_passport`=?2  OR `beneficiary_mobile_number`=?3 OR 'beneficiary_digital_id'=?4 AND `beneficiary_status`='ACTIVE' AND `validity_status`='ACTIVE';",
//            nativeQuery = true)
//    List<BeneficiaryInfoView> findByEmailOrPassportOrMobileNumberOrBeneficiaryDigitalId(String email, String passport, String mobileNumber, String BeneficiaryDigitalId);
//
//    @Query(value = "select * from beneficiary_info_view where `beneficiary_ugpass_email`=?1  OR `beneficiary_nin`=?2 OR `beneficiary_mobile_number`=?3 OR 'beneficiary_digital_id'=?4 AND `beneficiary_status`='ACTIVE' AND `validity_status`='ACTIVE'",
//            nativeQuery = true)
//    List<BeneficiaryInfoView> findByEmailOrNinOrMobileNumberOrBeneficiaryDigitalId(String email, String nin, String mobileNumber,String BeneficiaryDigitalId);

    @Query("SELECT b FROM BeneficiaryInfoView b " +
            "WHERE b.beneficiaryStatus = 'ACTIVE' " +
            "AND b.validityStatus = 'ACTIVE' " +
            "AND (b.beneficiaryUgPassEmail = ?1 " +
            "OR b.beneficiaryPassport = ?2 " +
            "OR b.beneficiaryNin = ?3 " +
            "OR b.beneficiaryMobileNumber = ?4 " +
            "OR b.beneficiaryDigitalId = ?5)")
    List<BeneficiaryInfoView> findByEmailOrPassportOrNinOrMobileNumberOrBeneficiaryDigitalId(
            String email, String passport, String nin, String mobileNumber, String BeneficiaryDigitalId);

    @Query("SELECT b FROM BeneficiaryInfoView b WHERE b.sponsorExternalId = ?1")
    List<BeneficiaryInfoView> getVendorsByVendorId(String vendorId);



}
