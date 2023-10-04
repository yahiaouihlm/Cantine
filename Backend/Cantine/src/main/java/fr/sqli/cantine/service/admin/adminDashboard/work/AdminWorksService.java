package fr.sqli.cantine.service.admin.adminDashboard.work;

import fr.sqli.cantine.dao.*;

import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.person.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.ExistingStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.mailer.EmailSenderService;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminWorksService implements  IAdminFunctionService{
    private static final Logger LOG = LogManager.getLogger();
    private  final  String  STUDENT_IMAGE_URL ;
    private IStudentDao studentDao;

    private IStudentClassDao studentClassDao;

    private Environment environment;
    @Autowired
    public AdminWorksService( IStudentClassDao iStudentClassDao , IStudentDao studentDao , Environment environment) {
        this.studentClassDao = iStudentClassDao;
        this.studentDao = studentDao ;
        this.environment = environment ;
        this.STUDENT_IMAGE_URL =  this.environment.getProperty("sqli.cantine.images.url.student");
    }


    @Override
    public void addStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException {
        if (studentClassDtoIn == null) {
            throw new InvalidStudentClassException("STUDENT CLASS IS NULL");
        }

        StudentClassEntity studentClassEntity = studentClassDtoIn.toStudentClassEntity();
        if  (this.studentClassDao.findByName(studentClassEntity.getName()).isPresent()) {
            throw new ExistingStudentClassException("STUDENT CLASS ALREADY EXIST");

        }
        this.studentClassDao.save(studentClassEntity);
    }


    @Override
    public StudentDtout getStudentById(Integer studentID) throws InvalidPersonInformationException, StudentNotFoundException {
          if  (studentID  == null ){
              AdminWorksService.LOG.error("studentID IS  NULL  IN  getStudentById ADMIN WORK SERVICE ");
              throw  new  InvalidPersonInformationException("INVALID  STUDENT ID ");
          }
          var  student   =  this.studentDao.findById(studentID) ;
          if  (student.isEmpty()){
              AdminWorksService.LOG.error("STUDENT  WITH  ID =  " + studentID + "  DOEST NOT EXISTS" );
              throw   new StudentNotFoundException("STUDENT NOT  FOUND");
          }

          return   new StudentDtout(student.get(), this.STUDENT_IMAGE_URL);
    }

    @Override
    public List<StudentDtout> getStudentsByNameAndBirthdate(String  firstname , String  lastname  ,String  birthdateAsString) throws InvalidPersonInformationException {
         StudentDtoIn studentDtoIn = new StudentDtoIn();
         studentDtoIn.setFirstname(firstname);
         studentDtoIn.setLastname(lastname);
         studentDtoIn.setBirthdateAsString(birthdateAsString);

         if  (studentDtoIn.getLastname() == null  || studentDtoIn.getFirstname() == null || studentDtoIn.getBirthdate()==null ){
            AdminWorksService.LOG.error("INVALID  REQUEST LASTNAME OR FIRSTNAME OR  BIRTHDATE ARE NOT ");
            throw   new InvalidPersonInformationException("INVALID  FIELD");
        }

        return this.studentDao.findByFirstnameAndLastnameAndBirthdate(studentDtoIn.getFirstname() ,studentDtoIn.getLastname(),studentDtoIn.getBirthdate())
                .stream().map(student -> new StudentDtout(student , this.STUDENT_IMAGE_URL)).toList();


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
