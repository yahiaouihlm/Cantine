package fr.sqli.Cantine.service.admin.adminDashboard;

import fr.sqli.Cantine.dao.AdminDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.dto.in.person.AdminDtoIn;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.ImageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
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
        this.adminService = new AdminService(adminDao, functionDao,imageService,  this.environment, new BCryptPasswordEncoder());

    }

    /****************************  TESTS FOR FUNCTIONS  ************************************/

    @Test
    void updateAdminInformationEmptyFunction(){

        this.adminDtoIn.setFunction("");
        var idMenu = 1;

        assertThrows(InvalidPersonInformationException.class, () ->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationNullFunction(){

        this.adminDtoIn.setFunction(null);
        var idMenu = 1;

        assertThrows(InvalidPersonInformationException.class, () ->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void  updateAdminInformationWithInvalidFunction() throws InvalidPersonInformationException {
        var idMenu = 1;
        this.adminDtoIn.setFunction("invalidFunction");
        Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.empty());
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    /****************************  TESTS FOR Phone  ************************************/
    @Test
    void updateAdminInfoWithTooLongPhoneTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setPhone("a".repeat(21));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
    }

    @Test
    void updateAdminInfoWithTooShortPhoneTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setPhone("a".repeat(5));
        assertThrows(InvalidPersonInformationException.class, () ->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
    }
    @Test
    void updateAdminInfoWithEmptyPhoneTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setPhone("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
    }
    @Test
    void updateAdminInfoWithNullPhoneTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setPhone(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
    }





    /****************************  TESTS FOR BirthdayAsString  ************************************/

    @Test
    void updateAdminInfoWithInvalidBirthdateAsStringFormatTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("18/07/2000");
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInfoWithTooLongBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("a".repeat(2001));
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInfoWithTooShortBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("ab");
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInfoWithEmptyBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("   ");
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInfoWithNullBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString(null);
        var idMenu = 1;
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }





    /****************************  TESTS FOR ADDRESS  ************************************/
    @Test
    void updateAdminInfoWithTooLongAddressTest() throws IOException {
        var idMenu = 1;
        this.adminDtoIn.setAddress("a".repeat(3001));
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInfoWithTooShortAddressTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setAddress("ab");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInfoWithEmptyAddressTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setAddress("   ");
        assertThrows(InvalidPersonInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addAdminWithNullAddressTest() throws IOException {
        var  idMenu = 1;
        this.adminDtoIn.setAddress(null);
        assertThrows(InvalidPersonInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn, idMenu));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
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
 void  updateAdminInformationWithNotFoundAdmin () throws InvalidPersonInformationException {
     Integer idMenu = 1 ;
     Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.of(this.functionEntity));
     Mockito.when(this.adminDao.findById(idMenu)).thenReturn(Optional.empty());
     Assertions.assertThrows(AdminNotFound.class, () -> {
         this.adminService.updateAdminInfo( this.adminDtoIn, idMenu);
     });


 }
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
