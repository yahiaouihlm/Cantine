package fr.sqli.cantine.service.admin.adminDashboard.work;

import fr.sqli.cantine.dao.*;

import fr.sqli.cantine.dto.in.person.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.person.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.admin.adminDashboard.exceptions.*;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.mailer.ConfirmationAddingAmountToStudent;
import fr.sqli.cantine.service.mailer.EmailSenderService;
import fr.sqli.cantine.service.mailer.TokenSender;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AdminWorksService implements IAdminFunctionService {
    private static final Logger LOG = LogManager.getLogger();
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
    public void addAmountToStudentAccountCodeValidation(Integer studentId, Integer validationCode, Double amount) throws InvalidPersonInformationException, StudentNotFoundException, ExpiredToken, InvalidTokenException {

        if (studentId == null || validationCode == null || amount == null || amount < 10 || amount > 200) {
            AdminWorksService.LOG.error("INVALID  studentId Or validationCode or Amount CAN  NOT BE NULL ");
            throw new InvalidPersonInformationException("INVALID FIELDS ");
        }


        //  check if student is valid
        var student = this.studentDao.findById(studentId).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error("STUDENT NOT  FOUND ");
                    return new StudentNotFoundException("STUDENT NOT  FOUND");
                }
        );

        // find  the  confirmationToken by the  student
        var confirmationToken = this.confirmationTokenDao.findByStudent(student).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error(" NO CONFIRMATION TOKEN HAS  BEEN  FOUND ");
                    return new InvalidPersonInformationException("NO  CONFIRMATION CODE HAS  BEEN  FOUND ");
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


        // add  new amount  to student  account

        student.setWallet(student.getWallet().add(new BigDecimal(amount)));
        this.studentDao.save(student);
        return;

    }

    @Override
    public void attemptAddAmountToStudentAccount(Integer studentId, Double amount) throws StudentNotFoundException, InvalidPersonInformationException, MessagingException {
        if (studentId == null || amount == null) {
            throw new InvalidPersonInformationException("INVALID  STUDENT ID OR AMOUNT");
        }
        var student = this.studentDao.findById(studentId);
        if (student.isEmpty()) {
            throw new StudentNotFoundException("STUDENT NOT FOUND");
        }

        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(student.get());
        this.confirmationTokenDao.findByStudent(student.get()).ifPresent(confirmationTokenEntity -> {
            this.confirmationTokenDao.delete(confirmationTokenEntity);
        });
        this.confirmationTokenDao.save(confirmationToken);

        this.confirmationAddingAmountToStudent.sendConfirmationAmount(student.get(), amount, confirmationToken);
    }

    @Override
    public StudentDtout getStudentById(Integer studentID) throws InvalidPersonInformationException, StudentNotFoundException {
        if (studentID == null) {
            AdminWorksService.LOG.error("studentID IS  NULL  IN  getStudentById ADMIN WORK SERVICE ");
            throw new InvalidPersonInformationException("INVALID  STUDENT ID ");
        }
        var student = this.studentDao.findById(studentID);
        if (student.isEmpty()) {
            AdminWorksService.LOG.error("STUDENT  WITH  ID =  " + studentID + "  DOEST NOT EXISTS");
            throw new StudentNotFoundException("STUDENT NOT  FOUND");
        }

        return new StudentDtout(student.get(), this.STUDENT_IMAGE_URL);
    }

    @Override
    public List<StudentDtout> getStudentsByNameAndBirthdate(String firstname, String lastname, String birthdateAsString) throws InvalidPersonInformationException {
        StudentDtoIn studentDtoIn = new StudentDtoIn();
        studentDtoIn.setFirstname(firstname);
        studentDtoIn.setLastname(lastname);
        studentDtoIn.setBirthdateAsString(birthdateAsString);

        if (studentDtoIn.getLastname() == null || studentDtoIn.getFirstname() == null || studentDtoIn.getBirthdate() == null) {
            AdminWorksService.LOG.error("INVALID  REQUEST LASTNAME OR FIRSTNAME OR  BIRTHDATE ARE NOT ");
            throw new InvalidPersonInformationException("INVALID  FIELD");
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
