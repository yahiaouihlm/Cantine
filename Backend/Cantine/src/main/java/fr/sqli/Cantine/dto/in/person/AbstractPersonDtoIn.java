package fr.sqli.Cantine.dto.in.person;

import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public  abstract  class AbstractPersonDtoIn {
    private static final Logger LOG = LogManager.getLogger();
    private  String firstname;
    private  String lastname;
    private  String email;
    private  String password;
    private  String birthdateAsString;
    private LocalDate birthdate;
    private  String town;
    private  String address;
    private  String phone;
    private MultipartFile image;

    protected   void ValidatePersonInformationWithOutPhone() throws InvalidPersonInformationException {


        if (this.email == null || this.email.isEmpty() || this.email.isBlank())
            throw new InvalidPersonInformationException("EMAIL IS  REQUIRED");

        if (this.password == null || this.password.isEmpty() || this.password.isBlank())
            throw new InvalidPersonInformationException("PASSWORD IS  REQUIRED");

        validateInformationWithOutEmailAndPassword();

            birthdateValidator ();



        if (this.email.length() > 1000 )
            throw new InvalidPersonInformationException("EMAIL MUST BE LESS THAN 1000 CHARACTERS");

        if (this.email.trim().length() < 5 )
            throw new InvalidPersonInformationException("EMAIL MUST BE AT LEAST 5 CHARACTERS");


        if  (this.password.trim().length() < 6)
            throw new InvalidPersonInformationException("PASSWORD MUST BE AT LEAST 6 CHARACTERS");

         if (this.password.length() > 20 )
            throw new InvalidPersonInformationException("PASSWORD MUST BE LESS THAN 20 CHARACTERS");

    }

  protected  void  validateInformationWithOutEmailAndPassword() throws InvalidPersonInformationException {
      if  (this.firstname == null || this.firstname.isEmpty() || this.firstname.isBlank())
          throw new InvalidPersonInformationException("FIRSTNAME IS  REQUIRED");

      if  (this.lastname == null || this.lastname.isEmpty() || this.lastname.isBlank())
          throw new InvalidPersonInformationException("LASTNAME IS  REQUIRED");

      if (this.town == null || this.town.isEmpty() || this.town.isBlank())
          throw new InvalidPersonInformationException("TOWN IS  REQUIRED");

      if (this.address == null || this.address.isEmpty() || this.address.isBlank())
          throw new InvalidPersonInformationException("ADDRESS IS  REQUIRED");

      if  (this.firstname.trim().length() < 3)
          throw new InvalidPersonInformationException("FIRSTNAME MUST BE AT LEAST 3 CHARACTERS");

      if  (this.lastname.trim().length() < 3)
          throw new InvalidPersonInformationException("LASTNAME MUST BE AT LEAST 3 CHARACTERS");

      if (this.firstname.length() > 90 )
          throw new InvalidPersonInformationException("FIRSTNAME MUST BE LESS THAN 90 CHARACTERS");

      if (this.lastname.length() > 90 )
          throw new InvalidPersonInformationException("LASTNAME MUST BE LESS THAN 90 CHARACTERS");

      if  (this.town.trim().length() <3 )
          throw new InvalidPersonInformationException("TOWN  MUST BE AT LEAST 2 CHARACTERS");

      if  (this.town.length() >1000 )
          throw new InvalidPersonInformationException("TOWN MUST BE LESS THAN 1000 CHARACTERS");

      if  (this.address.trim().length() <10 )
          throw new InvalidPersonInformationException("ADDRESS  MUST BE AT LEAST 10 CHARACTERS");

      if  (this.address.length() > 3000 )
          throw new InvalidPersonInformationException("ADDRESS MUST BE LESS THAN 3000 CHARACTERS");

  }

    private   void birthdateValidator () throws InvalidPersonInformationException {
        if (this.birthdateAsString == null || this.birthdateAsString.isEmpty() || this.birthdateAsString.isBlank())
              throw  new InvalidPersonInformationException("BIRTHDATE IS  REQUIRED");
        try {
            this.birthdate = LocalDate.parse(this.birthdateAsString);
        }catch (Exception e){
            AbstractPersonDtoIn.LOG.error("INVALID BIRTHDATE FORMAT");
            throw  new InvalidPersonInformationException("INVALID BIRTHDATE FORMAT");
        }
    }

    protected void phoneValidator () throws InvalidPersonInformationException {

        if (this.phone == null || this.phone.isEmpty() || this.phone.isBlank())
            throw new InvalidPersonInformationException("PHONE IS  REQUIRED");


        String regex = "^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$";

        if (!this.phone.matches(regex))
            throw new InvalidPersonInformationException("INVALID PHONE FORMAT");

    }






    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthdateAsString() {
        return birthdateAsString;
    }

    public void setBirthdateAsString(String birthdateAsString) {
        this.birthdateAsString = birthdateAsString;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
