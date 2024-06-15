package fr.sqli.cantine.service.admin;

import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.dto.in.users.AdminDtoIn;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.FunctionEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.service.users.admin.impl.AdminService;
import fr.sqli.cantine.service.users.exceptions.AdminFunctionNotFoundException;
import fr.sqli.cantine.service.users.exceptions.ExistingUserException;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.images.ImageService;

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
class AddAdminTest {

    private static final Logger LOG = LogManager.getLogger();
    final String IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";
    @Mock
    private IAdminDao adminDao;
    @Mock
    private ImageService imageService;
    private IConfirmationTokenDao iConfirmationToken;
    @Mock
    private IFunctionDao functionDao;
    @Mock
    private MockEnvironment environment;
    @InjectMocks
    private AdminService adminService;

    @Mock
    private IStudentDao studentDao;
    private FunctionEntity functionEntity;
    private AdminDtoIn adminDtoIn;

    @BeforeEach
    void setUp() throws IOException, FileNotFoundException {
        this.functionEntity = new FunctionEntity();
        this.functionEntity.setName("function");
        this.environment = new MockEnvironment();
        this.environment.setProperty("sqli.cantine.admin.default.image", "defaultAdminImageName");
        this.environment.setProperty("sqli.cantine.admin.email.domain", "social.aston-ecole.com");
        this.environment.setProperty("sqli.cantine.image.admin.path", "adminImagePath");
        this.adminDtoIn = new AdminDtoIn();
        this.adminDtoIn.setFirstname("firstName");
        this.adminDtoIn.setLastname("lastName");
        this.adminDtoIn.setEmail("email@social.aston-ecole.com");
        this.adminDtoIn.setFunction("function");
        this.adminDtoIn.setBirthdateAsString("1999-01-01");
        this.adminDtoIn.setPhone("0600000000");
        this.adminDtoIn.setAddress("166 Rue Jules Guesde, 92300 Levallois-Perret ");
        this.adminDtoIn.setTown("city");
        this.adminDtoIn.setPassword("password");
        this.adminDtoIn.setImage(new MockMultipartFile(
                "image",                         // nom du champ de fichier
                "ImageForTests",          // nom du fichier
                "images/png",                    // type MIME
                new FileInputStream(IMAGE_TESTS_PATH)));
        ;  // contenu du fichier
        this.functionEntity = new FunctionEntity();
        this.adminService = new AdminService(adminDao, functionDao, imageService, this.environment, new BCryptPasswordEncoder(), null, null);

    }


    @Test
    void addAdminWithExisingEmailTest() throws InvalidUserInformationException {
        this.adminDtoIn.setEmail("yahiaouihlm@gmail.com");
        Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.of(functionEntity));
        Mockito.when(this.adminDao.findByEmail(this.adminDtoIn.getEmail())).thenReturn(Optional.of(new AdminEntity()));

