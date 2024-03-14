package fr.sqli.cantine.service.users.admin.impl;

import fr.sqli.cantine.dao.*;

import fr.sqli.cantine.dto.in.users.StudentClassDtoIn;
import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.dto.out.person.TransactionDtout;
import fr.sqli.cantine.entity.ConfirmationTokenEntity;
import fr.sqli.cantine.entity.PaymentEntity;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.mailer.UserEmailSender;
import fr.sqli.cantine.service.users.admin.IAdminFunctionService;
import fr.sqli.cantine.service.users.exceptions.*;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminWorksService implements IAdminFunctionService {
    private static final Logger LOG = LogManager.getLogger();
    private final Integer MAX_STUDENT_WALLET = 3000;
    private final Integer MAX_STUDENT_WALLET_ADD_AMOUNT = 500;
    private final String STUDENT_IMAGE_URL;
    private IStudentDao studentDao;
    private IPaymentDao paymentDao;
    private final UserEmailSender userEmailSender;
    private IStudentClassDao studentClassDao;

    private IAdminDao adminDao;
    private Environment environment;
    private IConfirmationTokenDao confirmationTokenDao;


    @Autowired
    public AdminWorksService(IStudentClassDao iStudentClassDao, IStudentDao studentDao, Environment environment, UserEmailSender userEmailSender, IAdminDao adminDao,
                             IConfirmationTokenDao confirmationTokenDao, IPaymentDao paymentDao) {
        this.studentClassDao = iStudentClassDao;
        this.studentDao = studentDao;
        this.environment = environment;
        this.adminDao = adminDao;
        this.userEmailSender = userEmailSender;
        this.confirmationTokenDao = confirmationTokenDao;
        this.paymentDao = paymentDao;
        this.STUDENT_IMAGE_URL = this.environment.getProperty("sqli.cantine.images.url.student");
    }


    @Override
    public List<TransactionDtout> getStudentTransactions(String studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        if  (studentUuid == null || studentUuid.isEmpty() || studentUuid.isBlank() || studentUuid.length() < 10) {
            AdminWorksService.LOG.error("INVALID  STUDENT UUID  IN  getStudentTransactions ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  STUDENT UUID");
        }
        var  student = this.studentDao.findByUuid(studentUuid).orElseThrow(() -> {
            AdminWorksService.LOG.error("STUDENT  WITH  UUID =  " + studentUuid + "  DOEST NOT EXISTS");
            return new UserNotFoundException("STUDENT NOT  FOUND");
        });

        return this.paymentDao.findByStudent(student).stream().map(transaction ->
                new TransactionDtout(transaction.getAdmin() , transaction.getStudent(), transaction.getAmount(), transaction.getPaymentDate() , transaction.getPaymentTime())).toList();
    }

    @Override
    public void addAmountToStudentAccountCodeValidation(String adminUuid, String studentUuid, Integer validationCode, Double amount) throws InvalidUserInformationException, ExpiredToken, InvalidTokenException, UserNotFoundException, UnknownUser, MessagingException {

        if (studentUuid == null || studentUuid.isEmpty() || studentUuid.trim().length() < 10 || validationCode == null || amount == null || amount > MAX_STUDENT_WALLET_ADD_AMOUNT) {
            AdminWorksService.LOG.error("INVALID  studentId Or validationCode or Amount CAN  NOT BE NULL  studentId = {}  validationCode = {}   amount  = {} IN  addAmountToStudentAccountCodeValidation ADMIN WORK SERVICE", studentUuid, validationCode, amount);
            throw new InvalidUserInformationException("INVALID FIELDS ");
        }


        //  check if student is valid
        var student = this.studentDao.findByUuid(studentUuid).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error("STUDENT NOT  FOUND  WITH UUID = {}", studentUuid);
                    return new UserNotFoundException("STUDENT NOT  FOUND");
                }
        );

        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        var adminReq = this.adminDao.findByUuid(adminUuid).orElseThrow(() -> {
            AdminWorksService.LOG.error("ADMIN  WITH  UUID =  " + adminUuid + "  DOEST NOT EXISTS");
            return new UserNotFoundException("ADMIN NOT  FOUND");
        });
        var adminAuth = this.adminDao.findByEmail(adminEmail).orElseThrow(() -> {
            AdminWorksService.LOG.error("ADMIN  WITH  EMAIL =  " + adminEmail + "  DOEST NOT EXISTS");
            return new UserNotFoundException("ADMIN NOT  FOUND");
        });
        if (!adminReq.equals(adminAuth)) {
            AdminWorksService.LOG.error("ADMIN OF  TOKEN {} IS DIFFERENT FROM ADMIN OF  REQUEST {}", adminUuid, adminAuth.getEmail());
            throw new UnknownUser("UNKNOWN ADMIN FOUND");
        }


        // find  the  confirmationToken by the  student
        var confirmationToken = this.confirmationTokenDao.findByStudent(student).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error(" NO CONFIRMATION TOKEN HAS  BEEN  FOUND ");
                    return new InvalidUserInformationException("NO  CONFIRMATION CODE HAS  BEEN  FOUND ");
                }
        );
        if (!confirmationToken.getStudent().equals(student)) {
            AdminWorksService.LOG.error("ADMIN  AND STUDENT OF  TOKEN  admin = {}  student = {} IS DIFFERENT FROM ADMIN OF  REQUEST admin = {} , student= {}", adminUuid, studentUuid, adminReq.getEmail(), student.getEmail());
            throw new UserNotFoundException("UNKNOWN ADMIN FOUND");
        }


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

            var newWallet = student.getWallet().add(new BigDecimal(amount));

            if  (newWallet.compareTo(BigDecimal.ZERO) < 0) {
                AdminWorksService.LOG.error("WALLET  CAN  NOT  BE  NEGATIVE  AMOUNT");
                throw new InvalidUserInformationException("EXCESSIVE AMOUNT");
            }

            if (newWallet.compareTo(new BigDecimal(MAX_STUDENT_WALLET)) > 0) {
                AdminWorksService.LOG.error("WALLET  TO  BIG");
                throw new InvalidUserInformationException("EXCESSIVE AMOUNT");
            }



        // add  new amount  to student  account
        student.setWallet(student.getWallet().add(new BigDecimal(amount)));
        var paymentInformation = new PaymentEntity(adminAuth, student, new BigDecimal(amount));

        this.userEmailSender.sendNotificationAboutNewStudentAmount(student, student.getWallet().doubleValue(), amount);
        // save  the  payment  information
        this.paymentDao.save(paymentInformation);
        this.studentDao.save(student);

    }

    @Override
    public void attemptAddAmountToStudentAccount(String adminUuid, String studentUuid, Double amount) throws
            InvalidUserInformationException, MessagingException, UserNotFoundException, UnknownUser {

        if (studentUuid == null || amount == null || amount < 10 || amount > MAX_STUDENT_WALLET_ADD_AMOUNT || studentUuid.isEmpty() || studentUuid.isBlank() || studentUuid.length() < 10) {
            AdminWorksService.LOG.error("INVALID  STUDENT UUID OR AMOUNT IN  attemptAddAmountToStudentAccount ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  STUDENT ID OR AMOUNT");
        }
        var student = this.studentDao.findByUuid(studentUuid).orElseThrow(() -> {
                    AdminWorksService.LOG.error("STUDENT  WITH  UUID =  " + studentUuid + "  DOEST NOT EXISTS");
                    return new UserNotFoundException("STUDENT NOT  FOUND");
                }
        );


        ConfirmationTokenEntity confirmationToken = new ConfirmationTokenEntity(student);
        this.confirmationTokenDao.findByStudent(student).ifPresent(confirmationTokenEntity -> {
            this.confirmationTokenDao.delete(confirmationTokenEntity);
        });
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var adminReq = this.adminDao.findByUuid(adminUuid).orElseThrow(() -> {
            AdminWorksService.LOG.error("ADMIN  WITH  UUID =  " + adminUuid + "  DOEST NOT EXISTS");
            return new UserNotFoundException("ADMIN NOT  FOUND");
        });
        var adminAuth = this.adminDao.findByEmail(authentication.getPrincipal().toString()).orElseThrow(() -> {
            AdminWorksService.LOG.error("ADMIN  WITH  EMAIL =  " + authentication.getPrincipal().toString() + "  DOEST NOT EXISTS");
            return new UserNotFoundException("ADMIN NOT  FOUND");
        });
        if (!adminReq.equals(adminAuth)) {
            AdminWorksService.LOG.error("ADMIN OF  TOKEN {} IS DIFFERENT FROM ADMIN OF  REQUEST {}", adminUuid, adminAuth.getEmail());
            throw new UnknownUser("UNKNOWN ADMIN FOUND");
        }

        this.confirmationTokenDao.save(confirmationToken);
        this.userEmailSender.sendConfirmationCodeToCheckAddRemoveAmount(student, confirmationToken.getUuid(), amount);
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
    public StudentDtout getStudentByEmail(String email) throws InvalidUserInformationException, UserNotFoundException {
        if (email == null || email.isEmpty() || email.isBlank() || email.length() < 5) {
            AdminWorksService.LOG.error("INVALID  EMAIL  IN  getStudentByEmail ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  EMAIL");
        }
        var student = this.studentDao.findByEmail(email).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error("STUDENT  WITH  EMAIL =  " + email + "  DOEST NOT EXISTS");
                    return new UserNotFoundException("STUDENT NOT  FOUND");
                }
        );
        return new StudentDtout(student, this.STUDENT_IMAGE_URL);
    }

    @Override
    public void updateStudentEmail(String studentUuid, String newEmail) throws InvalidUserInformationException, UserNotFoundException, ExistingUserException, MessagingException {
        if (studentUuid == null || studentUuid.isEmpty() || studentUuid.isBlank() || studentUuid.length() < 20) {
            AdminWorksService.LOG.error("INVALID  STUDENT UUID  IN  updateStudentEmail ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  STUDENT UUID");
        }
        if (newEmail == null || newEmail.isEmpty() || newEmail.isBlank() || newEmail.length() < 5) {
            AdminWorksService.LOG.error("INVALID  EMAIL  IN  updateStudentEmail ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  EMAIL");
        }
        var student = this.studentDao.findByUuid(studentUuid).orElseThrow(
                () -> {
                    AdminWorksService.LOG.error("STUDENT  WITH  UUID =  " + studentUuid + "  DOEST NOT EXISTS");
                    return new UserNotFoundException("STUDENT NOT  FOUND");
                }
        );

        if (this.studentDao.findByEmail(newEmail).isPresent() || this.adminDao.findByEmail(newEmail).isPresent()) {
            AdminWorksService.LOG.error("EMAIL =  " + newEmail + "  ALREADY EXISTS IN ADMIN OR STUDENT  TABLE");
            throw new ExistingUserException("EMAIL ALREADY EXISTS");
        }
        student.setEmail(newEmail);
        student.setStatus(0);
        this.studentDao.save(student);
        this.userEmailSender.sendNotificationTOStudentWhenEmailHasBeenChanged(student);
    }


    public StudentDtout getStudentByUuid(String studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        if (studentUuid == null || studentUuid.isEmpty() || studentUuid.isBlank() || studentUuid.length() < 20) {
            AdminWorksService.LOG.error("studentUuid IS  NULL  IN  getStudentById ADMIN WORK SERVICE ");
            throw new InvalidUserInformationException("INVALID  STUDENT ID ");
        }
        var student = this.studentDao.findByUuid(studentUuid);
        if (student.isEmpty()) {
            AdminWorksService.LOG.error("STUDENT  WITH  ID =  " + studentUuid + "  DOEST NOT EXISTS");
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
