package fr.sqli.cantine.service.admin.account;

import fr.sqli.cantine.dto.in.person.AdminDtoIn;
import fr.sqli.cantine.dto.out.person.AdminDtout;
import fr.sqli.cantine.dto.out.superAdmin.FunctionDtout;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.service.admin.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.admin.exceptions.AdminNotFound;
import fr.sqli.cantine.service.admin.exceptions.ExistingAdminException;
import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;

import java.io.IOException;
import java.util.List;

public interface IAdminService {


    /* String  checkTokenValidity(String token) throws InvalidPersonInformationException, AdminNotFound, InvalidTokenException, ExpiredToken;*/

     void disableAdminAccount(Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound;

    AdminDtout getAdminById (Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound;
     void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound, AdminFunctionNotFoundException;
     AdminEntity signUp (AdminDtoIn  adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminFunctionNotFoundException;
    List<FunctionDtout> getAllAdminFunctions() ;
    //public void sendToken(String email) throws AdminNotFound, InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException;
    void existingAdmin(String  adminEmail ) throws ExistingAdminException;

    static   void  checkIDValidity(Integer idAdmin) throws InvalidPersonInformationException{
        if (idAdmin == null   || idAdmin < 0) {
            throw new InvalidPersonInformationException("INVALID ID");
        }
    }


     static  void validationArgument(  String ... arg ) throws InvalidPersonInformationException {
        for  (String  s : arg) {
            if (s == null ||   s.isEmpty()  || s.length() <= 0  ){
                throw new InvalidPersonInformationException("INVALID ARGUMENT");
            }
        }
     }
}
