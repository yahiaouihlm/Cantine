package fr.sqli.cantine.controller.student;

import fr.sqli.cantine.dto.in.person.StudentDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

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



    @GetMapping(GET_ALL_STUDENT_CLASS)
    ResponseEntity<List<StudentClassDtout>> getAllStudentClass() ;
/*
    @PostMapping(SEND_TOKEN_ENDPOINT)
    ResponseEntity<ResponseDtout> sendTokenStudent(@RequestParam("email")  String email ) throws InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException, StudentNotFoundException;
*/
    @GetMapping(GET_STUDENT_BY_ID_ENDPOINT)
    ResponseEntity<StudentDtout> getStudentById(Integer id) throws StudentNotFoundException, InvalidPersonInformationException;


     ResponseEntity<ResponseDtout> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, StudentNotFoundException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException;

     @PostMapping(STUDENT_SIGN_UP_ENDPOINT)
     ResponseEntity<ResponseDtout> signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException, ExistingStudentException;
}
