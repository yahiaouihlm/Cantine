package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;

import java.io.IOException;

public interface IAdminDashboardService {

    public void updateAdminInfo(AdminDtoIn adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, AdminNotFound;
    public AdminEntity signUp (AdminDtoIn  adminDtoIn) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException;


    public  void existingAdmin(String  adminEmail ) throws ExistingAdminException;

    static   void  checkIDValidity(Integer idAdmin) throws InvalidPersonInformationException{
        if (idAdmin == null   || idAdmin < 0) {
            throw new InvalidPersonInformationException("INVALID ID");
        }
    }
}
