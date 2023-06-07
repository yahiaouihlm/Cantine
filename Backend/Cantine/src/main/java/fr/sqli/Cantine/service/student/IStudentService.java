package fr.sqli.Cantine.service.student;

import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFound;

public interface IStudentService {


     void   signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, StudentClassNotFound;

}
