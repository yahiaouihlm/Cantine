package fr.sqli.cantine.controller.users.admin;


import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:4200")
public interface IAdminWorksController {

    String ADMIN_DASH_BOARD_BASIC_WORK_URL = "/cantine/admin/adminDashboard/works";
    String SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT = "/addStudentAmount";

    String  VALIDATE_STUDENT_AMOUNT_STUDENT =  "/validationAmount";
    String GET_STUDENTS = "/getStudents";
    String GET_STUDENT = "/getStudent";
    String ADD_STUDENT_CLASS_ENDPOINT = "/addStudentClass";
    String UPDATE_STUDENT_CLASS_ENDPOINT = "/updateStudentClass";
    String GET_STUDENT_BY_UUID = "/getStudentByUuId";
    String UPDATE_STUDENT_EMAIL = "/updateStudentEmail";
    /**************************** SERVER ANSWERS ************************************/

    String  STUDENT_VALIDATE_STUDENT_AMOUNT  = "NOTIFICATION SENT SUCCESSFULLY";
    String SEND_NEW_AMOUNT_TO_STUDENT_NOTIFICATION = "NOTIFICATION SENT SUCCESSFULLY";

    String STUDENT_CLASS_UPDATED_SUCCESSFULLY = "STUDENT CLASS UPDATED SUCCESSFULLY";
    String STUDENT_CLASS_ADDED_SUCCESSFULLY = "STUDENT CLASS ADDED SUCCESSFULLY";
    String STUDENT_EMAIL_UPDATED_SUCCESSFULLY = "STUDENT EMAIL UPDATED SUCCESSFULLY";


    @PostMapping(UPDATE_STUDENT_EMAIL)
    ResponseEntity<ResponseDtout> updateStudentEmail(@RequestParam("studentUuid") String studentUuid, @RequestParam("newEmail") String newEmail) throws UserNotFoundException, MessagingException, ExistingUserException, InvalidUserInformationException;
    @PostMapping(VALIDATE_STUDENT_AMOUNT_STUDENT)
    ResponseEntity<ResponseDtout> addAmountToStudentAccountCodeValidation(@RequestParam("studentId") Integer studentId, @RequestParam("validationCode") Integer validationCode, @RequestParam("amount") Double amount) throws InvalidUserInformationException, InvalidTokenException, ExpiredToken, UserNotFoundException;

    @PostMapping(SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT)
    ResponseEntity<ResponseDtout> attemptAddAmountToStudentAccount(@RequestParam("studentUuid") String studentUuid, @RequestParam("amount") Double amount) throws InvalidUserInformationException, MessagingException, UserNotFoundException;

    @GetMapping(GET_STUDENT_BY_UUID)
    ResponseEntity<StudentDtout> getStudentById(@RequestParam("studentUuid") String studentUuid) throws InvalidUserInformationException, UserNotFoundException;

    @GetMapping(GET_STUDENTS)
    ResponseEntity<List<StudentDtout>> getStudents(@RequestParam String firstname, @RequestParam String Lastname, @RequestParam String birthdateAsString) throws InvalidUserInformationException;

    ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;

    ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;

    @GetMapping(GET_STUDENT)
    ResponseEntity <StudentDtout> getStudentByEmail(@RequestParam("email") String email) throws UserNotFoundException, InvalidUserInformationException;
}
