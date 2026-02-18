package com.dtt.organization.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dtt.organization.model.OrganizationCertificates;


@Repository
public interface OrganizationCertificatesRepository extends JpaRepository<OrganizationCertificates, String> {


	@Query("SELECT o FROM OrganizationCertificates o WHERE o.organizationUid = ?1")
	OrganizationCertificates findByorganization_uid(String organization_uid);

	@Query("SELECT o FROM OrganizationCertificates o WHERE o.certificateStatus = ?1 AND o.organizationUid = ?2")
	OrganizationCertificates findByCertificateStatusAndOrganizationUniqueId(String certificateStatus, String organizationUid);

	@Query("SELECT o FROM OrganizationCertificates o WHERE o.certificateStatus = ?1 AND o.organizationUid = ?2")
	List<OrganizationCertificates> findByCertificateStatusAndOrganizationUid(String certificateStatus, String certificates);


	@Query("SELECT o FROM OrganizationCertificates o WHERE o.certificateEndDate <= CURRENT_DATE AND o.certificateStatus = 'ACTIVE'")
	List<OrganizationCertificates> findByCertificateStatusExpired();


	@Query("SELECT o FROM OrganizationCertificates o WHERE o.organizationUid = ?1")
	OrganizationCertificates getOrganizationDetails(String organization_uid);

	//	// Native - uses LIMIT and ORDER BY
//	@Query(value = "SELECT * FROM organization_certificates i WHERE i.organization_uid = ?1 ORDER BY i.certificate_issue_date DESC LIMIT 1", nativeQuery = true)
//	OrganizationCertificates findByorganizationUid(String organization_uid);
	OrganizationCertificates findTop1ByOrganizationUidOrderByCertificateStartDateDesc(String organizationUid);
	default OrganizationCertificates findByorganizationUid(String organizationUid) {
		return findTop1ByOrganizationUidOrderByCertificateStartDateDesc(organizationUid);
	}

	// No query needed, JPA can derive
	OrganizationCertificates findByTransactionReferenceId(String transactionReferenceId);

	@Query("SELECT o FROM OrganizationCertificates o " +
			"WHERE o.certificateEndDate >= CURRENT_DATE " +
			"AND o.certificateEndDate <= :futureDate " +
			"AND o.certificateStatus = 'ACTIVE'")
	List<OrganizationCertificates> findByOrganizationCertificateStatusExpired(@Param("futureDate") LocalDate futureDate);

	// Default method to call JPQL with fixed interval (100 days)
	default List<OrganizationCertificates> findByOrganizationCertificateStatusExpired() {
		LocalDate futureDate = LocalDate.now().plusDays(100);
		return findByOrganizationCertificateStatusExpired(futureDate);
	}


}
