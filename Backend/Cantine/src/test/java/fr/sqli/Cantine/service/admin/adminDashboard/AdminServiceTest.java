package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.IImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    final   String  IMAGE_TESTS_PATH = "imagesForTests/meals/ImageMealForTest.jpg";
    @Mock
    private AdminDao adminDao;
    @Mock
    private IImageService imageService;

    @Mock
    private IFunctionDao functionDao;
    @Mock
    private MockEnvironment environment;
    @InjectMocks
    private AdminService adminService;

    private AdminDtoIn adminDtoIn;

    @BeforeEach
    void setUp() throws IOException {
        environment.setProperty("sqli.cantine.default.persons.admin.imagename","defaultAdminImageName");
        environment.setProperty("sqli.cantine.admin.email.domain","social.aston-ecole.com");
        environment.setProperty("sqli.cantine.image.admin.path","adminImagePath");
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
    }

    /****************************  TESTS FOR email  ************************************/



   @Test
   void addAdminWithInvalidEmailTest() throws IOException, InvalidPersonInformationException {
       this.adminDtoIn.setLastname("a".repeat(91));
       var  functionEntity = new FunctionEntity();
         functionEntity.setName(this.adminDtoIn.getFunction());

       Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(  Optional.of(functionEntity));

       assertThrows(InvalidPersonInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));

   }


    @Test
    void addAdminWithTooLongEmailTest() throws IOException {
        this.adminDtoIn.setLastname("a".repeat(91));
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