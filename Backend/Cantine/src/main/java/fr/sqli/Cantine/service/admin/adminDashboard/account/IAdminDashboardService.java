package fr.sqli.Cantine.service.admin.adminDashboard.account;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.dto.out.person.AdminDtout;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidTokenException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface IAdminDashboardService {


     String  checkTokenValidity(String token) throws InvalidPersonInformationException, AdminNotFound, InvalidTokenException;

     void disableAdminAccount(Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound;

    AdminDtout getAdminById (Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound;
     void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound;
     AdminEntity signUp (AdminDtoIn  adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException;

    public void sendToken(String email) throws AdminNotFound, InvalidPersonInformationException, MessagingException;
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
