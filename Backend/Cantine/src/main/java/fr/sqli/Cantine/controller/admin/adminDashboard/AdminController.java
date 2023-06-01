package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.dto.out.person.AdminDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.account.AdminService;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static fr.sqli.Cantine.controller.admin.adminDashboard.IAdminDashboardController.ADMIN_DASH_BOARD_BASIC_URL;

@RestController
@RequestMapping(ADMIN_DASH_BOARD_BASIC_URL)
public class AdminController  implements IAdminDashboardController {

    private AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @Override
    @PostMapping(ADMIN_DASH_BOARD_VALIDATE_EMAIL_ENDPOINT)
    public ResponseEntity<String> sendToken(@RequestParam("email") String email) throws InvalidPersonInformationException, AdminNotFound, MessagingException {
         this.adminService.sendToken(email);
        return ResponseEntity.ok(TOKEN_SENDED_SUCCESSFULLY);
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
    public ResponseEntity<String> updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException,InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound {
       this.adminService.updateAdminInfo(adminDtoIn);
            return ResponseEntity.ok(ADMIN_INFO_UPDATED_SUCCESSFULLY);
    }

    @Override
    @PostMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
    public ResponseEntity<String> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingAdminException {
        this.adminService.signUp(adminDtoIn);
        return ResponseEntity.ok(ADMIN_ADDED_SUCCESSFULLY);
    }


}
