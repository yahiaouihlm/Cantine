package fr.sqli.cantine.controller.users.admin;


import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.service.users.admin.impl.AdminService;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;
import static fr.sqli.cantine.controller.users.admin.AdminController.ADMIN_DASH_BOARD_BASIC_URL;

@RestController
@RequestMapping(ADMIN_DASH_BOARD_BASIC_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    public static final String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin";
    final String ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT = "/getAdmin";
    final String ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT = "/register";
    final String ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT = "/updateAdmin/info";
    final String ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT = "/removeAccount";
    final String ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT = "/getAllAdminFunctions";

    /**************************** SERVER ANSWERS ************************************/
    final String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";
    final String ADMIN_INFO_UPDATED_SUCCESSFULLY = "ADMIN UPDATED SUCCESSFULLY";
    final String ADMIN_DISABLED_SUCCESSFULLY = "ADMIN DISABLED SUCCESSFULLY";

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT)
    public ResponseEntity<ResponseDtout> removeAdminAccount(@RequestParam("adminUuid") String adminUuid) throws InvalidUserInformationException, UserNotFoundException {
        this.adminService.removeAdminAccount(adminUuid);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_DISABLED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @PostMapping(ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT)
    public ResponseEntity<ResponseDtout> updateAdminInfo(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException, AdminFunctionNotFoundException {
        this.adminService.updateAdminInfo(adminDtoIn);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_INFO_UPDATED_SUCCESSFULLY));
    }

    @PreAuthorize("hasAuthority('" + ADMIN_ROLE_LABEL + "')")
    @GetMapping(ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT)
    public ResponseEntity<AdminDtout> getAdminByUuID(@RequestParam("adminUuid") String adminUuid) throws InvalidUserInformationException, UserNotFoundException {
        return ResponseEntity.ok(this.adminService.getAdminByUuID(adminUuid));
    }

    @PostMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
    public ResponseEntity<ResponseDtout> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingUserException, UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException, AdminFunctionNotFoundException {
        this.adminService.signUp(adminDtoIn);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_ADDED_SUCCESSFULLY));
    }

    @GetMapping(ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT)
    public ResponseEntity<List<FunctionDtout>> getAllAdminFunctions() {
        return ResponseEntity.ok(this.adminService.getAllAdminFunctions());
    }


}
