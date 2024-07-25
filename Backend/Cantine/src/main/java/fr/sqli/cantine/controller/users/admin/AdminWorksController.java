package fr.sqli.cantine.controller.users.admin;



import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.dto.out.person.TransactionDtout;
import fr.sqli.cantine.service.users.admin.impl.AdminWorksService;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AdminWorksController.ADMIN_DASH_BOARD_BASIC_WORK_URL)
public class AdminWorksController {
    final static  String ADMIN_DASH_BOARD_BASIC_WORK_URL = "/cantine/admin/adminDashboard/works";
    final String GET_STUDENT_TRANSACTIONS_ENDPOINT = "/getStudentTransactions";
    final String SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT = "/addStudentAmount";
    final String  VALIDATE_STUDENT_AMOUNT_STUDENT =  "/validationAmount";
    final String GET_STUDENTS = "/getStudents";
    final String GET_STUDENT = "/getStudent";
    final String ADD_STUDENT_CLASS_ENDPOINT = "/addStudentClass";
    final String UPDATE_STUDENT_CLASS_ENDPOINT = "/updateStudentClass";
    final String GET_STUDENT_BY_UUID = "/getStudentByUuId";
    final String UPDATE_STUDENT_EMAIL = "/updateStudentEmail";
    /**************************** SERVER ANSWERS ************************************/

    final String AMOUNT_ADDED_SUCCESSFULLY = "AMOUNT ADDED SUCCESSFULLY";
    final String SEND_NEW_AMOUNT_TO_STUDENT_NOTIFICATION = "NOTIFICATION SENT SUCCESSFULLY";
    final String STUDENT_CLASS_UPDATED_SUCCESSFULLY = "STUDENT CLASS UPDATED SUCCESSFULLY";
    final String STUDENT_CLASS_ADDED_SUCCESSFULLY = "STUDENT CLASS ADDED SUCCESSFULLY";
    final String STUDENT_EMAIL_UPDATED_SUCCESSFULLY = "STUDENT EMAIL UPDATED SUCCESSFULLY";


    private final AdminWorksService adminWorksService;

    @Autowired
    public  AdminWorksController  (AdminWorksService adminWorksService){
        this.adminWorksService = adminWorksService;
    }


    @GetMapping(GET_STUDENT_TRANSACTIONS_ENDPOINT)
    public ResponseEntity<List<TransactionDtout>> getStudentTransactions(@RequestParam("studentUuid") String studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        return ResponseEntity.ok(this.adminWorksService.getStudentTransactions(studentUuid));
    }

    @PostMapping(UPDATE_STUDENT_EMAIL)
    public ResponseEntity<ResponseDtout> updateStudentEmail(@RequestParam("studentUuid") String studentUuid, @RequestParam("newEmail") String newEmail) throws UserNotFoundException, MessagingException, ExistingUserException, InvalidUserInformationException {
        this.adminWorksService.updateStudentEmail(studentUuid , newEmail);
        return  ResponseEntity.ok(new ResponseDtout(STUDENT_EMAIL_UPDATED_SUCCESSFULLY));
    }

    @PostMapping(VALIDATE_STUDENT_AMOUNT_STUDENT)
    public ResponseEntity<ResponseDtout> addAmountToStudentAccountCodeValidation( @RequestParam("adminUuid")String  adminUuid , @RequestParam("studentUuid")String studentUuid, @RequestParam("validationCode")Integer validationCode,@RequestParam("amount") Double amount) throws InvalidUserInformationException, InvalidTokenException, ExpiredToken, UserNotFoundException, UnknownUser, MessagingException {
         this.adminWorksService.addAmountToStudentAccountCodeValidation(adminUuid ,  studentUuid , validationCode ,amount);
        return  ResponseEntity.ok(new ResponseDtout(AMOUNT_ADDED_SUCCESSFULLY));
    }

    @PostMapping(SEND_STUDENT_AMOUNT_NOTIFICATION_ENDPOINT)
    public ResponseEntity<ResponseDtout> attemptAddAmountToStudentAccount(@RequestParam("adminUuid")String adminUuid,@RequestParam("studentUuid") String studentUuid, @RequestParam("amount") Double amount)  throws InvalidUserInformationException, MessagingException, UserNotFoundException, UnknownUser {
        this.adminWorksService.attemptAddAmountToStudentAccount(adminUuid , studentUuid , amount);
        return ResponseEntity.ok(new ResponseDtout(SEND_NEW_AMOUNT_TO_STUDENT_NOTIFICATION));
    }

    @GetMapping(GET_STUDENT_BY_UUID)
    public ResponseEntity<StudentDtout> getStudentById(@RequestParam("studentUuid") String studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        return  ResponseEntity.ok(this.adminWorksService.getStudentByUuid(studentUuid));
    }

    @GetMapping(GET_STUDENTS)
    public ResponseEntity<List<StudentDtout>> getStudents(@RequestParam String firstname, @RequestParam String lastname, @RequestParam String birthdateAsString) throws InvalidUserInformationException {
        return ResponseEntity.ok(this.adminWorksService.getStudentsByNameAndBirthdate(firstname ,lastname  , birthdateAsString));
    }

    @PostMapping(ADD_STUDENT_CLASS_ENDPOINT)
    public ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException {
        this.adminWorksService.addStudentClass(studentClassDtoIn);
        return ResponseEntity.ok(STUDENT_CLASS_ADDED_SUCCESSFULLY);
    }

    @GetMapping(GET_STUDENT)
    public ResponseEntity<StudentDtout> getStudentByEmail(@RequestParam("email") String email)  throws UserNotFoundException, InvalidUserInformationException {
        return ResponseEntity.ok(this.adminWorksService.getStudentByEmail(email));
    }

    @PutMapping(UPDATE_STUDENT_CLASS_ENDPOINT)
    public ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException {
        this.adminWorksService.updateStudentClass(studentClassDtoIn);
        return ResponseEntity.ok(STUDENT_CLASS_UPDATED_SUCCESSFULLY);
    }


}
