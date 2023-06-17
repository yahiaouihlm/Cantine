package fr.sqli.Cantine.controller.superAdmin;


import fr.sqli.Cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.Cantine.dto.in.superAdmin.TaxDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.superAdmin.SuperAdminService;
import fr.sqli.Cantine.service.superAdmin.exception.ExistingTax;
import fr.sqli.Cantine.service.superAdmin.exception.InvalidTaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cantine/superAdmin")
public class SuperAdminController {

    private  final SuperAdminService superAdminService;
    @Autowired
    public SuperAdminController (SuperAdminService superAdminService){
        this.superAdminService = superAdminService;
    }


    @PostMapping("/addFunction")
    public ResponseEntity<String> addFunction (@RequestBody FunctionDtoIn functionDtoIn) throws InvalidPersonInformationException {
        this.superAdminService.addFunction(functionDtoIn);
        return ResponseEntity.ok("function added successfully");
    }


    @PostMapping("/addTax")
    public ResponseEntity<String> addTax (@RequestBody TaxDtoIn taxDtoIn) throws InvalidTaxException, ExistingTax {
        this.superAdminService.addTax(taxDtoIn);
        return ResponseEntity.ok("tax added successfully");
    }



}
