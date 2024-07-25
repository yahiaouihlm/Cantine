package fr.sqli.cantine.controller.users.student;

import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.ResponseDtout;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.student.Impl.StudentService;
import fr.sqli.cantine.service.users.exceptions.AccountActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.util.List;

import static fr.sqli.cantine.constants.ConstCantine.ADMIN_ROLE_LABEL;
import static fr.sqli.cantine.constants.ConstCantine.STUDENT_ROLE_LABEL;

@RestController
@RequestMapping(StudentController.STUDENT_BASIC_URL)
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {

    final static String STUDENT_BASIC_URL = "/cantine/user/student";
    final String STUDENT_SIGN_UP_ENDPOINT = "/signUp";
    final String GET_STUDENT_BY_ID_ENDPOINT = "/getStudent";
    final String UPDATE_STUDENT_INFO_ENDPOINT = "/update/studentInfo";
    final String GET_ALL_STUDENT_CLASS = "/getAllStudentClass";
    final String STUDENT_SIGNED_UP_SUCCESSFULLY = "STUDENT SAVED SUCCESSFULLY";
    final String STUDENT_INFO_UPDATED_SUCCESSFULLY = "STUDENT UPDATED SUCCESSFULLY";

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping(GET_STUDENT_BY_ID_ENDPOINT)
    public ResponseEntity<StudentDtout> getStudentByUuid(@RequestParam("studentUuid") String studentUuid) throws InvalidUserInformationException, UserNotFoundException {

        var student = this.studentService.getStudentByUuid(studentUuid);
        return ResponseEntity
                .ok()
                .body(student);
    }
    @PreAuthorize("hasAuthority('" + STUDENT_ROLE_LABEL + "')")
    @PostMapping(UPDATE_STUDENT_INFO_ENDPOINT)
    public ResponseEntity<ResponseDtout> updateStudentInformation(@ModelAttribute StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException, UserNotFoundException {
        this.studentService.updateStudentInformation(studentDtoIn);
        return ResponseEntity.ok(new ResponseDtout(STUDENT_INFO_UPDATED_SUCCESSFULLY));
    }

    @PostMapping(STUDENT_SIGN_UP_ENDPOINT)
    public ResponseEntity<ResponseDtout> signUpStudent(@ModelAttribute StudentDtoIn studentDtoIn) throws UserNotFoundException, InvalidStudentClassException, MessagingException, InvalidFormatImageException, AccountActivatedException, RemovedAccountException, InvalidImageException, InvalidUserInformationException, StudentClassNotFoundException, ImagePathException, IOException, ExistingUserException, RoleNotFoundException {
        this.studentService.signUpStudent(studentDtoIn);
        return ResponseEntity.ok(new ResponseDtout(STUDENT_SIGNED_UP_SUCCESSFULLY));
    }

    @GetMapping(GET_ALL_STUDENT_CLASS)
    public ResponseEntity<List<StudentClassDtout>> getAllStudentClass() {
        return ResponseEntity.ok(this.studentService.getAllStudentClass());
    }
}
