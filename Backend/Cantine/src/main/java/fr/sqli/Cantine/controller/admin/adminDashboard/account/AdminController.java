package fr.sqli.Cantine.controller.admin.adminDashboard.account;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.dto.out.ResponseDtout;
import fr.sqli.Cantine.dto.out.person.AdminDtout;
import fr.sqli.Cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.account.AdminService;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminFunctionNotFoundException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.Cantine.controller.admin.adminDashboard.account.IAdminController.ADMIN_DASH_BOARD_BASIC_URL;

@RestController
@RequestMapping(ADMIN_DASH_BOARD_BASIC_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController  implements IAdminController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @Override
    public ResponseEntity<ResponseDtout> sendToken(@RequestParam("email") String email) throws InvalidPersonInformationException, AdminNotFound, MessagingException, AccountAlreadyActivatedException {
        this.adminService.sendToken(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENDED_SUCCESSFULLY));
    }
    @Override
    @PutMapping(ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT)
    public ResponseEntity<String> disableAdmin(@RequestParam("idAdmin")  Integer idAdmin) throws AdminNotFound, InvalidPersonInformationException {
       this.adminService.disableAdminAccount(idAdmin);
        return ResponseEntity.ok(ADMIN_DISABLED_SUCCESSFULLY);
    }

    @Override
    @GetMapping(ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT)
    public ResponseEntity<AdminDtout> getAdminById(@RequestParam("idAdmin") Integer idAdmin) throws AdminNotFound, InvalidPersonInformationException {
        return ResponseEntity.ok(this.adminService.getAdminById(idAdmin));
    }

    @Override
     @PutMapping(ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT)
    public ResponseEntity<String> updateAdminInfo(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException {
       this.adminService.updateAdminInfo(adminDtoIn);
            return ResponseEntity.ok(ADMIN_INFO_UPDATED_SUCCESSFULLY);
    }

    @Override
    public ResponseEntity<ResponseDtout> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingAdminException, AdminFunctionNotFoundException {
        this.adminService.signUp(adminDtoIn);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_ADDED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<FunctionDtout>> getAllAdminFunctions() {
        return ResponseEntity.ok(this.adminService.getAllAdminFunctions());
    }


}
