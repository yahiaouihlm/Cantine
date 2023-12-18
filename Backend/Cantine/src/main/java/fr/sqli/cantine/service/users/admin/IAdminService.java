package fr.sqli.cantine.service.users.admin;

import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;

import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface IAdminService {


    AdminDtout getAdminByUuID(String  adminUuid) throws InvalidUserInformationException,  UserNotFoundException;
   void   checkLinkValidity(String token) throws InvalidTokenException, TokenNotFoundException, ExpiredToken, UserNotFoundException;
    void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountAlreadyActivatedException, MessagingException;
     void disableAdminAccount(String  adminUuid) throws InvalidUserInformationException, UserNotFoundException;




     void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException;
     void signUp (AdminDtoIn  adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingUserException, AdminFunctionNotFoundException, UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;
    List<FunctionDtout> getAllAdminFunctions() ;
    //public void sendToken(String email) throws AdminNotFound, InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException;
    void existingAdmin(String  adminEmail ) throws  ExistingUserException;

    static   void  checkUuIdValidity(String adminUuid) throws InvalidUserInformationException {
        if (adminUuid == null || adminUuid.isBlank() || adminUuid.length() < 20) {
            throw new InvalidUserInformationException("INVALID UUID");
        }
    }


}
