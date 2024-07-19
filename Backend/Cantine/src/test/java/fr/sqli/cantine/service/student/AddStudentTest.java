package fr.sqli.cantine.service.student;


import fr.sqli.cantine.dao.IRoleDao;
import fr.sqli.cantine.dao.IStudentClassDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.dto.in.users.StudentDtoIn;
import fr.sqli.cantine.entity.StudentClassEntity;
import fr.sqli.cantine.entity.UserEntity;
import fr.sqli.cantine.service.users.exceptions.ExistingUserException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.InvalidStudentClassException;
import fr.sqli.cantine.service.users.exceptions.StudentClassNotFoundException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.users.student.Impl.StudentService;
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
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AddStudentTest {
    private static final Logger LOG = LogManager.getLogger();
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";

    @Mock
    private IStudentClassDao iStudentClassDao;
    @Mock
    private IUserDao studentDao;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private StudentService studentService;
    @Mock
    private MockEnvironment environment;
    @Mock
    private IRoleDao roleDao;

    @Mock
    private IUserDao adminDao;
    private StudentClassEntity studentClassEntity;
    private StudentDtoIn studentDtoIn ;

    @BeforeEach
     void  setUp  () throws IOException {
         this.studentClassEntity = new StudentClassEntity();
         this.studentClassEntity.setId(java.util.UUID.randomUUID().toString());
         this.studentClassEntity.setName("SQLI JAVA");
         this.environment = new MockEnvironment();
         this.environment.setProperty("sqli.cantine.admin.default.image","defaultAdminImageName");
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
         this.studentService = new StudentService(this.studentDao, this.roleDao , this.iStudentClassDao, this.environment , new BCryptPasswordEncoder(),this.imageService,  null );

     }

    @Test
    void addStudentWithExitingEmailInAdminTable()  {
        this.adminDao = Mockito.mock(IUserDao.class);
       // this.studentService.set(this.adminDao); // inject mock  because the  adminDao  is  not  injected  with  setter method
        Mockito.when(this.adminDao.findAdminByEmail(this.studentDtoIn.getEmail())).thenReturn(Optional.of(new UserEntity()));
        Mockito.when(this.iStudentClassDao.findByName(this.studentDtoIn.getStudentClass())).thenReturn(Optional.of(this.studentClassEntity));
        assertThrows(ExistingUserException.class, ()-> this.studentService.signUpStudent(this.studentDtoIn));

        Mockito.verify(this.iStudentClassDao, Mockito.times(1)).findByName(this.studentDtoIn.getStudentClass());
        Mockito.verify(this.adminDao, Mockito.times(1)).findAdminByEmail(this.studentDtoIn.getEmail());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }



     @Test
     void addStudentWithExistingEmailTest() throws IOException {
         Mockito.when(this.iStudentClassDao.findByName(this.studentDtoIn.getStudentClass())).thenReturn(Optional.of(this.studentClassEntity));
         Mockito.when(this.studentDao.findStudentByEmail(this.studentDtoIn.getEmail())).thenReturn(Optional.of(new UserEntity()));
         assertThrows(ExistingUserException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
            Mockito.verify(this.iStudentClassDao, Mockito.times(1)).findByName(this.studentDtoIn.getStudentClass());
         Mockito.verify(this.studentDao, Mockito.times(1)).findStudentByEmail(this.studentDtoIn.getEmail());
         Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
     }
    /****************************  TESTS FOR STUDENT CLASS  ************************************/


    @Test
    void addStudentInformationEmptyStudentClas()  {

        this.studentDtoIn.setStudentClass("");
        assertThrows(InvalidStudentClassException.class, ()-> this.studentService.signUpStudent(this.studentDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentInformationWithInvalidStudentClass() throws InvalidUserInformationException {

        this.studentDtoIn.setStudentClass("invalidFunction");
        Mockito.when(this.iStudentClassDao.findByName(this.studentDtoIn.getStudentClass())).thenReturn(Optional.empty());
        assertThrows(StudentClassNotFoundException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(1)).findByName(this.studentDtoIn.getStudentClass());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addStudentInformationNullStudentClass(){

        this.studentDtoIn.setStudentClass(null);

        assertThrows(InvalidStudentClassException.class, () ->this.studentService.signUpStudent(this.studentDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }



    /****************************  TESTS FOR Phone  ************************************/
    @Test
    void addStudentWithTooLongPhoneTest() throws IOException {
        this.studentDtoIn.setPhone("a".repeat(21));
        assertThrows(InvalidUserInformationException.class, () ->this.studentService.signUpStudent(this.studentDtoIn));
    }

    @Test
    void addStudentWithTooShortPhoneTest() throws IOException {
        this.studentDtoIn.setPhone("a".repeat(5));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
    }
    @Test
    void addStudentWithInvalidPhoneTest() throws IOException {
        this.studentDtoIn.setPhone(" good phone");
        assertThrows(InvalidUserInformationException.class,
                () -> this.studentService.signUpStudent(this.studentDtoIn));
    }






    /****************************  TESTS FOR PASSWORD  ************************************/

    @Test
    void addStudentWithTooLongPasswordTest() throws IOException {
        this.studentDtoIn.setPassword("a".repeat(21));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortPasswordTest() throws IOException {
        this.studentDtoIn.setPassword("a".repeat(5));
        assertThrows(InvalidUserInformationException.class,  () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyPasswordTest() throws IOException {
        this.studentDtoIn.setPassword("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithNullPasswordTest() throws IOException {
        this.studentDtoIn.setPassword(null);
        assertThrows(InvalidUserInformationException.class,() -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }



    /****************************  TESTS FOR BirthdayAsString  ************************************/

    @Test
    void addStudentWithInvalidBirthdateAsStringFormatTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("18/07/2000");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooLongBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithNullBirthdateAsStringTest() throws IOException {
        this.studentDtoIn.setBirthdateAsString(null);
        assertThrows(InvalidUserInformationException.class,() -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }




    /****************************  TESTS FOR Town  ************************************/
    @Test
    void addStudentWithTooLongTownTest() throws IOException {
        this.studentDtoIn.setTown("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortTownTest() throws IOException {
        this.studentDtoIn.setTown("nm");
        assertThrows(InvalidUserInformationException.class, () ->  this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyTownTest() throws IOException {
        this.studentDtoIn.setTown("   ");
        assertThrows(InvalidUserInformationException.class, () ->  this.studentService.signUpStudent(this.studentDtoIn)   );
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithNullTownTest() throws IOException {
        this.studentDtoIn.setTown(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.signUpStudent(this.studentDtoIn) );
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }



    /****************************  TESTS FOR email  ************************************/

    @Test
    void addStudentWithInvalidEmailTest() throws IOException, InvalidUserInformationException {
        this.studentDtoIn.setEmail("a".repeat(91));

        this.studentClassEntity.setName(this.studentDtoIn.getStudentClass());

        Mockito.when(this.iStudentClassDao.findByName(this.studentDtoIn.getStudentClass())).thenReturn(  Optional.of(this.studentClassEntity));

        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));


        Mockito.verify(this.iStudentClassDao, Mockito.times(1)).findByName(this.studentDtoIn.getStudentClass());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addStudentWithTooLongEmailTest() throws IOException {
        this.studentDtoIn.setEmail("a".repeat(1001));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addAdminWithEmptyEmailTest() throws IOException {
        this.studentDtoIn.setEmail("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithNullEmailTest() throws IOException {
        this.studentDtoIn.setEmail(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }














    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void addStudentWithTooLongLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyLastNameTest() throws IOException {
        this.studentDtoIn.setLastname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithNullLastNameTest() throws IOException {
        this.studentDtoIn.setLastname(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.signUpStudent(this.studentDtoIn)) ;
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }




    /****************************  TESTS FOR NAME  ************************************/
    @Test
    void addStudentWithTooLongNameTest() throws IOException {
        this.studentDtoIn.setFirstname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addStudentWithTooShortNameTest() throws IOException {
        this.studentDtoIn.setFirstname("ab");
        assertThrows(InvalidUserInformationException.class, () ->this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addStudentWithEmptyNameTest() throws IOException {
        this.studentDtoIn.setFirstname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addStudentWithNullNameTest() {
        this.studentDtoIn.setFirstname(null);
        assertThrows(InvalidUserInformationException.class,()->this.studentService.signUpStudent(this.studentDtoIn));
        Mockito.verify(this.iStudentClassDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }



}