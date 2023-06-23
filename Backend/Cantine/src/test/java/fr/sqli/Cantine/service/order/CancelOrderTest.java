package fr.sqli.Cantine.service.order;


import fr.sqli.Cantine.dao.IOrderDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.entity.OrderEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import fr.sqli.Cantine.service.order.exception.OrderNotFoundException;
import fr.sqli.Cantine.service.order.exception.UnableToCancelOrderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

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
    private  OrderService orderService;



    @BeforeEach
    void setUp() {
        this.mockEnvironment.setProperty("sqli.canine.order.qrcode.path" , "images/orders/");
        this.mockEnvironment.setProperty("sqli.canine.order.qrcode.image.format" , ".png");
        this.orderService = new OrderService(mockEnvironment , orderDao, studentDao , null , null , null );

    }










/************************************* ORDER  CAN NOT  BE  CANCELED  BECAUSE IT'S ALREADY VALIDATED  *************************************/

@Test
void  cancelOrderWithOrderWrongStudentTest() {

    var  orderId =  3;
    OrderEntity  orderEntity =  new OrderEntity();
    orderEntity.setId(orderId);
    orderEntity.setStatus(0);

    Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(orderEntity));
    Assertions.assertThrows(UnableToCancelOrderException.class  ,  ()->{
        orderService.cancelOrder(orderId);
    });

    Mockito.verify(orderDao ,  Mockito.times(1)).findById(orderId);
    Mockito.verify(studentDao ,  Mockito.times(0)).save(Mockito.any(StudentEntity.class));
    Mockito.verify(orderDao , Mockito.times(0)).deleteById(Mockito.anyInt());
}

   @Test
    void  cancelOrderWithOrderAlreadyValidatedTest() {

        var  orderId =  3;
        OrderEntity  orderEntity =  new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setStatus(0);

        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.of(orderEntity));
        Assertions.assertThrows(UnableToCancelOrderException.class  ,  ()->{
            orderService.cancelOrder(orderId);
        });

        Mockito.verify(orderDao ,  Mockito.times(1)).findById(orderId);
        Mockito.verify(studentDao ,  Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao , Mockito.times(0)).deleteById(Mockito.anyInt());
    }



    /************************************* ORDER NOT  FOUND  TESTS  *************************************/



    @Test
    void  cancelOrderWithOrderNotFoundTest() {
        var  orderId =  3;
        Mockito.when(orderDao.findById(orderId)).thenReturn(Optional.empty());
        Assertions.assertThrows(OrderNotFoundException.class  ,  ()->{
            orderService.cancelOrder(orderId);
        });

        Mockito.verify(orderDao ,  Mockito.times(1)).findById(orderId);

        Mockito.verify(studentDao ,  Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao , Mockito.times(0)).deleteById(Mockito.anyInt());
    }



     /************************************* ORDER ID  TESTS *************************************/

    @Test
    void  cancelOrderWithNegativeOrderIdTest() {
        Assertions.assertThrows(InvalidOrderException.class  ,  ()->{
            orderService.cancelOrder(-3);
        });


        Mockito.verify(studentDao ,  Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao , Mockito.times(0)).deleteById(Mockito.anyInt());
    }

    @Test
    void  cancelOrderWithNUllOrderIdTest() {
        Assertions.assertThrows(InvalidOrderException.class  ,  ()->{
            orderService.cancelOrder(null);
        });
        Mockito.verify(studentDao ,  Mockito.times(0)).save(Mockito.any(StudentEntity.class));
        Mockito.verify(orderDao , Mockito.times(0)).deleteById(Mockito.anyInt());
    }


}
