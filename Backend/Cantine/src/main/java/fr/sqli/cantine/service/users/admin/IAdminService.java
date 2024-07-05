package fr.sqli.cantine.service.users.admin;

import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;

import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountActivatedException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface IAdminService {


    AdminDtout getAdminByUuID(String adminUuid) throws InvalidUserInformationException, UserNotFoundException;

    void removeAdminAccount(String adminUuid) throws InvalidUserInformationException, UserNotFoundException;

    void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException;

    void signUp(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingUserException, AdminFunctionNotFoundException, UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException;

    List<FunctionDtout> getAllAdminFunctions();

    void existingEmail(String adminEmail) throws ExistingUserException;

    static void checkUuIdValidity(String adminUuid) throws InvalidUserInformationException {
        if (adminUuid == null || adminUuid.isBlank() || adminUuid.length() < 20) {
            throw new InvalidUserInformationException("INVALID UUID");
        }
    }

    public AdminEntity findByUsername(String username) throws UserNotFoundException;

}
