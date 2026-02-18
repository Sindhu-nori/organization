package com.dtt.organization.repository;


import com.dtt.organization.model.Benificiaries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface BeneficiariesRepo extends JpaRepository<Benificiaries, Integer> {



//    @Query(value = "SELECT * FROM beneficiaries WHERE sponsor_digital_id=?1 AND beneficiary_type='INDIVIDUAL' ORDER BY id DESC;", nativeQuery = true)
//    List<Benificiaries> getAllBeneficiariesBySponsor(String sponsorId);
//
//    @Query(value = "SELECT * FROM beneficiaries WHERE sponsor_digital_id=?1 ORDER BY id DESC", nativeQuery = true)
//    List<Benificiaries> getAllBeneficiariesBySponsorByDigitalId(String sponsorId);
//
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and (beneficiary_mobile_number=?2 or beneficiary_passport=?3 or beneficiary_office_email=?4)",nativeQuery = true)
//    List<Benificiaries> findDuplicateBeneficiariesByPassport(String sponsorId, String number, String passport, String officeEmail);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and (beneficiary_mobile_number=?2 or beneficiary_nin=?3 or beneficiary_office_email=?4)",nativeQuery = true)
//    List<Benificiaries> findDuplicateBeneficiariesByNIN(String sponsorId, String number, String NIN, String officeEmail);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and beneficiary_ugpass_email=?2",nativeQuery = true)
//    Benificiaries findDuplicateBeneficiariesByUgPassEmail(String sponsorId, String ugPassEmail);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and beneficiary_mobile_number=?2",nativeQuery = true)
//    Benificiaries findDuplicateBeneficiariesByMobileNumber(String sponsorId, String mobileNumber);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and beneficiary_passport=?2",nativeQuery = true)
//    Benificiaries findDuplicateBeneficiariesByPassport(String sponsorId, String passport);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and beneficiary_nin=?2",nativeQuery = true)
//    Benificiaries findDuplicateBeneficiariesByNIN(String sponsorId, String NIN);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and beneficiary_office_email=?2",nativeQuery = true)
//    Benificiaries findDuplicateBeneficiariesByOfficeEmail(String sponsorId, String OfficeEmail);
//
//    @Query(value = "select * from beneficiaries where sponsor_digital_id=?1 and beneficiary_digital_id=?2",nativeQuery = true)
//    Benificiaries findDuplicateBeneficiariesByBeneficiaryDigitalId(String sponsorId, String beneficiaryDigitalId);
//    @Modifying
//    @Transactional
//    @Query(value = "update beneficiaries set status='INACTIVE' where id=?1",nativeQuery = true)
//    int changeStatusById(int Id);
//
//
//
//
//    @Query(value = "SELECT e.* " +
//            "FROM beneficiaries e " +
//            "JOIN beneficiary_validity v ON e.id = v.beneficiary_id " +
//            "WHERE (e.beneficiary_ugpass_email = ?1 " +
//            "       OR e.beneficiary_passport = ?2 " +
//            "       OR e.beneficiary_nin = ?3 " +
//            "       OR e.beneficiary_mobile_number = ?4 " +
//            "       OR e.beneficiary_digital_id = ?5) "+
//            "AND e.status = 'ACTIVE' " +
//            "AND ((v.validity_applicable = 1 AND CURRENT_DATE BETWEEN v.valid_from AND v.valid_upto) OR v.validity_applicable = 0) " +
//            "AND v.privilege_service_id = 3 " +
//            "AND v.status = 'ACTIVE'",
//            nativeQuery = true)
//
//    List<Benificiaries> findByEmailOrPassportOrNinOrMobileNumberorBeneficiaryDigitalId(String email, String passport,String nin, String mobileNumber,String beneficiaryDigitalId);
//
//    @Query(value =
//            "SELECT * FROM beneficiaries  WHERE  status='ACTIVE' AND (beneficiary_ugpass_email =?1  or beneficiary_passport =?2 or beneficiary_nin =?3 or beneficiary_mobile_number =?4  or beneficiary_digital_id =?5)",
//            nativeQuery = true)
//    List<Benificiaries> findAllSponsor(String email, String passport,String nin, String mobileNumber,String BeneficiaryDigitalId);
//
//
//    @Modifying
//    @Transactional
//    @Query(value = "update beneficiaries set status='ACTIVE' where id=?1",nativeQuery = true)
//    int changeStatusForSSP(int Id);
//


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryType = 'INDIVIDUAL' ORDER BY b.id DESC")
    List<Benificiaries> getAllBeneficiariesBySponsor(String sponsorId);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 ORDER BY b.id DESC")
    List<Benificiaries> getAllBeneficiariesBySponsorByDigitalId(String sponsorId);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND (b.beneficiaryMobileNumber = ?2 OR b.beneficiaryPassport = ?3 OR b.beneficiaryOfficeEmail = ?4)")
    List<Benificiaries> findDuplicateBeneficiariesByPassport(String sponsorId, String number, String passport, String officeEmail);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND (b.beneficiaryMobileNumber = ?2 OR b.beneficiaryNin = ?3 OR b.beneficiaryOfficeEmail = ?4)")
    List<Benificiaries> findDuplicateBeneficiariesByNIN(String sponsorId, String number, String NIN, String officeEmail);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryUgPassEmail = ?2")
    Benificiaries findDuplicateBeneficiariesByUgPassEmail(String sponsorId, String ugPassEmail);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryMobileNumber = ?2")
    Benificiaries findDuplicateBeneficiariesByMobileNumber(String sponsorId, String mobileNumber);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryPassport = ?2")
    Benificiaries findDuplicateBeneficiariesByPassport(String sponsorId, String passport);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryNin = ?2")
    Benificiaries findDuplicateBeneficiariesByNIN(String sponsorId, String NIN);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryOfficeEmail = ?2")
    Benificiaries findDuplicateBeneficiariesByOfficeEmail(String sponsorId, String OfficeEmail);


    @Query("SELECT b FROM Benificiaries b WHERE b.sponsorDigitalId = ?1 AND b.beneficiaryDigitalId = ?2")
    Benificiaries findDuplicateBeneficiariesByBeneficiaryDigitalId(String sponsorId, String beneficiaryDigitalId);


    @Modifying
    @Transactional
    @Query("UPDATE Benificiaries b SET b.status = 'INACTIVE' WHERE b.id = ?1")
    int changeStatusById(int Id);


//    @Query("SELECT b FROM Benificiaries b JOIN BeneficiaryValidity v ON b.id = v.beneficiaryId " +
//            "WHERE (b.beneficiaryUgPassEmail = ?1 OR b.beneficiaryPassport = ?2 OR b.beneficiaryNin = ?3 OR b.beneficiaryMobileNumber = ?4 OR b.beneficiaryDigitalId = ?5) " +
//            "AND b.status = 'ACTIVE' " +
//            "AND ((v.validityApplicable = true AND CURRENT_DATE BETWEEN v.validFrom AND v.validUpTo) OR v.validityApplicable = false) " +
//            "AND v.privilegeServiceId = 3 AND v.status = 'ACTIVE'")
//    List<Benificiaries> findByEmailOrPassportOrNinOrMobileNumberorBeneficiaryDigitalId(String email, String passport, String nin, String mobileNumber, String beneficiaryDigitalId);

    @Query("SELECT e FROM Benificiaries e " +
            "JOIN BeneficiaryValidity v ON v.beneficiaryId = e.id " +
            "WHERE (e.beneficiaryUgPassEmail = :email " +
            "       OR e.beneficiaryPassport = :passport " +
            "       OR e.beneficiaryNin = :nin " +
            "       OR e.beneficiaryMobileNumber = :mobileNumber " +
            "       OR e.beneficiaryDigitalId = :beneficiaryDigitalId) " +
            "AND e.status = 'ACTIVE' " +
            "AND ((v.validityApplicable = TRUE AND CURRENT_DATE BETWEEN " +
            "       FUNCTION('TO_DATE', v.validFrom, 'YYYY-MM-DD') AND FUNCTION('TO_DATE', v.validUpTo, 'YYYY-MM-DD')) " +
            "       OR v.validityApplicable = FALSE) " +
            "AND v.privilegeServiceId = 3 " +
            "AND v.status = 'ACTIVE'")
    List<Benificiaries> findByEmailOrPassportOrNinOrMobileNumberorBeneficiaryDigitalId(
            @Param("email") String email,
            @Param("passport") String passport,
            @Param("nin") String nin,
            @Param("mobileNumber") String mobileNumber,
            @Param("beneficiaryDigitalId") String beneficiaryDigitalId);


    @Query("SELECT b FROM Benificiaries b " +
            "WHERE b.status = 'ACTIVE' " +
            "AND (b.beneficiaryUgPassEmail = :email " +
            "     OR b.beneficiaryPassport = :passport " +
            "     OR b.beneficiaryNin = :nin " +
            "     OR b.beneficiaryMobileNumber = :mobileNumber " +
            "     OR b.beneficiaryDigitalId = :beneficiaryDigitalId)")
    List<Benificiaries> findAllSponsor(@Param("email") String email,
                                       @Param("passport") String passport,
                                       @Param("nin") String nin,
                                       @Param("mobileNumber") String mobileNumber,
                                       @Param("beneficiaryDigitalId") String beneficiaryDigitalId);

    @Modifying
    @Transactional
    @Query("UPDATE Benificiaries b SET b.status = 'ACTIVE' WHERE b.id = ?1")
    int changeStatusForSSP(int Id);




}