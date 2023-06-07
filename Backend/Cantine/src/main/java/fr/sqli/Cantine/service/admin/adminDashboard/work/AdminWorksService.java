package fr.sqli.Cantine.service.admin.adminDashboard.work;

import fr.sqli.Cantine.dao.*;

import fr.sqli.Cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.Cantine.entity.StudentClassEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.ExistingStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.mailer.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminWorksService implements  IAdminFunctionService{

    private IAdminDao adminDao;
    private IFunctionDao functionDao;
    private ImageService imageService;
    private Environment environment;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private IConfirmationTokenDao confirmationTokenDao;
    private EmailSenderService emailSenderService;

    private IStudentDao studentDao;

    private IStudentClassDao studentClassDao;

    @Autowired
    public AdminWorksService( IStudentClassDao iStudentClassDao) {
        this.studentClassDao = iStudentClassDao;
    }


    @Override
    public void addStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException {
        StudentClassEntity studentClassEntity = studentClassDtoIn.toStudentClassEntity();
        if  (this.studentClassDao.findByName(studentClassEntity.getName()).isPresent()) {
            throw new ExistingStudentClassException("STUDENT CLASS ALREADY EXIST");

        }
        this.studentClassDao.save(studentClassEntity);
    }



    @Override
    public void updateStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException {
        studentClassDtoIn.checkIdValidity();
        StudentClassEntity studentClassEntity = studentClassDtoIn.toStudentClassEntity();

        var  studentClass = this.studentClassDao.findById(studentClassDtoIn.getId());
        if  (studentClass.isEmpty()) {
            throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        }
        studentClass.get().setName((studentClassEntity.getName()));
        this.studentClassDao.save(studentClass.get());
    }
}
