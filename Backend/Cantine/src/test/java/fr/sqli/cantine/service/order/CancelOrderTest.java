package fr.sqli.cantine.service.order;

import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.entity.AdminEntity;
import fr.sqli.cantine.entity.OrderEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.service.order.exception.CancelledOrderException;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;
import fr.sqli.cantine.service.order.exception.OrderNotFoundException;
import fr.sqli.cantine.service.order.exception.UnableToCancelOrderException;
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

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CancelOrderTest {
    @Mock
    private IOrderDao orderDao;
    private IMenuDao menuDao;
    @Mock
    private IMealDao mealDao;
    @Mock
    private IStudentDao studentDao;

    @Mock
    private IAdminDao adminDao;

    @Mock
    private Environment environment;

    @InjectMocks
    private OrderService orderService;

    private StudentEntity studentEntity;
    private AdminEntity adminEntity;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        this.studentEntity = new StudentEntity();
        this.studentEntity.setUuid(java.util.UUID.randomUUID().toString());
        this.studentEntity.setFirstname("John");
        this.studentEntity.setLastname("Doe");
        this.studentEntity.setEmail("student@student.com");
        this.studentEntity.setWallet(BigDecimal.valueOf(100));

        this.adminEntity = new AdminEntity();
        this.adminEntity.setUuid(java.util.UUID.randomUUID().toString());
        this.adminEntity.setFirstname("John");
        this.adminEntity.setLastname("Doe");
        this.adminEntity.setEmail("admin@admin.com");


        this.orderEntity = new OrderEntity();
        this.orderEntity.setUuid(this.studentEntity.getUuid());
        this.orderEntity.setStudent(this.studentEntity);
        this.orderEntity.setStatus(0);
        this.orderEntity.setPrice(BigDecimal.valueOf(10));
        this.orderEntity.setCancelled(false);

    }

    @AfterEach
    void tearDown() {
        this.studentEntity = null;
        this.orderEntity = null;
    }

    // ******************************************* Cancel Order  By  Admin *******************************************/
    @Test
    void cancelOrderByAdminWithStudentNotFound() {

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(this.adminDao.findByEmail(this.adminEntity.getEmail())).thenReturn(Optional.of(this.adminEntity));
        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.of(this.orderEntity));
        Mockito.when(authentication.getPrincipal()).thenReturn(this.adminEntity.getEmail());

        Mockito.when(this.studentDao.findByUuid(this.orderEntity.getStudent().getUuid())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.orderService.cancelOrderByAdmin(this.orderEntity.getUuid());
        });

        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderEntity.getStudent().getUuid());
        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }


    @Test
    void cancelOrderByAdminWithAlreadyCancelledOrder() {

        this.orderEntity.setCancelled(true);

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(this.adminDao.findByEmail(this.adminEntity.getEmail())).thenReturn(Optional.of(this.adminEntity));
        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.of(this.orderEntity));

        Mockito.when(authentication.getPrincipal()).thenReturn(this.adminEntity.getEmail());

        Assertions.assertThrows(CancelledOrderException.class, () -> {
            this.orderService.cancelOrderByAdmin(this.orderEntity.getUuid());
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }

    @Test
    void cancelOrderByAdminWithAlreadyValidatedOrder() {

        this.orderEntity.setStatus(2);

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(this.adminDao.findByEmail(this.adminEntity.getEmail())).thenReturn(Optional.of(this.adminEntity));
        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.of(this.orderEntity));

        Mockito.when(authentication.getPrincipal()).thenReturn(this.adminEntity.getEmail());

        Assertions.assertThrows(CancelledOrderException.class, () -> {
            this.orderService.cancelOrderByAdmin(this.orderEntity.getUuid());
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }

    @Test
    void cancelOrderByAdminWithOrderNotFound() {

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Mockito.when(this.adminDao.findByEmail(this.adminEntity.getEmail())).thenReturn(Optional.of(this.adminEntity));
        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.empty());
        Mockito.when(authentication.getPrincipal()).thenReturn(this.adminEntity.getEmail());

        Assertions.assertThrows(OrderNotFoundException.class, () -> {
            this.orderService.cancelOrderByAdmin(this.orderEntity.getUuid());
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }

    @Test
    void cancelOrderByAdminWithAdminNotFound() {
        String orderUuid = this.orderEntity.getUuid();
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getPrincipal()).thenReturn(this.adminEntity.getEmail());
        Mockito.when(this.adminDao.findByEmail(this.adminEntity.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidUserInformationException.class, () -> {
            this.orderService.cancelOrderByAdmin(orderUuid);
        });

        Mockito.verify(this.adminDao, Mockito.times(1)).findByEmail(this.adminEntity.getEmail());
    }

    @Test
    void cancelOrderByAdminWithInvalidUuid() {
        String orderUuid = "null";
        Assertions.assertThrows(InvalidOrderException.class, () -> {
            this.orderService.cancelOrderByAdmin(orderUuid);
        });
    }


    @Test
    void cancelOrderByAdminWithNullUuid() {
        String orderUuid = null;
        Assertions.assertThrows(InvalidOrderException.class, () -> {
            this.orderService.cancelOrderByAdmin(orderUuid);
        });
    }


    // ******************************************* Cancel Order  By  Student *******************************************/

    @Test
    void cancelOrderByStudentWithStudentNotFoundByRequest() {

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.of(this.orderEntity));
        Mockito.when(authentication.getPrincipal()).thenReturn(this.studentEntity.getEmail());

        Mockito.when(this.studentDao.findByUuid(this.orderEntity.getStudent().getUuid())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.orderService.cancelOrderByStudent(this.orderEntity.getUuid());
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.studentDao, Mockito.times(1)).findByUuid(this.orderEntity.getStudent().getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }

    @Test
    void cancelOrderByStudentWithOrderAlreadyCanceled() {

        this.orderEntity.setCancelled(true);

        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.of(this.orderEntity));

        Mockito.when(authentication.getPrincipal()).thenReturn(this.studentEntity.getEmail());

        Assertions.assertThrows(UnableToCancelOrderException.class, () -> {
            this.orderService.cancelOrderByStudent(this.orderEntity.getUuid());
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }

    @Test
    void cancelOrderByStudentWithOrderAlreadyValidated() {
        this.orderEntity.setStatus(2);
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Mockito.when(this.orderDao.findOrderById(this.orderEntity.getUuid())).thenReturn(Optional.of(this.orderEntity));

        Mockito.when(authentication.getPrincipal()).thenReturn(this.studentEntity.getEmail());

        Assertions.assertThrows(UnableToCancelOrderException.class, () -> {
            this.orderService.cancelOrderByStudent(this.orderEntity.getUuid());
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(this.orderEntity.getUuid());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(this.orderEntity);
    }


    @Test
    void cancelOrderByStudentWithStudentNotFound() {
        String orderUuid = java.util.UUID.randomUUID().toString();
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Mockito.when(this.orderDao.findOrderById(orderUuid)).thenReturn(Optional.of(this.orderEntity));

        Mockito.when(authentication.getPrincipal()).thenReturn(java.util.UUID.randomUUID().toString());

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            this.orderService.cancelOrderByStudent(orderUuid);
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(orderUuid);
    }

    @Test
    void cancelOrderByStudentWithOrderNotFound() {
        String orderUuid = java.util.UUID.randomUUID().toString();
        // security context
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(this.orderDao.findOrderById(orderUuid)).thenReturn(Optional.empty());

        Mockito.when(authentication.getPrincipal()).thenReturn(this.studentEntity.getEmail());
        Assertions.assertThrows(OrderNotFoundException.class, () -> {
            this.orderService.cancelOrderByStudent(orderUuid);
        });


        Mockito.verify(this.orderDao, Mockito.times(1)).findOrderById(orderUuid);
    }

    @Test
    void cancelOrderByStudentWithInvalidUuid() {
        String orderUuid = "null";
        Assertions.assertThrows(InvalidOrderException.class, () -> {
            this.orderService.cancelOrderByStudent(orderUuid);
        });

    }

    @Test
    void cancelOrderByStudentWithNullUuid() {
        String orderUuid = null;
        Assertions.assertThrows(InvalidOrderException.class, () -> {
            this.orderService.cancelOrderByStudent(orderUuid);
        });

    }
}
