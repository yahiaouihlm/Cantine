package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public  abstract  class AbstractUsersDtoIn {
    private static final Logger LOG = LogManager.getLogger();

    private  Integer id ;
     private String  uuid ;
    private  String firstname;
    private  String lastname;
    private  String email;
    private  String password;
    private  String birthdateAsString;
    private LocalDate birthdate;
    private  String town;

    private  String phone;
    private MultipartFile image;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    protected   void ValidatePersonInformationWithOutPhone() throws InvalidUserInformationException {

        validateInformationWithOutEmailAndPassword();

        if (this.email == null || this.email.isEmpty() || this.email.isBlank())
            throw new InvalidUserInformationException("EMAIL IS  REQUIRED");

        if (this.password == null || this.password.isEmpty() || this.password.isBlank())
            throw new InvalidUserInformationException("PASSWORD IS  REQUIRED");


        if (this.email.length() > 1000 )
            throw new InvalidUserInformationException("EMAIL MUST BE LESS THAN 1000 CHARACTERS");

        if (this.email.trim().length() < 5 )
            throw new InvalidUserInformationException("EMAIL MUST BE AT LEAST 5 CHARACTERS");


        if  (this.password.trim().length() < 6)
            throw new InvalidUserInformationException("PASSWORD MUST BE AT LEAST 6 CHARACTERS");

         if (this.password.length() > 20 )
            throw new InvalidUserInformationException("PASSWORD MUST BE LESS THAN 20 CHARACTERS");

    }
    @JsonIgnore
  protected  void  validateInformationWithOutEmailAndPassword() throws InvalidUserInformationException {
      if  (this.firstname == null || this.firstname.isEmpty() || this.firstname.isBlank())
          throw new InvalidUserInformationException("FIRSTNAME IS  REQUIRED");

      if  (this.lastname == null || this.lastname.isEmpty() || this.lastname.isBlank())
          throw new InvalidUserInformationException("LASTNAME IS  REQUIRED");

      if (this.town == null || this.town.isEmpty() || this.town.isBlank())
          throw new InvalidUserInformationException("TOWN IS  REQUIRED");



      if  (this.firstname.trim().length() < 3)
          throw new InvalidUserInformationException("FIRSTNAME MUST BE AT LEAST 3 CHARACTERS");

      if  (this.lastname.trim().length() < 3)
          throw new InvalidUserInformationException("LASTNAME MUST BE AT LEAST 3 CHARACTERS");

      if (this.firstname.length() > 90 )
          throw new InvalidUserInformationException("FIRSTNAME MUST BE LESS THAN 90 CHARACTERS");

      if (this.lastname.length() > 90 )
          throw new InvalidUserInformationException("LASTNAME MUST BE LESS THAN 90 CHARACTERS");

      if  (this.town.trim().length() <3 )
          throw new InvalidUserInformationException("TOWN  MUST BE AT LEAST 2 CHARACTERS");

      if  (this.town.length() >1000 )
          throw new InvalidUserInformationException("TOWN MUST BE LESS THAN 1000 CHARACTERS");


      birthdateValidator ();

  }

    @JsonIgnore
    private   void birthdateValidator () throws InvalidUserInformationException {
        if (this.birthdateAsString == null || this.birthdateAsString.isEmpty() || this.birthdateAsString.isBlank())
              throw  new InvalidUserInformationException("BIRTHDATE IS  REQUIRED");
        try {
            this.birthdate = LocalDate.parse(this.birthdateAsString.trim());
        }catch (Exception e){
            AbstractUsersDtoIn.LOG.error("INVALID BIRTHDATE FORMAT");
            throw  new InvalidUserInformationException("INVALID BIRTHDATE FORMAT");
        }
    }

    @JsonIgnore
    protected void phoneValidator () throws InvalidUserInformationException {

        if (this.phone == null || this.phone.isEmpty() || this.phone.isBlank())
            throw new InvalidUserInformationException("PHONE IS  REQUIRED");


        String regex = "^(?:(?:\\+|00)33[\\s.-]{0,3}(?:\\(0\\)[\\s.-]{0,3})?|0)[1-9](?:(?:[\\s.-]?\\d{2}){4}|\\d{2}(?:[\\s.-]?\\d{3}){2})$";

        if (!this.phone.matches(regex))
            throw new InvalidUserInformationException("INVALID PHONE FORMAT");

    }


    @JsonIgnore
    protected   String  camelCase(String  str){
        if (str == null || str.isEmpty()) {
            return "";
        }
        return  str.substring(0,1).toUpperCase()+str.substring(1).toLowerCase();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public LocalDate getBirthdate() throws InvalidUserInformationException {
        birthdateValidator();
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