        assertThrows(ExistingUserException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithExitingEmailInStudentTable() throws InvalidUserInformationException {
        this.studentDao = Mockito.mock(IStudentDao.class);
        this.adminService.setStudentDao(this.studentDao); // inject mock  because the  adminDao  is  not  injected  with  setter method
        Mockito.when(this.studentDao.findByEmail(this.adminDtoIn.getEmail())).thenReturn(Optional.of(new StudentEntity()));
        Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.of(functionEntity));
        assertThrows(ExistingUserException.class, () -> this.adminService.signUp(this.adminDtoIn));

        Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());
        Mockito.verify(this.adminDao, Mockito.times(1)).findByEmail(this.adminDtoIn.getEmail());
        Mockito.verify(this.studentDao, Mockito.times(0)).save(Mockito.any());
    }

    /****************************  TESTS FOR FUNCTIONS  ************************************/
    @Test
    void addAdminInformationEmptyFunction() {

        this.adminDtoIn.setFunction("");
        assertThrows(InvalidUserInformationException.class, () -> this.adminDtoIn.getFunction());

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminInformationWithInvalidFunction() throws InvalidUserInformationException {

        this.adminDtoIn.setFunction("invalidFunction");
        Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.empty());
        assertThrows(AdminFunctionNotFoundException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addAdminInformationNullFunction() {

        this.adminDtoIn.setFunction(null);

        assertThrows(InvalidUserInformationException.class, () -> this.adminDtoIn.getFunction());

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    /****************************  TESTS FOR Phone  ************************************/
    @Test
    void addAdminWithTooLongPhoneTest() throws IOException {
        this.adminDtoIn.setPhone("a".repeat(21));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithTooShortPhoneTest() throws IOException {
        this.adminDtoIn.setPhone("a".repeat(5));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithEmptyPhoneTest() throws IOException {
        this.adminDtoIn.setPhone("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }

    @Test
    void addAdminWithNullPhoneTest() throws IOException {
        this.adminDtoIn.setPhone(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
    }


    /****************************  TESTS FOR PASSWORD  ************************************/

    @Test
    void addAdminWithTooLongPasswordTest() throws IOException {
        this.adminDtoIn.setPassword("a".repeat(21));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooShortPasswordTest() throws IOException {
        this.adminDtoIn.setPassword("a".repeat(5));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithEmptyPasswordTest() throws IOException {
        this.adminDtoIn.setPassword("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullPasswordTest() throws IOException {
        this.adminDtoIn.setPassword(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR BirthdayAsString  ************************************/

    @Test
    void addAdminWithInvalidBirthdateAsStringFormatTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("18/07/2000");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooLongBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooShortBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithEmptyBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullBirthdateAsStringTest() throws IOException {
        this.adminDtoIn.setBirthdateAsString(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR ADDRESS  ************************************/
    @Test
    void addAdminWithTooLongAddressTest() throws IOException {
        this.adminDtoIn.setAddress("a".repeat(3001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooShortAddressTest() throws IOException {
        this.adminDtoIn.setAddress("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithEmptyAddressTest() throws IOException {
        this.adminDtoIn.setAddress("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullAddressTest() throws IOException {
        this.adminDtoIn.setAddress(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR Town  ************************************/
    @Test
    void addAdminWithTooLongTownTest() {
        this.adminDtoIn.setTown("a".repeat(2001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooShortTownTest() {
        this.adminDtoIn.setTown("nm");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithEmptyTownTest() {
        this.adminDtoIn.setTown("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullTownTest() throws IOException {
        this.adminDtoIn.setTown(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR email  ************************************/


    @Test
    void addAdminWithInvalidEmailTest() throws InvalidUserInformationException {
        this.adminDtoIn.setEmail("a".repeat(91));

        this.functionEntity.setName(this.adminDtoIn.getFunction());

        Mockito.when(this.functionDao.findByName(this.adminDtoIn.getFunction())).thenReturn(Optional.of(functionEntity));

        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));


        Mockito.verify(this.functionDao, Mockito.times(1)).findByName(this.adminDtoIn.getFunction());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addAdminWithTooLongEmailTest() throws IOException {
        this.adminDtoIn.setEmail("a".repeat(1001));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addAdminWithEmptyEmailTest() throws IOException {
        this.adminDtoIn.setEmail("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullEmailTest() throws IOException {
        this.adminDtoIn.setEmail(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR LASTNAME  ************************************/

    @Test
    void addAdminWithTooLongLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooShortLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithEmptyLastNameTest() throws IOException {
        this.adminDtoIn.setLastname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullLastNameTest() throws IOException {
        this.adminDtoIn.setLastname(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    /****************************  TESTS FOR NAME  ************************************/
    @Test
    void addAdminWithTooLongNameTest() throws IOException {
        this.adminDtoIn.setFirstname("a".repeat(91));
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithTooShortNameTest() throws IOException {
        this.adminDtoIn.setFirstname("ab");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithEmptyNameTest() throws IOException {
        this.adminDtoIn.setFirstname("   ");
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addAdminWithNullNameTest() throws IOException {
        this.adminDtoIn.setFirstname(null);
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(this.adminDtoIn));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addAdminWithNullRequest() throws IOException {
        assertThrows(InvalidUserInformationException.class, () -> this.adminService.signUp(null));
        Mockito.verify(this.functionDao, Mockito.times(0)).findByName(Mockito.anyString());
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }


}