package fr.sqli.Cantine.service.order;


import fr.sqli.Cantine.dao.IOrderDao;
import fr.sqli.Cantine.dao.IStudentDao;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

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







}
