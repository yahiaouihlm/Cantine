package fr.sqli.cantine.service.users.student;

import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountAlreadyActivatedException;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;

public interface IStudentService {


    void checkLinkValidity(String token) throws InvalidTokenException, TokenNotFoundException, ExpiredToken, UserNotFoundException;

    void sendConfirmationLink(String email) throws UserNotFoundException, RemovedAccountException, AccountAlreadyActivatedException, MessagingException;

    void updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException;

    StudentDtout getStudentByUuid(String studentUuid) throws InvalidUserInformationException, UserNotFoundException;

    void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException, MessagingException, AccountAlreadyActivatedException, RemovedAccountException, ExistingUserException;

    void existingEmail(String adminEmail) throws ExistingUserException;

    List<StudentClassDtout> getAllStudentClass();

    static void checkUuIdValidity(String studentUuid) throws InvalidUserInformationException {
        if (studentUuid == null || studentUuid.isBlank() || studentUuid.length() < 20) {
            throw new InvalidUserInformationException("INVALID UUID");
        }
    }
}
