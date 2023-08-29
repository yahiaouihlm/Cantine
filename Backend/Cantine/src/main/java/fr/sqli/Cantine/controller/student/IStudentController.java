package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.dto.out.person.StudentDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import fr.sqli.Cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

public interface IStudentController {
    String STUDENT_BASIC_URL = "/cantine/student";



    String  SEND_TOKEN_ENDPOINT = "/sendToken";
    String  STUDENT_SIGN_UP_ENDPOINT =  "/signUp";

    String  GET_STUDENT_BY_ID_ENDPOINT = "/getStudent";

   String UPDATE_STUDENT_INFO_ENDPOINT = "/update/studentInfo";
    String  GET_ALL_STUDENT_CLASS ="/getAllStudentClass";


   String  TOKEN_SENT_SUCCESSFULLY = "TOKEN SENT SUCCESSFULLY" ;
    String  STUDENT_SIGNED_UP_SUCCESSFULLY = "STUDENT SAVED SUCCESSFULLY";

    String STUDENT_INFO_UPDATED_SUCCESSFULLY = "STUDENT UPDATED SUCCESSFULLY";


    @PostMapping(SEND_TOKEN_ENDPOINT)
    ResponseEntity<String> sendTokenStudent(String email ) throws InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException, StudentNotFoundException;

    ResponseEntity<StudentDtout> getStudentById(Integer id) throws StudentNotFoundException, InvalidPersonInformationException;


     ResponseEntity<String> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, StudentNotFoundException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException;


     ResponseEntity<String> signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException, ExistingStudentException;
}
