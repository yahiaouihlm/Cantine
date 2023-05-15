package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.dto.out.person.AdminDtout;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;

import java.io.IOException;

public interface IAdminDashboardService {



     void disableAdminAccount(Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound;

    AdminDtout getAdminById (Integer idAdmin) throws InvalidPersonInformationException, AdminNotFound;
     void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound;
     AdminEntity signUp (AdminDtoIn  adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException;


      void existingAdmin(String  adminEmail ) throws ExistingAdminException;

    static   void  checkIDValidity(Integer idAdmin) throws InvalidPersonInformationException{
        if (idAdmin == null   || idAdmin < 0) {
            throw new InvalidPersonInformationException("INVALID ID");
        }
    }
}
