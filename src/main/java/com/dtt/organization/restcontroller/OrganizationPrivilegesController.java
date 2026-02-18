package com.dtt.organization.restcontroller;


import com.dtt.organization.constant.ApiResponses;
//import com.dtt.organization.dto.OrganisationPrivilegesDto;

import com.dtt.organization.dto.OrganisationPrivilegesRequestDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegeDto;
import com.dtt.organization.dto.UpdateOrganizationPrivilegesListDto;
import com.dtt.organization.repository.WalletSignCertRepo;
import com.dtt.organization.service.iface.OrganizationPrivilegesIface;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrganizationPrivilegesController {


   private final WalletSignCertRepo walletSignCertRepo;

    private final OrganizationPrivilegesIface organizationPrivilegesIface;

    public OrganizationPrivilegesController(WalletSignCertRepo walletSignCertRepo, OrganizationPrivilegesIface organizationPrivilegesIface) {
        this.walletSignCertRepo = walletSignCertRepo;
        this.organizationPrivilegesIface = organizationPrivilegesIface;
    }
//
//    @PostMapping("/api/create/privileges")
//    public ApiResponse createPrivileges(@RequestBody OrganisationPrivilegesDto organisationPrivilegesDto){
//
//            return organizationPrivilegesIface.createPrivileges(organisationPrivilegesDto);
//
//    }
//
//    @PutMapping("/api/update/privileges")
//    public ApiResponse updatePrivileges(@RequestBody OrganisationPrivilegesDto organisationPrivilegesDto){
//
//            return organizationPrivilegesIface.updatePrivileges(organisationPrivilegesDto);
//
//    }
//
//    @GetMapping("/api/get/privileges")
//    public ApiResponse getPrivileges() {
//        return organizationPrivilegesIface.getPrivileges();
//    }
//
//    @GetMapping("/api/get/privileges/{id}")
//    public ApiResponse getPrivileges(@PathVariable int id) {
//        return organizationPrivilegesIface.getPrivilegesById(id);
//    }
//
//
//    @DeleteMapping("/api/delete/privilege/{id}")
//    public ApiResponse deletePrivilege(@PathVariable int id){
//        return organizationPrivilegesIface.deletePrivilege(id);
//    }
//
//
@GetMapping("/api/get/privilege/by/orgId/{orgId}")
    public ApiResponses getPrivilegesByOrgId(@PathVariable String orgId){
    return organizationPrivilegesIface.getPrivilegesByOrgId(orgId);
}



@PostMapping("/api/request/organization/privilege")
    public ApiResponses requestPrivilege(@RequestBody OrganisationPrivilegesRequestDto organisationPrivilegesRequestDto){

    return organizationPrivilegesIface.requestPrivilege(organisationPrivilegesRequestDto);
    }


    @PostMapping("/api/update/organization/privilege")
    public ApiResponses updatePrivilege(@RequestBody UpdateOrganizationPrivilegeDto updateOrganizationPrivilegeDto){
        return organizationPrivilegesIface.updatePrivilege(updateOrganizationPrivilegeDto);
    }

    @GetMapping("/api/get/all/privileges")
    public ApiResponses getAllPrivileges(){
        return organizationPrivilegesIface.getAllPrivileges();
    }

    @GetMapping("/api/get/privilege/by/id/{id}")
    public ApiResponses getPrivilegeById(@PathVariable("id") int id){
        return organizationPrivilegesIface.getOrganizationPrivilegeById(id);
    }

    @GetMapping("/api/get/privileges/by/organization/{orgId}")
    public ApiResponses getPrivilegesByOrganization(@PathVariable("orgId") String orgId){
        return organizationPrivilegesIface.getPrivilegesByOrganization(orgId);
    }


    @PostMapping("/api/update/organization/privileges")
    public ApiResponses updateOrganizationPrivilegeList(@RequestBody UpdateOrganizationPrivilegesListDto updateOrganizationPrivilegesListDto){
        return organizationPrivilegesIface.updateOrganizationPrivilegeList(updateOrganizationPrivilegesListDto);

    }

    @GetMapping("/api/get/privileges")
    public ApiResponses getPrivilegesNames(){
        return organizationPrivilegesIface.getPrivilegesNames();
    }








}
