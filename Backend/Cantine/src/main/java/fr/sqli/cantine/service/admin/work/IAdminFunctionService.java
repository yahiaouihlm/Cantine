package fr.sqli.cantine.service.admin.work;

import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.*;
import fr.sqli.cantine.service.admin.exceptions.*;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;

import java.util.List;

public interface IAdminFunctionService {





    void addAmountToStudentAccountCodeValidation(Integer studentId , Integer validationCode , Double amount) throws InvalidPersonInformationException, StudentNotFoundException, ExpiredToken, InvalidTokenException;

    void attemptAddAmountToStudentAccount(Integer studentId, Double amount) throws StudentNotFoundException, InvalidPersonInformationException, MessagingException;

    StudentDtout  getStudentById(Integer studentID) throws InvalidPersonInformationException, StudentNotFoundException;

    List<StudentDtout> getStudentsByNameAndBirthdate( String  firstname , String  lastname  , String  birthdateAsString) throws InvalidPersonInformationException;
    void updateStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;

    void  addStudentClass  (StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;
}
