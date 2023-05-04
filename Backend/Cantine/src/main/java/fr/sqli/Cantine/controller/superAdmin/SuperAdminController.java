package fr.sqli.Cantine.controller.superAdmin;


import fr.sqli.Cantine.dto.in.superAdmin.FunctionDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.superAdmin.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cantine/superAdmin")
public class SuperAdminController {

    private  final SuperAdminService superAdminService;
    @Autowired
    public SuperAdminController (SuperAdminService superAdminService){
        this.superAdminService = superAdminService;
    }


    @RequestMapping("/addFunction")
    public ResponseEntity<String> addFunction (@RequestBody FunctionDtoIn functionDtoIn) throws InvalidPersonInformationException {
        this.superAdminService.addFunction(functionDtoIn);
        return ResponseEntity.ok("function added successfully");
    }


}
