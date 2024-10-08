package fr.sqli.cantine.service.users.admin;

import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IAdminService {


    static void checkUuIdValidity(String adminUuid) throws InvalidUserInformationException {
        if (adminUuid == null || adminUuid.isBlank() || adminUuid.length() < 20) {
            throw new InvalidUserInformationException("INVALID ID");
        }
    }

    AdminDtout getAdminByUuID(String adminUuid) throws InvalidUserInformationException, UserNotFoundException;

    void removeAdminAccount(String adminUuid) throws InvalidUserInformationException, UserNotFoundException;

    void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException, UserNotFoundException;

    void signUp(AdminDtoIn adminDtoIn) throws InvalidUserInformationException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingUserException, AdminFunctionNotFoundException, UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException, RoleNotFoundException;

    List<FunctionDtout> getAllAdminFunctions();

    void existingEmail(String adminEmail) throws ExistingUserException;

    public UserEntity findByUsername(String username) throws UserNotFoundException;

}
