package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.StudentClassEntity;
import fr.sqli.Cantine.entity.StudentEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface IStudentTest {

    String STUDENT_BASIC_URL = "/cantine/student";

    String  STUDENT_SIGN_UP = STUDENT_BASIC_URL +  "/signUp";
    String  IMAGE_NAME = "imageForTest.jpg";
    String IMAGE_FOR_TEST_PATH= "imagesTests/"+IMAGE_NAME;

    String  STUDENT_SIGNED_UP_SUCCESSFULLY = "STUDENT SAVED SUCCESSFULLY";


    Map<String, String> exceptionsMap = Map.ofEntries(
            Map.entry("FirstNameRequire", "FIRSTNAME IS  REQUIRED"),
            Map.entry("ShortFirstName", "FIRSTNAME MUST BE AT LEAST 3 CHARACTERS"),
            Map.entry("LongFirstName", "FIRSTNAME MUST BE LESS THAN 90 CHARACTERS"),
            Map.entry("LastNameRequire", "LASTNAME IS  REQUIRED"),
            Map.entry("ShortLastName", "LASTNAME MUST BE AT LEAST 3 CHARACTERS"),
            Map.entry("LongLastName", "LASTNAME MUST BE LESS THAN 90 CHARACTERS"),
            Map.entry("BirthdateRequire", "BIRTHDATE IS  REQUIRED"),
            Map.entry("InvalidBirthdateFormat", "INVALID BIRTHDATE FORMAT"),
            Map.entry("TownRequire", "TOWN IS  REQUIRED"),
            Map.entry("ShortTown", "TOWN  MUST BE AT LEAST 2 CHARACTERS"),
            Map.entry("LongTown", "TOWN MUST BE LESS THAN 1000 CHARACTERS"),
            Map.entry("InvalidPhoneFormat", "INVALID PHONE FORMAT"),
            Map.entry("EmailRequire", "EMAIL IS  REQUIRED"),
            Map.entry("ShortEmail", "EMAIL MUST BE AT LEAST 5 CHARACTERS"),
            Map.entry("LongEmail", "EMAIL MUST BE LESS THAN 1000 CHARACTERS"),
            Map.entry("InvalidEmailFormat", "YOUR EMAIL IS NOT VALID"),
            Map.entry("PasswordRequire", "PASSWORD IS  REQUIRED"),
            Map.entry("ShortPassword", "PASSWORD MUST BE AT LEAST 6 CHARACTERS"),
            Map.entry("LongPassword", "PASSWORD MUST BE LESS THAN 20 CHARACTERS"),
               Map.entry("InvalidStudentClass", "INVALID STUDENT CLASS NAME"),
            Map.entry("StudentClassRequire", "STUDENT CLASS IS  REQUIRED"),
            Map.entry("StudentClassNotFound", "STUDENT CLASS NOT FOUND"),
            Map.entry("ExistingStudent", "THIS STUDENT IS ALREADY EXISTS")
            );


      static StudentEntity createStudentClassEntity(String email , StudentClassEntity studentClassEntity){
          StudentEntity student  = new StudentEntity();
          student.setEmail(email);
            student.setFirstname("firstName");
            student.setLastname("lastName");
            student.setBirthdate(LocalDate.now());
            student.setTown("town");
            student.setPhone("0606060606");
            student.setPassword("password");
          ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename( IMAGE_NAME);
            student.setImage(imageEntity);
            student.setRegistrationDate(LocalDate.now());
            student.setStatus(0);
            student.setWallet(new BigDecimal(0));
            student.setStudentClass(studentClassEntity);
            return student;
     }
}
