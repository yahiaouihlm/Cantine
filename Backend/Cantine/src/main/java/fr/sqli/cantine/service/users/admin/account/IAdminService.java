package fr.sqli.cantine.service.users.admin.account;

import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.service.users.admin.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.users.admin.exceptions.AdminNotFound;
import fr.sqli.cantine.service.users.admin.exceptions.ExistingAdminException;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.student.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface IAdminService {


    /* String  checkTokenValidity(String token) throws InvalidPersonInformationException, AdminNotFound, InvalidTokenException, ExpiredToken;*/

   void   checkLinkValidity(String token) throws InvalidTokenException, TokenNotFoundException, ExpiredToken, UserNotFoundException;
    void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountAlreadyActivatedException, MessagingException;
     void disableAdminAccount(Integer idAdmin) throws InvalidUserInformationException, AdminNotFound;

    AdminDtout getAdminById (Integer idAdmin) throws InvalidUserInformationException, AdminNotFound;
     void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException;
     void signUp (AdminDtoIn  adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingUserException, AdminFunctionNotFoundException, UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;
    List<FunctionDtout> getAllAdminFunctions() ;
    //public void sendToken(String email) throws AdminNotFound, InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException;
    void existingAdmin(String  adminEmail ) throws ExistingAdminException, ExistingUserException;

    static   void  checkIDValidity(Integer idAdmin) throws InvalidUserInformationException {
        if (idAdmin == null   || idAdmin < 0) {
            throw new InvalidUserInformationException("INVALID ID");
        }
    }


     static  void validationArgument(  String ... arg ) throws InvalidUserInformationException {
        for  (String  s : arg) {
            if (s == null ||   s.isEmpty()  || s.length() <= 0  ){
                throw new InvalidUserInformationException("INVALID ARGUMENT");
            }
        }
     }
}
