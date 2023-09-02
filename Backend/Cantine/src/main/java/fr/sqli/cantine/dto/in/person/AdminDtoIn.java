package fr.sqli.cantine.dto.in.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;

public class AdminDtoIn  extends  AbstractPersonDtoIn{


    private String function;

    private  String address;

    @JsonIgnore
    public AdminEntity toAdminEntityWithOutFunction() throws InvalidPersonInformationException {

        super.ValidatePersonInformationWithOutPhone();
        checkAddressValidity();


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
    public    void  checkAddressValidity () throws InvalidPersonInformationException {
        //  check  Address  validity  because  it  is  not  checked  in  ValidatePersonInformationWithOutPhone  (is  not  shared  between  Admin  and  Student)
        if (this.address == null || this.address.isEmpty() || this.address.isBlank())
            throw new InvalidPersonInformationException("ADDRESS IS  REQUIRED");

        if  (this.address.trim().length() <10 )
            throw new InvalidPersonInformationException("ADDRESS  MUST BE AT LEAST 10 CHARACTERS");

        if  (this.address.length() > 3000 )
            throw new InvalidPersonInformationException("ADDRESS MUST BE LESS THAN 3000 CHARACTERS");
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
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
