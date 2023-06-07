package fr.sqli.Cantine.dto.in.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;

public class AdminDtoIn  extends  AbstractPersonDtoIn{


    private String function;



    @JsonIgnore
    public AdminEntity toAdminEntityWithOutFunction() throws InvalidPersonInformationException {
        super.ValidatePersonInformationWithOutPhone();
        super.phoneValidator();
        AdminEntity admin = new AdminEntity();
          admin.setFirstname(super.camelCase(this.getFirstname().trim()));
          admin.setLastname(super.camelCase(this.getLastname().trim()));
          admin.setEmail(this.getEmail().replaceAll("\\s+","").toLowerCase());
          admin.setPassword(this.getPassword());
          admin.setBirthdate(this.getBirthdate());
          admin.setTown(this.getTown().trim());
          admin.setAddress(this.getAddress().trim());
          admin.setPhone(this.getPhone().trim());
             return admin;

    }


    @JsonIgnore
    public   void   checkInformationValidityExceptEmailAndPassword() throws InvalidPersonInformationException {
        super.phoneValidator();
        functionValidator();
        validateInformationWithOutEmailAndPassword();

    }


   @JsonIgnore
   public  void  functionValidator() throws InvalidPersonInformationException {
        if (this.function == null || this.function.isEmpty()  || this.function.trim().isEmpty()){
            throw new InvalidPersonInformationException("FUNCTION IS  REQUIRED");
        }
    }
    public String getFunction() throws InvalidPersonInformationException {
        functionValidator();
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
