package fr.sqli.cantine.service.users.student.Impl;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.service.users.UserService;
import fr.sqli.cantine.service.users.exceptions.*;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.images.exception.ImagePathException;
import fr.sqli.cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.cantine.service.images.exception.InvalidImageException;
import fr.sqli.cantine.service.users.exceptions.AccountActivatedException;
import fr.sqli.cantine.service.users.student.IStudentService;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class StudentService implements IStudentService {
    private static final Logger LOG = LogManager.getLogger();
    final String SERVER_ADDRESS;
    private final String CONFIRMATION_TOKEN_URL;
    final String DEFAULT_STUDENT_IMAGE;
    final String IMAGES_STUDENT_PATH;
    final String EMAIL_STUDENT_DOMAIN;
    final String EMAIL_STUDENT_REGEX;
    private final IStudentDao studentDao;
    private final IStudentClassDao iStudentClassDao;
    private final Environment environment;
    private final UserService userService;
    private final ImageService imageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


     private IAdminDao adminDao;

    public StudentService(IStudentDao studentDao, IStudentClassDao iStudentClassDao, Environment environment
            , BCryptPasswordEncoder bCryptPasswordEncoder, ImageService imageService, UserService userService) {
        this.iStudentClassDao = iStudentClassDao;
        this.studentDao = studentDao;
        this.environment = environment;
        this.userService = userService;
        this.DEFAULT_STUDENT_IMAGE = this.environment.getProperty("sqli.cantine.default.persons.student.imagename");
        this.IMAGES_STUDENT_PATH = this.environment.getProperty("sqli.cantine.image.student.path");
        this.EMAIL_STUDENT_DOMAIN = this.environment.getProperty("sqli.cantine.admin.email.domain");
        this.EMAIL_STUDENT_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
        /*TODO make  the  only  available  email  is      the  emails ends  with  the  domain  of  the  school  */
        //"^[a-zA-Z0-9._-]+@" + EMAIL_STUDENT_DOMAIN + "$";
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.imageService = imageService;
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol + host + ":" + port;
        this.CONFIRMATION_TOKEN_URL = environment.getProperty("sqli.cantine.server.confirmation.token.url");
    }


    /*TODO  ;  REMOVE   checkLinkValidity AND sendConfirmationLink */

    @Override
    public void updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException {

        IStudentService.checkUuIdValidity(studentDtoIn.getUuid());

        studentDtoIn.checkStudentClassValidity();

        var studentClass = studentDtoIn.getStudentClass();

        var studentClassEntity = this.iStudentClassDao.findByName(studentClass);

        if (studentClassEntity.isEmpty()) {
            StudentService.LOG.error("student class  is  not  found");
            throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        }


        var studentEntity = this.studentDao.findByUuid(studentDtoIn.getUuid());

        if (studentEntity.isEmpty()) {
            StudentService.LOG.error("student  is  not  found");
            throw new UserNotFoundException("STUDENT NOT FOUND");
        }

        var studentUpdated = studentEntity.get();
        studentUpdated.setFirstname(studentDtoIn.getFirstname());
        studentUpdated.setLastname(studentDtoIn.getLastname());
        studentUpdated.setBirthdate(studentDtoIn.getBirthdate());
        studentUpdated.setStudentClass(studentClassEntity.get());
        studentUpdated.setTown(studentDtoIn.getTown());

        if (studentDtoIn.getPhone() != null && !studentDtoIn.getPhone().trim().isEmpty()) {
            studentUpdated.setPhone(studentDtoIn.getPhone());
        }


        if (studentDtoIn.getImage() != null && !studentDtoIn.getImage().isEmpty()) {
            MultipartFile image = studentDtoIn.getImage();
            String imageName;
            if (studentUpdated.getImage().getImagename().equals(this.DEFAULT_STUDENT_IMAGE)) {
                imageName = this.imageService.uploadImage(image, this.IMAGES_STUDENT_PATH);
            } else {
                var oldImageName = studentUpdated.getImage().getImagename();
                imageName = this.imageService.updateImage(oldImageName, image, this.IMAGES_STUDENT_PATH);
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
            studentUpdated.setImage(imageEntity);

        }

        if (studentDtoIn.getPhone() == null || studentDtoIn.getPhone().trim().isEmpty()) {
            studentUpdated.setPhone(null);
        }
        this.studentDao.save(studentUpdated);

    }

    public StudentDtout getStudentByUuid(String studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        IStudentService.checkUuIdValidity(studentUuid);

        var studentEntity = this.studentDao.findByUuid(studentUuid);
        if (studentEntity.isEmpty()) {
            StudentService.LOG.error("STUDENT  IS  NOT  FOUND");
            throw new UserNotFoundException("STUDENT NOT FOUND");
        }

        return new StudentDtout(studentEntity.get(), this.environment.getProperty("sqli.cantine.images.url.student"));
    }


    @Override
    public void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException, ExistingUserException {
        StudentEntity studentEntity = studentDtoIn.toStudentEntity();

        var studentClass = studentDtoIn.getStudentClass();

        var studentClassEntity = this.iStudentClassDao.findByName(studentClass);
        if (studentClassEntity.isEmpty()) {
            throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        }

        if (!studentEntity.getEmail().matches(this.EMAIL_STUDENT_REGEX)) {
            StudentService.LOG.error("email :  {}  is  not  valid", studentEntity.getEmail());
            throw new InvalidUserInformationException("YOUR EMAIL IS NOT VALID");
        }

        this.existingEmail(studentEntity.getEmail());

        studentEntity.setStatus(0);
        studentEntity.setWallet(new BigDecimal(0));
        studentEntity.setStudentClass(studentClassEntity.get());
        studentEntity.setRegistrationDate(java.time.LocalDate.now());


        studentEntity.setPassword(this.bCryptPasswordEncoder.encode(studentEntity.getPassword()));

        if (studentDtoIn.getImage() != null && !studentDtoIn.getImage().isEmpty()) {
            MultipartFile image = studentDtoIn.getImage();
            var imageName = this.imageService.uploadImage(image, this.IMAGES_STUDENT_PATH);
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
            studentEntity.setImage(imageEntity);
        } else {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(this.DEFAULT_STUDENT_IMAGE);
            studentEntity.setImage(imageEntity);
        }


        this.studentDao.save(studentEntity);
        this.userService.sendConfirmationLink(studentEntity.getEmail());
    }



    @Override
    public List<StudentClassDtout> getAllStudentClass() {
        return this.iStudentClassDao.findAll()
                .stream()
                .map(studentClassEntity -> new StudentClassDtout(studentClassEntity.getId(), studentClassEntity.getName()))
                .toList();
    }



    public void existingEmail(String adminEmail) throws ExistingUserException {
        if (this.studentDao.findByEmail(adminEmail).isPresent() || this.adminDao.findByEmail(adminEmail).isPresent()) {
            StudentService.LOG.error("this student is already exists");
            throw new ExistingUserException(" EMAIL IS ALREADY EXISTS");
        }
    }


    @Autowired
    public void  setAdminDao(IAdminDao adminDao){
        this.adminDao = adminDao;
    }
}
