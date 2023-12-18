package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

public interface IStudentController {
    String STUDENT_BASIC_URL = "/cantine/student";


    String CHECK_TOKEN_VALIDITY_ENDPOINT = "/checkTokenValidity";
    String SEND_TOKEN_ENDPOINT = "/sendToken";
    String STUDENT_SIGN_UP_ENDPOINT = "/signUp";

    String GET_STUDENT_BY_ID_ENDPOINT = "/getStudent";

    String UPDATE_STUDENT_INFO_ENDPOINT = "/update/studentInfo";
    String GET_ALL_STUDENT_CLASS = "/getAllStudentClass";

    String TOKEN_CHECKED_SUCCESSFULLY = "TOKEN CHECKED SUCCESSFULLY";
    String TOKEN_SENT_SUCCESSFULLY = "TOKEN SENT SUCCESSFULLY";
    String STUDENT_SIGNED_UP_SUCCESSFULLY = "STUDENT SAVED SUCCESSFULLY";

    String STUDENT_INFO_UPDATED_SUCCESSFULLY = "STUDENT UPDATED SUCCESSFULLY";


    @PostMapping(CHECK_TOKEN_VALIDITY_ENDPOINT)
    ResponseEntity<ResponseDtout> checkLinkValidity(@RequestParam("token") String token) throws UserNotFoundException, InvalidTokenException, ExpiredToken, TokenNotFoundException;

    @PostMapping(SEND_TOKEN_ENDPOINT)
    ResponseEntity<ResponseDtout> sendStudentConfirmationLink(@RequestParam("email") String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException;

    @GetMapping(GET_STUDENT_BY_ID_ENDPOINT)
    ResponseEntity<StudentDtout> getStudentByUuid(@RequestParam("studentUuid")String  studentUuid) throws  InvalidUserInformationException, UserNotFoundException;


    ResponseEntity<ResponseDtout> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException, UserNotFoundException;

    @PostMapping(STUDENT_SIGN_UP_ENDPOINT)
    ResponseEntity<ResponseDtout> signUpStudent(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException,UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException, ExistingUserException;


    @GetMapping(GET_ALL_STUDENT_CLASS)
    ResponseEntity<List<StudentClassDtout>> getAllStudentClass();
}
