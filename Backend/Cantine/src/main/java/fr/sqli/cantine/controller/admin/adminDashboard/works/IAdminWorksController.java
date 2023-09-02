package fr.sqli.cantine.controller.admin.adminDashboard.works;


import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.ExistingStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAdminWorksController {

    String ADMIN_DASH_BOARD_BASIC_WORK_URL = "/cantine/admin/adminDashboard/works";

    String  ADD_STUDENT_CLASS_ENDPOINT = "/addStudentClass";
    String UPDATE_STUDENT_CLASS_ENDPOINT = "/updateStudentClass";
   String  STUDENT_CLASS_UPDATED_SUCCESSFULLY = "STUDENT CLASS UPDATED SUCCESSFULLY";
    String STUDENT_CLASS_ADDED_SUCCESSFULLY = "STUDENT CLASS ADDED SUCCESSFULLY";
    public ResponseEntity<String> updateStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;
    public ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;
}
