package fr.sqli.Cantine.dto.in.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.service.admin.registration.exceptions.InvalidPersonInformationException;
import org.springframework.web.multipart.MultipartFile;

public class AdminDtoIn  extends  AbstractPersonDtoIn{


    private String function;



    @JsonIgnore
    public AdminEntity toAdminEntityWithOutFunction() throws InvalidPersonInformationException {
        super.ValidatePersonInformationWithOutPhone();
        super.phoneValidator();
        AdminEntity admin = new AdminEntity();
          admin.setFirstname(this.getFirstname());
          admin.setLastname(this.getLastname());
          admin.setEmail(this.getEmail());
          admin.setPassword(this.getPassword());
          admin.setBirthdate(this.getBirthdate());
          admin.setTowen(this.getTowen());
          admin.setAdresse(this.getAdresse());
          admin.setPhone(this.getPhone());
             return admin;

    }



    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
