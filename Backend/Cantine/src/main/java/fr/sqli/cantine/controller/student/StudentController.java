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
import fr.sqli.cantine.service.student.StudentService;
import fr.sqli.cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(StudentController.STUDENT_BASIC_URL)
public class StudentController implements IStudentController {

   private StudentService studentService;
   @Autowired
   public  StudentController( StudentService studentService) {
        this.studentService = studentService;
   }

 /* TODO  :    change  get Student  Image  URl  */


    @Override
    public ResponseEntity<List<StudentClassDtout>> getAllStudentClass() {
        return ResponseEntity.ok(this.studentService.getAllStudentClass());
    }

    /*@Override
    public ResponseEntity<ResponseDtout> sendTokenStudent(@RequestParam("email") String email) throws InvalidPersonInformationException, MessagingException, AccountAlreadyActivatedException, StudentNotFoundException {
        this.studentService.sendTokenStudent(email);
            return ResponseEntity.ok(  new ResponseDtout(TOKEN_SENT_SUCCESSFULLY));
    }
*/

    @Override
    public ResponseEntity<StudentDtout> getStudentById(@RequestParam("idStudent") Integer id) throws StudentNotFoundException, InvalidPersonInformationException {

      var student =  this.studentService.getStudentByID(id);
         return ResponseEntity
                 .ok()
                 .body(student);
    }

    @Override
   @PutMapping(UPDATE_STUDENT_INFO_ENDPOINT)
   public ResponseEntity<ResponseDtout> updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, InvalidFormatImageException, StudentNotFoundException, InvalidImageException, StudentClassNotFoundException, ImagePathException, IOException {
         this.studentService.updateStudentInformation(studentDtoIn);
         return ResponseEntity.ok(new ResponseDtout (STUDENT_INFO_UPDATED_SUCCESSFULLY));
   }

 @Override
 public ResponseEntity<ResponseDtout> signUpStudent(@ModelAttribute StudentDtoIn studentDtoIn) throws InvalidPersonInformationException,
         InvalidStudentClassException, InvalidFormatImageException, InvalidImageException,
         StudentClassNotFoundException, ImagePathException, IOException, ExistingStudentException {
     System.out.println(studentDtoIn.getPhone());
       this.studentService.signUpStudent(studentDtoIn);
        return ResponseEntity.ok(  new ResponseDtout(STUDENT_SIGNED_UP_SUCCESSFULLY));
 }
}