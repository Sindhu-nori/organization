package com.dtt.organization.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dtt.organization.constant.ApiResponses;
import com.dtt.organization.service.iface.EGPVerifyVendorIFace;

@RestController
public class EGPVerifyVendorController {
	
	@Autowired
    EGPVerifyVendorIFace eGPVerifyVendorIFace;

	@GetMapping({"/api/verify-vendor-id"})
    public ApiResponses verifyByEgpForVendor(@RequestParam String vendorId, @RequestParam String orgid) {
        return eGPVerifyVendorIFace.verifyByEgpForVendor(vendorId, orgid);
    }
}
