package fr.sqli.cantine.service.users.admin.work;

import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.student.exceptions.StudentNotFoundException;
import fr.sqli.cantine.service.users.admin.exceptions.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface IAdminFunctionService {





    void addAmountToStudentAccountCodeValidation(Integer studentId , Integer validationCode , Double amount) throws InvalidUserInformationException, StudentNotFoundException, ExpiredToken, InvalidTokenException;

    void attemptAddAmountToStudentAccount(Integer studentId, Double amount) throws StudentNotFoundException, InvalidUserInformationException, MessagingException;

    StudentDtout  getStudentById(Integer studentID) throws InvalidUserInformationException, StudentNotFoundException;

    List<StudentDtout> getStudentsByNameAndBirthdate( String  firstname , String  lastname  , String  birthdateAsString) throws InvalidUserInformationException;
    void updateStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;

    void  addStudentClass  (StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;
}
