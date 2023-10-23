package fr.sqli.cantine.controller.admin.adminDashboard.account;

import fr.sqli.cantine.dto.in.person.AdminDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.service.admin.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.admin.exceptions.AdminNotFound;
import fr.sqli.cantine.service.admin.exceptions.ExistingAdminException;
import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface IAdminController {

    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin/adminDashboard";

   String  ADMIN_DASH_BOARD_CHECK_TOKEN_VALIDITY = "/checkTokenValidity";
   String ADMIN_DASH_BOARD_VALIDATE_EMAIL_ENDPOINT = "/sendToken";
    String ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT = "/getAdmin";
    String ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT =  "/signUp";

    String  ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT = "/updateAdmin/info";

    String  ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT = "/disableAdmin";

    String  ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT ="/getAllAdminFunctions";


    /**************************** SERVER ANSWERS ************************************/

    String  TOKEN_VALID = "TOKEN VALID";


   String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";

   String  ADMIN_INFO_UPDATED_SUCCESSFULLY = "ADMIN UPDATED SUCCESSFULLY";
   String ADMIN_DISABLED_SUCCESSFULLY = "ADMIN DISABLED SUCCESSFULLY";


/*
   @GetMapping(ADMIN_DASH_BOARD_CHECK_TOKEN_VALIDITY)
    public  ResponseEntity<ResponseDtout>checkTokenValidity(@RequestParam("token") String  token) throws InvalidTokenException, ExpiredToken, AdminNotFound;
*/


/*
    @PostMapping(ADMIN_DASH_BOARD_VALIDATE_EMAIL_ENDPOINT)
    ResponseEntity<ResponseDtout> sendToken(@RequestParam("email") String email) throws InvalidPersonInformationException, AdminNotFound, MessagingException, AccountAlreadyActivatedException;
*/

   ResponseEntity<String> disableAdmin(@RequestParam("idAdmin") Integer idAdmin) throws AdminNotFound, InvalidPersonInformationException;
   ResponseEntity<AdminDtout>getAdminById(@RequestParam("idAdmin")  Integer idAdmin) throws AdminNotFound, InvalidPersonInformationException;

    @PostMapping(ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT)
    ResponseEntity<ResponseDtout> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingAdminException, AdminFunctionNotFoundException;

    ResponseEntity<String> updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException;


    @GetMapping(ADMIN_DASH_BOARD_GET_ALL_ADMIN_FUNCTIONS_ENDPOINT)
     ResponseEntity<List<FunctionDtout>> getAllAdminFunctions() ;
}
