package fr.sqli.cantine.service.admin.adminDashboard.work;

import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.person.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.ExistingStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;

import java.util.List;

public interface IAdminFunctionService {



    List<StudentDtout> getStudentByNameAndBirthdate(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException;
    void updateStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;

    void  addStudentClass  (StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;
}
