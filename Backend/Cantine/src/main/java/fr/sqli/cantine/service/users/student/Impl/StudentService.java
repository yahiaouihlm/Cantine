package fr.sqli.cantine.service.users.student.Impl;


import fr.sqli.cantine.constants.ConstCantine;
import fr.sqli.cantine.dao.IRoleDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.dto.out.person.StudentClassDtout;
import fr.sqli.cantine.dto.out.person.StudentDtout;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.entity.RoleEntity;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.users.user.impl.UserService;
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

import javax.management.relation.RoleNotFoundException;
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
    private final IUserDao userDao;
    private final IRoleDao roleDao;
    private final IStudentClassDao iStudentClassDao;
    private final Environment environment;
    private final UserService userService;
    private final ImageService imageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public StudentService(IUserDao userDao, IRoleDao roleDao, IStudentClassDao iStudentClassDao, Environment environment
            , BCryptPasswordEncoder bCryptPasswordEncoder, ImageService imageService, UserService userService) {
        this.iStudentClassDao = iStudentClassDao;
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.environment = environment;
        this.userService = userService;
        this.DEFAULT_STUDENT_IMAGE = this.environment.getProperty("sqli.cantine.default.persons.student.imagename");
        this.IMAGES_STUDENT_PATH = this.environment.getProperty("sqli.cantine.image.student.path");
        this.EMAIL_STUDENT_DOMAIN = this.environment.getProperty("sqli.cantine.admin.email.domain");
        this.EMAIL_STUDENT_REGEX = "^[a-zA-Z0-9._-]+@" + this.EMAIL_STUDENT_DOMAIN + "$";
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.imageService = imageService;
        var protocol = environment.getProperty("sqli.cantine.server.protocol");
        var host = environment.getProperty("sqli.cantine.server.ip.address");
        var port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol + host + ":" + port;
        this.CONFIRMATION_TOKEN_URL = environment.getProperty("sqli.cantine.server.confirmation.token.url");
    }


    @Override
    public void updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidUserInformationException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, UserNotFoundException {

        IStudentService.checkUuIdValidity(studentDtoIn.getId());

        studentDtoIn.checkStudentInformationForUpdate();

        var studentClassEntity = this.iStudentClassDao.findByName(studentDtoIn.getStudentClass()).orElseThrow(() -> {
            StudentService.LOG.error("STUDENT CLASS  IS  NOT  FOUND");
            return new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        });


        var studentEntity = this.userDao.findStudentById(studentDtoIn.getId()).orElseThrow(() -> {
            StudentService.LOG.error("STUDENT  IS  NOT  FOUND");
            return new UserNotFoundException("STUDENT NOT FOUND");
        });

        studentEntity.setFirstname(studentDtoIn.getFirstname());
        studentEntity.setLastname(studentDtoIn.getLastname());
        studentEntity.setBirthdate(studentDtoIn.getBirthdate());
        studentEntity.setStudentClass(studentClassEntity);
        studentEntity.setTown(studentDtoIn.getTown());
        studentEntity.setPhone(studentDtoIn.getPhone());


        if (studentDtoIn.getImage() != null && !studentDtoIn.getImage().isEmpty()) {
            MultipartFile image = studentDtoIn.getImage();
            String imageName;
            if (studentEntity.getImage().getName().equals(this.DEFAULT_STUDENT_IMAGE)) {
                imageName = this.imageService.uploadImage(image, this.IMAGES_STUDENT_PATH);
            } else {
                var oldImageName = studentEntity.getImage().getName();
                imageName = this.imageService.updateImage(oldImageName, image, this.IMAGES_STUDENT_PATH);
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setName(imageName);
            studentEntity.setImage(imageEntity);
        }

        this.userDao.save(studentEntity);

    }

    public StudentDtout getStudentByUuid(String studentUuid) throws InvalidUserInformationException, UserNotFoundException {
        IStudentService.checkUuIdValidity(studentUuid);

        var studentEntity = this.userDao.findStudentById(studentUuid).orElseThrow(() -> {
            StudentService.LOG.error("STUDENT  IS  NOT  FOUND");
            return new UserNotFoundException("STUDENT NOT FOUND");
        });

        return new StudentDtout(studentEntity, this.environment.getProperty("sqli.cantine.images.url.student"));
    }


    @Override
    public void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidStudentClassException, InvalidUserInformationException, ExistingUserException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException,
            UserNotFoundException, MessagingException, AccountActivatedException, RemovedAccountException {
        UserEntity studentEntity = studentDtoIn.toStudentEntity();

        var studentClass = studentDtoIn.getStudentClass();

        var studentClassEntity = this.iStudentClassDao.findByName(studentClass).orElseThrow(() -> {
            StudentService.LOG.error("STUDENT CLASS ={}  IS  NOT  FOUND", studentClass);
            return new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        });


        if (!studentEntity.getEmail().matches(this.EMAIL_STUDENT_REGEX)) {
            StudentService.LOG.error("EMAIL = {} IS NOT  ASTON  EMAIL  DOMAIN", studentEntity.getEmail());
            throw new InvalidUserInformationException("YOUR EMAIL IS NOT VALID");
        }

        this.existingEmail(studentEntity.getEmail());

        studentEntity.setStatus(0);
        studentEntity.setWallet(new BigDecimal(0));
        studentEntity.setStudentClass(studentClassEntity);
        studentEntity.setRegistrationDate(java.time.LocalDate.now());


        studentEntity.setPassword(this.bCryptPasswordEncoder.encode(studentEntity.getPassword()));

        ImageEntity imageEntity = new ImageEntity();

        if (studentDtoIn.getImage() != null && !studentDtoIn.getImage().isEmpty()) {
            MultipartFile image = studentDtoIn.getImage();
            var imageName = this.imageService.uploadImage(image, this.IMAGES_STUDENT_PATH);
            imageEntity.setName(imageName);
        } else {
            imageEntity.setName(this.DEFAULT_STUDENT_IMAGE);

        }
        studentEntity.setImage(imageEntity);
        studentEntity.setRoles(List.of(new RoleEntity(ConstCantine.STUDENT_ROLE_LABEL, ConstCantine.STUDENT_ROLE_DESCRIPTION, studentEntity)));
        this.userDao.save(studentEntity);
        this.userService.sendConfirmationLink(studentEntity.getEmail());
    }


    @Override
    public List<StudentClassDtout> getAllStudentClass() {
        return this.iStudentClassDao.findAll()
                .stream()
                .map(studentClassEntity -> new StudentClassDtout(studentClassEntity.getId(), studentClassEntity.getName()))
                .toList();
    }

    @Override
    public UserEntity findStudentByUserName(String username) throws UserNotFoundException {
        return this.userDao.findStudentByEmail(username).orElseThrow(() -> {
            StudentService.LOG.error("STUDENT  WITH  EMAIL = {}  IS  NOT  FOUND", username);
            return new UserNotFoundException("STUDENT NOT FOUND");
        });
    }


    public void existingEmail(String adminEmail) throws ExistingUserException {
        if (this.userDao.findUserByEmail(adminEmail).isPresent()) {
            StudentService.LOG.error("THE  USER  WITH  EMAIL = {}  IS  ALREADY  EXISTS", adminEmail);
            throw new ExistingUserException("EMAIL IS ALREADY EXISTS");
        }
    }


}
