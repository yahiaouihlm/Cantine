package fr.sqli.cantine.service.users.admin;

import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;

import java.util.List;

public interface IAdminFunctionService {


    void addAmountToStudentAccountCodeValidation(String adminUuid, String studentUuid, Integer validationCode, Double amount) throws InvalidUserInformationException, ExpiredToken, InvalidTokenException, UserNotFoundException;

    void attemptAddAmountToStudentAccount(String adminUuid, String studentUuid, Double amount) throws InvalidUserInformationException, MessagingException, UserNotFoundException, UnknownUser;

    public StudentDtout getStudentByUuid(String studentUuid) throws InvalidUserInformationException, UserNotFoundException;

    List<StudentDtout> getStudentsByNameAndBirthdate(String firstname, String lastname, String birthdateAsString) throws InvalidUserInformationException;

    void updateStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException;

    void addStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException;

    StudentDtout getStudentByEmail(String email) throws InvalidUserInformationException, UserNotFoundException;

    void updateStudentEmail(String studentUuid, String newEmail) throws InvalidUserInformationException, UserNotFoundException, ExistingUserException, MessagingException;
}
