package fr.sqli.Cantine.dto.in.person;

import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public  abstract  class AbstractPersonDtoIn {
    private  String firstname;
    private  String lastname;
    private  String email;
    private  String password;
    private  String birthdateAsString;
    private LocalDate birthdate;
    private  String towen;
    private  String adresse;
    private  String phone;
    private MultipartFile image;

    protected   void ValidatePersonInformationWithOutPhone() throws InvalidPersonInformationException {
        if  (this.firstname == null || this.firstname.isEmpty() || this.firstname.isBlank())
            throw new InvalidPersonInformationException("FIRSTNAME IS  REQUIRED");

        if  (this.lastname == null || this.lastname.isEmpty() || this.lastname.isBlank())
            throw new InvalidPersonInformationException("LASTNAME IS  REQUIRED");

        if (this.email == null || this.email.isEmpty() || this.email.isBlank())
            throw new InvalidPersonInformationException("EMAIL IS  REQUIRED");

        if (this.password == null || this.password.isEmpty() || this.password.isBlank())
            throw new InvalidPersonInformationException("PASSWORD IS  REQUIRED");


            birthdateValidator ();

        if (this.towen == null || this.towen.isEmpty() || this.towen.isBlank())
            throw new InvalidPersonInformationException("TOWEN IS  REQUIRED");

        if (this.adresse == null || this.adresse.isEmpty() || this.adresse.isBlank())
            throw new InvalidPersonInformationException("ADRESSE IS  REQUIRED");

        if  (this.firstname.length() < 3)
            throw new InvalidPersonInformationException("FIRSTNAME MUST BE AT LEAST 3 CHARACTERS");

        if  (this.lastname.length() < 3)
            throw new InvalidPersonInformationException("LASTNAME MUST BE AT LEAST 3 CHARACTERS");

        if  (this.password.length() < 6)
            throw new InvalidPersonInformationException("PASSWORD MUST BE AT LEAST 6 CHARACTERS");

        if (this.firstname.length() > 90 )
            throw new InvalidPersonInformationException("FIRSTNAME MUST BE LESS THAN 90 CHARACTERS");

        if (this.lastname.length() > 90 )
            throw new InvalidPersonInformationException("LASTNAME MUST BE LESS THAN 90 CHARACTERS");

         if (this.email.length() > 90 )
            throw new InvalidPersonInformationException("EMAIL MUST BE LESS THAN 90 CHARACTERS");

         if (this.password.length() > 20 )
            throw new InvalidPersonInformationException("PASSWORD MUST BE LESS THAN 20 CHARACTERS");

    }


    private   void birthdateValidator () throws InvalidPersonInformationException {
        if (this.birthdateAsString == null || this.birthdateAsString.isEmpty() || this.birthdateAsString.isBlank())
              throw  new InvalidPersonInformationException("BIRTHDATE IS  REQUIRED");
        try {
            this.birthdate = LocalDate.parse(this.birthdateAsString);
        }catch (Exception e){
            throw  new InvalidPersonInformationException(" INVALID BIRTHDATE FORMAT");
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

    public String getTowen() {
        return towen;
    }

    public void setTowen(String towen) {
        this.towen = towen;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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
