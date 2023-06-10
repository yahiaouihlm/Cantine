package fr.sqli.Cantine.service.student;

import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;

import java.io.IOException;

public interface IStudentService {

     public  void  updateStudentInformation (StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, StudentNotFoundException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException;
          void   signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingStudentException;
     public void existingStudent(String  adminEmail ) throws ExistingStudentException;

}
