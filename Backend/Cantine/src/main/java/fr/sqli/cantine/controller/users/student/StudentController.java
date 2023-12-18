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
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(StudentController.STUDENT_BASIC_URL)
public class StudentController implements IStudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /* TODO  :    change  get Student  Image  URl  */




    @Override
    public ResponseEntity<ResponseDtout> checkLinkValidity(String token) throws UserNotFoundException, InvalidTokenException, ExpiredToken, TokenNotFoundException {
        this.studentService.checkLinkValidity(token);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_CHECKED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<ResponseDtout> sendStudentConfirmationLink(String email) throws UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException {
        this.studentService.sendConfirmationLink(email);
        return ResponseEntity.ok(new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }


    @Override
    public ResponseEntity<StudentDtout> getStudentByUuid( String studentUuid ) throws InvalidUserInformationException, UserNotFoundException {

        var student = this.studentService.getStudentByUuid(studentUuid);
        return ResponseEntity
                .ok()
                .body(student);
    }

    @Override
    @PutMapping(UPDATE_STUDENT_INFO_ENDPOINT)
    public ResponseEntity<ResponseDtout> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, InvalidFormatImageException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException, UserNotFoundException {
        this.studentService.updateStudentInformation(studentDtoIn);
        return ResponseEntity.ok(new ResponseDtout(STUDENT_INFO_UPDATED_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<ResponseDtout> signUpStudent(@ModelAttribute StudentDtoIn studentDtoIn) throws UserNotFoundException, InvalidStudentClassException, MessagingException, InvalidFormatImageException, AccountAlreadyActivatedException, RemovedAccountException, InvalidImageException, InvalidUserInformationException, StudentClassNotFoundException, ImagePathException, IOException, ExistingUserException {
        this.studentService.signUpStudent(studentDtoIn);
        return ResponseEntity.ok(new ResponseDtout(STUDENT_SIGNED_UP_SUCCESSFULLY));
    }

    @Override
    public ResponseEntity<List<StudentClassDtout>> getAllStudentClass() {
        return ResponseEntity.ok(this.studentService.getAllStudentClass());
    }
}
