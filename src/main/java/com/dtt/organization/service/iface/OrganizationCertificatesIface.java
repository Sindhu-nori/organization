package com.dtt.organization.service.iface;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.dto.IssueCertificateDTO;
import com.dtt.organization.dto.OrganizationIssueCetificatesDto;
import com.dtt.organization.dto.RARequestDTO;
import com.dtt.organization.request.entity.GenerateSignature;
import com.dtt.organization.response.entity.APIResponse;


public interface OrganizationCertificatesIface {

	/**
	 * Issue organization certificates.
	 *
	 * @return the string
	 * @throws Exception
	 */
	public String issueOrganizationCertificates(String organizationId, Boolean isPostPaid) throws Exception;
	
	public String revokeCertificate(RARequestDTO requestBody) throws  Exception;
	
	public String checkCertificateStatus() throws Exception;
	
	
	/**
	 * Generate signature.
	 *
	 //* @param setPin            the set pin
	 * @return the string
	 * @throws RAServiceException the RA service exception
	 * @throws Exception             the exception
	 */
	public String generateSignature(GenerateSignature generateSignature) throws Exception;
	
	/**
	 * Issue E-seal certificate
	 * 
	 * @return the string
	 * @throws Exception
	*/
	public String issueOrganizationCertificatesNew(OrganizationIssueCetificatesDto cetificatesDto) throws Exception;
	
	
	/**
	 * Issue wallet certificate.
	 * 
	 * @return the string
	 * @throws Exception
	 */
	public String issueWalletOrganizationCertificates(OrganizationIssueCetificatesDto cetificatesDto) throws Exception;


	ApiResponses sendEmailEsealCertificateGenerated(String orgId);

	ApiResponses getAllOrganizationsAndCert(String orgId);

	ApiResponses getWalletCertByOuid(String ouid);



	APIResponse getPaymentDetailsForWallet(IssueCertificateDTO issueCertificateDTO);

}
