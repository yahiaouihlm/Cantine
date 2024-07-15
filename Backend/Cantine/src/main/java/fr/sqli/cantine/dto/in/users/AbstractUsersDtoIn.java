package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Setter
@Getter
public abstract class AbstractUsersDtoIn {
    private static final Logger LOG = LogManager.getLogger();


    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String birthdateAsString;
    private LocalDate birthdate;
    private String town;
    private String phone;
    private MultipartFile image;


    @JsonIgnore
    protected void ValidatePersonInformationWithOutPhone() throws InvalidUserInformationException {

        validateInformationWithOutEmailAndPassword();

        if (this.email == null || this.email.isEmpty() || this.email.isBlank())
            throw new InvalidUserInformationException("EMAIL IS  REQUIRED");

        if (this.password == null || this.password.isEmpty() || this.password.isBlank())
            throw new InvalidUserInformationException("PASSWORD IS  REQUIRED");


        if (this.email.length() > 1000)
            throw new InvalidUserInformationException("EMAIL MUST BE LESS THAN 1000 CHARACTERS");

        if (this.email.trim().length() < 5)
            throw new InvalidUserInformationException("EMAIL MUST BE AT LEAST 5 CHARACTERS");


        if (this.password.trim().length() < 6)
            throw new InvalidUserInformationException("PASSWORD MUST BE AT LEAST 6 CHARACTERS");

        if (this.password.length() > 20)
            throw new InvalidUserInformationException("PASSWORD MUST BE LESS THAN 20 CHARACTERS");

    }

    @JsonIgnore
    protected void validateInformationWithOutEmailAndPassword() throws InvalidUserInformationException {
        if (this.firstname == null || this.firstname.isEmpty() || this.firstname.isBlank())
            throw new InvalidUserInformationException("FIRSTNAME IS  REQUIRED");

        if (this.lastname == null || this.lastname.isEmpty() || this.lastname.isBlank())
            throw new InvalidUserInformationException("LASTNAME IS  REQUIRED");

        if (this.town == null || this.town.isEmpty() || this.town.isBlank())
            throw new InvalidUserInformationException("TOWN IS  REQUIRED");


        if (this.firstname.trim().length() < 3)
            throw new InvalidUserInformationException("FIRSTNAME MUST BE AT LEAST 3 CHARACTERS");

        if (this.lastname.trim().length() < 3)
            throw new InvalidUserInformationException("LASTNAME MUST BE AT LEAST 3 CHARACTERS");

        if (this.firstname.length() > 90)
            throw new InvalidUserInformationException("FIRSTNAME MUST BE LESS THAN 90 CHARACTERS");

        if (this.lastname.length() > 90)
            throw new InvalidUserInformationException("LASTNAME MUST BE LESS THAN 90 CHARACTERS");

        if (this.town.trim().length() < 3)
            throw new InvalidUserInformationException("TOWN  MUST BE AT LEAST 2 CHARACTERS");

        if (this.town.length() > 1000)
            throw new InvalidUserInformationException("TOWN MUST BE LESS THAN 1000 CHARACTERS");


        birthdateValidator();

    }

    @JsonIgnore
    private void birthdateValidator() throws InvalidUserInformationException {
        if (this.birthdateAsString == null || this.birthdateAsString.isEmpty() || this.birthdateAsString.isBlank())
            throw new InvalidUserInformationException("BIRTHDATE IS  REQUIRED");
        try {
            this.birthdate = LocalDate.parse(this.birthdateAsString.trim());
        } catch (Exception e) {
            AbstractUsersDtoIn.LOG.error("INVALID BIRTHDATE FORMAT");
            throw new InvalidUserInformationException("INVALID BIRTHDATE FORMAT");
        }
    }

    @JsonIgnore
    protected void phoneValidator() throws InvalidUserInformationException {

        if (this.phone == null || this.phone.isEmpty() || this.phone.isBlank())
            throw new InvalidUserInformationException("PHONE IS  REQUIRED");


        String regex = "^(?:(?:\\+|00)33[\\s.-]{0,3}(?:\\(0\\)[\\s.-]{0,3})?|0)[1-9](?:(?:[\\s.-]?\\d{2}){4}|\\d{2}(?:[\\s.-]?\\d{3}){2})$";

        if (!this.phone.matches(regex))
            throw new InvalidUserInformationException("INVALID PHONE FORMAT");

    }


    @JsonIgnore
    protected String camelCase(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


    public LocalDate getBirthdate() throws InvalidUserInformationException {
        birthdateValidator();
        return birthdate;
    }


}
