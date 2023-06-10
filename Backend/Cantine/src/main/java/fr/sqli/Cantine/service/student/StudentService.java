package fr.sqli.Cantine.service.student;



import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidStudentClassException;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.StudentClassNotFoundException;
import fr.sqli.Cantine.service.images.ImageService;
import fr.sqli.Cantine.service.images.exception.ImagePathException;
import fr.sqli.Cantine.service.images.exception.InvalidFormatImageException;
import fr.sqli.Cantine.service.images.exception.InvalidImageException;
import fr.sqli.Cantine.service.student.exceptions.ExistingStudentException;
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
    final  String DEFAULT_STUDENT_IMAGE;
    final  String  IMAGES_STUDENT_PATH ;
    final  String EMAIL_STUDENT_DOMAIN ;
    final  String EMAIL_STUDENT_REGEX ;
    private IStudentDao studentDao;
  private IStudentClassDao iStudentClassDao;
  private Environment environment;

  private ImageService imageService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public  StudentService  (IStudentDao studentDao,  IStudentClassDao iStudentClassDao , Environment environment
              ,  BCryptPasswordEncoder bCryptPasswordEncoder, ImageService imageService) {
      this.iStudentClassDao = iStudentClassDao;
      this.studentDao = studentDao;
      this.environment = environment;
        this.DEFAULT_STUDENT_IMAGE = this.environment.getProperty("sqli.cantine.default.persons.student.imagename");
        this.IMAGES_STUDENT_PATH = this.environment.getProperty("sqli.cantine.image.student.path");
         this.EMAIL_STUDENT_DOMAIN = this.environment.getProperty("sqli.cantine.admin.email.domain");
         this.EMAIL_STUDENT_REGEX = "^[a-zA-Z0-9._-]+@"+EMAIL_STUDENT_DOMAIN+"$" ;
     this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.imageService = imageService;

  }


  @Override
  public  void  updateStudentInformation (StudentDtoIn studentDtoIn) throws InvalidPersonInformationException {
      /*if  (studentDtoIn.getId()== null || studentDtoIn.getId() < 0){
          StudentService.LOG.error("id  is  null or  0");
          throw  new InvalidPersonInformationException(" INVALID STUDENT ID");
      }

      var  studentEntity = this.studentDao.findById(studentDtoIn.getId());
        if  (studentEntity.isEmpty()){
            StudentService.LOG.error("student  is  not  found");
            throw new  StudentN("STUDENT NOT FOUND");
        }*/
     return ;
  }

        @Override
        public void signUpStudent(StudentDtoIn studentDtoIn) throws InvalidPersonInformationException,
                 InvalidStudentClassException, StudentClassNotFoundException, InvalidFormatImageException, InvalidImageException, ImagePathException, IOException, ExistingStudentException {
            StudentEntity studentEntity = studentDtoIn.toStudentEntity();

            var   studentClass   =  studentDtoIn.getStudentClass();

            var  studentClassEntity   =  this.iStudentClassDao.findByName(studentClass);
            if (studentClassEntity.isEmpty()) {
                throw new StudentClassNotFoundException("STUDENT CLASS NOT FOUND");
            }

            if (!studentEntity.getEmail().matches(this.EMAIL_STUDENT_REGEX ) ){
                StudentService.LOG.error("email  is  not  valid");
                throw  new InvalidPersonInformationException("YOUR EMAIL IS NOT VALID");
            }

            this.existingStudent(studentEntity.getEmail());

            studentEntity.setStatus(0);
            studentEntity.setWallet(new BigDecimal(0));
            studentEntity.setStudentClass(studentClassEntity.get());
            studentEntity.setRegistrationDate(java.time.LocalDate.now());


            studentEntity.setPassword(this.bCryptPasswordEncoder.encode(studentEntity.getPassword()));

            if  (studentDtoIn.getImage() != null && !studentDtoIn.getImage().isEmpty()) {
                MultipartFile image = studentDtoIn.getImage();
                var  imageName =  this.imageService.uploadImage(image, this.IMAGES_STUDENT_PATH );
                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setImagename(imageName);
                studentEntity.setImage(imageEntity);
            }
            else{
                ImageEntity imageEntity = new ImageEntity();
                imageEntity.setImagename(this.DEFAULT_STUDENT_IMAGE);
                studentEntity.setImage(imageEntity);
            }

               this.studentDao.save(studentEntity);

        }

    @Override
    public void existingStudent(String  adminEmail ) throws ExistingStudentException {
        if  (this.studentDao.findByEmail(adminEmail).isPresent()){
             StudentService.LOG.error("this student is already exists");
            throw  new ExistingStudentException("THIS STUDENT IS ALREADY EXISTS");
        }
    }

}
