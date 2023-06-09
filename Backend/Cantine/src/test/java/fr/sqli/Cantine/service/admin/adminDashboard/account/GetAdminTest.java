package fr.sqli.Cantine.service.admin.adminDashboard.account;


import fr.sqli.Cantine.dao.IAdminDao;
import fr.sqli.Cantine.dao.IConfirmationTokenDao;
import fr.sqli.Cantine.dao.IFunctionDao;
import fr.sqli.Cantine.entity.AdminEntity;
import fr.sqli.Cantine.entity.FunctionEntity;
import fr.sqli.Cantine.entity.ImageEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.account.AdminService;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.AdminNotFound;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.images.ImageService;
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
    void  getAdminByIDTest () throws InvalidPersonInformationException, AdminNotFound {
        var id = 1 ;
        FunctionEntity functionEntity = new FunctionEntity();
        functionEntity.setId(1);
        functionEntity.setName("test-function");

        AdminEntity adminEntity = new AdminEntity();
        adminEntity.setId(1);
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
        Mockito.when(this.adminDao.findById(adminEntity.getId())).thenReturn(Optional.of(adminEntity));

        var  rsult =  this.adminService.getAdminById(id);

        Assertions.assertEquals(rsult.getId(), adminEntity.getId());
        Assertions.assertEquals(rsult.getFirstname(), adminEntity.getFirstname());
        Assertions.assertEquals(rsult.getLastname(), adminEntity.getLastname());
        Assertions.assertEquals(rsult.getEmail(), adminEntity.getEmail());
        Assertions.assertEquals(rsult.getBirthdate(), adminEntity.getBirthdate());
        Assertions.assertEquals(rsult.getAddress(), adminEntity.getAddress());
        Assertions.assertEquals(rsult.getTown(), adminEntity.getTown());
        Assertions.assertEquals(rsult.getPhone(), adminEntity.getPhone());
        Assertions.assertEquals(rsult.getImage(), adminEntity.getImage().getImagename());


    }

    @Test
    void  getAdminByIdWithNotFoundAdmin () throws InvalidPersonInformationException {
        Integer idAdmin = 1 ;
        Mockito.when(this.adminDao.findById(idAdmin)).thenReturn(Optional.empty());
        Assertions.assertThrows(AdminNotFound.class, () -> {
            this.adminService.getAdminById(idAdmin)  ;
        });


    }


    @Test
    void getAdminByIdWithNegativeID (){
        Integer idAdmin = -1;
        assertThrows(InvalidPersonInformationException.class, () -> {
            this.adminService.getAdminById(idAdmin) ;
        });
    }
    @Test
    void getAdminByIdWithNullID (){
        Integer idAdmin = null;
         assertThrows(InvalidPersonInformationException.class, () -> {
            this.adminService.getAdminById(idAdmin);
        });
    }








}
