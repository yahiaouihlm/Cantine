package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface IStudentController {
    String STUDENT_BASIC_URL = "/cantine/student";



    String  STUDENT_SIGN_UP_ENDPOINT =  "/signUp";

   String STUDENT_INFO_UPDATE_ENDPOINT = "/update/studentInfo";
    String  STUDENT_SIGNED_UP_SUCCESSFULLY = "STUDENT SAVED SUCCESSFULLY";

    String STUDENT_INFO_UPDATED_SUCCESSFULLY = "STUDENT UPDATED SUCCESSFULLY";

     ResponseEntity<String> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, StudentNotFoundException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException;


     ResponseEntity<String> signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException, ExistingStudentException;
}
