package fr.sqli.cantine.service.admin;


import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IConfirmationTokenDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.FunctionEntity;
import fr.sqli.cantine.entity.ImageEntity;
import fr.sqli.cantine.service.users.admin.impl.AdminService;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.images.ImageService;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GetAdminTest {
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";
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

    @BeforeEach
    void  setUp (){
        this.adminService = new AdminService(adminDao, functionDao,imageService,  this.environment, new BCryptPasswordEncoder(), this.iConfirmationToken,  null);

    }


    @Test
    void  getAdminByUuidTest () throws InvalidUserInformationException, UserNotFoundException {
        var adminUuid = java.util.UUID.randomUUID().toString() ;
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setId(1);
        functionEntity.setName("test-function");

        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setId(1);
        adminEntity.setUuid(adminUuid);
        adminEntity.setFirstname("firstname");
        adminEntity.setLastname("lastname");
        adminEntity.setEmail("email@test.fr");
        adminEntity.setBirthdate(LocalDate.now());
        adminEntity.setAddress("address");
        adminEntity.setTown("town");
        adminEntity.setPhone("0631990180");
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImagename(IMAGE_TESTS_PATH);
        adminEntity.setImage(imageEntity);
        adminEntity.setFunction(functionEntity);
        Mockito.when(this.adminDao.findByUuid(adminEntity.getUuid())).thenReturn(Optional.of(adminEntity));

        var  result =  this.adminService.getAdminByUuID(adminUuid);

        Assertions.assertEquals(result.getUuid(), adminEntity.getUuid());
        Assertions.assertEquals(result.getFirstname(), adminEntity.getFirstname());
        Assertions.assertEquals(result.getLastname(), adminEntity.getLastname());
        Assertions.assertEquals(result.getEmail(), adminEntity.getEmail());
        Assertions.assertEquals(result.getBirthdate(), adminEntity.getBirthdate());
        Assertions.assertEquals(result.getAddress(), adminEntity.getAddress());
        Assertions.assertEquals(result.getTown(), adminEntity.getTown());
        Assertions.assertEquals(result.getPhone(), adminEntity.getPhone());

        Mockito.verify(this.adminDao, Mockito.times(1)).findByUuid(adminEntity.getUuid());


    }

    @Test
    void  getAdminByUuidWithNotFoundAdmin () throws InvalidUserInformationException {
        String adminUuid = java.util.UUID.randomUUID().toString();
        Mockito.when(this.adminDao.findByUuid(adminUuid)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.adminService.getAdminByUuID(adminUuid)  ;
        });

        Mockito.verify(this.adminDao, Mockito.times(1)).findByUuid(adminUuid);
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());

    }


    @Test
    void getAdminByUuidWithShortUuId (){
        String adminUuid = "izezedzedze*4zed4";
        assertThrows(InvalidUserInformationException.class, () -> {
            this.adminService.getAdminByUuID(adminUuid) ;
        });
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void getAdminByUuidWithNullUuid (){
         assertThrows(InvalidUserInformationException.class, () -> {
            this.adminService.getAdminByUuID(null);
        });

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }








}
