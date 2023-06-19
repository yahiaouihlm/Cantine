package fr.sqli.Cantine.service.student;



import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.dto.out.person.StudentDtout;
import fr.sqli.Cantine.entity.ConfirmationTokenEntity;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.mailer.EmailSenderService;
import fr.sqli.Cantine.service.student.exceptions.AccountAlreadyActivatedException;
import fr.sqli.Cantine.service.student.exceptions.ExistingStudentException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import jakarta.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class StudentService implements IStudentService {
    private static final Logger LOG = LogManager.getLogger();
    final String  SERVER_ADDRESS ;
    final  String DEFAULT_STUDENT_IMAGE;
    final  String  IMAGES_STUDENT_PATH ;
    final  String EMAIL_STUDENT_DOMAIN ;
    final  String EMAIL_STUDENT_REGEX ;
    private IStudentDao studentDao;
  private IStudentClassDao iStudentClassDao;
  private Environment environment;

   private EmailSenderService emailSenderService;
  private ImageService imageService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private IConfirmationTokenDao confirmationTokenDao;

    public StudentService(IStudentDao studentDao, IStudentClassDao iStudentClassDao, Environment environment
            , BCryptPasswordEncoder bCryptPasswordEncoder, ImageService imageService ,  IConfirmationTokenDao confirmationTokenDao ,  EmailSenderService emailSenderService) {
        this.iStudentClassDao = iStudentClassDao;
        this.studentDao = studentDao;
        this.environment = environment;
        this.emailSenderService = emailSenderService;
        this.DEFAULT_STUDENT_IMAGE = this.environment.getProperty("sqli.cantine.default.persons.student.imagename");
        this.IMAGES_STUDENT_PATH = this.environment.getProperty("sqli.cantine.image.student.path");
        this.EMAIL_STUDENT_DOMAIN = this.environment.getProperty("sqli.cantine.admin.email.domain");
        this.EMAIL_STUDENT_REGEX = "^[a-zA-Z0-9._-]+@" + EMAIL_STUDENT_DOMAIN + "$";
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.imageService = imageService;
        this.confirmationTokenDao = confirmationTokenDao;
        var  protocol = environment.getProperty("sqli.cantine.server.protocol");
        var  host = environment.getProperty("sqli.cantine.server.ip.address");
        var  port = environment.getProperty("sali.cantine.server.port");
        this.SERVER_ADDRESS = protocol+host+":"+port;

    }


    /* TODO  ;  make  the  Tests */
    @Override
    public void sendTokenStudent( String  email ) throws InvalidPersonInformationException, StudentNotFoundException, AccountAlreadyActivatedException, MessagingException {
        if (email == null || email.isEmpty()) {
            StudentService.LOG.error("email  is  null or  empty");
            throw new InvalidPersonInformationException("INVALID EMAIL");
        }


        var studentEntity = this.studentDao.findByEmail(email.trim()).orElseThrow(
                () -> {
                    StudentService.LOG.error("student  is  not  found with  email  : {}", email) ;
                    return new StudentNotFoundException("STUDENT NOT FOUND");
                }
        );

        if (studentEntity.getStatus() == 1) {
            StudentService.LOG.error("student  is  already  confirmed");
            throw new AccountAlreadyActivatedException("STUDENT IS ALREADY CONFIRMED");
        }

        var  confirmationTokenEntity =  this.confirmationTokenDao.findByStudent(studentEntity);
        confirmationTokenEntity.ifPresent(tokenEntity -> this.confirmationTokenDao.delete(tokenEntity));


        var  confirmationToken =  new ConfirmationTokenEntity(studentEntity);
        this.confirmationTokenDao.save(confirmationToken);

        var url = this.SERVER_ADDRESS+"/api/v1/admin/confirm-account?token=" + confirmationToken.getToken();

        var  header = "<h2> Bonjour  "+studentEntity.getFirstname()+" "+studentEntity.getLastname()+",</h2>";

        var body = "<p> Merci de vous être inscrit sur notre Cantière . Afin de finaliser votre inscription, veuillez confirmer votre adresse e-mail en cliquant sur le lien ci-dessous"  +
                "<button><a href=\""+url+"\">Confirmer mon compte</a></button></p>";

        var  footer = "<p> Cordialement </p>"
                   + "<p> L'équipe de la cantine </p>"
                  + "<img src=http://localhost:8080/cantine/download/images/logos/logo-aston.png> ";

       //var message =  header + body + footer;

        var message = "<h1>Contenu de l'email</h1>" ;
        this.emailSenderService.send("hayahiaoui@sqli.com", "Confirmation de votre compte", message);
        this.emailSenderService.send("yahiaouihlm@gmail.com", "Confirmation de votre compte", message);
    }


    @Override
    public void updateStudentInformation(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException, StudentNotFoundException, InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException {


        if (studentDtoIn.getId() == null || studentDtoIn.getId() < 0) {
            StudentService.LOG.error("id  is  null or  0");
            throw new InvalidPersonInformationException("INVALID STUDENT ID");
        }

        studentDtoIn.checkStudentClassValidity();

        var studentClass = studentDtoIn.getStudentClass();

        var studentClassEntity = this.iStudentClassDao.findByName(studentClass);

        if (studentClassEntity.isEmpty()) {
            StudentService.LOG.error("student class  is  not  found");
            throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        }



        var studentEntity = this.studentDao.findById(studentDtoIn.getId());

        if (studentEntity.isEmpty()) {
            StudentService.LOG.error("student  is  not  found");
            throw new StudentNotFoundException("STUDENT NOT FOUND");
        }

        var studentUpdated = studentEntity.get();
        studentUpdated.setFirstname(studentDtoIn.getFirstname());
        studentUpdated.setLastname(studentDtoIn.getLastname());
        studentUpdated.setBirthdate(studentDtoIn.getBirthdate());
        studentUpdated.setStudentClass(studentClassEntity.get());
        studentUpdated.setTown(studentDtoIn.getTown());

        if  ( studentDtoIn.getPhone() != null  &&  !studentDtoIn.getPhone().trim().isEmpty()) {
             studentUpdated.setPhone(studentDtoIn.getPhone());
        }


        if (studentDtoIn.getImage() != null && !studentDtoIn.getImage().isEmpty()) {
            MultipartFile image = studentDtoIn.getImage();
              String imageName ;
            if (studentUpdated.getImage().getImagename().equals(this.DEFAULT_STUDENT_IMAGE)) {
                imageName   =  this.imageService.uploadImage(image , this.IMAGES_STUDENT_PATH);
            }
         else  {
                var oldImageName = studentUpdated.getImage().getImagename();
                 imageName = this.imageService.updateImage(oldImageName, image, this.IMAGES_STUDENT_PATH);
            }
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImagename(imageName);
            studentUpdated.setImage(imageEntity);

        }

        this.studentDao.save(studentUpdated);

    }

    public StudentDtout getStudentByID (Integer  id) throws InvalidPersonInformationException, StudentNotFoundException {
        if (id == null || id < 0) {
            StudentService.LOG.error("id  is  null or  0");
            throw new InvalidPersonInformationException("INVALID STUDENT ID");
        }

        var  studentEntity = this.studentDao.findById(id);
        if (studentEntity.isEmpty()) {
            StudentService.LOG.error("student  is  not  found");
            throw new StudentNotFoundException("STUDENT NOT FOUND");
        }

        return  new StudentDtout(studentEntity.get());
    }


    @Override
    public void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException,
            InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingStudentException {
        StudentEntity studentEntity = studentDtoIn.toStudentEntity();

        var studentClass = studentDtoIn.getStudentClass();

        var studentClassEntity = this.iStudentClassDao.findByName(studentClass);
        if (studentClassEntity.isEmpty()) {
            throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
        }

        if (!studentEntity.getEmail().matches(this.EMAIL_STUDENT_REGEX)) {
            StudentService.LOG.error("email  is  not  valid");
            throw new InvalidPersonInformationException("YOUR EMAIL IS NOT VALID");
        }

        this.existingStudent(studentEntity.getEmail());

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

    }

    @Override
    public void existingStudent(String adminEmail) throws ExistingStudentException {
        if (this.studentDao.findByEmail(adminEmail).isPresent()) {
            StudentService.LOG.error("this student is already exists");
            throw new ExistingStudentException("THIS STUDENT IS ALREADY EXISTS");
        }
    }

}
