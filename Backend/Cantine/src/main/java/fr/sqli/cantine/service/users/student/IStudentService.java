package fr.sqli.cantine.service.users.student;

import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.users.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.student.exceptions.ExistingStudentException;
import fr.sqli.cantine.service.users.student.exceptions.StudentNotFoundException;

import java.io.IOException;
import java.util.List;

public interface IStudentService {


    List<StudentClassDtout> getAllStudentClass() ;
   // void sendTokenStudent( String  email ) throws InvalidPersonInformationException, StudentNotFoundException, AccountAlreadyActivatedException, MessagingException;

       void  updateStudentInformation (StudentDtoIn studentDtoIn) throws InvalidUserInformationException, StudentNotFoundException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException;

        StudentDtout getStudentByID (Integer  id) throws InvalidUserInformationException, StudentNotFoundException ;
       void   signUpStudent(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingStudentException;
      void existingStudent(String  adminEmail ) throws ExistingStudentException;

}
