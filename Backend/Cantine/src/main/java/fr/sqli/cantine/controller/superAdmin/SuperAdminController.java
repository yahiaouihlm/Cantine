package fr.sqli.cantine.controller.superAdmin;


import fr.sqli.cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.cantine.dto.in.superAdmin.TaxDtoIn;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.superAdmin.SuperAdminService;
import fr.sqli.cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.cantine.service.superAdmin.exception.InvalidTaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static fr.sqli.cantine.controller.superAdmin.SuperAdminController.BASIC_SUPER_ADMIN_URL;

@RestController
@RequestMapping(BASIC_SUPER_ADMIN_URL)
public class SuperAdminController {
    public static final String BASIC_SUPER_ADMIN_URL = "/cantine/superAdmin";
    private  final SuperAdminService superAdminService;
    @Autowired
    public SuperAdminController (SuperAdminService superAdminService){
        this.superAdminService = superAdminService;
    }


    @PostMapping("/addFunction")
    public ResponseEntity<String> addFunction (@RequestBody FunctionDtoIn functionDtoIn) throws InvalidUserInformationException {
        this.superAdminService.addFunction(functionDtoIn);
        return ResponseEntity.ok("function added successfully");
    }


    @PostMapping("/addTax")
    public ResponseEntity<String> addTax (@RequestBody TaxDtoIn taxDtoIn) throws InvalidTaxException, ExistingTax {
        this.superAdminService.addTax(taxDtoIn);
        return ResponseEntity.ok("tax added successfully");
    }
}
