package fr.sqli.Cantine.service.student;


import fr.sqli.Cantine.dao.IStudentClassDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.dto.in.person.StudentDtoIn;
import fr.sqli.Cantine.entity.StudentClassEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import fr.sqli.Cantine.service.images.ImageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddStudentTest {
    private static final Logger LOG = LogManager.getLogger();
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";

    @Mock
    private IStudentClassDao iStudentClassDao;
    @Mock
    private IStudentDao studentDao;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private StudentService studentService;
    @Mock
    private MockEnvironment environment;
    private StudentClassEntity studentClassEntity;
    private StudentDtoIn studentDtoIn ;

    @BeforeEach
     void  setUp  () throws IOException {
         this.studentClassEntity = new StudentClassEntity();
         this.studentClassEntity.setId(1);
         this.studentClassEntity.setName("SQLI JAVA ");
         this.environment = new MockEnvironment();
         this.environment.setProperty("sqli.cantine.default.persons.admin.imagename","defaultAdminImageName");
         this.environment.setProperty("sqli.cantine.admin.email.domain","social.aston-ecole.com");
         this.environment.setProperty("sqli.cantine.image.admin.path","adminImagePath");

         this.studentDtoIn = new StudentDtoIn();
            this.studentDtoIn.setFirstname("firstname");
            this.studentDtoIn.setLastname("lastname");
            this.studentDtoIn.setEmail("halim@social.aston-ecole.com") ;
            this.studentDtoIn.setPassword("password");
            this.studentDtoIn.setBirthdateAsString("1999-01-01");
            this.studentDtoIn.setTown("town");
            this.studentDtoIn.setStudentClass("SQLI JAVA");
            this.studentDtoIn.setPhone("0606060606");
            this.studentDtoIn.setImage(new MockMultipartFile(
                 "image",                         // nom du champ de fichier
                 "ImageForTests",          // nom du fichier
                 "images/png",                    // type MIME
                 new FileInputStream(IMAGE_TESTS_PATH)));
         ;  // contenu du fichier
         this.studentService = new StudentService(this.studentDao,this.iStudentClassDao,this.environment , new BCryptPasswordEncoder(),this.imageService);

     }



    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void addStudentWithTooLongLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("a".repeat(91));
        assertThrows(InvalidPersonInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithNullLastNameTest() throws IOException {
        this.studentDtoIn.setLastname(null);
        assertThrows(InvalidPersonInformationException.class,()->this.studentService.signUpStudent(this.studentDtoIn)) ;
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }




    /****************************  TESTS FOR NAME  ************************************/
    @Test
    void addStudentWithTooLongNameTest() throws IOException {
        this.studentDtoIn.setFirstname("a".repeat(91));
        assertThrows(InvalidPersonInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortNameTest() throws IOException {
        this.studentDtoIn.setFirstname("ab");
        assertThrows(InvalidPersonInformationException.class, () ->this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyNameTest() throws IOException {
        this.studentDtoIn.setFirstname("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addStudentWithNullNameTest() throws IOException {
        this.studentDtoIn.setFirstname(null);
        assertThrows(InvalidPersonInformationException.class,()->this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }



}