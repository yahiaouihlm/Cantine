package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentDtoIn extends AbstractUsersDtoIn {

    private String studentClass;


    @JsonIgnore
    public StudentEntity toStudentEntity() throws InvalidUserInformationException, InvalidStudentClassException {
        super.ValidatePersonInformationWithOutPhone();
        StudentEntity student = new StudentEntity();
        if (super.getPhone() != null && !super.getPhone().trim().isEmpty()) {
            super.phoneValidator();
            student.setPhone(super.getPhone().trim());
        }

        if (this.studentClass == null || this.studentClass.trim().isEmpty()) {
            throw new InvalidStudentClassException("STUDENT CLASS IS  REQUIRED");
        }

        student.setFirstname(super.camelCase(super.getFirstname().trim()));
        student.setLastname(super.camelCase(super.getLastname().trim()));
        student.setEmail(super.getEmail().replaceAll("\\s+", "").toLowerCase());
        student.setPassword(super.getPassword());
        student.setBirthdate(super.getBirthdate());
        student.setTown(super.getTown().trim());


        return student;

    }


    @JsonIgnore
    public void checkStudentInformationForUpdate() throws InvalidUserInformationException, InvalidStudentClassException {
        super.validateInformationWithOutEmailAndPassword();

        if (super.getPhone() != null && !super.getPhone().trim().isEmpty()) {
            super.phoneValidator();
            super.setPhone(super.getPhone().trim());
        }

        if (this.studentClass == null || this.studentClass.trim().isEmpty()) {
            throw new InvalidStudentClassException("STUDENT CLASS IS  REQUIRED");
        }

        super.setFirstname(super.camelCase(super.getFirstname().trim()));
        super.setLastname(super.camelCase(super.getLastname().trim()));
        super.setTown(super.getTown().trim());
    }


    @JsonIgnore
    public void checkStudentClassAndPhoneValidity() throws InvalidStudentClassException, InvalidUserInformationException {
        super.validateInformationWithOutEmailAndPassword();
        if (super.getPhone() != null && !super.getPhone().trim().isEmpty()) {
            super.phoneValidator();
            super.setPhone(super.getPhone().trim());
        }
        if (this.studentClass == null || this.studentClass.trim().isEmpty()) {
            throw new InvalidStudentClassException("STUDENT CLASS IS  REQUIRED");
        }
    }


}
