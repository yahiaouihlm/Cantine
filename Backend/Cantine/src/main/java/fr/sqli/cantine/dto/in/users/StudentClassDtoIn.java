package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentClassDtoIn {

    private String id;

    private String name;


    @JsonIgnore
    public StudentClassEntity toStudentClassEntity() throws InvalidStudentClassException {
        this.checkNameValidity();
        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName(this.name.trim());

        return studentClassEntity;
    }


    @JsonIgnore
    public void checkNameValidity() throws InvalidStudentClassException {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new InvalidStudentClassException("INVALID STUDENT CLASS NAME");
        }
    }

    @JsonIgnore
    public void checkIdValidity() throws InvalidStudentClassException {
        if (this.id == null || this.id.length() < 10) {
            throw new InvalidStudentClassException("INVALID STUDENT CLASS ID");
        }
    }


}
