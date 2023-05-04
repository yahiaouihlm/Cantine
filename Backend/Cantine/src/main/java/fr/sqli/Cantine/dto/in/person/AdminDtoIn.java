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
          admin.setFirstname(CamelCase(this.getFirstname().trim()));
          admin.setLastname(CamelCase(this.getLastname().trim()));
          admin.setEmail(this.getEmail().trim().toLowerCase());
          admin.setPassword(this.getPassword());
          admin.setBirthdate(this.getBirthdate());
          admin.setTown(this.getTown().trim());
          admin.setAddress(this.getAddress().trim());
          admin.setPhone(this.getPhone().trim());
             return admin;

    }


    @JsonIgnore
    private  String  CamelCase(String  str){
        if (str == null || str.isEmpty()) {
            return new String();
        }
        return  str.substring(0,1).toUpperCase()+str.substring(1).toLowerCase();
    }

   @JsonIgnore
   public  void  functionValidator() throws InvalidPersonInformationException {
        if (this.function == null || this.function.isEmpty()) {
            throw new InvalidPersonInformationException("function is required");
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
