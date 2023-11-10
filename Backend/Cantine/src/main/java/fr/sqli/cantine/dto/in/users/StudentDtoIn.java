package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;

public class StudentDtoIn  extends AbstractUsersDtoIn {

    private String studentClass;





    @JsonIgnore
    public StudentEntity toStudentEntity() throws InvalidStudentClassException, InvalidUserInformationException {
        super.ValidatePersonInformationWithOutPhone();
        StudentEntity student = new StudentEntity();
        if  ( super.getPhone() != null  && !super.getPhone().trim().isEmpty()) {
            super.phoneValidator();
            student.setPhone(super.getPhone().trim());
        }

        student.setFirstname(super.camelCase(super.getFirstname().trim()));
        student.setLastname(super.camelCase(super.getLastname().trim()));
        student.setEmail(super.getEmail().replaceAll("\\s+","").toLowerCase());
        student.setPassword(super.getPassword());
        student.setBirthdate(super.getBirthdate());
        student.setTown(super.getTown().trim());
        this.checkStudentClassValidity  ();


        return  student;

    }




    @JsonIgnore
    public  void  checkStudentClassValidity  () throws InvalidStudentClassException, InvalidUserInformationException {
        super.validateInformationWithOutEmailAndPassword();
        if  ( super.getPhone() != null  &&  !super.getPhone().trim().isEmpty()) {
            super.phoneValidator();
            super.setPhone(super.getPhone().trim());
        }
            if (this.studentClass == null || this.studentClass.trim().isEmpty()){
            throw new InvalidStudentClassException("STUDENT CLASS IS  REQUIRED");
        }
    }



    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }





}
