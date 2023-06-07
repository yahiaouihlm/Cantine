package fr.sqli.Cantine.dto.in.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.Cantine.entity.StudentClassEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;

public class StudentClassDtoIn {

     private  Integer  id ;

     private  String  name ;



     @JsonIgnore

     public StudentClassEntity toStudentClassEntity () throws InvalidStudentClassException {
          this.checkNameValidity();
          StudentClassEntity studentClassEntity = new StudentClassEntity();
          studentClassEntity.setName(this.name);

          return  studentClassEntity;
     }



     @JsonIgnore
     public   void   checkNameValidity () throws InvalidStudentClassException {
         if  (this.name == null  || this.name.trim().isEmpty() ) {
             throw new InvalidStudentClassException("INVALID STUDENT CLASS NAME");
         }
      }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }
}