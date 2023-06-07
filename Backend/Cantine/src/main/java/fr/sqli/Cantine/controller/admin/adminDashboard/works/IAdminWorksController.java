package fr.sqli.Cantine.controller.admin.adminDashboard.works;


import fr.sqli.Cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingStudentClass;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAdminWorksController {

    String ADMIN_DASH_BOARD_BASIC_WORK_URL = "/cantine/admin/adminDashboard/works";

    String  ADD_STUDENT_CLASS_ENDPOINT = "/addStudentClass";

    String STUDENT_CLASS_ADDED_SUCCESSFULLY = "STUDENT CLASS ADDED SUCCESSFULLY";
    public ResponseEntity<String> addStudentClass(@RequestBody StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClass;
}
