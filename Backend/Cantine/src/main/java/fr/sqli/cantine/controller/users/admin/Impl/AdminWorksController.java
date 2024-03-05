package fr.sqli.cantine.controller.users.admin.Impl;


import fr.sqli.cantine.controller.users.admin.IAdminWorksController;
import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.admin.impl.AdminWorksService;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(IAdminWorksController.ADMIN_DASH_BOARD_BASIC_WORK_URL)
public class AdminWorksController  implements  IAdminWorksController {

    private AdminWorksService adminWorksService;

    @Autowired
    public  AdminWorksController  (AdminWorksService adminWorksService){
        this.adminWorksService = adminWorksService;
    }


    @Override
    public ResponseEntity<ResponseDtout> updateStudentEmail(String studentUuid, String newEmail) throws UserNotFoundException, MessagingException, ExistingUserException, InvalidUserInformationException {
        this.adminWorksService.updateStudentEmail(studentUuid , newEmail);
        return  ResponseEntity.ok(new ResponseDtout(STUDENT_EMAIL_UPDATED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<ResponseDtout> addAmountToStudentAccountCodeValidation(Integer studentId, Integer validationCode, Double amount) throws InvalidUserInformationException, InvalidTokenException, ExpiredToken, UserNotFoundException {
         this.adminWorksService.addAmountToStudentAccountCodeValidation(studentId ,  validationCode , amount);
        return  ResponseEntity.ok(new ResponseDtout(STUDENT_VALIDATE_STUDENT_AMOUNT));
    }

    @Override
    public ResponseEntity<ResponseDtout> attemptAddAmountToStudentAccount(Integer studentId, Double amount) throws InvalidUserInformationException, MessagingException, UserNotFoundException {
        this.adminWorksService.attemptAddAmountToStudentAccount(studentId , amount);
        return ResponseEntity.ok(new ResponseDtout(SEND_NEW_AMOUNT_TO_STUDENT_NOTIFICATION));
    }

    @Override
    public ResponseEntity<StudentDtout> getStudentById(String  studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        return  ResponseEntity.ok(this.adminWorksService.getStudentByUuid(studentUuid));
    }

    @Override
    public ResponseEntity<List<StudentDtout>> getStudents( String  firstname , String  lastname  ,  String  birthdateAsString) throws InvalidUserInformationException {
        return ResponseEntity.ok(this.adminWorksService.getStudentsByNameAndBirthdate(firstname ,lastname  , birthdateAsString));
    }



    @Override
    @PostMapping(ADD_STUDENT_CLASS_ENDPOINT)
    public ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException {
        this.adminWorksService.addStudentClass(studentClassDtoIn);
        return ResponseEntity.ok(STUDENT_CLASS_ADDED_SUCCESSFULLY);
    }

    @Override
    public ResponseEntity<StudentDtout> getStudentByEmail(String email) throws UserNotFoundException, InvalidUserInformationException {
        return ResponseEntity.ok(this.adminWorksService.getStudentByEmail(email));
    }


    @Override
    @PutMapping(UPDATE_STUDENT_CLASS_ENDPOINT)
    public ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException {
        this.adminWorksService.updateStudentClass(studentClassDtoIn);
        return ResponseEntity.ok(STUDENT_CLASS_UPDATED_SUCCESSFULLY);
    }
}
