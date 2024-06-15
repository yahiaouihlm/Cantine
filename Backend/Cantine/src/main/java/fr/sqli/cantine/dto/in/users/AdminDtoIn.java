package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;

public class AdminDtoIn  extends AbstractUsersDtoIn {


    private String function;

    private  String address;

    @JsonIgnore
    public void checkAdminInformationValidity() throws InvalidUserInformationException {

        super.ValidatePersonInformationWithOutPhone();
        checkAddressValidity();
        super.phoneValidator();

        super.setFirstname(super.camelCase(this.getFirstname().trim()));
        super.setLastname(super.camelCase(this.getLastname().trim()));
        super.setEmail(this.getEmail().replaceAll("\\s+","").toLowerCase());

    }

    @JsonIgnore
    public    void  checkAddressValidity () throws InvalidUserInformationException {
        //  check  Address  validity  because  it  is  not  checked  in  ValidatePersonInformationWithOutPhone  (is  not  shared  between  Admin  and  Student)
        if (this.address == null || this.address.isEmpty() || this.address.isBlank())
            throw new InvalidUserInformationException("ADDRESS IS  REQUIRED");

        if  (this.address.trim().length() <10 )
            throw new InvalidUserInformationException("ADDRESS  MUST BE AT LEAST 10 CHARACTERS");

        if  (this.address.length() > 3000 )
            throw new InvalidUserInformationException("ADDRESS MUST BE LESS THAN 3000 CHARACTERS");
    }

    @JsonIgnore
    public   void   checkInformationValidityExceptEmailAndPassword() throws InvalidUserInformationException {
        super.phoneValidator();
        functionValidator();
        validateInformationWithOutEmailAndPassword();
        super.setFirstname(super.camelCase(this.getFirstname().trim()));
        super.setLastname(super.camelCase(this.getLastname().trim()));
    }


   @JsonIgnore
   public  void  functionValidator() throws InvalidUserInformationException {
        if (this.function == null || this.function.isEmpty()  || this.function.trim().isEmpty()){
            throw new InvalidUserInformationException("FUNCTION IS  REQUIRED");
        }
    }
    public String getFunction() throws InvalidUserInformationException {
        functionValidator();
        return function.trim();
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
