package fr.sqli.cantine.service.order;

import fr.sqli.cantine.dao.IOrderDao;
import fr.sqli.cantine.dao.IUserDao;
import fr.sqli.cantine.entity.*;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;
import fr.sqli.cantine.service.order.impl.OrderService;
import fr.sqli.cantine.service.users.exceptions.InvalidUserInformationException;
import fr.sqli.cantine.service.users.exceptions.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GetOrdersTests {

    @Mock
    private IOrderDao orderDao;
    @Mock
    private Environment environment;
    @Mock
    private IUserDao userDao;

    @InjectMocks
    private OrderService orderService;
    private UserEntity studentEntity;

    @BeforeEach
    void setUp() {
        this.studentEntity = new UserEntity();
        this.studentEntity.setId(java.util.UUID.randomUUID().toString());
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setName("image");
        this.studentEntity.setImage(imageEntity);
        StudentClassEntity studentClassEntity = new StudentClassEntity();
        studentClassEntity.setName("class");
        this.studentEntity.setStudentClass(studentClassEntity);
    }

    @AfterEach
    void tearDown() {
        this.studentEntity = null;
    }


    /******************************************* GET  ORDERS  BY  DATE TESTS *******************************************/
    @Test
    void getOrdersByDateWithInvalidUser() {
        LocalDate date = LocalDate.now();
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        UserEntity adminEntity = new UserEntity();
        adminEntity.setEmail("admin@email.com");


        Mockito.when(authentication.getPrincipal()).thenReturn(adminEntity.getEmail());
        Mockito.when(this.userDao.findAdminByEmail(adminEntity.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidUserInformationException.class, () -> this.orderService.getOrdersByDate(date));
    }

    @Test
    void getOrdersByDateWithNullDate() {
        LocalDate date = null;
        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.getOrdersByDate(date));
    }

    /******************************************* GET  ORDERS TESTS *******************************************/

    @Test
    void getStudentOrdersWithStudentNotFound() {
        String studentUuid = java.util.UUID.randomUUID().toString();
        Mockito.when(this.userDao.findStudentById(studentUuid)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> this.orderService.getStudentOrder(studentUuid));

    }

    @Test
    void getStudentOrdersWithInvalidUuid() throws UserNotFoundException {
        String studentUuid = "null";
        Assertions.assertEquals(this.orderService.getStudentOrder(studentUuid), List.of());
    }

    @Test
    void getStudentOrdersWithNullStudentUuid() throws UserNotFoundException {
        String studentUuid = null;
        Assertions.assertEquals(this.orderService.getStudentOrder(studentUuid), List.of());
    }


}
