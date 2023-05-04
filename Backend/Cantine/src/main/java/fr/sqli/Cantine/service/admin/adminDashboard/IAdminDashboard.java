package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;

import java.io.IOException;

public interface IAdminDashboard {


    public AdminEntity signUp (AdminDtoIn  adminDtoIn, String function) throws InvalidPersonInformationException, ExistingAdminException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException;


    public  void  exstingAdmin (String  adminEmail ) throws ExistingAdminException;
}
