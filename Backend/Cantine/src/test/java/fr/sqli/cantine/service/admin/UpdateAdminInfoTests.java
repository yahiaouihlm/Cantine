package fr.sqli.cantine.service.admin;

import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.entity.FunctionEntity;
import fr.sqli.cantine.service.users.admin.impl.AdminService;

import fr.sqli.cantine.service.users.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
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
    private IAdminDao adminDao;
    @Mock
    private ImageService imageService;

    @Mock
    private IConfirmationTokenDao iConfirmationToken;
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
        this.adminDtoIn.setUuid(java.util.UUID.randomUUID().toString());
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
        this.adminService = new AdminService(adminDao, functionDao,imageService,  this.environment, new BCryptPasswordEncoder(),  null, null);

    }





    /****************************  TESTS FOR FUNCTIONS  ************************************/


    @Test
    void updateAdminInformationEmptyFunction(){

        this.adminDtoIn.setFunction("");


        assertThrows(InvalidUserInformationException.class, () ->this.adminService.updateAdminInfo( this.adminDtoIn));

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationNullFunction(){

        this.adminDtoIn.setFunction(null);

        assertThrows(InvalidUserInformationException.class, () ->this.adminService.updateAdminInfo( this.adminDtoIn));

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void  updateAdminInformationWithInvalidFunction() throws InvalidUserInformationException {

        this.adminDtoIn.setFunction("invalidFunction");
        Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.empty());
        assertThrows(AdminFunctionNotFoundException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    /****************************  TESTS FOR Phone  ************************************/
    @Test
    void updateAdminInfoWithTooLongPhoneTest() throws IOException {

        this.adminDtoIn.setPhone("a".repeat(21));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
    }

    @Test
    void updateAdminInfoWithTooShortPhoneTest() throws IOException {
        this.adminDtoIn.setPhone("a".repeat(5));
        assertThrows(InvalidUserInformationException.class, () ->this.adminService.updateAdminInfo( this.adminDtoIn));
    }
    @Test
    void updateAdminInfoWithEmptyPhoneTest() throws IOException {
        this.adminDtoIn.setPhone("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
    }
    @Test
    void updateAdminInfoWithNullPhoneTest() throws IOException {
        this.adminDtoIn.setPhone(null);
        assertThrows(InvalidUserInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn));
    }





    /****************************  TESTS FOR BirthdayAsString  ************************************/

    @Test
    void updateAdminInfoWithInvalidBirthdateAsStringFormatTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("18/07/2000");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInfoWithTooLongBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInfoWithTooShortBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInfoWithEmptyBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInfoWithNullBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString(null);
        assertThrows(InvalidUserInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }





    /****************************  TESTS FOR ADDRESS  ************************************/
    @Test
    void updateAdminInfoWithTooLongAddressTest() throws IOException {
        this.adminDtoIn.setAddress("a".repeat(3001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInfoWithTooShortAddressTest() throws IOException {
        this.adminDtoIn.setAddress("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInfoWithEmptyAddressTest() throws IOException {
        this.adminDtoIn.setAddress("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addAdminWithNullAddressTest() throws IOException {

        this.adminDtoIn.setAddress(null);
        assertThrows(InvalidUserInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }





    /****************************  TESTS FOR Town  ************************************/
    @Test
    void updateAdminWithTooLongTownTest() throws IOException {
        this.adminDtoIn.setTown("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminWithTooShortTownTest() throws IOException {

        this.adminDtoIn.setTown("nm");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminWithEmptyTownTest() throws IOException {
        this.adminDtoIn.setTown("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminWithNullTownTest() throws IOException {
        this.adminDtoIn.setTown(null);
        assertThrows(InvalidUserInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn ));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }










    /****************************  TESTS FOR email And Password presences   ************************************/



    @Test
    void updateAdminWithPresenceOfEmail() throws IOException {
        this.adminDtoIn.setEmail("test");

        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminWithPresenceOfPassword() throws IOException {
        this.adminDtoIn.setPassword("test33");

        assertThrows(InvalidUserInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }



    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void updateAdminInformationWithTooLongLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("a".repeat(91));

        assertThrows(InvalidUserInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInformationWithTooShortLastNameTest() throws IOException {

        this.adminDtoIn.setLastname("ab");
        assertThrows(InvalidUserInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithEmptyLastNameTest() throws IOException {

        this.adminDtoIn.setLastname("   ");
        assertThrows(InvalidUserInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithNullLastNameTest() throws IOException {

        this.adminDtoIn.setLastname(null);
        assertThrows(InvalidUserInformationException.class,()-> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }









    /****************************  TESTS FOR NAME  ************************************/
    @Test
    void updateAdminInformationWithTooLongNameTest() throws IOException {

        this.adminDtoIn.setFirstname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () ->  this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void updateAdminInformationWithTooShortNameTest() throws IOException {

        this.adminDtoIn.setFirstname("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithEmptyNameTest() throws IOException {

        this.adminDtoIn.setFirstname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void updateAdminInformationWithNullNameTest() throws IOException {

        this.adminDtoIn.setFirstname(null);
        assertThrows(InvalidUserInformationException.class,()->this.adminService.updateAdminInfo( this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }



    /******************************** TESTS  ADMIN ID  ********************************/

 @Test
 void  updateAdminInformationWithNotFoundAdmin () throws InvalidUserInformationException {
     String adminUuid = java.util.UUID.randomUUID().toString();
     this.adminDtoIn.setUuid(adminUuid);
     FunctionEntity function = new FunctionEntity();
        function.setId(1);
        function.setName(this.adminDtoIn.getFunction());

     Mockito.when(this.adminDao.findByUuid(adminUuid)).thenReturn(Optional.empty());
     Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.of(function));

     Assertions.assertThrows(UserNotFoundException.class, () -> {
         this.adminService.updateAdminInfo( this.adminDtoIn);
     });

        Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());

 }
     @Test
   void updateAdminWithInvalidUuid (){
         this.adminDtoIn.setUuid("ehbrerfrfr");
         assertThrows(InvalidUserInformationException.class, () -> {
              this.adminService.updateAdminInfo( this.adminDtoIn);
         });
  }
    @Test
     void updateAdminInfoWithNullUuid (){
        this.adminDtoIn.setUuid(null);
        assertThrows(InvalidUserInformationException.class, () -> {
            this.adminService.updateAdminInfo( this.adminDtoIn);
        });
    }



    @Test
    void updateAdminInfoWithNullRequest (){
        assertThrows(InvalidUserInformationException.class, () -> {
            this.adminService.updateAdminInfo(  null );
        });
    }













}
