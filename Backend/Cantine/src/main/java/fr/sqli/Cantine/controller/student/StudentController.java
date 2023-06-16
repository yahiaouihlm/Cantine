package fr.sqli.Cantine.controller.student;

import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.dto.out.person.StudentDtout;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.student.StudentService;
import fr.sqli.Cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(StudentController.STUDENT_BASIC_URL)
public class StudentController implements IStudentController {

   private StudentService studentService;
   @Autowired
   public  StudentController( StudentService studentService) {
        this.studentService = studentService;
   }


    @Override
    @GetMapping(GET_STUDENT_BY_ID_ENDPOINT)
    public ResponseEntity<StudentDtout> getStudentById(@RequestParam("idStudent") Integer id) throws StudentNotFoundException, InvalidPersonInformationException {
      var student =  this.studentService.getStudentByID(id);
         return ResponseEntity
                 .ok()
                 .body(student);
    }

    @Override
   @PutMapping(UPDATE_STUDENT_INFO_ENDPOINT)
   public ResponseEntity<String> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, StudentNotFoundException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException {
         this.studentService.updateStudentInformation(studentDtoIn);
         return ResponseEntity.ok(STUDENT_INFO_UPDATED_SUCCESSFULLY);
   }

 @Override
 @PostMapping(STUDENT_SIGN_UP_ENDPOINT)
 public ResponseEntity<String> signUpStudent(@ModelAttribute StudentDtoIn studentDtoIn) throws InvalidPersonInformationException,
         InvalidStudentClassException, InvalidFormatImageException, InvalidImageException,
         StudentClassNotFoundException, ImagePathException, IOException, ExistingStudentException {

       this.studentService.signUpStudent(studentDtoIn);
        return ResponseEntity.ok(STUDENT_SIGNED_UP_SUCCESSFULLY);
 }
}
