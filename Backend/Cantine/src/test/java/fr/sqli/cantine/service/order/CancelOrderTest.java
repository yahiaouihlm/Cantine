package fr.sqli.cantine.service.order;


import fr.sqli.cantine.dao.IOrderDao;
import fr.sqli.cantine.dao.IStudentDao;
import fr.sqli.cantine.entity.OrderEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;
import fr.sqli.cantine.service.order.exception.OrderNotFoundException;
import fr.sqli.cantine.service.order.exception.UnableToCancelOrderException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CancelOrderTest {
    @Mock
    private IOrderDao orderDao;
    @Mock
    private IStudentDao studentDao;

    @Mock
    private MockEnvironment mockEnvironment;
    @InjectMocks
    private OrderService orderService;


    @BeforeEach
    void setUp() {
        this.mockEnvironment.setProperty("sqli.canine.order.qrcode.path", "images/orders/");
        this.mockEnvironment.setProperty("sqli.canine.order.qrcode.image.format", ".png");
        this.orderService = new OrderService(mockEnvironment, orderDao,null ,studentDao, null, null, null , null );

    }


    @Test
    void cancelOrderTest() throws OrderNotFoundException, InvalidOrderException, StudentNotFoundException, UnableToCancelOrderException {
        var orderId = 3;
        var studentId = 1;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setCancelled(false);
        orderEntity.setPrice(BigDecimal.TEN);
        orderEntity.setStatus(0);

        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setId(studentId);
        studentEntity.setWallet(BigDecimal.TWO);

        orderEntity.setStudent(studentEntity);


        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(orderEntity));
        Mockito.when(studentDao.findById(studentId)).thenReturn(Optional.of(studentEntity));

        this.orderService.cancelOrder(orderId);


        Assertions.assertTrue(studentEntity.getWallet().compareTo(BigDecimal.TWO.add(BigDecimal.TEN)) == 0);


        Mockito.verify(orderDao, Mockito.times(1)).findById(orderId);
        Mockito.verify(studentDao, Mockito.times(1)).findById(studentId);
        Mockito.verify(studentDao, Mockito.times(1)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(1)).save(Mockito.any(OrderEntity.class));


    }


    /************************************* ORDER  CAN NOT  BE  CANCELED  BECAUSE IT'S ALREADY VALIDATED  *************************************/

    @Test
    void cancelOrderWithOrderWrongStudentTest() {

        var orderId = 3;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setStatus(1);

        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(orderEntity));
        Assertions.assertThrows(UnableToCancelOrderException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        Mockito.verify(orderDao, Mockito.times(1)).findById(orderId);
        Mockito.verify(studentDao, Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void cancelOrderWithOrderAlreadyCancelledTest() {

        var orderId = 3;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setStatus(0);
        orderEntity.setCancelled(true);

        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(orderEntity));
        Assertions.assertThrows(UnableToCancelOrderException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        Mockito.verify(orderDao, Mockito.times(1)).findById(orderId);
        Mockito.verify(studentDao, Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void cancelOrderWithOrderAlreadyValidatedTest() {

        var orderId = 3;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setStatus(1);

        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(orderEntity));
        Assertions.assertThrows(UnableToCancelOrderException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        Mockito.verify(orderDao, Mockito.times(1)).findById(orderId);
        Mockito.verify(studentDao, Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /************************************* ORDER NOT  FOUND  TESTS  *************************************/


    @Test
    void cancelOrderWithOrderNotFoundTest() {
        var orderId = 3;
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        Mockito.verify(orderDao, Mockito.times(1)).findById(orderId);

        Mockito.verify(studentDao, Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /************************************* ORDER ID  TESTS *************************************/

    @Test
    void cancelOrderWithNegativeOrderIdTest() {
        Assertions.assertThrows(InvalidOrderException.class, () -> {
            orderService.cancelOrder(-3);
        });


        Mockito.verify(studentDao, Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void cancelOrderWithNUllOrderIdTest() {
        Assertions.assertThrows(InvalidOrderException.class, () -> {
            orderService.cancelOrder(null);
        });
        Mockito.verify(studentDao, Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao, Mockito.times(0)).save(Mockito.any());
    }


}
