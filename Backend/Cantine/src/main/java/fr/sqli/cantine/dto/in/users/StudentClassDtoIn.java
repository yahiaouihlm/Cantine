package fr.sqli.cantine.dto.in.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;

public class StudentClassDtoIn {

     private  Integer  id ;

     private  String  name ;



     @JsonIgnore
     public StudentClassEntity toStudentClassEntity () throws InvalidStudentClassException {
          this.checkNameValidity();
          StudentClassEntity studentClassEntity = new StudentClassEntity();
          studentClassEntity.setName(this.name.trim());

          return  studentClassEntity;
     }



     @JsonIgnore
     public   void   checkNameValidity () throws InvalidStudentClassException {
         if  (this.name == null  || this.name.trim().isEmpty() ) {
             throw new InvalidStudentClassException("INVALID STUDENT CLASS NAME");
         }
      }
      @JsonIgnore
      public  void checkIdValidity () throws InvalidStudentClassException {
          if (this.id == null || this.id <0) {
              throw new InvalidStudentClassException("INVALID STUDENT CLASS ID");
          }
      }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }


        public Integer getId() {
            return id;
        }
}
