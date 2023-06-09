package fr.sqli.Cantine.controller.admin.adminDashboard.account;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.dto.out.person.AdminDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminFunctionNotFoundException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public interface IAdminController {

    String ADMIN_DASH_BOARD_BASIC_URL = "/cantine/admin/adminDashboard";


   String ADMIN_DASH_BOARD_VALIDATE_EMAIL_ENDPOINT = "/sendToken";
    String ADMIN_DASH_BOARD_GET_ADMIN_BY_ID_ENDPOINT = "/getAdmin";
    String ADMIN_DASH_BOARD_SIGN_UP_ENDPOINT =  "/signUp";

    String  ADMIN_DASH_BOARD_UPDATE_ADMIN_ENDPOINT = "/updateAdmin/info";

    String  ADMIN_DASH_BOARD_DISABLE_ADMIN_ENDPOINT = "/disableAdmin";



    String TOKEN_SENDED_SUCCESSFULLY =  "TOKEN SENDED SUCCESSFULLY";

   String ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";

   String  ADMIN_INFO_UPDATED_SUCCESSFULLY = "ADMIN UPDATED SUCCESSFULLY";
   String ADMIN_DISABLED_SUCCESSFULLY = "ADMIN DISABLED SUCCESSFULLY";


    ResponseEntity<String> sendToken(@RequestParam("email") String email) throws InvalidPersonInformationException, AdminNotFound, MessagingException;

   ResponseEntity<String> disableAdmin(@RequestParam("idAdmin") Integer idAdmin) throws AdminNotFound, InvalidPersonInformationException;
   ResponseEntity<AdminDtout>getAdminById(@RequestParam("idAdmin")  Integer idAdmin) throws AdminNotFound, InvalidPersonInformationException;ResponseEntity<String> signUp(@ModelAttribute AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingAdminException, AdminFunctionNotFoundException;

    ResponseEntity<String> updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException;

}
