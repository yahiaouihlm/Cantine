package fr.sqli.Cantine.service.order;


import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AddOrderTest {
    @Mock
    private IOrderDao orderDao;
    @Mock
    private IMenuDao  menuDao;
    @Mock
    private IMealDao mealDao;
    @Mock
    private IStudentDao studentDao;
    @Mock
    private ITaxDao  taxDao;

    @Mock
    private MockEnvironment  env;
    @InjectMocks
    private  OrderService orderService;
    private OrderDtoIn orderDtoIn;


    @BeforeEach
    void  SetUp  () {
        this.env  = new MockEnvironment();
        /* TODO  if we change  the  path property */
        this.env.setProperty("sqli.canine.order.qrcode.path" , "images/orders/qrcode");
        this.env.setProperty("sqli.canine.order.qrcode.image.format" , "png");
        this.orderService =  new OrderService(env , orderDao , studentDao , mealDao , menuDao , taxDao);


         this.orderDtoIn = new OrderDtoIn();
            this.orderDtoIn.setStudentId(1);
            this.orderDtoIn.setMealsId(List.of(  1 , 2 , 3));
            this.orderDtoIn.setMenusId(List.of(  1 , 2 , 3));


    }


    @AfterEach
    void  tearDown () {
        this.orderDtoIn = null;
        this.env = null;
    }







}
