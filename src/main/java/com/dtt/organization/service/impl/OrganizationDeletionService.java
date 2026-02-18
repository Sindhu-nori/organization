package com.dtt.organization.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrganizationDeletionService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void deleteOrganization(String ouid) {
        String[] deleteQueries = {
                "DELETE FROM OrgContactsEmail e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationEmailDomain e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationPricingSlabDefinitions e WHERE e.organizationId = :ouid",
                "DELETE FROM OrganizationDocuments e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationSignatureTemplates e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationDirectors e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationDocumentsCheckBox e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationCertificates e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationCertificateLifeCycle e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationStatus e WHERE e.organizationUid = :ouid",
                "DELETE FROM SoftwareLicenseApprovalRequests e WHERE e.ouid = :ouid",
                "DELETE FROM WalletSignCertificate e WHERE e.organizationUid = :ouid",
                "DELETE FROM SoftwareLicenses e WHERE e.ouid = :ouid",
                "DELETE FROM OrgSubscriberEmailOld e WHERE e.organizationUid = :ouid",
                "DELETE FROM OrganizationDetails e WHERE e.organizationUid = :ouid"
        };

        for (String query : deleteQueries) {
            entityManager.createQuery(query)
                    .setParameter("ouid", ouid)
                    .executeUpdate();
        }
    }
}

//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class OrganizationDeletionService {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//      @Transactional
//    public void deleteOrganization(String ouid) {
//        String[] deleteQueries = {
//
//                "DELETE FROM OrgContactsEmail s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationEmailDomain s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationPricingSlabDefinitions s WHERE s.organizationId = :ouid",
//                "DELETE FROM OrganizationDocuments s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationSignatureTemplates s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationDirectors s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationDocumentsCheckBox s  WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationCertificates s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationCertificateLifeCycle s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationStatus s WHERE s.organizationUid = :ouid",
//
//                "DELETE FROM SoftwareLicenseApprovalRequests s WHERE s.ouid = :ouid",
//                "DELETE FROM WalletSignCertificate w WHERE w.organizationUid = :ouid",
//                "DELETE FROM SoftwareLicenses s WHERE s.ouid = :ouid",
//                "DELETE FROM OrgSubscriberEmailOld s WHERE s.organizationUid = :ouid",
//                "DELETE FROM OrganizationDetails s WHERE s.organizationUid = :ouid"
//        };
//
//        for (String query : deleteQueries) {
//            jdbcTemplate.update(query, ouid);
//        }
//    }
//}
