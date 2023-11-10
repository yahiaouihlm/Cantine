package fr.sqli.cantine.controller.users.admin.account;

import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface IAdminController {

    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin";

    String SEND_CONFIRMATION_LINK_ENDPOINT = "/sendConfirmationLink";
    String CHECK_TOKEN_VALIDITY_ENDPOINT = "/checkTokenValidity";
   String  ADMIN_DASH_BOARD_CHECK_TOKEN_VALIDITY = "/checkTokenValidity";
   String ADMIN_DASH_BOARD_VALIDATE_EMAIL_ENDPOINT = "/sendToken";
    String ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT = "/getAdmin";
    String ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT =  "/register";

    String  ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT = "/updateAdmin/info";

    String  ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT = "/disableAdmin";

    String  ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT ="/getAllAdminFunctions";


    /**************************** SERVER ANSWERS ************************************/

    String  TOKEN_VALID = "TOKEN VALID";



    String TOKEN_CHECKED_SUCCESSFULLY = "TOKEN CHECKED SUCCESSFULLY";
    String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";

   String  ADMIN_INFO_UPDATED_SUCCESSFULLY = "ADMIN UPDATED SUCCESSFULLY";
   String ADMIN_DISABLED_SUCCESSFULLY = "ADMIN DISABLED SUCCESSFULLY";

    String TOKEN_SENT_SUCCESSFULLY = "TOKEN SENT SUCCESSFULLY";



    @PostMapping(CHECK_TOKEN_VALIDITY_ENDPOINT)
    ResponseEntity<ResponseDtout>checkLinkValidity(@RequestParam("token") String  token) throws UserNotFoundException, InvalidTokenException, ExpiredToken, TokenNotFoundException;

    @PostMapping(SEND_CONFIRMATION_LINK_ENDPOINT)
    ResponseEntity<ResponseDtout>sendConfirmationLinkForAdminEmail (@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;

    @PostMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
    ResponseEntity<ResponseDtout> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, ExistingUserException, UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;



    ResponseEntity<ResponseDtout> disableAdmin(@RequestParam("idAdmin") Integer idAdmin) throws InvalidUserInformationException, UserNotFoundException;

   ResponseEntity<AdminDtout>getAdminById(@RequestParam("idAdmin")  Integer idAdmin) throws InvalidUserInformationException, UserNotFoundException;

    ResponseEntity<String> updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException, AdminFunctionNotFoundException;


    @GetMapping(ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT)
     ResponseEntity<List<FunctionDtout>> getAllAdminFunctions() ;
}
