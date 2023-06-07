package fr.sqli.Cantine.service.admin.adminDashboard.work;

import fr.sqli.Cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingStudentClass;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;

public interface IAdminFunctionService {


    void  addStudentClass  (StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClass;
}
