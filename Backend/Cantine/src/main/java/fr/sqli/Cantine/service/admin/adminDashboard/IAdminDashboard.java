package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingAdminException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;

public interface IAdminDashboard {


    public void  signUp (AdminDtoIn  adminDtoIn, String function) throws InvalidPersonInformationException;


    public  void  exstingAdmin (AdminDtoIn  adminDtoIn) throws ExistingAdminException;
}
