package fr.sqli.cantine.service.admin;

import fr.sqli.cantine.dao.IAdminDao;
import fr.sqli.cantine.dao.IFunctionDao;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.FunctionEntity;
import fr.sqli.cantine.service.users.admin.impl.AdminService;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DisableAccount {
    private static final Logger LOG = LogManager.getLogger();
    final   String  IMAGE_TESTS_PATH = "imagesTests/ImageForTest.jpg";
    @Mock
    private IAdminDao adminDao;

    @Mock
    private IFunctionDao functionDao;

    @InjectMocks
    private AdminService adminService;

    @Mock
     private  MockEnvironment env;
    private FunctionEntity functionEntity;
    private  AdminEntity adminEntity;


    @BeforeEach
    void  init  () {
         this.functionEntity = new FunctionEntity();
        functionEntity.setId(1);
        functionEntity.setName("name");

         this.adminEntity = new AdminEntity();
        adminEntity.setId(1);

        adminEntity.setFirstname("firstname");
        adminEntity.setLastname("lastname");
        adminEntity.setEmail("email");
        adminEntity.setPassword("password");
        adminEntity.setBirthdate(LocalDate.now());
        adminEntity.setTown("town");
        adminEntity.setAddress("address");
        adminEntity.setFunction(this.functionEntity);
    }

    @Test  /*** No Exception thrown  ***/
    void  disabledAccountWithValidAdmin () throws InvalidUserInformationException {
        Integer idMenu = 1;
        Mockito.when(this.adminDao.findById(idMenu)).thenReturn(Optional.of(this.adminEntity));
       /* this.adminService.disableAdminAccount(idMenu);*/
        Mockito.verify(this.adminDao, Mockito.times(1)).findById(idMenu);
        Mockito.verify(this.adminDao, Mockito.times(1)).save(this.adminEntity);

    }








    /******************************** TESTS  ADMIN ID  ********************************/

    @Test
    void  disabledAccountWithNotFoundAdmin () {
        Integer idMenu = 1 ;


        Mockito.when(this.adminDao.findById(idMenu)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.adminService.disableAdminAccount("idMenu");
        });

        Mockito.verify(this.adminDao, Mockito.times(1)).findById(idMenu);
        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void disabledAccountWithNegativeID (){
        assertThrows(InvalidUserInformationException.class, () -> {
            this.adminService.disableAdminAccount( "");
        });

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void disabledAccountWithNullUuid(){
        assertThrows(InvalidUserInformationException.class, () -> {
            this.adminService.disableAdminAccount( null);
        });

        Mockito.verify(this.adminDao, Mockito.times(0)).save(Mockito.any());

    }





}
