package fr.sqli.cantine.controller.users.admin.Impl;

import fr.sqli.cantine.controller.users.admin.IAdminController;
import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.service.users.admin.impl.AdminService;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.controller.users.admin.IAdminController.ADMIN_DASH_BOARD_BASIC_URL;

@RestController
@RequestMapping(ADMIN_DASH_BOARD_BASIC_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController implements IAdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public ResponseEntity<ResponseDtout> disableAdmin(String adminUuid) throws InvalidUserInformationException, UserNotFoundException {
        this.adminService.disableAdminAccount(adminUuid);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_DISABLED_SUCCESSFULLY));
    }

    @Override
    @PutMapping(ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT)
    public ResponseEntity<ResponseDtout> updateAdminInfo(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException, AdminFunctionNotFoundException {
        this.adminService.updateAdminInfo(adminDtoIn);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_INFO_UPDATED_SUCCESSFULLY));
    }

    @Override
    @GetMapping(ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT)
    public ResponseEntity<AdminDtout> getAdminByUuID(@RequestParam("adminUuid") String adminUuid) throws InvalidUserInformationException, UserNotFoundException {
        return ResponseEntity.ok(this.adminService.getAdminByUuID(adminUuid));
    }

    @Override
    public ResponseEntity<ResponseDtout> checkLinkValidity(String token) throws UserNotFoundException, InvalidTokenException, ExpiredToken, TokenNotFoundException {
        this.adminService.checkLinkValidity(token);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_CHECKED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> signUp(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingUserException, UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException, AdminFunctionNotFoundException {
        this.adminService.signUp(adminDtoIn);
        return ResponseEntity.ok(new ResponseDtout(ADMIN_ADDED_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<ResponseDtout> sendConfirmationLinkForAdminEmail(String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException {
        this.adminService.sendConfirmationLink(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }


    /*------------------------------------------------ pas  encore  fait --------------------------------------------*/


    @Override
    public ResponseEntity<List<FunctionDtout>> getAllAdminFunctions() {
        return ResponseEntity.ok(this.adminService.getAllAdminFunctions());
    }


}
