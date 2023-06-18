package fr.sqli.Cantine.service.order;


import fr.sqli.Cantine.dao.*;
import fr.sqli.Cantine.dto.in.food.OrderDtoIn;
import fr.sqli.Cantine.entity.MealEntity;
import fr.sqli.Cantine.entity.StudentEntity;
import fr.sqli.Cantine.service.admin.adminDashboard.exceptions.InvalidPersonInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.InvalidMealInformationException;
import fr.sqli.Cantine.service.admin.meals.exceptions.MealNotFoundException;
import fr.sqli.Cantine.service.admin.menus.exceptions.InvalidMenuInformationException;
import fr.sqli.Cantine.service.order.exception.InvalidOrderException;
import fr.sqli.Cantine.service.student.exceptions.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
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
import java.util.List;
import java.util.Optional;

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

    private StudentEntity  studentEntity;


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

            //  student  entity  for  test
            this.studentEntity = new StudentEntity();
            this.studentEntity.setId(1);
            this.studentEntity.setFirstname("student");
            this.studentEntity.setLastname("student");
            this.studentEntity.setEmail("student@test,com");

    }


    @AfterEach
    void  tearDown () {
        this.orderDtoIn = null;
        this.env = null;
    }

    /***************************  TESTS  ORDERS  WITH   EMPTY MENUS ID *****************************/




    /***************************  TESTS  ORDERS  WITH   EMPTY MEALS ID *****************************/


    @Test
    void  addOrderWithMealNotFoundAndOtherFoundMeal() {
        var  mealIdFound =  1 ;
        var  mealIdNotFound =  2 ;

        //  make  only  the  information    that we  need  for  the  test  ( Our  Meal Mock  )
         MealEntity mealEntity = new MealEntity();
            mealEntity.setId(mealIdFound);;
            mealEntity.setPrice(new BigDecimal(10));

        this.orderDtoIn.setMealsId(List.of(mealIdFound , mealIdNotFound));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealIdNotFound)).thenReturn(Optional.empty());
         Mockito.when(this.mealDao.findById(mealIdFound)).thenReturn(Optional.of(mealEntity));


        Assertions.assertThrows(MealNotFoundException.class , () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao , Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.mealDao , Mockito.times(1)).findById(mealIdNotFound);
        Mockito.verify(this.mealDao , Mockito.times(1)).findById(mealIdFound);
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void  addOrderWithMealNotFoundWithEmptyMenusIdTest () {
        var  mealId =  1 ;
         this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(mealId));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealId)).thenReturn(Optional.empty());
        Assertions.assertThrows(MealNotFoundException.class , () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao , Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.mealDao , Mockito.times(1)).findById(mealId);
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());

    }


    @Test
    void  addOrderWithMealNotFoundTest () {
        var  mealId =  1 ;
        this.orderDtoIn.setMealsId(List.of(mealId));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealId)).thenReturn(Optional.empty());
        Assertions.assertThrows(MealNotFoundException.class , () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao , Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.mealDao , Mockito.times(1)).findById(mealId);
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());

    }


    /***************************  TESTS  ORDERS  WITH   EMPTY MEALS ID  AND MENUS ID  *****************************/

    @Test
    void addOrderWithEmptyMealsIdAndMenusIdTest() {

        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of());

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));

        Assertions.assertThrows(InvalidOrderException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao , Mockito.times(1)).findById(this.orderDtoIn.getStudentId());

        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }



    /***************************  TESTS  ORDERS  WITH  STUDENT NOT  FOUND  *****************************/

    @Test
    void  addOrderWithStudentNotFound() {
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.empty());
        Assertions.assertThrows(StudentNotFoundException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao , Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }


    /***************************  TESTS  ORDERS  INFORMATION  ***********************/


    @Test
    void addOrderWithNegativeMenusIdWithNullMealIdTest()  {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(List.of( 1 , 1 , -1));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addOrderWithNegativeMenuIdTest()  {
        this.orderDtoIn.setMenusId(List.of( 1 , 1 , -1));
        Assertions.assertThrows(InvalidMenuInformationException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }






    @Test
    void addOrderWithNegativeMealIdWithNullMenuIdTest()  {
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of( 1 , 1 , -1));
        Assertions.assertThrows(InvalidMealInformationException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addOrderWithNegativeMealIdTest()  {
        this.orderDtoIn.setMealsId(List.of( 1 , 1 , -1));
        Assertions.assertThrows(InvalidMealInformationException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNullMealIdAndNullMenuId()  {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(null);
        Assertions.assertThrows(InvalidOrderException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addOrderWithNegativeStudentIDTest () {
        this.orderDtoIn.setStudentId(-4);
        Assertions.assertThrows(InvalidPersonInformationException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }
    @Test
    void addOrderWithNullStudentIDTest () {
       this.orderDtoIn.setStudentId(null);
        Assertions.assertThrows(InvalidPersonInformationException.class , () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao , Mockito.times(0)).save(Mockito.any());
    }





}
