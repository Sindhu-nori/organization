package com.dtt.organization.service.iface;

import com.dtt.organization.constant.ApiResponses;
//import com.dtt.organization.dto.OrganisationPrivilegesDto;
import com.dtt.organization.dto.OrganisationPrivilegesRequestDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegeDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegesListDto;

public interface OrganizationPrivilegesIface {

//    ApiResponse createPrivileges(OrganisationPrivilegesDto organisationPrivilegesDto);
//
//    ApiResponse updatePrivileges(OrganisationPrivilegesDto organisationPrivilegesDto);
//
//    ApiResponse getPrivileges();
//
//    ApiResponse getPrivilegesById(int id);
//
//    ApiResponse deletePrivilege(int id);
//
    ApiResponses getPrivilegesByOrgId(String orgId);

    ApiResponses requestPrivilege(OrganisationPrivilegesRequestDto organisationPrivilegesRequestDto);

    ApiResponses updatePrivilege(UpdateOrganizationPrivilegeDto updateOrganizationPrivilegeDto);

    ApiResponses getAllPrivileges();

    ApiResponses getOrganizationPrivilegeById(int id);

    ApiResponses getPrivilegesByOrganization(String orgId);

    ApiResponses updateOrganizationPrivilegeList(UpdateOrganizationPrivilegesListDto updateOrganizationPrivilegesListDto);

    ApiResponses getPrivilegesNames();
}
