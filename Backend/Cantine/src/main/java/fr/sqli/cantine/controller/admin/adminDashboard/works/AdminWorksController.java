package fr.sqli.cantine.controller.admin.adminDashboard.works;


import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.person.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.ExistingStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.admin.adminDashboard.work.AdminWorksService;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<StudentDtout> getStudentById(Integer studentId) throws InvalidPersonInformationException, StudentNotFoundException {
        return  ResponseEntity.ok(this.adminWorksService.getStudentById(studentId));
    }

    @Override
    public ResponseEntity<List<StudentDtout>> getStudents( String  firstname , String  lastname  ,  String  birthdateAsString) throws InvalidPersonInformationException {
        return ResponseEntity.ok(this.adminWorksService.getStudentsByNameAndBirthdate(firstname ,lastname  , birthdateAsString));
    }



    @Override
    @PostMapping(ADD_STUDENT_CLASS_ENDPOINT)
    public ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException {
        this.adminWorksService.addStudentClass(studentClassDtoIn);
        return ResponseEntity.ok(STUDENT_CLASS_ADDED_SUCCESSFULLY);
    }


    @Override
    @PutMapping(UPDATE_STUDENT_CLASS_ENDPOINT)
    public ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException {
        this.adminWorksService.updateStudentClass(studentClassDtoIn);
        return ResponseEntity.ok(STUDENT_CLASS_UPDATED_SUCCESSFULLY);
    }
}
