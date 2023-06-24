package fr.sqli.Cantine.controller.order;

import fr.sqli.Cantine.controller.AbstractContainerConfig;
import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;


@SpringBootTest
@AutoConfigureMockMvc
public class CancelOrderTest  extends AbstractContainerConfig implements   IOrderTest {

    final  String paramReq = "?" + "orderId" + "=";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IOrderDao orderDao;
    @Autowired
    private IStudentDao studentDao;
    @Autowired
    private IMealDao mealDao;
    @Autowired
    private IMenuDao menuDao;
    @Autowired
    private IStudentClassDao studentClassDao;
    private MealEntity mealEntity;
    private MenuEntity menuEntity;
    private StudentEntity studentEntity;
    private OrderEntity orderEntity;

    void cleanDataBase ()  {
        this.orderDao.deleteAll();
        this.studentDao.deleteAll();
        this.mealDao.deleteAll();
        this.menuDao.deleteAll();
        this.studentClassDao.deleteAll();
    }

    void  createInfoForOrder  ()   {
        this.mealEntity = IOrderTest.createMeal();
        this.mealEntity = this.mealDao.save(this.mealEntity);

        this.menuEntity = IOrderTest.createMenu(this.mealEntity);
        this.menuEntity = this.menuDao.save(this.menuEntity);

        StudentClassEntity studentClassEntity =new  StudentClassEntity();
        studentClassEntity.setName("JAVA SQLI");

        studentClassEntity =  this.studentClassDao.save(studentClassEntity);

        this.studentEntity =  IOrderTest.createStudent( "yahiaoui@halim.com" ,  studentClassEntity);
        this.studentDao.save(this.studentEntity);

    }


    void createOrder  ()   {
        var  tax =  BigDecimal.valueOf(2);
        this.orderEntity = new OrderEntity();
        this.orderEntity.setStudent(this.studentEntity);
        this.orderEntity.setMeals(List.of(this.mealEntity));
        this.orderEntity.setMenus(List.of(this.menuEntity));
        this.orderEntity.setPrice(this.mealEntity.getPrice().add(this.menuEntity.getPrice()).add(tax));
        this.orderEntity.setCreationDate(LocalDate.now());
        this.orderEntity.setQRCode("QRCode"); // just because  we  dont  need to create   Image QRCODE for  this  test
        this.orderEntity.setCreationTime(new Time(System.currentTimeMillis()));
        this.orderEntity.setStatus(0);
        this.orderEntity =  this.orderDao.save(this.orderEntity);
    }


    @BeforeEach
    void setUp() {
        this.cleanDataBase();
        this.createInfoForOrder();
        this.createOrder();
    }


    @Test
    void   cancelOrderTest () throws Exception {
        var orderId =  this.orderEntity.getId() ;
        var  oldStudentWallet =  this.studentEntity.getWallet() ;
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + orderId )) ;

        result.andExpect(MockMvcResultMatchers.status().isOk()) ;
        result.andExpect(MockMvcResultMatchers.content().string(ORDER_CANCELLED_SUCCESSFULLY)) ;

        var  student =  this.studentDao.findById(this.studentEntity.getId()).get();
        Assertions.assertTrue(
                student.getWallet().compareTo(this.orderEntity.getPrice().add(oldStudentWallet)) == 0
        );

        Assertions.assertTrue(this.orderDao.findById(this.orderEntity.getId()).get().isCancelled());



    }




    /*********************************************** TESTS  ORDER ***************************************************/

    @Test
    void cancelOrderWithOrderAlreadyCancelled() throws Exception {
        this.orderEntity.setCancelled(true);
        this.orderEntity =  this.orderDao.save(this.orderEntity);
        var orderId =  this.orderEntity.getId() ;


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + orderId )) ;

        result.andExpect(MockMvcResultMatchers.status().isForbidden()) ;
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("OrderAlreadyCanceled")))) ;
    }




    @Test
    void cancelOrderWithOrderAlreadyValidated() throws Exception {
        this.orderEntity.setStatus(1);
        this.orderEntity =  this.orderDao.save(this.orderEntity);
        var orderId =  this.orderEntity.getId() ;


        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + orderId )) ;

        result.andExpect(MockMvcResultMatchers.status().isForbidden()) ;
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("OrderAlreadyValidated")))) ;
    }

    @Test
    void cancelOrderWithOrderNotFound() throws Exception {
         var  orderId =  this.orderEntity.getId() + 10 ;
        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + orderId )) ;

        result.andExpect(MockMvcResultMatchers.status().isNotFound()) ;
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("OrderNotFound")))) ;
    }


    /*********************************************** TESTS  ID'S  ***************************************************/
  @Test
  void cancelOrderWithNegativeId() throws Exception {

      var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + "-3" )) ;

      result.andExpect(MockMvcResultMatchers.status().isBadRequest()) ;
      result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("InvalidOrderId")))) ;
  }


  @Test
  void cancelOrderWithWrongId() throws Exception {

      var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + "null" )) ;

      result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()) ;
      result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("ArgumentNotValid")))) ;
  }


    @Test
    void cancelOrderWithNullId() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq + null )) ;

       result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()) ;
       result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("ArgumentNotValid")))) ;
    }



    @Test
    void cancelOrderWithOutId() throws Exception {

        var result = this.mockMvc.perform(MockMvcRequestBuilders.post(CANCEL_ORDER_URL + paramReq )) ;

        result.andExpect(MockMvcResultMatchers.status().isNotAcceptable()) ;
        result.andExpect(MockMvcResultMatchers.content().string(super.exceptionMessage(exceptionsMap.get("MissingArgument")))) ;
    }






}
