package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
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
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    private static final Logger LOG = LogManager.getLogger();
    final   String  IMAGE_TESTS_PATH = "imagesForTests/meals/ImageMealForTest.jpg";
    @Mock
    private AdminDao adminDao;
    @Mock
    private ImageService imageService;

    @Mock
    private IFunctionDao functionDao;
    @Mock
    private MockEnvironment environment;
    @InjectMocks
    private AdminService adminService;

    private AdminDtoIn adminDtoIn;

    @BeforeEach
    void setUp() throws IOException, FileNotFoundException {
        this.environment= new MockEnvironment();
        this.environment.setProperty("sqli.cantine.default.persons.admin.imagename","defaultAdminImageName");
        this.environment.setProperty("sqli.cantine.admin.email.domain","social.aston-ecole.com");
        this.environment.setProperty("sqli.cantine.image.admin.path","adminImagePath");
        this.adminDtoIn = new AdminDtoIn();
        this.adminDtoIn.setFirstname("firstName");
        this.adminDtoIn.setLastname("lastName");
        this.adminDtoIn.setEmail("email@social.aston-ecole.com");
        this.adminDtoIn.setFunction("function");
        this.adminDtoIn.setBirthdateAsString("1999-01-01");
        this.adminDtoIn.setPhone("0600000000");
        this.adminDtoIn.setAddress("address");
        this.adminDtoIn.setTown("city");
        this.adminDtoIn.setPassword("password");
        this.adminDtoIn.setImage(new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageForTests",          // nom du fichier
                "images/png",                    // type MIME
                new FileInputStream(IMAGE_TESTS_PATH)));
                ;  // contenu du fichier

        this.adminService = new AdminService(adminDao, functionDao,imageService,  this.environment, new BCryptPasswordEncoder());
    }



    /****************************  TESTS FOR BirthdayAsString  ************************************/

    @Test
    void addAdminWithInvalidBirthdateAsStringFormatTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("18/07/2000");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithTooLongBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("a".repeat(2001));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithTooShortBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithEmptyBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithNullBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.signUp(this.adminDtoIn));
    }








    /****************************  TESTS FOR ADDRESS  ************************************/
    @Test
    void addAdminWithTooLongAddressTest() throws IOException {
        this.adminDtoIn.setAddress("a".repeat(2001));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithTooShortAddressTest() throws IOException {
        this.adminDtoIn.setAddress("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithEmptyAddressTest() throws IOException {
        this.adminDtoIn.setAddress("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithNullAddressTest() throws IOException {
        this.adminDtoIn.setAddress(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.signUp(this.adminDtoIn));
    }








    /****************************  TESTS FOR Town  ************************************/
    @Test
    void addAdminWithTooLongTownTest() throws IOException {
        this.adminDtoIn.setTown("a".repeat(2001));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithTooShortTownTest() throws IOException {
        this.adminDtoIn.setTown("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithEmptyTownTest() throws IOException {
        this.adminDtoIn.setTown("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithNullTownTest() throws IOException {
        this.adminDtoIn.setTown(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.signUp(this.adminDtoIn));
    }






    /****************************  TESTS FOR email  ************************************/



   @Test
   void addAdminWithInvalidEmailTest() throws IOException, InvalidPersonInformationException {
       this.adminDtoIn.setEmail("a".repeat(91));
       var  functionEntity = new FunctionEntity();
         functionEntity.setName(this.adminDtoIn.getFunction());

       Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(  Optional.of(functionEntity));

       assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));


       Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());
       Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
   }


    @Test
    void addAdminWithTooLongEmailTest() throws IOException {
        this.adminDtoIn.setEmail("a".repeat(1001));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }


    @Test
    void addAdminWithEmptyEmailTest() throws IOException {
        this.adminDtoIn.setEmail("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithNullEmailTest() throws IOException {
        this.adminDtoIn.setEmail(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.signUp(this.adminDtoIn));
    }





    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void addAdminWithTooLongLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("a".repeat(91));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithTooShortLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithEmptyLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }
    @Test
    void addAdminWithNullLastNameTest() throws IOException {
        this.adminDtoIn.setLastname(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.signUp(this.adminDtoIn));
    }


    /****************************  TESTS FOR NAME  ************************************/
   @Test
   void addAdminWithTooLongNameTest() throws IOException {
       this.adminDtoIn.setFirstname("a".repeat(91));
       assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
   }

  @Test
   void addAdminWithTooShortNameTest() throws IOException {
       this.adminDtoIn.setFirstname("ab");
      assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
   }
   @Test
   void addAdminWithEmptyNameTest() throws IOException {
       this.adminDtoIn.setFirstname("   ");
       assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
   }
    @Test
    void addAdminWithNullNameTest() throws IOException {
        this.adminDtoIn.setFirstname(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.signUp(this.adminDtoIn));
    }





}