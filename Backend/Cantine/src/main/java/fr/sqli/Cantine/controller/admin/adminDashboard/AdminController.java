package fr.sqli.Cantine.controller.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.AdminService;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
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
     @PutMapping(ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT)
    public ResponseEntity<String> updateAdminInfo(AdminDtoIn adminDtoIn, @RequestParam("idAdmin") Integer idAdmin) throws InvalidPersonInformationException,InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound {
       this.adminService.updateAdminInfo(adminDtoIn, idAdmin);
            return ResponseEntity.ok(ADMIN_ADDED_SUCCESSFULLY);
    }

    @Override
    @PostMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
    public ResponseEntity<String> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingAdminException {
        this.adminService.signUp(adminDtoIn);
        return ResponseEntity.ok(ADMIN_ADDED_SUCCESSFULLY);
    }


}
