package fr.sqli.cantine.controller.users.admin;

import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

public interface IAdminController {

    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin";

    String ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT = "/getAdmin";
    String ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT =  "/register";

    String  ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT = "/updateAdmin/info";

    String  ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT = "/disableAccount";

    String  ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT ="/getAllAdminFunctions";


    /**************************** SERVER ANSWERS ************************************/


    String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";

   String  ADMIN_INFO_UPDATED_SUCCESSFULLY = "ADMIN UPDATED SUCCESSFULLY";
   String ADMIN_DISABLED_SUCCESSFULLY = "ADMIN DISABLED SUCCESSFULLY";



    @PutMapping(ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT)
    ResponseEntity<ResponseDtout> updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException, AdminFunctionNotFoundException;

    @GetMapping(ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT)
    ResponseEntity<AdminDtout> getAdminByUuID(@RequestParam("adminUuid")  String adminUuid) throws InvalidUserInformationException, UserNotFoundException;

    @PostMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
    ResponseEntity<ResponseDtout> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, ExistingUserException, UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException;



    @PostMapping(ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT)
    ResponseEntity<ResponseDtout> disableAdmin(@RequestParam("adminUuid") String adminUuid) throws InvalidUserInformationException, UserNotFoundException;



    @GetMapping(ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT)
     ResponseEntity<List<FunctionDtout>> getAllAdminFunctions() ;
}
