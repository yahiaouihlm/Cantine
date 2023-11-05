package fr.sqli.cantine.service.order;


import fr.sqli.cantine.dao.*;
import fr.sqli.cantine.dto.in.food.OrderDtoIn;
import fr.sqli.cantine.entity.MealEntity;
import fr.sqli.cantine.entity.MenuEntity;
import fr.sqli.cantine.entity.StudentEntity;
import fr.sqli.cantine.entity.TaxEntity;
import fr.sqli.cantine.service.admin.exceptions.InvalidPersonInformationException;

import fr.sqli.cantine.service.food.exceptions.InvalidFoodInformationException;
import fr.sqli.cantine.service.order.exception.InsufficientBalanceException;
import fr.sqli.cantine.service.order.exception.InvalidOrderException;
import fr.sqli.cantine.service.order.exception.OrderLimitExceededException;
import fr.sqli.cantine.service.order.exception.UnavailableFoodException;
import fr.sqli.cantine.service.student.exceptions.StudentNotFoundException;
import fr.sqli.cantine.service.superAdmin.exception.TaxNotFoundException;
import org.junit.jupiter.api.*;
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
    private IMenuDao menuDao;
    @Mock
    private IMealDao mealDao;
    @Mock
    private IStudentDao studentDao;
    @Mock
    private ITaxDao taxDao;

    @Mock
    private MockEnvironment env;
    @InjectMocks
    private OrderService orderService;
    private OrderDtoIn orderDtoIn;

    private StudentEntity studentEntity;
    final Integer MAXIMUM_ORDER_PER_DAY = 20;

    @BeforeEach
    void SetUp() {
        this.env = new MockEnvironment();
        /* TODO  if we change  the  path property */
        this.env.setProperty("sqli.canine.order.qrcode.path", "images/orders/qrcode");
        this.env.setProperty("sqli.canine.order.qrcode.image.format", "png");
        this.orderService = new OrderService(env, orderDao, null, studentDao, mealDao, menuDao, taxDao, null);


        this.orderDtoIn = new OrderDtoIn();
        this.orderDtoIn.setStudentId(1);
        this.orderDtoIn.setMealsId(List.of(1, 2, 3));
        this.orderDtoIn.setMenusId(List.of(1, 2, 3));

        //  student  entity  for  test
        this.studentEntity = new StudentEntity();
        this.studentEntity.setId(1);
        this.studentEntity.setFirstname("student");
        this.studentEntity.setLastname("student");
        this.studentEntity.setEmail("student@test,com");

    }


    @AfterEach
    void tearDown() {
        this.orderDtoIn = null;
        this.env = null;
    }
 /*   @Test
    @Disabled
    void addOrderWithTheSomePrice () throws InvalidPersonInformationException, InvalidMenuInformationException, TaxNotFoundException, MealNotFoundException, InvalidOrderException, InvalidMealInformationException, MenuNotFoundException, InsufficientBalanceException, StudentNotFoundException, IOException, WriterException {
        // Init
        MealEntity  meal1 = new MealEntity();
        meal1.setId(1);
        meal1.setPrice(BigDecimal.valueOf(8));

        MealEntity  meal2 = new MealEntity();
        meal2.setId(2);
        meal2.setPrice(BigDecimal.valueOf(10));


        MenuEntity  menu1 = new MenuEntity();
        menu1.setId(1);
        menu1.setPrice(BigDecimal.valueOf(14));

        MenuEntity  menu2 = new MenuEntity();
        menu2.setId(2);
        menu2.setPrice(BigDecimal.valueOf(15));

        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(1));

        var  somePrice = BigDecimal.valueOf(14).add(BigDecimal.valueOf(10)).add(BigDecimal.valueOf(8)).add(BigDecimal.valueOf(15)).add(BigDecimal.valueOf(1));

        this.orderDtoIn.setMealsId(List.of(1 , 2));
        this.orderDtoIn.setMenusId(List.of(1 , 2));

        this.studentEntity.setWallet(somePrice);
        // when
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(1)).thenReturn(Optional.of(meal1));
        Mockito.when(this.mealDao.findById(2)).thenReturn(Optional.of(meal2));
        Mockito.when(this.menuDao.findById(1)).thenReturn(Optional.of(menu1));
        Mockito.when(this.menuDao.findById(2)).thenReturn(Optional.of(menu2));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of(taxEntity));

        this.orderService.addOrder(this.orderDtoIn);  //  we  make  the  test  here
        // then
        Assertions.assertEquals(this.studentEntity.getWallet() , BigDecimal.valueOf(0));





    }

*/

    /************************ TEST ADD ORDER LIMIT  ************************/
    @Test
    void addOrderWitExceedMenuAndMealOrderLimitTest() {
        List<Integer> meals = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        List<Integer> menu = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // we  make One  Test with  One  Menu  iN  The  Order
        this.orderDtoIn.setMenusId(menu);
        this.orderDtoIn.setMealsId(meals);

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(OrderLimitExceededException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }

    @Test
    void addOrderWitExceedMenuOrderLimitTest() {
        List<Integer> menu = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21);
        // we  make One  Test with  One  Menu  iN  The  Order
        this.orderDtoIn.setMenusId(menu);
        this.orderDtoIn.setMealsId(null);

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(OrderLimitExceededException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }

    @Test
    void addOrderWitExceedMealOrderLimitTest() {
        List<Integer> meals = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21);
        // we  make One  Test with  One  Menu  iN  The  Order
        this.orderDtoIn.setMenusId(meals);
        this.orderDtoIn.setMealsId(null);

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(OrderLimitExceededException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }


    /************************ TEST ADD ORDER  WITH   Student  Balance ************************/


    @Test
    void addOrderWithInsufficientStudentBalance() {
        //  Init
        // we  make One  Test with  One  Menu  iN  The  Order
        var menuIdFound = 1;
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        this.orderDtoIn.setMealsId(null);

        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);

        this.studentEntity.setWallet(BigDecimal.valueOf(5));

        TaxEntity taxEntity = new TaxEntity();
        taxEntity.setTax(BigDecimal.valueOf(1));


        // when
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findById(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of(taxEntity));

        // then

        Assertions.assertThrows(InsufficientBalanceException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(menuIdFound);
        Mockito.verify(this.menuDao, Mockito.times(1)).findById(1);
        Mockito.verify(this.taxDao, Mockito.times(1)).findAll();
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }


    /*************************** TESTS  TAX     ************************/


    @Test
    void addOrderWitTwoTaxInDataBase() {
        // we  make One  Test with  One  Menu  iN  The  Order
        var menuIdFound = 1;
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        this.orderDtoIn.setMealsId(null);


        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findById(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of(new TaxEntity(), new TaxEntity()));

        Assertions.assertThrows(TaxNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findById(menuIdFound);
        Mockito.verify(this.menuDao, Mockito.times(1)).findById(menuIdFound);
        Mockito.verify(this.taxDao, Mockito.times(1)).findAll();
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }


    @Test
    void addOrderWithOutTax() {
        // we  make One  Test with  One  Menu  iN  The  Order
        var menuIdFound = 1;
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        this.orderDtoIn.setMealsId(null);


        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findById(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.taxDao.findAll()).thenReturn(List.of());

        Assertions.assertThrows(TaxNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findById(menuIdFound);
        Mockito.verify(this.menuDao, Mockito.times(1)).findById(1);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());


    }

    /***************************  TESTS  ORDERS  WITH   Unavailable  MENUS ID AND  MEALS  *****************************/
    @Test
    void addOrderWithUnavailableMenu() {
        this.orderDtoIn.setMealsId(null);
        var menuIdFound = 1;
        this.orderDtoIn.setMenusId(List.of(menuIdFound));
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(0);

        //  When
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findById(menuIdFound)).thenReturn(Optional.of(menuEntity));


        Assertions.assertThrows(UnavailableFoodException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.menuDao, Mockito.times(1)).findById(menuIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithUnavailableMeal() {
        var mealIdFound = 1;
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(mealIdFound));
        MealEntity mealEntity = new MealEntity();
        mealEntity.setId(mealIdFound);
        mealEntity.setPrice(BigDecimal.valueOf(10));
        mealEntity.setStatus(0);

        //  When
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealIdFound)).thenReturn(Optional.of(mealEntity));


        Assertions.assertThrows(UnavailableFoodException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.mealDao, Mockito.times(1)).findById(mealIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /***************************  TESTS  ORDERS  WITH   EMPTY MENUS ID AND  MEALS  *****************************/


    @Test
    void addOrderWithEmptyMealsAndMenu() {
        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of());
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(this.menuDao, Mockito.times(0)).findById(Mockito.any());
        Mockito.verify(this.mealDao, Mockito.times(0)).findById(Mockito.any());
    }


    /***************************  TESTS  ORDERS  WITH   EMPTY MENUS ID *****************************/
/*
    @Test
    void addOrderWithMenuNotFoundAndOtherFoundMenu() {

        this.orderDtoIn.setMealsId(null); // avoid  to  check  meals  id validation
        var menuIdFound = 1;
        var menuIdNotFound = 2;

        //  make  only  the  information    that we  need  for  the  test  ( Our  Meal Mock  )
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuIdFound);
        menuEntity.setPrice(BigDecimal.valueOf(10));
        menuEntity.setStatus(1);

        this.orderDtoIn.setMenusId(List.of(menuIdFound, menuIdNotFound));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.menuDao.findById(menuIdFound)).thenReturn(Optional.of(menuEntity));
        Mockito.when(this.menuDao.findById(menuIdNotFound)).thenReturn(Optional.empty());


        Assertions.assertThrows(MenuNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.menuDao, Mockito.times(1)).findById(menuIdNotFound);
        Mockito.verify(this.menuDao, Mockito.times(1)).findById(menuIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithMenuNotFoundTest() {
        var menuId = 1;
        this.orderDtoIn.setMealsId(null); // avoid  to  check  meals  id validation
        this.orderDtoIn.setMenusId(List.of(menuId));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));

        Mockito.when(this.menuDao.findById(menuId)).thenReturn(Optional.empty());
        Assertions.assertThrows(MenuNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.menuDao, Mockito.times(1)).findById(menuId);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());

    }
*/


    /***************************  TESTS  ORDERS  WITH   EMPTY MEALS ID *****************************/

/*
    @Test
    void addOrderWithMealNotFoundAndOtherFoundMeal() {
        var mealIdFound = 1;
        var mealIdNotFound = 2;

        //  make  only  the  information    that we  need  for  the  test  ( Our  Meal Mock  )
        MealEntity mealEntity = new MealEntity();
        mealEntity.setId(mealIdFound);
        ;
        mealEntity.setPrice(new BigDecimal(10));
        mealEntity.setStatus(1);
        this.orderDtoIn.setMealsId(List.of(mealIdFound, mealIdNotFound));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealIdNotFound)).thenReturn(Optional.empty());
        Mockito.when(this.mealDao.findById(mealIdFound)).thenReturn(Optional.of(mealEntity));


        Assertions.assertThrows(MealNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.mealDao, Mockito.times(1)).findById(mealIdNotFound);
        Mockito.verify(this.mealDao, Mockito.times(1)).findById(mealIdFound);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithMealNotFoundWithEmptyMenusIdTest() {
        var mealId = 1;
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(mealId));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealId)).thenReturn(Optional.empty());
        Assertions.assertThrows(MealNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.mealDao, Mockito.times(1)).findById(mealId);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());

    }*/


/*
    @Test
    void addOrderWithMealNotFoundTest() {
        var mealId = 1;
        this.orderDtoIn.setMealsId(List.of(mealId));
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));
        Mockito.when(this.mealDao.findById(mealId)).thenReturn(Optional.empty());
        Assertions.assertThrows(MealNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));

        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.mealDao, Mockito.times(1)).findById(mealId);
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());

    }
*/


    /***************************  TESTS  ORDERS  WITH   EMPTY MEALS ID  AND MENUS ID  *****************************/

    @Test
    void addOrderWithEmptyMealsIdAndMenusIdTest() {

        this.orderDtoIn.setMealsId(List.of());
        this.orderDtoIn.setMenusId(List.of());

        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.of(this.studentEntity));

        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());

        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /***************************  TESTS  ORDERS  WITH  STUDENT NOT  FOUND  *****************************/

    @Test
    void addOrderWithStudentNotFound() {
        Mockito.when(this.studentDao.findById(this.orderDtoIn.getStudentId())).thenReturn(Optional.empty());
        Assertions.assertThrows(StudentNotFoundException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.studentDao, Mockito.times(1)).findById(this.orderDtoIn.getStudentId());
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    /***************************  TESTS  ORDERS  INFORMATION  ***********************/
/*

    @Test
    void addOrderWithNegativeMenusIdWithNullMealIdTest() {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(List.of(1, 1, -1));
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithNegativeMenuIdTest() {
        this.orderDtoIn.setMenusId(List.of(1, 1, -1));
        Assertions.assertThrows(InvalidMenuInformationException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNegativeMealIdWithNullMenuIdTest() {
        this.orderDtoIn.setMenusId(null);
        this.orderDtoIn.setMealsId(List.of(1, 1, -1));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithNegativeMealIdTest() {
        this.orderDtoIn.setMealsId(List.of(1, 1, -1));
        Assertions.assertThrows(InvalidFoodInformationException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNullMealIdAndNullMenuId() {
        this.orderDtoIn.setMealsId(null);
        this.orderDtoIn.setMenusId(null);
        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithNegativeStudentIDTest() {
        this.orderDtoIn.setStudentId(-4);
        Assertions.assertThrows(InvalidPersonInformationException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    void addOrderWithNullStudentIDTest() {
        this.orderDtoIn.setStudentId(null);
        Assertions.assertThrows(InvalidPersonInformationException.class, () -> this.orderService.addOrder(this.orderDtoIn));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }


    @Test
    void addOrderWithNullBody() {

        Assertions.assertThrows(InvalidOrderException.class, () -> this.orderService.addOrder(null));
        Mockito.verify(this.orderDao, Mockito.times(0)).save(Mockito.any());
    }
*/


}
