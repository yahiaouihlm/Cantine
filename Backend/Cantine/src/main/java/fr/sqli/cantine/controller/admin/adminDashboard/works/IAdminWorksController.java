package fr.sqli.cantine.controller.admin.adminDashboard.works;


import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.person.StudentDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.ExistingStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public interface IAdminWorksController {

    String ADMIN_DASH_BOARD_BASIC_WORK_URL = "/cantine/admin/adminDashboard/works";
     String   SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT = "/addStudentAmount";

    String GET_STUDENTS = "/getStudents";
    String  GET_STUDENT_BY_ID =  "/getStudent";
    String  ADD_STUDENT_CLASS_ENDPOINT = "/addStudentClass";
    String UPDATE_STUDENT_CLASS_ENDPOINT = "/updateStudentClass";


    /**************************** SERVER ANSWERS ************************************/

    String SEND_NEW_AMOUNT_TO_STUDENT_NOTIFICATION = "NOTIFICATION SENT SUCCESSFULLY";

    String  STUDENT_CLASS_UPDATED_SUCCESSFULLY = "STUDENT CLASS UPDATED SUCCESSFULLY";
    String STUDENT_CLASS_ADDED_SUCCESSFULLY = "STUDENT CLASS ADDED SUCCESSFULLY";



    ResponseEntity<ResponseDtout> addAmountToStudentAccountCodeValidation( @RequestParam("studentId") Integer studentId,@RequestParam("validationCode")  Integer validationCode,  @RequestParam("amount") Double amount);

    @PutMapping(SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT)
    ResponseEntity<ResponseDtout>attemptAddAmountToStudentAccount(@RequestParam("studentId") Integer studentId , @RequestParam("amount") Double amount) throws StudentNotFoundException, InvalidPersonInformationException, MessagingException;

    @GetMapping(GET_STUDENT_BY_ID)
    ResponseEntity<StudentDtout>getStudentById(@RequestParam("studentId") Integer studentId) throws InvalidPersonInformationException, StudentNotFoundException;

    @GetMapping(GET_STUDENTS)
    ResponseEntity<List<StudentDtout>> getStudents (@RequestParam String firstname , @RequestParam String Lastname , @RequestParam String  birthdateAsString) throws InvalidPersonInformationException;

    ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;
    ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;
}
