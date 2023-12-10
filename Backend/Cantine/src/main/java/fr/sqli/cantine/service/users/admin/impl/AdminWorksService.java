package fr.sqli.cantine.service.users.admin.impl;

import fr.sqli.cantine.dao.*;

import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.mailer.ConfirmationAddingAmountToStudent;
import fr.sqli.cantine.service.users.admin.IAdminFunctionService;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminWorksService implements IAdminFunctionService {
    private static final Logger LOG = LogManager.getLogger();
    private  final  Integer  MAX_STUDENT_WALLET  =   3000;
    private final String STUDENT_IMAGE_URL;
    private IStudentDao studentDao;

    private IStudentClassDao studentClassDao;

    private Environment environment;
    private IConfirmationTokenDao confirmationTokenDao;
    private ConfirmationAddingAmountToStudent confirmationAddingAmountToStudent;

    @Autowired
    public AdminWorksService(IStudentClassDao iStudentClassDao, IStudentDao studentDao, Environment environment,
                             ConfirmationAddingAmountToStudent confirmationAddingAmountToStudent, IConfirmationTokenDao confirmationTokenDao) {
        this.studentClassDao = iStudentClassDao;
        this.studentDao = studentDao;
        this.environment = environment;
        this.confirmationAddingAmountToStudent = confirmationAddingAmountToStudent;
        this.confirmationTokenDao = confirmationTokenDao;
        this.STUDENT_IMAGE_URL = this.environment.getProperty("sqli.cantine.images.url.student");
    }


    @Override
    public void addStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, ExistingStudentClassException {
        if (studentClassDtoIn == null) {
            throw new InvalidStudentClassException("STUDENT CLASS IS NULL");
        }

        StudentClassEntity studentClassEntity = studentClassDtoIn.toStudentClassEntity();
        if (this.studentClassDao.findByName(studentClassEntity.getName()).isPresent()) {
            throw new ExistingStudentClassException("STUDENT CLASS ALREADY EXIST");

        }
        this.studentClassDao.save(studentClassEntity);
    }


    @Override
    public void addAmountToStudentAccountCodeValidation(Integer studentId, Integer validationCode, Double amount) throws InvalidUserInformationException, ExpiredToken, InvalidTokenException, UserNotFoundException {

        if (studentId == null || validationCode == null || amount == null || amount < 10 || amount > 200) {
            AdminWorksService.LOG.error("INVALID  studentId Or validationCode or Amount CAN  NOT BE NULL ");
            throw new InvalidUserInformationException("INVALID FIELDS ");
        }


        //  check if student is valid
        var student = this.studentDao.findById(studentId).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error("STUDENT NOT  FOUND ");
                    return new UserNotFoundException("STUDENT NOT  FOUND");
                }
        );

        // find  the  confirmationToken by the  student
        var confirmationToken = this.confirmationTokenDao.findByStudent(student).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error(" NO CONFIRMATION TOKEN HAS  BEEN  FOUND ");
                    return new InvalidUserInformationException("NO  CONFIRMATION CODE HAS  BEEN  FOUND ");
                }
        );


        // check  if  the  code found with the  confirmation  token is equals with  the  validationCode SENT  by user
        if (confirmationToken.getUuid().intValue() != validationCode) {
            AdminWorksService.LOG.error("STUDENT FOUND  BUT  THE  CODE ARE  NOT  THE  SAME ");
            throw new InvalidTokenException("INVALID   CODE ");
        }


        var expiredTime = System.currentTimeMillis() - confirmationToken.getCreatedDate().getTime();
        long fiveMinutesInMillis = 2 * 60 * 1000; //  2 MINUTES

        //  expired  token  ///
        if (expiredTime > fiveMinutesInMillis) {
            this.confirmationTokenDao.delete(confirmationToken);
            AdminWorksService.LOG.error(" CONFIRMATION CODE IS  EXPIRED ");
            throw new ExpiredToken("CONFIRMATION  CODE IS  EXPIRED ");
        }



        //  if we want we can add the condifition     than  student  wallet  must  be  less than  3000

        var  newWallet  =  student.getWallet().add(new BigDecimal(amount));
         if  (newWallet.compareTo(new BigDecimal(MAX_STUDENT_WALLET)) > 0 ){
             AdminWorksService.LOG.error("WALLET  TO  BIG");
             throw  new InvalidUserInformationException("EXCESSIVE AMOUNT");
         }


        // add  new amount  to student  account

        student.setWallet(student.getWallet().add(new BigDecimal(amount)));
        this.studentDao.save(student);

    }

    @Override
    public void attemptAddAmountToStudentAccount(Integer studentId, Double amount) throws InvalidUserInformationException, MessagingException, UserNotFoundException {
        if (studentId == null || amount == null || amount < 10 || amount > 200) {
            throw new InvalidUserInformationException("INVALID  STUDENT ID OR AMOUNT");
        }
        var student = this.studentDao.findById(studentId);
        if (student.isEmpty()) {
            throw new UserNotFoundException("STUDENT NOT FOUND");
        }

        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(student.get());
        this.confirmationTokenDao.findByStudent(student.get()).ifPresent(confirmationTokenEntity -> {
            this.confirmationTokenDao.delete(confirmationTokenEntity);
        });
        this.confirmationTokenDao.save(confirmationToken);

        this.confirmationAddingAmountToStudent.sendConfirmationAmount(student.get(), amount, confirmationToken);
    }

    @Override
    public StudentDtout getStudentById(Integer studentID) throws InvalidUserInformationException, UserNotFoundException {
        if (studentID == null) {
            AdminWorksService.LOG.error("studentID IS  NULL  IN  getStudentById ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  STUDENT ID ");
        }
        var student = this.studentDao.findById(studentID);
        if (student.isEmpty()) {
            AdminWorksService.LOG.error("STUDENT  WITH  ID =  " + studentID + "  DOEST NOT EXISTS");
            throw new UserNotFoundException("STUDENT NOT  FOUND");
        }

        return new StudentDtout(student.get(), this.STUDENT_IMAGE_URL);
    }

    @Override
    public List<StudentDtout> getStudentsByNameAndBirthdate(String firstname, String lastname, String birthdateAsString) throws InvalidUserInformationException {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setFirstname(firstname);
        studentDtoIn.setLastname(lastname);
        studentDtoIn.setBirthdateAsString(birthdateAsString);

        if (studentDtoIn.getLastname() == null || studentDtoIn.getFirstname() == null || studentDtoIn.getBirthdate() == null) {
            AdminWorksService.LOG.error("INVALID  REQUEST LASTNAME OR FIRSTNAME OR  BIRTHDATE ARE NOT ");
            throw new InvalidUserInformationException("INVALID  FIELD");
        }

        return this.studentDao.findByFirstnameAndLastnameAndBirthdate(studentDtoIn.getFirstname(), studentDtoIn.getLastname(), studentDtoIn.getBirthdate())
                .stream().map(student -> new StudentDtout(student, this.STUDENT_IMAGE_URL)).toList();


    }

    @Override
    public void updateStudentClass(StudentClassDtoIn studentClassDtoIn) throws InvalidStudentClassException, StudentClassNotFoundException {
        studentClassDtoIn.checkIdValidity();
        StudentClassEntity studentClassEntity = studentClassDtoIn.toStudentClassEntity();

        var studentClass = this.studentClassDao.findById(studentClassDtoIn.getId());
        if (studentClass.isEmpty()) {
            throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        }
        studentClass.get().setName((studentClassEntity.getName()));
        this.studentClassDao.save(studentClass.get());
    }
}
