package fr.sqli.cantine.controller.users.admin;


import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.users.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface IAdminWorksController {

    String ADMIN_DASH_BOARD_BASIC_WORK_URL = "/cantine/admin/adminDashboard/works";
    String SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT = "/addStudentAmount";

    String      VALIDATE_STUDENT_AMOUNT_STUDENT =  "/validationAmount";
    String GET_STUDENTS = "/getStudents";
    String GET_STUDENT_BY_ID = "/getStudent";
    String ADD_STUDENT_CLASS_ENDPOINT = "/addStudentClass";
    String UPDATE_STUDENT_CLASS_ENDPOINT = "/updateStudentClass";

    /**************************** SERVER ANSWERS ************************************/

    String  STUDENT_VALIDATE_STUDENT_AMOUNT  = "NOTIFICATION SENT SUCCESSFULLY";
    String SEND_NEW_AMOUNT_TO_STUDENT_NOTIFICATION = "NOTIFICATION SENT SUCCESSFULLY";

    String STUDENT_CLASS_UPDATED_SUCCESSFULLY = "STUDENT CLASS UPDATED SUCCESSFULLY";
    String STUDENT_CLASS_ADDED_SUCCESSFULLY = "STUDENT CLASS ADDED SUCCESSFULLY";


    @PostMapping(VALIDATE_STUDENT_AMOUNT_STUDENT)
    ResponseEntity<ResponseDtout> addAmountToStudentAccountCodeValidation(@RequestParam("studentId") Integer studentId, @RequestParam("validationCode") Integer validationCode, @RequestParam("amount") Double amount) throws InvalidUserInformationException, InvalidTokenException, ExpiredToken, StudentNotFoundException;

    @PutMapping(SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT)
    ResponseEntity<ResponseDtout> attemptAddAmountToStudentAccount(@RequestParam("studentId") Integer studentId, @RequestParam("amount") Double amount) throws StudentNotFoundException, InvalidUserInformationException, MessagingException;

    @GetMapping(GET_STUDENT_BY_ID)
    ResponseEntity<StudentDtout> getStudentById(@RequestParam("studentId") Integer studentId) throws InvalidUserInformationException, StudentNotFoundException;

    @GetMapping(GET_STUDENTS)
    ResponseEntity<List<StudentDtout>> getStudents(@RequestParam String firstname, @RequestParam String Lastname, @RequestParam String birthdateAsString) throws InvalidUserInformationException;

    ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;

    ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;
}
