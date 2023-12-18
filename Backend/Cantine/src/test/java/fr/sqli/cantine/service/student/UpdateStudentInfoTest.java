package fr.sqli.cantine.service.student;

import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.users.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import fr.sqli.cantine.service.users.student.Impl.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class UpdateStudentInfoTest {
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
        this.studentClassEntity.setName("SQLI JAVA");
        this.environment = new MockEnvironment();
        this.environment.setProperty("sqli.cantine.default.persons.admin.imagename","defaultAdminImageName");
        this.environment.setProperty("sqli.cantine.admin.email.domain","social.aston-ecole.com");
        this.environment.setProperty("sqli.cantine.image.admin.path","adminImagePath");

        this.studentDtoIn = new StudentDtoIn();
        this.studentDtoIn.setUuid(java.util.UUID.randomUUID().toString());
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
        this.studentService = new StudentService(this.studentDao,this.iStudentClassDao,this.environment , new BCryptPasswordEncoder(),this.imageService ,    null ,  null);

    }



    @Test
    void  updateStudentWithStudentNotFound() {
        Mockito.when(this.studentDao.findByUuid(this.studentDtoIn.getUuid())).thenReturn(Optional.empty());
        Mockito.when(this.iStudentClassDao.findByName(this.studentDtoIn.getStudentClass())).thenReturn(Optional.of(this.studentClassEntity));
        assertThrows(UserNotFoundException.class, ()-> this.studentService.updateStudentInformation(this.studentDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.studentDtoIn.getUuid());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }





    @Test
    void  updateStudentInformationEmptyStudentClas()  {

        this.studentDtoIn.setStudentClass("");
        assertThrows(InvalidStudentClassException.class, ()-> this.studentService.updateStudentInformation(this.studentDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void  updateStudentInformationWithInvalidStudentClass() throws InvalidUserInformationException {

        this.studentDtoIn.setStudentClass("invalidFunction");
        Mockito.when(this.iStudentClassDao.findByName(this.studentDtoIn.getStudentClass())).thenReturn(Optional.empty());
        assertThrows(StudentClassNotFoundException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(1)).findByName(this.studentDtoIn.getStudentClass());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void  updateStudentInformationNullStudentClass(){

        this.studentDtoIn.setStudentClass(null);

        assertThrows(InvalidStudentClassException.class, () ->this.studentService.updateStudentInformation(this.studentDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }







    /****************************  TESTS FOR Phone  ************************************/
    @Test
    void  updateStudentWithTooLongPhoneTest() throws IOException {
        this.studentDtoIn.setPhone("a".repeat(21));
        assertThrows(InvalidUserInformationException.class, () ->this.studentService.updateStudentInformation(this.studentDtoIn));
    }

    @Test
    void  updateStudentWithTooShortPhoneTest() throws IOException {
        this.studentDtoIn.setPhone("a".repeat(5));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
    }
    @Test
    void  updateStudentWithInvalidPhoneTest() throws IOException {
        this.studentDtoIn.setPhone(" good phone");
        assertThrows(InvalidUserInformationException.class,
                () -> this.studentService.updateStudentInformation(this.studentDtoIn));
    }








    /****************************  TESTS FOR BirthdayAsString  ************************************/

    @Test
    void updateStudentWithInvalidBirthdateAsStringFormatTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("18/07/2000");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateStudentWithTooLongBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateStudentWithTooShortBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void  updateStudentWithEmptyBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateStudentWithNullBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString(null);
        assertThrows(InvalidUserInformationException.class,() -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }




    /****************************  TESTS FOR Town  ************************************/
    @Test
    void updateStudentWithTooLongTownTest() throws IOException {
        this.studentDtoIn.setTown("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateStudentWithTooShortTownTest() throws IOException {
        this.studentDtoIn.setTown("nm");
        assertThrows(InvalidUserInformationException.class, () ->  this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateStudentWithEmptyTownTest() throws IOException {
        this.studentDtoIn.setTown("   ");
        assertThrows(InvalidUserInformationException.class, () ->  this.studentService.updateStudentInformation(this.studentDtoIn)   );
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateStudentWithNullTownTest() throws IOException {
        this.studentDtoIn.setTown(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.updateStudentInformation(this.studentDtoIn) );
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
















    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void updateStudentWithTooLongLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateStudentWithTooShortLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateStudentWithEmptyLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateStudentWithNullLastNameTest() throws IOException {
        this.studentDtoIn.setLastname(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.updateStudentInformation(this.studentDtoIn)) ;
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }







    /****************************  TESTS FOR NAME  ************************************/
    @Test
    void updateStudentWithTooLongNameTest() throws IOException {
        this.studentDtoIn.setFirstname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateStudentWithTooShortNameTest() throws IOException {
        this.studentDtoIn.setFirstname("ab");
        assertThrows(InvalidUserInformationException.class, () ->this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateStudentWithEmptyNameTest() throws IOException {
        this.studentDtoIn.setFirstname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void updateStudentWithNullNameTest() throws IOException {
        this.studentDtoIn.setFirstname(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR ID  ************************************/

    @Test
    void updateStudentWithShortUuid() throws IOException {
        this.studentDtoIn.setUuid("ajbajbzdjazdaz");
        assertThrows(InvalidUserInformationException.class,()->this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void updateStudentWithNullUuID() throws IOException {
        this.studentDtoIn.setUuid(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.updateStudentInformation(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


}
