package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class UpdateAdminInfoTests {
    private static final Logger LOG = LogManager.getLogger();
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";
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
    private FunctionEntity functionEntity;
    private AdminDtoIn adminDtoIn;

    @BeforeEach
    void setUp() throws IOException, FileNotFoundException {
        this.functionEntity = new FunctionEntity();
        this.functionEntity.setName("function");
        this.environment= new MockEnvironment();
        this.environment.setProperty("sqli.cantine.default.persons.admin.imagename","defaultAdminImageName");
        this.environment.setProperty("sqli.cantine.admin.email.domain","social.aston-ecole.com");
        this.environment.setProperty("sqli.cantine.image.admin.path","adminImagePath");
        this.adminDtoIn = new AdminDtoIn();
        this.adminDtoIn.setFirstname("firstName");
        this.adminDtoIn.setLastname("lastName");
        this.adminDtoIn.setFunction("function");
        this.adminDtoIn.setBirthdateAsString("1999-01-01");
        this.adminDtoIn.setPhone("0600000000");
        this.adminDtoIn.setAddress("166 Rue Jules Guesde, 92300 Levallois-Perret ");
        this.adminDtoIn.setTown("city");
        this.adminDtoIn.setImage(new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageForTests",          // nom du fichier
                "images/png",                    // type MIME
                new FileInputStream(IMAGE_TESTS_PATH)));
        ;  // contenu du fichier
        this.functionEntity =  new FunctionEntity();
        this.adminService = new AdminService(adminDao, functionDao,imageService,  this.environment, new BCryptPasswordEncoder());

    }





    /****************************  TESTS FOR Town  ************************************/
    @Test
    void updateAdminWithTooLongTownTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setTown("a".repeat(2001));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminWithTooShortTownTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setTown("nm");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminWithEmptyTownTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setTown("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminWithNullTownTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setTown(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }










    /****************************  TESTS FOR email And Password presences   ************************************/



    @Test
    void updateAdminWithPresenceOfEmail() throws IOException {
        this.adminDtoIn.setEmail("test");
        var  idMenu = 1;
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminWithPresenceOfPassword() throws IOException {
        this.adminDtoIn.setPassword("test33");
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }



    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void updateAdminInformationWithTooLongLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("a".repeat(91));
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInformationWithTooShortLastNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setLastname("ab");
        assertThrows(InvalidPersonInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithEmptyLastNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setLastname("   ");
        assertThrows(InvalidPersonInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithNullLastNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setLastname(null);
        assertThrows(InvalidPersonInformationException.class,()-> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }









    /****************************  TESTS FOR NAME  ************************************/
    @Test
    void updateAdminInformationWithTooLongNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setFirstname("a".repeat(91));
        assertThrows(InvalidPersonInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInformationWithTooShortNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setFirstname("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithEmptyNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setFirstname("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithNullNameTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setFirstname(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }



    /******************************** TESTS  ADMIN ID  ********************************/


  @Test
   void updateAdminWithNegativeID (){
       Integer  idMenu =  -1;
         assertThrows(InvalidPersonInformationException.class, () -> {
              this.adminService.updateAdminInfo( this.adminDtoIn, idMenu);
         });
  }
    @Test
     void updateAdminInfoWithNullID (){
        Integer  idMenu =  null;
        assertThrows(InvalidPersonInformationException.class, () -> {
            this.adminService.updateAdminInfo( this.adminDtoIn, idMenu);
        });
    }















}
